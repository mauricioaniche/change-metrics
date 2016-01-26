package br.com.aniche.changemetrics;

import java.text.SimpleDateFormat;

import br.com.aniche.changemetrics.repo.ClassInfoRepository;
import br.com.metricminer2.MetricMiner2;
import br.com.metricminer2.RepositoryMining;
import br.com.metricminer2.Study;
import br.com.metricminer2.persistence.csv.CSVFile;
import br.com.metricminer2.scm.GitRepository;
import br.com.metricminer2.scm.SCMRepository;
import br.com.metricminer2.scm.commitrange.CommitRange;
import br.com.metricminer2.scm.commitrange.Commits;
import br.com.metricminer2.scm.commitrange.Range;

public class ChangeMetricsStudy implements Study {

	private String projectPath;
	private String outputPath;
	private String type;
	private String first;
	private String last;

	public ChangeMetricsStudy(String projectPath, String outputPath, String type) {
		this(projectPath, outputPath, type, null, null);
	}

	public ChangeMetricsStudy(String projectPath, String outputPath, String type, String first, String last) {
		this.projectPath = projectPath;
		this.outputPath = outputPath;
		this.type = type;
		this.first = first;
		this.last = last;
	}

	public static void main(String[] args) {
		
		if(args == null || (args.length != 3 && args.length != 5)) {
			System.out.println("Usage: java -jar <tool.jar> /dir/to/the/git/project /dir/to/the/file/output.csv all|single optional:first optional:last");
			System.exit(-1);
		}
		
		ChangeMetricsStudy study = args.length == 3 ?
			new ChangeMetricsStudy(args[0], args[1], args[2]) :
			new ChangeMetricsStudy(args[0], args[1], args[2], args[3], args[4]); 
			
		new MetricMiner2().start(study);
	}
	
	@Override
	public void execute() {
		
		ClassInfoRepository repo = new ClassInfoRepository();
		CSVFile csv = new CSVFile(outputPath);
		
		SCMRepository[] repositories = type.equals("all") ? 
				GitRepository.allProjectsIn(projectPath) : 
				new SCMRepository[] { GitRepository.singleProject(projectPath)};
				
		CommitRange range = first == null ? 
				Commits.all() : 
				new Range(last, first);
		
		new RepositoryMining()
			.in(repositories)
			.through(range)
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
					info.getProject(),
					info.getFile().replace(",", ""),
					info.getRevisions(),
					info.getRefactorings(),
					info.getBugfixes(),
					info.getUniqueAuthorsQuantity(),
					info.getLocAdded(),
					info.getLocRemoved(),
					info.getMaxLocAdded(),
					info.getMaxLocRemoved(),
					info.getAvgLocAdded(),
					info.getAvgLocRemoved(),
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
				"avgLocAdded",
				"avgLogRemoved",
				"codeChurn",
				"maxChangeset",
				"avgChangeset",
				"firstCommit",
				"lastCommit",
				"weeks"
		);
		
		
	}

}
