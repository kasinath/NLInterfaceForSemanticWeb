package edu.nii.sesame.syntacticParsing;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.nii.sesame.utils.ParseTreeUtils;
import edu.nii.sesame.utils.Constant.QUESTION_TYPE;
import edu.nii.sesame.utils.Constant.SPARQL_QUESTION_TYPE;
import edu.stanford.nlp.trees.Tree;

public class SPARQLTemplate {
	
	Tree parse;
	List<Phrase> phrases;
	SPARQL_QUESTION_TYPE quesType ;
	
	
	
	
	
	public void setSubjectStr() {

		String subStr = "";
		String objStr = "";
		String verbStr = "";
				
				
		if(isSBARQ())
		{
			List<Tree>  q= new LinkedList<Tree>();
			q.add(parse);
			
			while(!q.isEmpty())
			{
				Tree t = q.remove(0);
				
				if(t.pennString().startsWith("(WHNP "))
				{
					for(Tree c : t.children())
						if(c.pennString().startsWith("(NN"))
							subStr += (c.firstChild() +  " ");
				}
				if(t.pennString().startsWith("(SQ"))
				{
					List<Tree> NPs = ParseTreeUtils.getNP(t);
					for(Tree NP : NPs)
					for(Tree c:NP.children())
						if(c.pennString().startsWith("(NN") ||c.pennString().startsWith("(CD") || c.pennString().startsWith("(JJ") )
							objStr += (c.firstChild() + " ");
				}
				if(t.pennString().startsWith("(SQ"))
				{
					List<Tree> NPs = ParseTreeUtils.getVP(t);
					for(Tree NP : NPs)
					for(Tree c:NP.children())
						if(c.pennString().startsWith("(VB") )
							verbStr += (c.firstChild() + " ");
				}
				
				for(Tree c : t.children())
					q.add(c);
			}
			
		}
		else if(isSBAR())
		{
			List<Tree>  q= new LinkedList<Tree>();
			q.add(parse);
			
			while(!q.isEmpty())
			{
				Tree t = q.remove(0);
				
				if(t.pennString().startsWith("(S") && t.firstChild().pennString().startsWith("(NP"))
				{
					
					for(Tree c : t.firstChild().children())
						if(c.pennString().startsWith("(NN"))
							subStr += (c.firstChild() + " " );
				}
				if(t.pennString().startsWith("(S") && t.children()[1].pennString().startsWith("(VP"))
				{
					
					List<Tree> NPs = ParseTreeUtils.getNP(t.children()[1]);
					for(Tree NP:NPs)
					for(Tree c:NP.children())
						if(c.pennString().startsWith("(NN") ||c.pennString().startsWith("(CD") || c.pennString().startsWith("(JJ") )
							objStr += (c.firstChild() + " ");
				}
				if(t.pennString().startsWith("(S") && t.children()[1].pennString().startsWith("(VP"))
				{
					
					List<Tree> NPs = ParseTreeUtils.getVP(t.children()[1]);
					for(Tree NP:NPs)
					for(Tree c:NP.children())
						if(c.pennString().startsWith("(VB")  )
							verbStr += (c.firstChild() + " ");
				}
				
				for(Tree c : t.children())
					q.add(c);
			}
		}
		else if(isImperative())
		{
			//imperative
			List<Tree>  q= new LinkedList<Tree>();
			q.add(parse);
			
			while(!q.isEmpty())
			{
				Tree t = q.remove(0);
				
				if(t.pennString().startsWith("(S") && t.firstChild().pennString().startsWith("(VP"))
				{
					
					for(Tree c : t.firstChild().children())
						if(c.pennString().startsWith("(NP") && c.firstChild().pennString().startsWith("(NP"))
							for(Tree gc : c.firstChild().children())
								if(gc.pennString().startsWith("(NN") || gc.pennString().startsWith("(CD"))
									subStr += ( gc.firstChild() + "  ");
					
					
				}
				if(t.pennString().startsWith("(S") && t.firstChild().pennString().startsWith("(VP"))
				{
					
					
					boolean fChild = true;
					for(Tree x : t.firstChild().children()[1].children())
					{
						if(fChild)
						{
							fChild = false;
							continue;
						}
						List<Tree> NPs = ParseTreeUtils.getNP(x);	
						for(Tree NP:NPs)
							for(Tree c:NP.children())
								if(c.pennString().startsWith("(NN") ||c.pennString().startsWith("(CD") || c.pennString().startsWith("(JJ") )
									objStr += (c.firstChild() + " ");
								
					}
				}
				
				
				for(Tree c : t.children())
					q.add(c);
			}
		}
		else
		{
			//ASK
			List<Tree> q = new ArrayList<Tree>();
			q.add(parse);
			
			while(!q.isEmpty())
			{
			
				Tree t = q.remove(0);
				
				if(t.pennString().startsWith("(SQ") && t.firstChild().pennString().startsWith("(VBD"))
				{
					
					List<Tree> NPs = ParseTreeUtils.getNP(t);
					for(Tree NP:NPs)
					for(Tree c:NP.children())
						if(c.pennString().startsWith("(NN") ||c.pennString().startsWith("(CD") || c.pennString().startsWith("(JJ") )
							subStr += (c.firstChild() + " ");
				
					NPs = ParseTreeUtils.getVP(t);
					for(Tree NP:NPs)
					for(Tree c:NP.children())
						if(c.pennString().startsWith("(VB")  )
							verbStr += (c.firstChild() + " ");
				
				
				}
				
				for(Tree c : t.children())
					q.add(c);
			}
			
		}
		
	}
	private boolean isImperative() {
		
		if(parse.firstChild().pennString().startsWith("(S") && parse.firstChild().firstChild().pennString().startsWith("(VP"))
			return true;
		return false;
	}
	private boolean isSBAR() {
		
		List<Tree>  q= new LinkedList<Tree>();
		q.add(parse);
		while(!q.isEmpty())
		{
			Tree t  = q.remove(0);
			
			if(t.pennString().startsWith("(SBAR"))
				return true;
			
			for(Tree c : t.children())
				q.add(c);
		}
		
		return false;
		
	}
	private boolean isSBARQ() {
		List<Tree>  q= new LinkedList<Tree>();
		q.add(parse);
		while(!q.isEmpty())
		{
			Tree t  = q.remove(0);
			
			if(t.pennString().startsWith("(SBARQ"))
				return true;
			
			for(Tree c : t.children())
				q.add(c);
		}
		
		return false;
		
	}
	
	

}
