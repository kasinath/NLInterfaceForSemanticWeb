package edu.nii.sesame.cli;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFParseException;

import edu.nii.sesame.semanticStore.Gazeteer;
import edu.nii.sesame.semanticStore.PopulateTripleStore;
import edu.nii.sesame.semanticStore.Similarity;
import edu.nii.sesame.syntacticParsing.TemplateCreation;
import edu.nii.sesame.utils.Util;

public class Test {

	 static Gazeteer gazeteer = new Gazeteer();

    
	public static void main(String[] args) throws RepositoryException, RDFParseException, MalformedQueryException, QueryEvaluationException, IOException {
		
		 RepositoryConnection conn = Util.getConn();
         
         PopulateTripleStore popTripleStore = new PopulateTripleStore();
         popTripleStore.populate(conn);
        
        
         gazeteer.generateIndex(conn);
         gazeteer.printProperties(conn);
         
         
         
		test_movement();
		test_geography();
		test_dates();
		test_influence();
		test_Artwork();
	    
	}
	private static void test_Artwork()
	{
		List<String> questions = new ArrayList<String>();
		questions.add( "Which artist had the most number of art works");
		questions.add( "List the top five artists with most number of art works");
		questions.add("List the top five artists with least number of art works");

		test(questions);				
	}
	private static void test_influence() {
		List<String> questions = new ArrayList<String>();
		questions.add( "Which artists did Picasso influence/inspire?");
		questions.add( "Which artists were influenced by Picasso?");
		questions.add("Which artists were influenced by Picasso?");

		test(questions);		
	}
	private static void test_dates() {
		List<String> questions = new ArrayList<String>();
		questions.add( "Which artists were born in the 15th century");
		questions.add( "Which artists were born in 1654");
		questions.add("Which artists were born in 1650s");
		questions.add("Which artists died in 1654");
		questions.add("Which artists died in 1650s");
		questions.add("When did Picasso die?");
		
		test(questions);
		
	}
	public static void test_geography()
	{
		
		List<String> questions = new ArrayList<String>();
		questions.add( "Which artists came from France?");
	//	questions.add( "Show the artists that came from France?");
		
	//	questions.add( "Which artists came from Picasso's hometown");
		questions.add("Where did Picasso die");
		questions.add("Did Picasso die in Europe?");
		test(questions);
		
	}
	
	public static void test_movement()
	{
		
		List<String> questions = new ArrayList<String>();
		questions.add( "Which art movement did Van Horn belong to");
		questions.add( "What art movement did VanHorn and Raphael belong to");
		questions.add("Did VanHorn and Raphael belong to same movement?");
		questions.add("Did VanHorn  belong to Expressionism?");
		questions.add("Which artists were involved in  Renaissance movement?");
		test(questions);
		
	}
	
	public static void test(List<String> questions)
	{
		for(String  question : questions)
		{
			System.out.println("\n");
			TemplateCreation tc = new TemplateCreation(question);
			//tc.generateTemplate();
			// tc.selectTemplate();
			tc.extractVPNPs();
			tc.findURIs(gazeteer);
			
		}
		Similarity.semanticallyClosePicasso(gazeteer);
		
		
	}
}
