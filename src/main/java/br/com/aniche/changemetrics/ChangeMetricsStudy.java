package br.com.aniche.changemetrics;

import java.text.SimpleDateFormat;

import br.com.aniche.changemetrics.repo.ClassInfoRepository;
import br.com.metricminer2.MetricMiner2;
import br.com.metricminer2.RepositoryMining;
import br.com.metricminer2.Study;
import br.com.metricminer2.persistence.csv.CSVFile;
import br.com.metricminer2.scm.GitRepository;
import br.com.metricminer2.scm.commitrange.Commits;

public class ChangeMetricsStudy implements Study {

	private String projectPath;
	private String outputPath;

	public ChangeMetricsStudy(String projectPath, String outputPath) {
		this.projectPath = projectPath;
		this.outputPath = outputPath;
	}

	public static void main(String[] args) {
		ChangeMetricsStudy study = new ChangeMetricsStudy(args[0], args[1]);
		new MetricMiner2().start(study);
	}
	
	@Override
	public void execute() {
		
		ClassInfoRepository repo = new ClassInfoRepository();
		CSVFile csv = new CSVFile(outputPath);
		
		new RepositoryMining()
			.in(GitRepository.singleProject(projectPath))
			.through(Commits.all())
			.process(new ChangeMetricProcessor(repo))
			.mine();
		
		outpur(repo, csv);
		
	}

	private void outpur(ClassInfoRepository repo, CSVFile csv) {
		
		printHead(csv);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		for(ClassInfo info : repo.all()) {
			csv.write(
					info.getFile(),
					info.getRevisions(),
					info.getRefactorings(),
					info.getBugfixes(),
					info.getUniqueAuthorsQuantity(),
					info.getLocAdded(),
					info.getLocRemoved(),
					info.getMaxLocAdded(),
					info.getMaxLocRemoved(),
					info.getCodeChurn(),
					info.getMaxChangeset(),
					info.getAvgChangeset(),
					sdf.format(info.getFirstCommit().getTime()),
					sdf.format(info.getLastCommit().getTime()),
					info.getWeeks()
			);
			
		}
	}

	private void printHead(CSVFile csv) {
		csv.write(
				"file",
				"revisions",
				"refactorings",
				"bugfixes",
				"authors",
				"locAdded",
				"locRemoved",
				"maxLocAdded",
				"maxLocRemoved",
				"codeChurn",
				"maxChangeset",
				"avgChangeset",
				"firstCommit",
				"lastCommit",
				"weeks"
		);
		
		
	}

}
