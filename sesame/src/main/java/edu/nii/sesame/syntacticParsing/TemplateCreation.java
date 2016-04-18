package edu.nii.sesame.syntacticParsing;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import edu.nii.sesame.semanticStore.Gazeteer;
import edu.nii.sesame.semanticStore.Similarity;
import edu.nii.sesame.utils.Constant;
import edu.nii.sesame.utils.Constant.QUESTION_TYPE;
import edu.nii.sesame.utils.Constant.SPARQL_QUESTION_TYPE;
import edu.nii.sesame.utils.ParseTreeUtils;
import edu.stanford.nlp.ling.Label;
import edu.stanford.nlp.ling.Sentence;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.WordStemmer;

public class TemplateCreation {
	
	String query;
	List tokens;
	Tree parse;
	SPARQLTemplate template;
	List<Phrase> phrases = new ArrayList<Phrase>();
	static LexicalizedParser lp = LexicalizedParser.loadModel(Constant.PCG_MODEL);
	
	public TemplateCreation(String query)
	{
		this.query = query;
	}
	
	public void parse()
	{
		
		StringReader sr; 
	    PTBTokenizer tkzr;
	    WordStemmer ls = new WordStemmer(); 
	    sr = new StringReader(query);
    	tkzr = PTBTokenizer.newPTBTokenizer(sr);
    	tokens =  tkzr.tokenize();
    	System.out.println ("tokens: "+tokens);
    	parse = (Tree) lp.apply(tokens); 
    	System.out.println(parse.toString());
    	
	}
	
	public Phrase extractNN(Tree t)
	{
		String r = "";
		Phrase np =  new Phrase();
		
		np.nounType = "NN";
		for(Tree c : t.children())
		{
			if(c.pennString().startsWith("(NNP"))
				np.nounType = "NNP";

			
			if(c.pennString().startsWith("(NN"))
					r += c.firstChild().toString() + " " ;
			if(c.pennString().startsWith("(JJ"))
				np.adj = c.firstChild().toString();
			if(c.pennString().startsWith("(CD"))
				np.cd = c.firstChild().toString() ;
			if(c.pennString().startsWith("(CC"))
			{
				np.noun.add(r.trim());
				r = "";
			}
			
		}
		np.noun.add(r.trim());
		return np;
		
	}
	public SPARQLTemplate extractVPNPs()
	{
		parse();
		SPARQLTemplate temp = new SPARQLTemplate();
		
		SPARQL_QUESTION_TYPE qType = findQuestionType();
		
		List<Tree> q = new ArrayList<Tree>();
		q.add(parse);
		
		while(!q.isEmpty())
		{
			Tree t  = q.remove(0);
			Phrase np;
			if(t.pennString().startsWith("(WHNP") || t.pennString().startsWith("(NP") )
			{
				np = extractNN(t);
				phrases.add(np);
				System.out.println(np.toString());
			}
			else if(t.pennString().startsWith("(VP") )
			{
				np = extractVP(t);
				phrases.add(np);
				System.out.println(np.toString());
			}
			for(Tree c : t.children())
					q.add(c);
		}
		
		temp.parse = parse;
		temp.phrases = phrases;
		return temp;
	}
	
	private Phrase extractVP(Tree t) {
		String r = "";
		Phrase np =  new Phrase();
		np.nounType = "VP";
		for(Tree c : t.children())
		{
			
			if(c.pennString().startsWith("(VB"))
					r += c.firstChild().toString() + " " ;
			
			
		}
		np.noun.add(r.trim());
		return np;
	}

