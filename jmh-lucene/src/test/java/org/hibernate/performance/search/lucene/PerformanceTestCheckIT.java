package org.hibernate.performance.search.lucene;

import org.junit.jupiter.api.Test;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * Inspired from org.hibernate.search.integrationtest.performance.backend.lucene.SmokeIT.
 * <p>
 * Just check if the tests work producing some results,
 * see README to know how to run the benchmark from the command line to obtain more reliable results.
 */
public class PerformanceTestCheckIT {

	@Test
	public void test() throws Exception {
		Options opts = new OptionsBuilder()
				.include( ".*" )
				.warmupIterations( 0 )
				.measurementIterations( 1 )
				.forks( 0 )
				.build();

		new Runner( opts ).run();
	}
}
