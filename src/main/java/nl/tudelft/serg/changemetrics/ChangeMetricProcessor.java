package nl.tudelft.serg.changemetrics;

import org.repodriller.domain.Commit;
import org.repodriller.domain.Modification;
import org.repodriller.domain.ModificationType;
import org.repodriller.persistence.PersistenceMechanism;
import org.repodriller.scm.CommitVisitor;
import org.repodriller.scm.SCMRepository;

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
				classes.rename(repo, modification);
			}

			ClassInfo clazz = classes.saveOrGet(repo, modification);
			clazz.update(commit, modification);
		}
	}

	@Override
	public String name() {
		return "change-metric";
	}

}
