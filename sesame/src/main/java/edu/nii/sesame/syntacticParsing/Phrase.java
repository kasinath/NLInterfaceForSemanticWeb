package edu.nii.sesame.syntacticParsing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Phrase {

	Map<String,List<String>> noun;
	String adj;
	String cd;
	String nounType;
	
	public Phrase()
	{
		noun = new HashMap<String,List<String>>();
		adj = "";
		cd = "";
		nounType = "";
	}
	
	public String toString()
	{
		return nounType + ":" + cd + " " + adj + " " +noun.toString();
		
	}
}
