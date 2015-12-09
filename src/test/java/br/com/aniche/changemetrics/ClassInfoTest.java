package br.com.aniche.changemetrics;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.com.metricminer2.domain.Commit;
import br.com.metricminer2.domain.Developer;
import br.com.metricminer2.domain.Modification;
import br.com.metricminer2.domain.ModificationType;

public class ClassInfoTest {

	private ClassInfo classInfo;
	private Developer dev;
	private Modification modification;
	@Before
	public void setUp() {
		classInfo = new ClassInfo("project", "/project/p1/p2/SomeClass.java");
		dev = new Developer("Mauricio", "mauricioaniche@gmail.com");
		
		modification = new Modification("/project/p1/p2/SomeClass.java", "/project/p1/p2/SomeClass.java", ModificationType.MODIFY, "+ add\n+add\n- remove", "any source");
	}
	
	@Test
	public void countRevisions() {
		Commit commit1 = new Commit("123", dev, dev, Calendar.getInstance(), "some normal commit", null);
		commit1.addModifications(Arrays.asList(modification));
		Commit commit2 = new Commit("345", dev, dev, Calendar.getInstance(), "some normal commit", null);
		commit2.addModifications(Arrays.asList(modification));
		Commit commit3 = new Commit("345", dev, dev, Calendar.getInstance(), "some normal commit", null);
		commit3.addModification("/file", "/file", ModificationType.DELETE, "- remove\n- remove\n- remove", "any source");
		
		Assert.assertEquals(0, classInfo.getRevisions());
		classInfo.update(commit1, modification);
		Assert.assertEquals(1, classInfo.getRevisions());
		classInfo.update(commit2, modification);
		Assert.assertEquals(2, classInfo.getRevisions());
		classInfo.update(commit3, commit3.getModifications().get(0));
		Assert.assertEquals(2, classInfo.getRevisions());
	}

	@Test
	public void countRefactoringsByHeuristicInCommitMsg() {
		Commit commit1 = new Commit("123", dev, dev, Calendar.getInstance(), "some normal commit", null);
		commit1.addModifications(Arrays.asList(modification));
		Commit commit2 = new Commit("345", dev, dev, Calendar.getInstance(), "refactor", null);
		commit2.addModifications(Arrays.asList(modification));
		Commit commit3 = new Commit("345", dev, dev, Calendar.getInstance(), "refactoring", null);
		commit3.addModifications(Arrays.asList(modification));
		Commit commit4 = new Commit("345", dev, dev, Calendar.getInstance(), "did some REfactoring in these classes", null);
		commit4.addModifications(Arrays.asList(modification));
		
		Assert.assertEquals(0, classInfo.getRefactorings());
		classInfo.update(commit1, modification);
		Assert.assertEquals(0, classInfo.getRefactorings());
		classInfo.update(commit2, modification);
		Assert.assertEquals(1, classInfo.getRefactorings());
		classInfo.update(commit3, modification);
		Assert.assertEquals(2, classInfo.getRefactorings());
		classInfo.update(commit4, modification);
		Assert.assertEquals(3, classInfo.getRefactorings());
	}

	@Test
	public void countBugFixesByHeuristicInCommitMsg() {
		Commit commit1 = new Commit("123", dev, dev, Calendar.getInstance(), "some normal commit", null);
		commit1.addModifications(Arrays.asList(modification));
		Commit commit2 = new Commit("345", dev, dev, Calendar.getInstance(), "did some fix in here", null);
		commit2.addModifications(Arrays.asList(modification));
		Commit commit3 = new Commit("678", dev, dev, Calendar.getInstance(), "did some prefix in here", null);
		commit3.addModifications(Arrays.asList(modification));
		Commit commit4 = new Commit("910", dev, dev, Calendar.getInstance(), "did some postfix in here", null);
		commit4.addModifications(Arrays.asList(modification));
		
		Assert.assertEquals(0, classInfo.getBugfixes());
		classInfo.update(commit1, modification);
		Assert.assertEquals(0, classInfo.getBugfixes());
		classInfo.update(commit2, modification);
		Assert.assertEquals(1, classInfo.getBugfixes());
		classInfo.update(commit3, modification);
		Assert.assertEquals(1, classInfo.getBugfixes());
		classInfo.update(commit4, modification);
		Assert.assertEquals(1, classInfo.getBugfixes());
	}

	@Test
	public void countDifferentAuthors() {
		Developer dev2 = new Developer("Guilherme", "guilhermesilveira@gmail.com");
		
		Commit commit1 = new Commit("123", dev, dev, Calendar.getInstance(), "some normal commit", null);
		commit1.addModifications(Arrays.asList(modification));
		Commit commit2 = new Commit("345", dev2, dev2, Calendar.getInstance(), "other commit, different dev", null);
		commit2.addModifications(Arrays.asList(modification));
		Commit commit3 = new Commit("678", dev, dev, Calendar.getInstance(), "other commit, same dev", null);
		commit3.addModifications(Arrays.asList(modification));
		
		Assert.assertEquals(0, classInfo.getUniqueAuthorsQuantity());
		classInfo.update(commit1, modification);
		Assert.assertEquals(1, classInfo.getUniqueAuthorsQuantity());
		classInfo.update(commit2, modification);
		Assert.assertEquals(2, classInfo.getUniqueAuthorsQuantity());
		classInfo.update(commit3, modification);
		Assert.assertEquals(2, classInfo.getUniqueAuthorsQuantity());
	}

