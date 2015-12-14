# Change Metrics

Calculates change (some people call it social?) metrics in Git repositories. It
generates a CSV file with the following header:

```
"project": the name of the project directory,
"file": the full file path,
"revisions": quantity of commits,
"refactorings": quantity of refactorings that occured (if said in commit msg),
"bugfixes": quantity of bugs that file has had (if said in commit msg),
"authors": quantity of different authors,
"locAdded": total of LOC added,
"locRemoved": total of LOC removed,
"maxLocAdded": maximum number of LOC added,
"maxLocRemoved": maximum number of LOC removed,
"avgLocAdded": average of LOC added,
"avgLocRemoved": average of LOC removed,
"codeChurn": sum of all LOC added and removed,
"maxChangeset": max number of files committed together with this file,
"avgChangeset": average number of files committed together,
"firstCommit": date of the first commit,
"lastCommit": date of the last commit,
"weeks": difference in weeks from the last commit - first commit.
```

The main method receives three parameters.

```
java -jar <tool.jar> /dir/to/the/git/project /dir/to/the/file/output.csv all|single
```

- The project path
- The output path
- Type of project path (all|single)

If the type is `all`, then it will analyse all projects in sub-directories
of the project path. If type is `single`, it will consider the directory as a 
single project.

Example of usage:

```
java -jar change-metrics.jar /Users/projects /Users/change-metrics.csv all
java -jar -Xms2g -Xmx2g change-metrics.jar /Users/projects/ant /Users/change-metrics-ant.csv single
```

# Automated Tests

To make the integration test to work, unzip the repo.zip (in `src/test/resources`) in any
directory of your machine (I usually put it in `src/test/repo`). Then, put the file path
to the unzipped dir inside `src/test/resources/config.txt`. 

Tests will run.

# References

Change metrics were based in the paper by Moser, R., Pedrycz, W., & Succi, G. (2008, May). A comparative analysis of the efficiency of change metrics and static code attributes for defect prediction. In Software Engineering, 2008. ICSE'08. ACM/IEEE 30th International Conference on (pp. 181-190). IEEE.

It uses MetricMiner2, a framework that supports researchers in MSR studies. See more at www.metricminer.org.br.

# Authors

Only me: Maurício Aniche! :)

# License

This is licensed under Apache license 2.0.
