package edu.nii.sesame;

import java.io.File;
import java.io.IOException;

import org.openrdf.model.Value;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.memory.MemoryStore;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws RepositoryException, RDFParseException, IOException, MalformedQueryException, QueryEvaluationException
    {
    	
        	File dataDir = new File("C:/temp/myRepository");
            Repository nativeRep = new SailRepository(new MemoryStore(dataDir));
            nativeRep.initialize();
            RepositoryConnection conn = nativeRep.getConnection();
           
            File folder = new File("C:\\Users\\kasinathan\\Google Drive\\NII\\Reserarch\\Queries2\\");
            for( File fileEntry : folder.listFiles())
            {
            	if(fileEntry.isFile() && fileEntry.getName().endsWith(".rdf"))
            	{
            		System.out.println(fileEntry.getName());
            		conn.add(fileEntry, "file://" + fileEntry.getAbsolutePath(), RDFFormat.RDFXML);
            		
            	}
            }
            
            
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
            conn.close();

    }
}
