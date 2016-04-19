package edu.nii.sesame.utils;

import java.io.File;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

public class Util {

	static Repository nativeRep;
	
	public static void initRepo() throws RepositoryException
	{
		File dataDir = new File(Constant.IN_MEMORY_SESAME_REPO);
         nativeRep = new SailRepository(new MemoryStore(dataDir));
        nativeRep.initialize();
        
	}
	
	public static Repository getRepo() throws RepositoryException
	{
		
        return nativeRep;
	}
	
	public static RepositoryConnection getConn() throws RepositoryException
	{
		
        return nativeRep.getConnection();	
	}

	public static String normalize(String x)
	{
		return x.replaceAll("[-+()_.^:,]","").toLowerCase();
	}
	public static void closeConn(RepositoryConnection conn) throws RepositoryException {
		conn.close();
		
	}
}
