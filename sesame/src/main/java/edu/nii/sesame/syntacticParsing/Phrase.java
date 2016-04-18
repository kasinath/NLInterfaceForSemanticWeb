package edu.nii.sesame.syntacticParsing;

import java.util.ArrayList;
import java.util.List;

public class Phrase {

	List<String> noun;
	String adj;
	String cd;
	String nounType;
	
	public Phrase()
	{
		noun = new ArrayList<String>();
		adj = "";
		cd = "";
		nounType = "";
	}
	
	public String toString()
	{
		return nounType + ":" + cd + " " + adj + " " +noun.toString();
		
	}
}
