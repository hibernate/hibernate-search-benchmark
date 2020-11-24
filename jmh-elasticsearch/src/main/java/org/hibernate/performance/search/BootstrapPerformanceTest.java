package org.hibernate.performance.search;

import org.hibernate.SessionFactory;
import org.hibernate.performance.search.application.HibernateORMHelper;
import org.hibernate.performance.search.application.ModelService;
import org.hibernate.performance.search.application.ModelServiceFactory;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@State(Scope.Benchmark)
@Warmup(iterations = 0) // usually the bootstrap is performed when the JVM is starting...
public class BootstrapPerformanceTest {

	private final ModelService modelService;

	public BootstrapPerformanceTest() {
		modelService = ModelServiceFactory.create();
	}

	@Benchmark
	@SuppressWarnings("unused")
	public void bootstrap() {
		try ( SessionFactory sessionFactory = HibernateORMHelper.buildSessionFactory(
				modelService.properties( ModelService.Kind.ELASTICSEARCH_AUTOMATIC_INDEXING ) ) ) {
			// do nothing, we need just to close the instance
		}
	}
}
