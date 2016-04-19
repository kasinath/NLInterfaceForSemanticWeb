package edu.nii.sesame.semanticStore;

import edu.nii.sesame.utils.Util;
import info.debatty.java.stringsimilarity.JaroWinkler;

public class Similarity {

	public static String semanticallyClose(String s, Gazeteer gazeteer)
	{
	
		JaroWinkler jw = new JaroWinkler();
		double max=0;
		String match = "";
		for(String y : gazeteer.index.keySet())
		{
			if(jw.similarity(y, s) > max)
			{
				max = jw.similarity(Util.normalize(y), Util.normalize(s));
				match = y;
			}
		}
		return match;
		
	}
	public static String semanticallyClosePicasso( Gazeteer gazeteer)
	{
	
		JaroWinkler jw = new JaroWinkler();
		double max=0;
		String match = "";
		
		for(String y : gazeteer.index.keySet())
		{
			if(y.toLowerCase().contains("picasso"))
				System.out.println(y);
		}
		/*for(String y : gazeteer.index.keySet())
		{
			if(jw.similarity(y,"Pablo Picasso" ) > max)
			{
				max = jw.similarity(y, "Pablo Picasso");
				match = y;
			}
			if(jw.similarity(y,"Pablo Picasso" ) > 0.8)
			{
				System.out.println(y + " :: " + jw.similarity(y,"Pablo Picasso" ));
			}
		}*/
		return match;
		
	}

}
