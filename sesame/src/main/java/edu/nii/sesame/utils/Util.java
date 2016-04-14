package edu.nii.sesame.utils;

import java.io.File;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;

public class Util {

	
	public static RepositoryConnection getConn() throws RepositoryException
	{
		File dataDir = new File(Constant.IN_MEMORY_SESAME_REPO);
        Repository nativeRep = new SailRepository(new MemoryStore(dataDir));
        nativeRep.initialize();
        return nativeRep.getConnection();	
	}

	public static void closeConn(RepositoryConnection conn) throws RepositoryException {
		conn.close();
		
	}
}
