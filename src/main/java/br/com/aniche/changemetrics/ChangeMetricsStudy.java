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
	private String projectName;

	public ChangeMetricsStudy(String projectPath, String outputPath) {
		this.projectPath = projectPath;
		this.outputPath = outputPath;
		
		String[] names = projectPath.split("/");
		projectName = names[names.length-1];
	}

	public static void main(String[] args) {
		
		if(args == null || args.length < 2) {
			System.out.println("Usage: java -jar <tool.jar> /dir/to/the/git/project /dir/to/the/file/output.csv");
			System.exit(-1);
		}
		
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
			.startingFromTheBeginning()
			.process(new ChangeMetricProcessor(repo))
			.mine();
		
		output(repo, csv);
		
	}

	private void output(ClassInfoRepository repo, CSVFile csv) {
		
		printHead(csv);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		for(ClassInfo info : repo.all()) {
			csv.write(
					projectName,
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
				"project",
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