	@Test
	public void countLocAddedAndRemoved() {
		Commit commit1 = new Commit("123", dev, dev, Calendar.getInstance(), "some normal commit", null);
		Commit commit2 = new Commit("345", dev, dev, Calendar.getInstance(), "other commit", null);
		Commit commit3 = new Commit("678", dev, dev, Calendar.getInstance(), "other other commit, file removed, ignore", null);
		
		commit1.addModification("/file", "/file", ModificationType.ADD, "+ add\n+add\n- remove", "any source");
		commit2.addModification("/file", "/file", ModificationType.MODIFY, "+ add\n+add\n- remove", "any source");
		commit3.addModification("/file", "/file", ModificationType.DELETE, "- remove\n- remove\n- remove", "any source");
		
		Assert.assertEquals(0, classInfo.getLocAdded());
		Assert.assertEquals(0, classInfo.getLocRemoved());
		classInfo.update(commit1, commit1.getModifications().get(0));
		Assert.assertEquals(2, classInfo.getLocAdded());
		Assert.assertEquals(1, classInfo.getLocRemoved());
		classInfo.update(commit2, commit2.getModifications().get(0));
		Assert.assertEquals(4, classInfo.getLocAdded());
		Assert.assertEquals(2, classInfo.getLocRemoved());
		classInfo.update(commit3, commit3.getModifications().get(0));
		Assert.assertEquals(4, classInfo.getLocAdded());
		Assert.assertEquals(2, classInfo.getLocRemoved());

	}

	@Test
	public void countMaxLocAddedAndRemoved() {
		Commit commit1 = new Commit("123", dev, dev, Calendar.getInstance(), "some normal commit", null);
		Commit commit2 = new Commit("345", dev, dev, Calendar.getInstance(), "other commit", null);
		Commit commit3 = new Commit("678", dev, dev, Calendar.getInstance(), "other other commit, file removed, ignore", null);
		
		commit1.addModification("/file", "/file", ModificationType.ADD, "+ add\n+add\n- remove", "any source");
		commit2.addModification("/file", "/file", ModificationType.MODIFY, "+ add\n+add\n+ add\n- remove\n-remove", "any source");
		commit3.addModification("/file", "/file", ModificationType.DELETE, "- remove\n- remove\n- remove", "any source");
		
		Assert.assertEquals(0, classInfo.getMaxLocAdded());
		Assert.assertEquals(0, classInfo.getMaxLocRemoved());
		classInfo.update(commit1, commit1.getModifications().get(0));
		Assert.assertEquals(2, classInfo.getMaxLocAdded());
		Assert.assertEquals(1, classInfo.getMaxLocRemoved());
		classInfo.update(commit2, commit2.getModifications().get(0));
		Assert.assertEquals(3, classInfo.getMaxLocAdded());
		Assert.assertEquals(2, classInfo.getMaxLocRemoved());
		classInfo.update(commit3, commit3.getModifications().get(0));
		Assert.assertEquals(3, classInfo.getMaxLocAdded());
		Assert.assertEquals(2, classInfo.getMaxLocRemoved());
	}

	@Test
	public void countAvgLocAddedAndRemoved() {
		Commit commit1 = new Commit("123", dev, dev, Calendar.getInstance(), "some normal commit", null);
		Commit commit2 = new Commit("345", dev, dev, Calendar.getInstance(), "other commit", null);
		Commit commit3 = new Commit("678", dev, dev, Calendar.getInstance(), "other other commit, file removed, ignore", null);
		
		commit1.addModification("/file", "/file", ModificationType.ADD, "+ add\n+add\n- remove", "any source");
		commit2.addModification("/file", "/file", ModificationType.MODIFY, "+ add\n+add\n+ add\n- remove\n-remove", "any source");
		commit3.addModification("/file", "/file", ModificationType.DELETE, "- remove\n- remove\n- remove", "any source");
		
		Assert.assertEquals(0, classInfo.getAvgLocAdded(), 0.0001);
		Assert.assertEquals(0, classInfo.getAvgLocRemoved(), 0.0001);
		classInfo.update(commit1, commit1.getModifications().get(0));
		Assert.assertEquals(2, classInfo.getAvgLocAdded(), 0.0001);
		Assert.assertEquals(1, classInfo.getAvgLocRemoved(), 0.0001);
		classInfo.update(commit2, commit2.getModifications().get(0));
		Assert.assertEquals(2.5, classInfo.getAvgLocAdded(), 0.0001);
		Assert.assertEquals(1.5, classInfo.getAvgLocRemoved(), 0.0001);
		classInfo.update(commit3, commit3.getModifications().get(0));
		Assert.assertEquals(2.5, classInfo.getAvgLocAdded(), 0.0001);
		Assert.assertEquals(1.5, classInfo.getAvgLocRemoved(), 0.0001);
	}
	