	public SPARQLTemplate generateTemplate()
	{
		parse();
		
		SPARQLTemplate temp  = new SPARQLTemplate();
		
		
		String domain = null ;
		String subject = "";
		String SUB  = ""; 
		String OBJ = "";
		SPARQL_QUESTION_TYPE qType = findQuestionType();
		QUESTION_TYPE quesType = QUESTION_TYPE.WHICH;
		
		if (qType.equals(SPARQL_QUESTION_TYPE.SELECT))
				quesType  = getWHQyestionTYpe();
		
		/*temp.setqType(quesType);
		temp.setsQtype(qType);
		
		if(qType.equals(SPARQL_QUESTION_TYPE.SELECT) && quesType.equals(QUESTION_TYPE.WHICH))
			temp.setSubjectStr();
		
		if(qType.equals(SPARQL_QUESTION_TYPE.SELECT) && quesType.equals(QUESTION_TYPE.WHERE))
			temp.setSubjectStr();
		
		if(qType.equals(SPARQL_QUESTION_TYPE.SELECT) && quesType.equals(QUESTION_TYPE.WHEN))
			temp.setSubjectStr();
		
		if(qType.equals(SPARQL_QUESTION_TYPE.ASK) )
			temp.setSubjectStr();
		
		
		System.out.println("QUESTION TYPE : " + qType);
		System.out.println("QUESTION TYPE : " + quesType);
		System.out.println("SUBJECT       : " + temp.subjectStr);
		System.out.println("OBJECT        : " + temp.objStr);

		System.out.println("VERB          : " + temp.verbStr);
		
		this.template  = temp;*/
		return temp;
	}

	private QUESTION_TYPE getWHQyestionTYpe() {
		List<Tree>  q= new LinkedList<Tree>();
		q.add(parse);
		
		while(!q.isEmpty())
		{
			Tree t = q.remove(0);
			
			if(t.pennString().startsWith("(WH"))
			{
				System.out.println(t.firstChild().firstChild());
				if( t.firstChild().firstChild().toString().toLowerCase().equals("when"))
					return QUESTION_TYPE.WHEN;
				if( t.firstChild().firstChild().toString().toLowerCase().equals("where"))
					return QUESTION_TYPE.WHERE;
				
			}
			
			for(Tree ch : t.children())
				q.add( ch);
		}
		return QUESTION_TYPE.WHICH;
		
	}

	private SPARQL_QUESTION_TYPE findQuestionType() {
		
		if(ParseTreeUtils.getFirstLeafNode(parse).pennString().startsWith("(VB "))
			return SPARQL_QUESTION_TYPE.SELECT;
		
		List<Tree>  q= new LinkedList<Tree>();
		q.add(parse);
		while(!q.isEmpty())
		{
			Tree t = q.remove(0);
			
			if( t.pennString().startsWith("(WH"))
				return SPARQL_QUESTION_TYPE.SELECT;
			
			for(Tree child : t.children())
				q.add(child);
		}
		return SPARQL_QUESTION_TYPE.ASK;
	}

	private Tree getVP(Tree SQ) {
		List<Tree> q = new ArrayList<Tree>();
		q.add(SQ);
		
		while(!q.isEmpty())
		{
			Tree t = q.remove(0);
			
			
			if(t.pennString().startsWith("(VP"))
				return t;
			
			
			for(Tree child : t.children())
				q.add(child);
		}
		return null;
	}

	private String getNPhrase(Tree Phrase) {
		
		String NPhrases = "";
		for(Tree c :  Phrase.children())
			if(c.pennString().startsWith("(NN"))
				NPhrases += c.yield() ;
		return NPhrases;
	}

	private Tree getPhrase(Tree SQ) {
		
		List<Tree> q = new ArrayList<Tree>();
		q.add(SQ);
		
		while(!q.isEmpty())
		{
			Tree t = q.remove(0);
			
			
			if(t.pennString().startsWith("(NP"))
				return t;
			
			
			for(Tree child : t.children())
				q.add(child);
		}
		return null;
		
	}

	private String getDomain(ArrayList<Label> yield) {
		
		for(Label y : yield)
			if(y.toString().equals("movement"))
				return "movement";
		return null;
		
	}

	public void selectTemplate() {
		// TODO Auto-generated method stub
		
	}

	public void findURIs(Gazeteer gazeteer) {
		
		
		for(Phrase p : phrases)
		{
			if(p.nounType.equals("NNP"))
			{
				for(String s : p.noun)
				{
					System.out.println(s + "--" + Similarity.semanticallyClose(s,gazeteer));
					
					
				}
			}
		}
		
	}
	
	

}
