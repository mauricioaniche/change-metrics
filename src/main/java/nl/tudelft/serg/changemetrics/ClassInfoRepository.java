package nl.tudelft.serg.changemetrics;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.repodriller.domain.Modification;
import org.repodriller.scm.SCMRepository;

public class ClassInfoRepository {
	private Map<String, ClassInfo> db;

	public ClassInfoRepository() {
		this.db = new HashMap<>();
	}
	
	public ClassInfo saveOrGet(SCMRepository repo, Modification m) {
		
		String file = m.getNewPath();
		String fullName = fullName(repo, file);
		
		if(!db.containsKey(fullName)) {
			db.put(fullName, new ClassInfo(repo.getLastDir(), fullName));
		}
		
		return db.get(fullName);
	}

	public void rename(SCMRepository repo, Modification m) {
		String oldPath = m.getOldPath();
		String newPath = m.getNewPath();
		
		ClassInfo classInfo = db.remove(fullName(repo, oldPath));
		
		if(classInfo!=null) {
			classInfo.rename(fullName(repo, newPath));
			db.put(fullName(repo, newPath), classInfo);
		}
	}

	public Collection<ClassInfo> all() {
		return db.values();
	}
	
	private String fullName(SCMRepository repo, String file) {
		String path = repo.getPath() + (repo.getPath().endsWith("/")?"":"/");
		return path + file;
	}

}
