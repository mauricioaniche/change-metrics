package br.com.aniche.changemetrics;

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
		new RepositoryMining()
			.in(GitRepository.singleProject(projectPath))
			.through(Commits.all())
			.process(new ChangeMetricProcessor(), new CSVFile(outputPath))
			.mine();
		
	}

}
