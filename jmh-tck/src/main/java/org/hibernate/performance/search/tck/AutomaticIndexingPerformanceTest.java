package org.hibernate.performance.search.tck;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;

@State(Scope.Thread)
public class AutomaticIndexingPerformanceTest {

	@Benchmark
	@Threads(3)
	public void test() throws Exception {
		Thread.sleep( 1000 );
	}

}
