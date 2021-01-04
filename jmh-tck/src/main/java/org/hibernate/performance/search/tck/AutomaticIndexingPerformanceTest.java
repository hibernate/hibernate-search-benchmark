package org.hibernate.performance.search.tck;

import org.hibernate.performance.search.model.asset.AutomaticIndexingState;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Threads;

@Fork(1)
@State(Scope.Thread)
public abstract class AutomaticIndexingPerformanceTest {

	private AutomaticIndexingState automaticIndexingState;
	private int threadIndex;

	@Setup(Level.Iteration)
	public void prepareIteration() {
		automaticIndexingState.start();
	}

	@TearDown(Level.Iteration)
	public void tearDownIteration() {
		automaticIndexingState.stop();
	}

	@Benchmark
	@Threads(3)
	public void test() throws Exception {
		Thread.sleep( 1000 );
	}

	protected void setAutomaticIndexingState(AutomaticIndexingState automaticIndexingState) {
		this.automaticIndexingState = automaticIndexingState;
	}

	protected void setThreadIndex(int threadIndex) {
		this.threadIndex = threadIndex;
	}
}
