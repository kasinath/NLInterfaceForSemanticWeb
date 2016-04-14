package edu.nii.sesame.semanticStore;

import java.io.File;
import java.io.IOException;

import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;

import edu.nii.sesame.utils.Constant;

public class PopulateTripleStore {

	public  void populate(RepositoryConnection conn) throws RDFParseException, RepositoryException, IOException, MalformedQueryException, QueryEvaluationException
	{
		 File folder = new File(Constant.DATA_DIR);
         for( File fileEntry : folder.listFiles())
         {
         	if(fileEntry.isFile() && fileEntry.getName().endsWith(Constant.RDF_FILE_EXTN))
         	{
         		System.out.println(fileEntry.getName());
         		conn.add(fileEntry, "file://" + fileEntry.getAbsolutePath(), RDFFormat.RDFXML);
         		
         	}
         }
         
         countTriples(conn);
         
		
	}
	
	
	
	public void countTriples(RepositoryConnection conn) throws RepositoryException, MalformedQueryException, QueryEvaluationException
	{
		 String queryString = "SELECT ?x ?y WHERE { ?x ?p ?y } ";
     	TupleQuery tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
     	TupleQueryResult  result =  tupleQuery.evaluate();
     	int len=0;
     	while (result.hasNext()) 
     	{  
     		BindingSet bindingSet = result.next();
     		Value valueOfX = bindingSet.getValue("x");
     		Value valueOfY = bindingSet.getValue("y");
     		//System.out.println(valueOfX.toString());
     		len++;

         }
     	
     	System.out.println(len);
	}
}
