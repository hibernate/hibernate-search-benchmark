package org.hibernate.performance.search;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.performance.search.application.ModelService;
import org.hibernate.performance.search.application.ModelServiceFactory;
import org.hibernate.performance.search.entity.Employee;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
public class JMHTest {

	private final ModelService modelService;
	private final SessionFactory sessionFactory;

	public JMHTest() {
		modelService = ModelServiceFactory.create();
		sessionFactory = ModelServiceFactory.buildSessionFactory( modelService.properties( false ) );
	}

	@Benchmark
	@SuppressWarnings("unused")
	public void bootstrap() {
		try ( SessionFactory sessionFactory = ModelServiceFactory.buildSessionFactory(
				modelService.properties( false ) ) ) {
			// do nothing, we need just to close the instance
		}
	}

	@Benchmark
	public void indexing() {
		Helper.inTransaction( sessionFactory, (session) ->
			session.persist( new Employee() )
		);

		modelService.waitForIndexFlush( sessionFactory, Employee.class );
	}

	@Benchmark
	public void search(Blackhole blackhole) throws Exception {
		try ( Session session = ( sessionFactory.openSession() ) ) {
			List<Employee> search = modelService.search( session, Employee.class );

			// This call provides a side effect preventing JIT to eliminate dependent computations
			blackhole.consume( search );
		}
	}

	@TearDown
	public void tearDown() {
		sessionFactory.close();
	}
}
