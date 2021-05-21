package org.hibernate.search.benchmark.lucene;

import org.hibernate.search.benchmark.tck.AutomaticIndexingBenchmark;

import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.ThreadParams;

@State(Scope.Thread)
public class LuceneAutomaticIndexingBenchmark extends AutomaticIndexingBenchmark {

	@Setup(Level.Trial)
	public void setupTrial(LuceneAutomaticIndexingStateHolder stateHolder, ThreadParams threadParams) {
		setIndexingState( stateHolder.getAutomaticIndexingState() );
		setThreadIndex( threadParams.getThreadIndex() );
	}
}
