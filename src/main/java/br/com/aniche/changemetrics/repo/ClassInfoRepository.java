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
	
	public ClassInfo saveOrGet(Modification m) {
		
		String file = m.getNewPath();
		if(!db.containsKey(file)) {
			db.put(file, new ClassInfo(file));
		}
		
		return db.get(file);
	}

	public void rename(Modification m) {
		String oldPath = m.getOldPath();
		String newPath = m.getNewPath();
		
		ClassInfo classInfo = db.remove(oldPath);
		
		if(classInfo!=null) {
			classInfo.rename(newPath);
			db.put(newPath, classInfo);
		}
	}

	public Collection<ClassInfo> all() {
		return db.values();
	}
	
	

}
