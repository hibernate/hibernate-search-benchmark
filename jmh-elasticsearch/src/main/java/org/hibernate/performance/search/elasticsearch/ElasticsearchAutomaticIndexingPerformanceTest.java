package org.hibernate.performance.search.elasticsearch;

import org.hibernate.performance.search.tck.AutomaticIndexingPerformanceTest;

import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.ThreadParams;

@State(Scope.Thread)
public class ElasticsearchAutomaticIndexingPerformanceTest extends AutomaticIndexingPerformanceTest {

	@Setup(Level.Trial)
	public void setupTrial(ElasticsearchAutomaticIndexingStateHolder stateHolder, ThreadParams threadParams) {
		setAutomaticIndexingState( stateHolder.getAutomaticIndexingState() );
		setThreadIndex( threadParams.getThreadIndex() );
	}
}
