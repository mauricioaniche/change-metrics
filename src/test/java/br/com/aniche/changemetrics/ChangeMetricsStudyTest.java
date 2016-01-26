package br.com.aniche.changemetrics;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ChangeMetricsStudyTest {

	private String path;
	private String outputFile;

	@Before
	public void setUp() throws FileNotFoundException {
		String cfgFile = ChangeMetricsStudyTest.class.getResource("/config.txt").getPath();
		Scanner sc = new Scanner(new File(cfgFile));
		path = sc.nextLine();
		if(!path.endsWith("/")) path += "/";
		sc.close();

		this.outputFile = "/tmp/test.csv";
	}
	
	@Test
	public void analyseTheAllRepo() throws IOException {
		ChangeMetricsStudy study = new ChangeMetricsStudy(path, outputFile, "single");
		study.execute();
		
		String result = FileUtils.readFileToString(new File(outputFile));
		result = result.replace(path, "fulldir/");
		
		Assert.assertTrue(result.contains("project,file,revisions,refactorings,bugfixes,authors,locAdded,locRemoved,maxLocAdded,maxLocRemoved,avgLocAdded,avgLogRemoved,codeChurn,maxChangeset,avgChangeset,firstCommit,lastCommit,weeks\n"));
		Assert.assertTrue(result.contains("repo1,fulldir/FileB.java,2,1,0,1,6,1,5,1,3.0,0.5,7,2,2.0,2015-12-09,2015-12-09,0\n"));
		Assert.assertTrue(result.contains("repo1,fulldir/FileC.java,2,0,0,1,5,1,4,1,2.5,0.5,6,1,1.0,2015-12-09,2015-12-09,0\n"));
		Assert.assertTrue(result.contains("repo1,fulldir/RenamedFileE.java,2,0,0,1,5,0,5,0,2.5,0.0,5,1,1.0,2015-12-09,2015-12-09,0\n"));
		Assert.assertTrue(result.contains("repo1,fulldir/dir/FileD.java,1,0,0,1,4,0,4,0,4.0,0.0,4,1,1.0,2015-12-09,2015-12-09,0\n"));
		Assert.assertTrue(result.contains("repo1,fulldir/FileA.java,5,1,1,1,8,2,4,1,1.6,0.4,10,2,1.4,2015-12-09,2015-12-09,0\n"));
		
	}

	@Test
	public void analyseByRange() throws IOException {
		String first = "4276a1251d7ac81bca2cef7ec2adcc5af3b4768a";
		String last = "44d6d26f20be49e129accf5e0579b1273d47c917";

		ChangeMetricsStudy study = new ChangeMetricsStudy(path, outputFile, "single", first, last);
		study.execute();
		
		String result = FileUtils.readFileToString(new File(outputFile));
		result = result.replace(path, "fulldir/");
		
		Assert.assertTrue(result.contains("repo1,fulldir/FileA.java,5,1,1,1,8,2,4,1,1.6,0.4,10,2,1.4,2015-12-09,2015-12-09,0"));
		Assert.assertTrue(result.contains("repo1,fulldir/FileB.java,2,1,0,1,6,1,5,1,3.0,0.5,7,2,2.0,2015-12-09,2015-12-09,0"));
		
	}
	
}