	@Test
	public void countCodeChurn() {
		Commit commit1 = new Commit("123", dev, dev, Calendar.getInstance(), "some normal commit", null);
		Commit commit2 = new Commit("345", dev, dev, Calendar.getInstance(), "other commit", null);
		Commit commit3 = new Commit("678", dev, dev, Calendar.getInstance(), "other other commit, file removed, ignore", null);
		
		commit1.addModification("/file", "/file", ModificationType.ADD, "+ add\n+add\n- remove", "any source");
		commit2.addModification("/file", "/file", ModificationType.MODIFY, "+ add\n+add\n- remove", "any source");
		commit3.addModification("/file", "/file", ModificationType.DELETE, "- remove\n- remove\n- remove", "any source");
		
		Assert.assertEquals(0, classInfo.getCodeChurn());
		classInfo.update(commit1, commit1.getModifications().get(0));
		Assert.assertEquals(3, classInfo.getCodeChurn());
		classInfo.update(commit2, commit2.getModifications().get(0));
		Assert.assertEquals(6, classInfo.getCodeChurn());
		classInfo.update(commit3, commit3.getModifications().get(0));
		Assert.assertEquals(6, classInfo.getCodeChurn());

	}

	@Test
	public void countChangeset() {
		Commit commit1 = new Commit("123", dev, dev, Calendar.getInstance(), "some normal commit", null);
		Commit commit2 = new Commit("345", dev, dev, Calendar.getInstance(), "other commit", null);
		Commit commit3 = new Commit("678", dev, dev, Calendar.getInstance(), "other other commit, file removed, ignore", null);
		
		commit1.addModification("/file", "/file", ModificationType.ADD, "+ add\n+add\n- remove", "any source");
		commit1.addModification("/file2", "/file2", ModificationType.ADD, "+ add\n+add\n- remove", "any source");
		
		commit2.addModification("/file", "/file", ModificationType.MODIFY, "+ add\n+add\n- remove", "any source");
		commit2.addModification("/file2", "/file2", ModificationType.MODIFY, "+ add\n+add\n- remove", "any source");
		commit2.addModification("/file3", "/file3", ModificationType.MODIFY, "+ add\n+add\n- remove", "any source");

		commit3.addModification("/file", "/file", ModificationType.DELETE, "- remove\n- remove\n- remove", "any source");
		
		Assert.assertEquals(0, classInfo.getMaxChangeset());
		Assert.assertEquals(0, classInfo.getAvgChangeset(), 0.0001);
		
		classInfo.update(commit1, commit1.getModifications().get(0));
		Assert.assertEquals(2, classInfo.getMaxChangeset());
		Assert.assertEquals(2, classInfo.getAvgChangeset(), 0.0001);
		
		classInfo.update(commit2, commit2.getModifications().get(0));
		Assert.assertEquals(3, classInfo.getMaxChangeset());
		Assert.assertEquals(2.5, classInfo.getAvgChangeset(), 0.0001);
		
		classInfo.update(commit3, commit3.getModifications().get(0));
		Assert.assertEquals(3, classInfo.getMaxChangeset());
		Assert.assertEquals(2.5, classInfo.getAvgChangeset(), 0.0001);
		
	}
	
	@Test
	public void firstCommitAndLastCommit() {
		
		Calendar firstCommit = new GregorianCalendar(2005, Calendar.JANUARY, 1);
		Calendar lastCommit = new GregorianCalendar(2006, Calendar.JANUARY, 1);
		
		Commit commit1 = new Commit("123", dev, dev, firstCommit, "some normal commit", null);
		commit1.addModifications(Arrays.asList(modification));
		Commit commit2 = new Commit("345", dev, dev, lastCommit, "other commit, different dev", null);
		commit2.addModifications(Arrays.asList(modification));
		Commit commit3 = new Commit("678", dev, dev, Calendar.getInstance(), "other commit, same dev", null);
		commit3.addModification("/file", "/file", ModificationType.DELETE, "- remove\n- remove\n- remove", "any source");
		
		Assert.assertNull(classInfo.getFirstCommit());
		Assert.assertNull(classInfo.getLastCommit());
		classInfo.update(commit1, modification);
		Assert.assertEquals(firstCommit, classInfo.getFirstCommit());
		Assert.assertEquals(firstCommit, classInfo.getLastCommit());
		
		classInfo.update(commit2, modification);
		Assert.assertEquals(firstCommit, classInfo.getFirstCommit());
		Assert.assertEquals(lastCommit, classInfo.getLastCommit());
		Assert.assertEquals(52, classInfo.getWeeks());

		classInfo.update(commit3, commit3.getModifications().get(0));
		Assert.assertEquals(firstCommit, classInfo.getFirstCommit());
		Assert.assertEquals(lastCommit, classInfo.getLastCommit());
	}

}
