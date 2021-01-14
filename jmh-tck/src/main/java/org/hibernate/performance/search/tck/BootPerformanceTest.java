package org.hibernate.performance.search.tck;

import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.performance.search.model.application.HibernateORMHelper;
import org.hibernate.performance.search.model.application.ModelService;
import org.hibernate.performance.search.model.application.ModelServiceFactory;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

/**
 * Test the bootstrap that is performed at JVM-start phase,
 * so we don't do any warmup and we do just one iteration.
 */
@State(Scope.Benchmark)
@Warmup(iterations = 0)
@Measurement(iterations = 1)
@BenchmarkMode(Mode.SingleShotTime)
public abstract class BootPerformanceTest {

	private final Properties properties;

	public BootPerformanceTest() {
		properties = autoProperties( ModelServiceFactory.create() );
	}

	@Benchmark
	@SuppressWarnings("unused")
	public void bootstrap() {
		try ( SessionFactory sessionFactory = HibernateORMHelper.buildSessionFactory( properties ) ) {
			// do nothing, we need just to close the instance
		}
	}

	protected abstract Properties autoProperties(ModelService modelService);
}
