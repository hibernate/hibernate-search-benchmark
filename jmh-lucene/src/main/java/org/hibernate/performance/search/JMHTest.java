package org.hibernate.performance.search;

import org.hibernate.SessionFactory;
import org.hibernate.performance.search.application.ModelService;
import org.hibernate.performance.search.application.ModelServiceFactory;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

@State(Scope.Benchmark)
public class JMHTest {

	private final ModelService modelService;
	private final SessionFactory sessionFactory;

	public JMHTest() {
		modelService = ModelServiceFactory.create();
		sessionFactory = ModelServiceFactory.buildSessionFactory( modelService.properties() );
	}

	@Benchmark
	@SuppressWarnings( "unused" )
	public void bootstrap() {
		try ( SessionFactory sessionFactory = ModelServiceFactory.buildSessionFactory( modelService.properties() ) ) {
			// do nothing, we need just to close the instance
		}
	}

	// TODO @Benchmark
	public void indexing() throws Exception {
		modelService.indexing();
		Thread.sleep( 100 );
	}

	// TODO @Benchmark
	public void search() throws Exception {
		modelService.search();
		Thread.sleep( 100 );
	}

	@TearDown
	public void tearDown() {
		modelService.stop();
		sessionFactory.close();
	}
}
