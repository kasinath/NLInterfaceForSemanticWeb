package edu.nii.sesame.semanticStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openrdf.model.Value;
import org.openrdf.model.vocabulary.FOAF;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

import edu.nii.sesame.utils.Util;

public class Gazeteer {

	public Map<String,String> index = new HashMap<String, String>();
	public Map<String,String> properties = new HashMap<String, String>();
	
	public void preProcess(Repository repo,RepositoryConnection conn) throws RepositoryException, MalformedQueryException, QueryEvaluationException
	{
		generateIndex(conn);
		printProperties(conn);
		addMvmtType(repo, conn);
		
	}
	
	private void addMvmtType(Repository rep,RepositoryConnection conn)  throws RepositoryException, MalformedQueryException, QueryEvaluationException
	{
		String prefix = "PREFIX rdfs:	<http://www.w3.org/2000/01/rdf-schema#>\n";
		prefix += "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n";
		prefix += "PREFIX dbo: <http://dbpedia.org/ontology/>\n";
		prefix += "PREFIX dbp: <http://dbpedia.org/property/>\n";
		prefix += "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n";
     	
		ArrayList<String> queries = new ArrayList<String>();
		queries.add("SELECT  ?s ?o WHERE {?s dbo:movement ?o.}");
		
		for(String queryString : queries)
		{
			TupleQuery tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, prefix + queryString);
	     	TupleQueryResult  result =  tupleQuery.evaluate();
	     	
	     	while (result.hasNext()) 
	     	{  
	     		BindingSet bindingSet = result.next();
	     		String valueOfX = bindingSet.getValue("o").toString();
	     		conn.add(rep.getValueFactory().createURI(valueOfX), RDF.TYPE , rep.getValueFactory().createURI("dbo:movement"));
	     		
	     	}
	     	
	     	conn.commit();
		}
		
		String queryString = "SELECT ?s WHERE { <http://dbpedia.org/resource/Kingdom_of_France> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?s}";
		
			TupleQuery tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, prefix + queryString);
	     	TupleQueryResult  result =  tupleQuery.evaluate();
	     	
	     	while (result.hasNext()) 
	     	{  
	     		BindingSet bindingSet = result.next();
	     		String valueOfX = bindingSet.getValue("s").toString();
	     		//if(valueOfX.toLowerCase().contains("french") || valueOfX.toLowerCase().contains("france"))
	     			System.out.println("::" + valueOfX);
	     	}
	     	
   
		
	}

	private void printProperties(RepositoryConnection conn) throws RepositoryException, MalformedQueryException, QueryEvaluationException
	{
		String prefix = "PREFIX rdfs:	<http://www.w3.org/2000/01/rdf-schema#>\n";
		prefix += "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n";
		prefix += "PREFIX dbo: <http://dbpedia.org/ontology/>\n";
		prefix += "PREFIX dbp: <http://dbpedia.org/property/>\n";
		prefix += "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n";
     	
		ArrayList<String> queries = new ArrayList<String>();
		queries.add("SELECT DISTINCT ?p WHERE {?s ?p ?o.}");
		
		int len=0;
		for(String queryString : queries)
		{
			TupleQuery tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, prefix + queryString);
	     	TupleQueryResult  result =  tupleQuery.evaluate();
	     	
	     	while (result.hasNext()) 
	     	{  
	     		BindingSet bindingSet = result.next();
	     		String valueOfX = bindingSet.getValue("p").toString();
	     		
	     		int indexHash  = valueOfX.lastIndexOf("#");
	     		int index= valueOfX.lastIndexOf("/");
	     		if(indexHash > index)
	     				index = indexHash;
	     		
	     		
	     		//System.out.println(valueOfX.substring(index+1) + " :: " +valueOfX.toString() );
	     		properties.put(valueOfX.substring(index), valueOfX.toString());
	     		len++;

	         }
	     	

		}
		
		//System.out.println(len);
		//System.out.println(index.size());
		
	}
	
	private void generateIndex(RepositoryConnection conn) throws RepositoryException, MalformedQueryException, QueryEvaluationException
	{
			String prefix = "PREFIX rdfs:	<http://www.w3.org/2000/01/rdf-schema#>\n";
			prefix += "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n";
			prefix += "PREFIX dbo: <http://dbpedia.org/ontology/>\n";
			prefix += "PREFIX dbp: <http://dbpedia.org/property/>\n";
			prefix += "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n";
	     	
			ArrayList<String> queries = new ArrayList<String>();
			queries.add("SELECT ?x ?y WHERE { ?x rdfs:label ?y FILTER(LANG(?y) = \"\" || LANGMATCHES(LANG(?y), \"en\")) } ");
			queries.add("SELECT ?x ?y WHERE { ?x foaf:name ?y FILTER(LANG(?y) = \"\" || LANGMATCHES(LANG(?y), \"en\")) } ");
			queries.add("SELECT ?x ?y WHERE { ?x dbp:name ?y FILTER(LANG(?y) = \"\" || LANGMATCHES(LANG(?y), \"en\")) } ");
			
			int len=0;
			for(String queryString : queries)
			{
				TupleQuery tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, prefix + queryString);
		     	TupleQueryResult  result =  tupleQuery.evaluate();
		     	
		     	while (result.hasNext()) 
		     	{  
		     		BindingSet bindingSet = result.next();
		     		String valueOfX = bindingSet.getValue("x").toString();
		     		String valueOfY = bindingSet.getValue("y").toString();
		     		String pattern = "^\"(.*)\"@en$";
		     		Pattern pat = Pattern.compile(pattern);
		     		Matcher m = pat.matcher(valueOfY);
		     		if(m.matches())
		     			valueOfY = m.group(1);
		     		//System.out.println(valueOfX.toString() + "  :: " + valueOfY );
		     		
		     	//	if(valueOfY.toString().toLowerCase().contains("france"))
		     	//		System.out.println(valueOfX.toString() + "  :: " + valueOfY );
		     		index.put(valueOfY.toString(), valueOfX.toString());
		     		len++;

		         }
		     	
	
			}
			
			//System.out.println(len);
			//System.out.println(index.size());
		}
	
	
}
