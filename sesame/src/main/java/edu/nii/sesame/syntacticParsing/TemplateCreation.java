package edu.nii.sesame.syntacticParsing;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openrdf.query.BooleanQuery;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryException;

import edu.nii.sesame.semanticStore.Gazeteer;
import edu.nii.sesame.semanticStore.Similarity;
import edu.nii.sesame.utils.Constant;
import edu.nii.sesame.utils.Constant.QUESTION_TYPE;
import edu.nii.sesame.utils.Constant.SPARQL_QUESTION_TYPE;
import edu.nii.sesame.utils.ParseTreeUtils;
import edu.nii.sesame.utils.Util;
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
				if(r.trim()!="")
				{
					np.noun.put(r.trim(),new ArrayList<String>());
					
				}
				r = "";
			}
			
		}
		if(r.trim()!="")
		np.noun.put(r.trim(),new ArrayList<String>());
		return np;
		
	}
	public SPARQLTemplate extractVPNPs()
	{
		parse();
		SPARQLTemplate temp = new SPARQLTemplate();
		
		SPARQL_QUESTION_TYPE qType = findQuestionType();
		
		QUESTION_TYPE qt  = getWHQyestionTYpe();
		
		temp.quesType = qType;
		temp.qType = qt;
		
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
			}
			else if(t.pennString().startsWith("(VP") )
			{
				np = extractVP(t);
				phrases.add(np);
			}
			for(Tree c : t.children())
					q.add(c);
		}
		
		temp.parse = parse;
		temp.phrases = phrases;
		this.template = temp;
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
		if(r.trim().contains("influence") && query.toLowerCase().contains("influenced by"))
		{
			r = "influenced by";

		}
		np.noun.put(r.trim(),new ArrayList<String>());
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

	/*private Tree getVP(Tree SQ) {
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
*/
	public void findURIs(Gazeteer gazeteer) {
		
		
		for(Phrase p : phrases)
		{
			if(p.nounType.equals("NNP"))
			{
				for(String s : p.noun.keySet())
				{
					List<String> l = p.noun.get(s);
					l.add(Similarity.semanticallyClose(s,gazeteer));
					p.noun.put(s, l);
					
					
				}
			}
			
			System.out.println(p.toString());
		}
		
		
	}

	public void createTemplate(Repository repository, Gazeteer gazeteer) throws RepositoryException, MalformedQueryException, QueryEvaluationException {
		
		if(isMvmtQuery(repository,gazeteer))
		{
			System.out.println("MOVEMENT");
			createMvmtTemplate(repository, gazeteer);
		}
		if(isGeoQuery(repository,gazeteer))
		{
			System.out.println("GEO");
		}
		if(isDateQuery(repository,gazeteer))
		{
			System.out.println("DATE");
			createDateTemplate(repository, gazeteer);
		}
		if(isInfluenceQuery(repository,gazeteer))
		{
			System.out.println("DATE");
			createInfluneceTemplate(repository, gazeteer);
		}
		
		if(isArtworksQuery(repository,gazeteer))
		{
			System.out.println("ARTWORKS");
			createArtworksTemplate(repository, gazeteer);
		}
		
		
	}

	private void createArtworksTemplate(Repository repository, Gazeteer gazeteer) throws RepositoryException, MalformedQueryException, QueryEvaluationException {
		List<String> subjects = new ArrayList<String>();
		boolean bDateQuery = false;
		String limit = null;
		for(Phrase p : phrases)
		{
			if(p.nounType.equals("NNP"))
			{
				for(String s : p.noun.keySet())
				{
					if(isArtist(p.noun.get(s),gazeteer))
					{
						subjects.add(gazeteer.index.get(p.noun.get(s).get(0)));
					}
					
					
				}
				
			}
			if(p.nounType.equals("NN"))
			{
				if(p.cd!=null && p.cd!="")
					limit  = "LIMIT " + getNum(p.cd);
				
			}
			
		}
		
		
		if( template.quesType==  SPARQL_QUESTION_TYPE.ASK)
		{
			System.out.println("ASK\nWHERE\n{");
		}
		if( template.quesType==  SPARQL_QUESTION_TYPE.SELECT)
		{
			System.out.println("SELECT ?s COUNT(?o) \nWHERE\n{");
		}
		
		for(int i=0;i<subjects.size();i++)
			System.out.println( "\t" + subjects.get(i) + " dbo:author " + " ?o" + " .");
		
		
		System.out.println("}\n GROUP BY ?s");
		if(limit!=null)
			System.out.println(limit);
		
	}

	private String getNum(String cd) {
		
		 int n = 0 ;
		  try  
		  {  
		    n = Integer.parseInt(cd);  
		  }  
		  catch(NumberFormatException nfe)  
		  {  
		     
		  }  
		 if(n!=0)
			 return String.valueOf(n);
		 
		 
		 if(cd.equals("five"))
			 return "5";
		 
		 return "";
		
		
	}

	private boolean isArtworksQuery(Repository repository, Gazeteer gazeteer) {
		boolean number = false;
		boolean artworks = false;
		
		for(Phrase p : phrases)
		{
			if(p.nounType.equals("NN"))
			{
				for(String s : p.noun.keySet())
				{
					if(s.toLowerCase().contains("number")  || s.toLowerCase().contains("no") )
						number = true;
					if(s.toLowerCase().contains("art works")  || s.toLowerCase().contains("paintings") )
						artworks=true;
		
				}
			}
			
			
			}
		return number && artworks;
	}

	private void createInfluneceTemplate(Repository repository, Gazeteer gazeteer) throws RepositoryException, MalformedQueryException, QueryEvaluationException {
		List<String> subjects = new ArrayList<String>();
		String pred = "dbo:influenced";
		boolean bDateQuery = false;
		for(Phrase p : phrases)
		{
			if(p.nounType.equals("NNP"))
			{
				for(String s : p.noun.keySet())
				{
					if(isArtist(p.noun.get(s),gazeteer))
					{
						subjects.add(gazeteer.index.get(p.noun.get(s).get(0)));
					}
					
				}
			}
			if(p.nounType.equals("VP"))
			{
				for(String s : p.noun.keySet())
				{
					if(s.contains("influenced by"))
						pred = "dbo:influencedBy";
					
				}
			}
			
			
		}
		
		
		if( template.quesType==  SPARQL_QUESTION_TYPE.ASK)
		{
			System.out.println("ASK\nWHERE\n{");
		}
		if( template.quesType==  SPARQL_QUESTION_TYPE.SELECT)
		{
			System.out.println("SELECT *\nWHERE\n{");
		}
		
		for(int i=0;i<subjects.size();i++)
			System.out.println( "\t" + subjects.get(i) + " "+ pred + " ?p" + " .");
		
		System.out.println("}");
		
		
	}

	private boolean isInfluenceQuery(Repository repository, Gazeteer gazeteer) {
		for(Phrase p : phrases)
		{
			if(p.nounType.equals("VP"))
			{
				for(String s : p.noun.keySet())
				{
					if(s.toLowerCase().contains("influence")  || s.toLowerCase().contains("inspire") )
						return true;
				}
			}
			
			
			}
		return false;
	}

	private void createDateTemplate(Repository repository, Gazeteer gazeteer) throws RepositoryException, MalformedQueryException, QueryEvaluationException
	{
		List<String> subjects = new ArrayList<String>();
		List<String> objects = new ArrayList<String>();
		boolean bDateQuery = false;
		String start=null;
		for(Phrase p : phrases)
		{
			if(p.nounType.equals("NNP"))
			{
				for(String s : p.noun.keySet())
				{
					if(isArtist(p.noun.get(s),gazeteer))
					{
						subjects.add(gazeteer.index.get(p.noun.get(s).get(0)));
					}
					
				}
			}
			else if(p.nounType.equals("VP"))
			{
				for(String s : p.noun.keySet())
				{
					if(s.toLowerCase().contains("born")  || s.toLowerCase().contains("birth") )
						bDateQuery = true;
				}
			}
			else if(p.nounType.equals("NN") && p.noun.size()==0 && p.cd!="")
			{
					
					String end = "";
					String pattern = "(\\d){4}[sS]?";
					Pattern r = Pattern.compile(pattern);
				
					Matcher m = r.matcher( p.cd);
					if(m.find())
					{
						start = m.group(0);
						if(p.cd.endsWith("s") || p.cd.endsWith("S"))
						{
							start  = start.substring(0, start.length()-1);
						}
						while(start.endsWith("0"))
						{
							start = start.substring(0,start.length()-1);
						}
							System.out.println(start);
						
					}
				}
			
			}
		
		 int cnt = 0;
		 String filter = null;
		 String prop = " dbo:deathDate ";
		if(subjects.size()==0)
		{
			subjects.add("?s");
		}
		if(start!=null)
		{
			filter = "FILTER REGEX(?d,\"^" + start + "\")";
		}
		if(bDateQuery)
			prop = " dbo:birthDate ";
		
		if( template.quesType==  SPARQL_QUESTION_TYPE.ASK)
		{
			System.out.println("ASK\nWHERE\n{");
		}
		if( template.quesType==  SPARQL_QUESTION_TYPE.SELECT)
		{
			System.out.println("SELECT *\nWHERE\n{");
		}
		
		for(int i=0;i<subjects.size();i++)
			System.out.println( "\t" + subjects.get(i) + prop + "?d" + " .");
		if(filter!=null)
			System.out.println("\t"+filter);
		System.out.println("}");
	}

	private boolean isDateQuery(Repository repository, Gazeteer gazeteer) {
		
		if(template.qType==QUESTION_TYPE.WHEN)
			return true;
		
		boolean NNpPresent = false;
		boolean borndieverb = false;
		for(Phrase p : phrases)
		{
			
			if(p.nounType=="VP")
			{
				for(String s : p.noun.keySet())
				{
					if(s.toLowerCase().contains("born") || s.toLowerCase().contains("die") || s.toLowerCase().contains("birth") || s.toLowerCase().contains("death"))
						borndieverb = true;
				}
			}
			else if(p.nounType=="NNP")
				NNpPresent = true;
			
		}
		return !NNpPresent && borndieverb;

	}

	private boolean isGeoQuery(Repository repository, Gazeteer gazeteer) throws QueryEvaluationException, RepositoryException, MalformedQueryException {
	
		
		
		/*if(template.qType == QUESTION_TYPE.WHERE)
			return true;
		for(Phrase  p : phrases)
		{
			for(String s : p.noun.keySet())
			{
				if(p.noun.get(s)!=null && p.noun.get(s).size()>0)
				{
					String prefix = "PREFIX rdfs:	<http://www.w3.org/2000/01/rdf-schema#>\n";
					prefix += "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n";
					prefix += "PREFIX dbo: <http://dbpedia.org/ontology/>\n";
					prefix += "PREFIX dbp: <http://dbpedia.org/property/>\n";
					prefix += "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n";
			     	
					ArrayList<String> queries = new ArrayList<String>();
					queries.add("ASK WHERE { <" + gazeteer.index.get(p.noun.get(s).get(0))  +  "> rdf:type <http://dbpedia.org/ontology/Place>  } ");
					System.out.println(queries);
					int len=0;
					for(String queryString : queries)
					{
						BooleanQuery tupleQuery = Util.getConn().prepareBooleanQuery(QueryLanguage.SPARQL, prefix + queryString);
				     	boolean  result =  tupleQuery.evaluate();
				     	
				     	if(result)
				     		return true;
			
					}
					
		
				}
			
			}
			
		}*/
		return false;
		


	}

	private void createMvmtTemplate(Repository repository, Gazeteer gazeteer) throws RepositoryException, MalformedQueryException, QueryEvaluationException
	{
		List<String> subjects = new ArrayList<String>();
		List<String> objects = new ArrayList<String>();
		for(Phrase p : phrases)
		{
			if(p.nounType.equals("NNP"))
			{
				for(String s : p.noun.keySet())
				{
					if(isArtist(p.noun.get(s),gazeteer))
					{
						subjects.add(gazeteer.index.get(p.noun.get(s).get(0)));
					}
					else if(isMovement(p.noun.get(s),gazeteer))
					{
						objects.add(gazeteer.index.get(p.noun.get(s).get(0)));
					}
				}
			}
		}
		
		int cnt = 0;
		if(subjects.size()==0)
		{
			cnt = objects.size();
			for(int i=0;i<cnt;i++)
				if(isSame())
					subjects.add("?s");
				else
					subjects.add("?s"+i);
		}
		else if(objects.size()==0)
		{
			cnt = subjects.size();
			for(int i=0;i<cnt;i++)
				if(isSame())
					objects.add("?o");
				else
					objects.add("?o"+i);
		}
		
		//System.out.println("SUBJECT : " + subjects);
		//System.out.println("OBJECT : " + objects);
		
		if( template.quesType==  SPARQL_QUESTION_TYPE.ASK)
		{
			System.out.println("ASK\nWHERE\n{");
		}
		if( template.quesType==  SPARQL_QUESTION_TYPE.SELECT)
		{
			System.out.println("SELECT *\nWHERE\n{");
		}
		for(int i=0;i<subjects.size();i++)
			System.out.println( "\t" + subjects.get(i) + " dbo:movement " + objects.get(i) + " .");
		System.out.println("}");
	}

	private boolean isSame() {
		
		for(Phrase p:phrases)
			if(p.adj.toLowerCase().contains("same"))
				return true;
		return false;
	}

	private boolean isMovement(List<String> list, Gazeteer gazeteer) throws RepositoryException, MalformedQueryException, QueryEvaluationException {
		for(String s : list)
		{
			String prefix = "PREFIX rdfs:	<http://www.w3.org/2000/01/rdf-schema#>\n";
			prefix += "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n";
			prefix += "PREFIX dbo: <http://dbpedia.org/ontology/>\n";
			prefix += "PREFIX dbp: <http://dbpedia.org/property/>\n";
			prefix += "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n";
	     	
			ArrayList<String> queries = new ArrayList<String>();
			queries.add("ASK WHERE { <" + gazeteer.index.get(s)  +  "> rdf:type <dbo:movement>  } ");
			
			int len=0;
			for(String queryString : queries)
			{
				BooleanQuery tupleQuery = Util.getConn().prepareBooleanQuery(QueryLanguage.SPARQL, prefix + queryString);
		     	boolean  result =  tupleQuery.evaluate();
		     	
		     	if(result)
		     		return true;
	
			}
	
		}
		return false;
	}

	private boolean isArtist(List<String> list, Gazeteer gazeteer) throws RepositoryException, MalformedQueryException, QueryEvaluationException {
		
		for(String s : list)
		{
			String prefix = "PREFIX rdfs:	<http://www.w3.org/2000/01/rdf-schema#>\n";
			prefix += "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n";
			prefix += "PREFIX dbo: <http://dbpedia.org/ontology/>\n";
			prefix += "PREFIX dbp: <http://dbpedia.org/property/>\n";
			prefix += "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n";
	     	
			ArrayList<String> queries = new ArrayList<String>();
			queries.add("ASK WHERE { <" + gazeteer.index.get(s)  +  "> rdf:type <http://dbpedia.org/ontology/Artist>  } ");
			
			int len=0;
			for(String queryString : queries)
			{
				BooleanQuery tupleQuery = Util.getConn().prepareBooleanQuery(QueryLanguage.SPARQL, prefix + queryString);
		     	boolean  result =  tupleQuery.evaluate();
		     	
		     	if(result)
		     		return true;
	
			}
	
		}
		return false;
	}

	private boolean isMvmtQuery(Repository repository, Gazeteer gazeteer) throws RepositoryException, MalformedQueryException, QueryEvaluationException
	{
		
		
		
		for(Phrase  p : phrases)
		{
			for(String s : p.noun.keySet())
			{
				if(s.toLowerCase().contains("movement"))
					return true;
				
				if(p.noun.get(s)!=null && p.noun.get(s).size()>0)
				{
					String prefix = "PREFIX rdfs:	<http://www.w3.org/2000/01/rdf-schema#>\n";
					prefix += "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n";
					prefix += "PREFIX dbo: <http://dbpedia.org/ontology/>\n";
					prefix += "PREFIX dbp: <http://dbpedia.org/property/>\n";
					prefix += "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n";
			     	
					ArrayList<String> queries = new ArrayList<String>();
					queries.add("ASK WHERE { <" + gazeteer.index.get(p.noun.get(s).get(0))  +  "> rdf:type <dbo:movement>  } ");
					
					int len=0;
					for(String queryString : queries)
					{
						BooleanQuery tupleQuery = Util.getConn().prepareBooleanQuery(QueryLanguage.SPARQL, prefix + queryString);
				     	boolean  result =  tupleQuery.evaluate();
				     	
				     	if(result)
				     		return true;
			
					}
					
		
				}
			
			}
			
		}
		return false;
		

		
	}
	
	

}
