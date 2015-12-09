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
		sc.close();

		this.outputFile = "/tmp/test.csv";
	}
	
	@Test
	public void repo1() throws IOException {
		ChangeMetricsStudy study = new ChangeMetricsStudy(path, outputFile, "single");
		study.execute();
		
		String result = FileUtils.readFileToString(new File(outputFile));
		
		String correctResult = 
				"project,file,revisions,refactorings,bugfixes,authors,locAdded,locRemoved,maxLocAdded,maxLocRemoved,avgLocAdded,avgLogRemoved,codeChurn,maxChangeset,avgChangeset,firstCommit,lastCommit,weeks\n"+
				"repo1,FileA.java,5,1,1,1,8,2,4,1,1.6,0.4,10,2,1.4,2015-12-09,2015-12-09,0\n"+
				"repo1,FileB.java,2,1,0,1,6,1,5,1,3.0,0.5,7,2,2.0,2015-12-09,2015-12-09,0\n"+
				"repo1,dir/FileD.java,1,0,0,1,4,0,4,0,4.0,0.0,4,1,1.0,2015-12-09,2015-12-09,0\n"+
				"repo1,RenamedFileE.java,2,0,0,1,5,0,5,0,2.5,0.0,5,1,1.0,2015-12-09,2015-12-09,0\n"+
				"repo1,FileC.java,2,0,0,1,5,1,4,1,2.5,0.5,6,1,1.0,2015-12-09,2015-12-09,0\n";
;
		
		Assert.assertEquals(correctResult, result);
		
	}
}
