package edu.nii.sesame.syntacticParsing;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import edu.nii.sesame.utils.Constant;
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
	
	public TemplateCreation(String query)
	{
		this.query = query;
	}
	
	public void parse()
	{
		LexicalizedParser lp = LexicalizedParser.loadModel(Constant.PCG_MODEL);
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
	
	
	public void generateTemplate()
	{
		parse();
		
		
		List<Tree>  q= new LinkedList<Tree>();
		
		String domain = null ;
		String subject = "";
		String SUB  = ""; 
		String OBJ = "";
		String qType = "ASK";
		q.add(parse);
		
		if(parse.toString().contains("SBARQ"))
			qType = "SELECT";
		while(!q.isEmpty())
		{
			Tree t = q.remove(0);
			
			
				
			if(t!=null && !t.isLeaf())
				for(Tree child : t.children())
				{
					if(child.pennString().startsWith("(WHNP"))
					{
						domain = getDomain(t.yield());
						
						String nounTags[] = new String[]{"NN","NNS"};
						
						for(Tree WHNPchildren : child.children())
						{
							if(WHNPchildren.pennString().startsWith("(NN") ||WHNPchildren.pennString().startsWith("(NNS") )
							{
								SUB += WHNPchildren.yield() + " ";
							}
						}
						
					}
					else if(child.pennString().startsWith("(SQ"))
					{
						/*for(int i=0;i<child.children().length-2;i++)
						{
							if(child.getChild(i).pennString().startsWith("(VBD") && child.getChild(i+1).pennString().startsWith("(NP"))
							{
								Tree NP = child.getChild(i+1);
								for(Tree NNP : NP.children())
								{
									if(NNP.pennString().startsWith("(NNP"))
										subject +=( Sentence.listToString(NNP.yield()) + " ");
								}
							}
						}*/
						
						
						Tree NP = getNP(child);
						OBJ = getNNP(NP);
						
						if(qType.equals("ASK"))
						{
							SUB = OBJ;
							Tree VP = getVP(child);
							OBJ = getNNP(getNP(VP));
							
						}
						
					}
					
					//System.out.println(child.yield());
					else
						q.add(child);
				}
		}
		
		
		
		System.out.println("SUB    : " + SUB);
		System.out.println("OBJ    : " + OBJ);
		System.out.println("QUESTION TYPE : " + qType);
		
		
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

	private String getNNP(Tree NP) {
		
		String NNPs = "";
		for(Tree c :  NP.children())
			if(c.pennString().startsWith("(NN"))
				NNPs += c.yield() ;
		return NNPs;
	}

	private Tree getNP(Tree SQ) {
		
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
	
	

}
