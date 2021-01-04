package org.hibernate.performance.search.lucene;

import org.hibernate.performance.search.tck.AutomaticIndexingPerformanceTest;

import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.ThreadParams;

@State(Scope.Thread)
public class LuceneAutomaticIndexingPerformanceTest extends AutomaticIndexingPerformanceTest {

	@Setup(Level.Trial)
	public void setupTrial(LuceneAutomaticIndexingStateHolder stateHolder, ThreadParams threadParams) {
		setAutomaticIndexingState( stateHolder.getAutomaticIndexingState() );
		setThreadIndex( threadParams.getThreadIndex() );
	}
}
