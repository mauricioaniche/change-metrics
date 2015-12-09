package br.com.aniche.changemetrics;

import br.com.aniche.changemetrics.repo.ClassInfoRepository;
import br.com.metricminer2.domain.Commit;
import br.com.metricminer2.domain.Modification;
import br.com.metricminer2.domain.ModificationType;
import br.com.metricminer2.persistence.PersistenceMechanism;
import br.com.metricminer2.scm.CommitVisitor;
import br.com.metricminer2.scm.SCMRepository;

public class ChangeMetricProcessor implements CommitVisitor {

	private ClassInfoRepository classes;

	public ChangeMetricProcessor(ClassInfoRepository repo) {
		this.classes = repo;
	}

	@Override
	public void process(SCMRepository repo, Commit commit, PersistenceMechanism writer) {
		for(Modification modification : commit.getModifications()) {
			if(!modification.fileNameEndsWith(".java")) continue;
			if(modification.getType() == ModificationType.RENAME) {
				classes.rename(modification);
			}

			ClassInfo clazz = classes.saveOrGet(modification);
			clazz.update(commit, modification);
		}
	}

	@Override
	public String name() {
		return "change-metric";
	}

}
