package edu.nii.sesame.cli;

import java.util.ArrayList;
import java.util.List;

import edu.nii.sesame.syntacticParsing.TemplateCreation;

public class Test {

        

    
	public static void main(String[] args) {
		//test_movement();
		//test_geography();
		//test_dates();
		//test_influence();
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
		test(questions);
		
	}
	public static void test_geography()
	{
		
		List<String> questions = new ArrayList<String>();
		questions.add( "Which artists came from France?");
		questions.add( "Which Artists came from Picasso's hometown");
		questions.add("Which Artists came from Picasso's hometown");
		questions.add("Where did Picasso die");
		questions.add("Did Picasso die in Europe?");
		test(questions);
		
	}
	
	public static void test_movement()
	{
		
		List<String> questions = new ArrayList<String>();
		questions.add( "Which art movement did Picasso belong to");
		questions.add( "What art movement did Picasso and Raphael belong to");
		questions.add("Did Picasso and Raphael belong to same movement?");
		questions.add("Did Picasso  belong to Expressionism?");
		questions.add("Which artists were involved in  Renaissance movement?");
		test(questions);
		
	}
	
	public static void test(List<String> questions)
	{
		for(String  question : questions)
		{
			System.out.println("\n");
			TemplateCreation tc = new TemplateCreation(question);
			tc.generateTemplate();	
		}
		
	}
}
