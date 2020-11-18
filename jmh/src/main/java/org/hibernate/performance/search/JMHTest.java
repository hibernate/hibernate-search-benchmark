package org.hibernate.performance.search;

import org.openjdk.jmh.annotations.Benchmark;

public class JMHTest {

	@Benchmark
	public void ciao() throws Exception {
		Thread.sleep( 100 );
	}
}
