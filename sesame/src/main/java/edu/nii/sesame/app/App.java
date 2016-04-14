package edu.nii.sesame.app;

import java.io.IOException;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFParseException;
import edu.nii.sesame.utils.Util;


public class App 
{
    public static void main( String[] args ) throws RepositoryException, RDFParseException, IOException, MalformedQueryException, QueryEvaluationException
    {

            RepositoryConnection conn = Util.getConn();
            
            PopulateTripleStore popTripleStore = new PopulateTripleStore();
            popTripleStore.populate(conn);
           
            
           
            Util.closeConn(conn);

    }
}
