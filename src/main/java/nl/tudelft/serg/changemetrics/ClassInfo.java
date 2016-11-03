package nl.tudelft.serg.changemetrics;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.repodriller.domain.Commit;
import org.repodriller.domain.Modification;
import org.repodriller.domain.ModificationType;

public class ClassInfo {

	private String file;
	private int revisions;
	private int refactorings;
	private int bugfixes;
	private Set<String> authors;
	private long locAdded;
	private long locRemoved;
	private long maxLocAdded;
	private long maxLocRemoved;
	private long codeChurn;
	private int maxChangeset;
	private long totalChangeset;
	private Calendar firstCommit;
	private Calendar lastCommit;
	private String project;
	

	public ClassInfo(String project, String file) {
		this.project = project;
		this.file = file;
		this.authors = new HashSet<>();
	}



	public void update(Commit commit, Modification modification) {
		String msg = commit.getMsg().toLowerCase();

		countRevision(modification);
		countRefactoring(msg);
		countBugFixes(msg);
		addAuthor(commit);
		countLocAddedAndRemoved(modification);
		countCodeChurn(modification);
		countChangeset(commit, modification);
		firstAndLastDates(commit, modification);
	}

	private void firstAndLastDates(Commit commit, Modification modification) {
		if(modification.getType() != ModificationType.DELETE) {
		
			if(firstCommit == null) firstCommit = commit.getDate();
			else if(commit.getDate().before(firstCommit)) firstCommit = commit.getDate();
	
			if(lastCommit == null) lastCommit = commit.getDate();
			else if(commit.getDate().after(lastCommit)) lastCommit = commit.getDate();
		}
	}



	private void countChangeset(Commit commit, Modification modification) {
		if(modification.getType() != ModificationType.DELETE) {
			maxChangeset = Math.max(maxChangeset, commit.getModifications().size());
			totalChangeset += commit.getModifications().size();
		}
	}



	private void countCodeChurn(Modification modification) {
		if(modification.getType() != ModificationType.DELETE)
			codeChurn += modification.getAdded() + modification.getRemoved();
	}



	private void countLocAddedAndRemoved(Modification modification) {
		if(modification.getType() != ModificationType.DELETE) {
			int added = modification.getAdded();
			int removed = modification.getRemoved();

			locAdded += added;
			locRemoved += removed;

			maxLocAdded = Math.max(maxLocAdded, added);
			maxLocRemoved = Math.max(maxLocRemoved, removed);
		}
		
	}



	private void addAuthor(Commit commit) {
		authors.add(commit.getAuthor().getName());
	}



	private void countBugFixes(String msg) {
		if(msg.contains("fix") && !msg.contains("postfix") && !msg.contains("prefix")) bugfixes++;
		if(msg.contains("bug")) bugfixes++;
	}

	private void countRevision(Modification modification) {
		if(modification.getType() != ModificationType.DELETE) revisions++;
	}
	

	private void countRefactoring(String msg) {
		if(msg.contains("refactor")) refactorings++;
	}



	public String getFile() {
		return file;
	}

	public int getRevisions() {
		return revisions;
	}


	public int getRefactorings() {
		return refactorings;
	}


	public int getBugfixes() {
		return bugfixes;
	}


	public long getLocAdded() {
		return locAdded;
	}


	public long getLocRemoved() {
		return locRemoved;
	}


	public double getAvgLocAdded() {
		if(revisions == 0) return 0;
		return locAdded / (double) revisions;
	}


	public double getAvgLocRemoved() {
		if(revisions == 0) return 0;
		return locRemoved / (double) revisions;
	}


	public long getMaxLocAdded() {
		return maxLocAdded;
	}


	public long getMaxLocRemoved() {
		return maxLocRemoved;
	}


	public long getCodeChurn() {
		return codeChurn;
	}


	public int getMaxChangeset() {
		return maxChangeset;
	}


	public double getAvgChangeset() {
		if(revisions == 0) return 0;
		return totalChangeset / (double) revisions;
	}


	public Calendar getFirstCommit() {
		return firstCommit;
	}


	public Calendar getLastCommit() {
		return lastCommit;
	}



	public int getUniqueAuthorsQuantity() {
		return authors.size();
	}

	public long getWeeks() {
		long end = lastCommit.getTimeInMillis();
	    long start = firstCommit.getTimeInMillis();
	    return TimeUnit.MILLISECONDS.toDays(Math.abs(end - start)) / 7;
	}



	public void rename(String newPath) {
		this.file = newPath;
	}

	public String getProject() {
		return project;
	}
}
