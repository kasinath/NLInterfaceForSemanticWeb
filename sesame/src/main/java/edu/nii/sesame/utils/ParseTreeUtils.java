package edu.nii.sesame.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.stanford.nlp.trees.Tree;

public class ParseTreeUtils {

	public static Tree getFirstLeafNode(Tree parse)
	{
		List<Tree>  q= new LinkedList<Tree>();
		q.add(parse);
		
		while(!q.isEmpty())
		{
			Tree t = q.remove(0);
			if(t.numChildren()==1 && t.firstChild().numChildren()==0)
				return t;
			for(int i=0;i<t.children().length;i++)
				q.add(i,t.children()[i]);
		}
		return null;
	}
	
	public static List<Tree> getNP(Tree parse)
	{
		List<Tree> NPs = new ArrayList<Tree>();
		List<Tree>  q= new LinkedList<Tree>();
		q.add(parse);
		
		while(!q.isEmpty())
		{
			Tree t = q.remove(0);
			
			if(t.pennString().startsWith("(NP "))
				NPs.add(t);
			for(int i=0;i<t.children().length;i++)
				q.add(i,t.children()[i]);
		}
		return NPs;
	}
	public static List<Tree> getVP(Tree parse)
	{
		List<Tree> NPs = new ArrayList<Tree>();
		List<Tree>  q= new LinkedList<Tree>();
		q.add(parse);
		
		while(!q.isEmpty())
		{
			Tree t = q.remove(0);
			
			if(t.pennString().startsWith("(VP "))
				NPs.add(t);
			for(int i=0;i<t.children().length;i++)
				q.add(i,t.children()[i]);
		}
		return NPs;
	}
}
