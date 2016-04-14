package edu.nii.sesame.cli;

import java.util.ArrayList;
import java.util.List;

import edu.nii.sesame.syntacticParsing.TemplateCreation;

public class Test {

        

    
	public static void main(String[] args) {

		List<String> l = new ArrayList<String>();
		String q  =  "Which art movement did Picasso belong to";
		l.add(q);
		
		q  =  "What art movement did Picasso and Raphael belong to";
		l.add(q);
		
		q  =  "Did Picasso and Raphael belong to same movement?";
		l.add(q);
		
		
		q  =  "Did Picasso  belong to Expressionism?";
		l.add(q);
	
		q  =  "Which artists were involved in  Renaissance movement?";
		l.add(q);
		
		
		
		for(String  x : l)
		{
			System.out.println("\n");
			TemplateCreation tc = new TemplateCreation(x);
			tc.generateTemplate();	
		}
		
				 
		
	    
	}
}
