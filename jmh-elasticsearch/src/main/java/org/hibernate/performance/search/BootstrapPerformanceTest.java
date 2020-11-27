package org.hibernate.performance.search;

import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.performance.search.application.HibernateORMHelper;
import org.hibernate.performance.search.application.ModelService;
import org.hibernate.performance.search.util.TckBackendHelperFactory;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@State(Scope.Benchmark)
@Warmup(iterations = 0) // usually the bootstrap is performed when the JVM is starting...
public class BootstrapPerformanceTest {

	private final ModelService modelService;
	private final Properties properties;

	public BootstrapPerformanceTest() {
		modelService = TckBackendHelperFactory.getModelService();
		properties = TckBackendHelperFactory.autoProperties();
	}

	@Benchmark
	@SuppressWarnings("unused")
	public void bootstrap() {
		try ( SessionFactory sessionFactory = HibernateORMHelper.buildSessionFactory( properties ) ) {
			// do nothing, we need just to close the instance
		}
	}
}
