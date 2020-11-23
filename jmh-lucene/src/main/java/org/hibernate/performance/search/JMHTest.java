package org.hibernate.performance.search;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;

@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 1)
public class JMHTest {

	private final ModelService modelService;

	public JMHTest() {
		modelService = ModelServiceFactory.create();
	}

	@Setup
	public void setup() {
		modelService.start();
	}

	@Benchmark
	public void indexing() throws Exception {
		modelService.indexing();
		Thread.sleep( 100 );
	}

	@Benchmark
	public void search() throws Exception {
		modelService.search();
		Thread.sleep( 100 );
	}

	@TearDown
	public void tearDown() {
		modelService.stop();
	}
}
