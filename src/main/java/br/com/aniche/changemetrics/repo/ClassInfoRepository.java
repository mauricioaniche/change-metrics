package br.com.aniche.changemetrics.repo;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import br.com.aniche.changemetrics.ClassInfo;
import br.com.metricminer2.domain.Modification;

public class ClassInfoRepository {
	private Map<String, ClassInfo> db;

	public ClassInfoRepository() {
		this.db = new HashMap<>();
	}
	
	public ClassInfo saveOrGet(String project, Modification m) {
		
		String file = m.getNewPath();
		String fullName = fullName(project, file);
		
		if(!db.containsKey(fullName)) {
			db.put(fullName, new ClassInfo(project, file));
		}
		
		return db.get(fullName);
	}

	public void rename(String project, Modification m) {
		String oldPath = m.getOldPath();
		String newPath = m.getNewPath();
		
		ClassInfo classInfo = db.remove(fullName(project, oldPath));
		
		if(classInfo!=null) {
			classInfo.rename(newPath);
			db.put(fullName(project, newPath), classInfo);
		}
	}

	public Collection<ClassInfo> all() {
		return db.values();
	}
	
	private String fullName(String project, String file) {
		return project + "/" + file;
	}


}
