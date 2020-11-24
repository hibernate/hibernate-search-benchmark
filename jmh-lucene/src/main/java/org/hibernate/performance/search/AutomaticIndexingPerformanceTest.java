package org.hibernate.performance.search;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.performance.search.application.ModelService;
import org.hibernate.performance.search.application.ModelServiceFactory;
import org.hibernate.performance.search.entity.Employee;
import org.hibernate.performance.search.application.HibernateORMHelper;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
public class AutomaticIndexingPerformanceTest {

	private final ModelService modelService;
	private final SessionFactory sessionFactory;

	public AutomaticIndexingPerformanceTest() {
		modelService = ModelServiceFactory.create();
		sessionFactory = HibernateORMHelper.buildSessionFactory(
				modelService.properties( ModelService.Kind.LUCENE_AUTOMATIC_INDEXING ) );
	}

	@Benchmark
	public void indexing() {
		HibernateORMHelper.inTransaction( sessionFactory, (session) ->
			session.persist( new Employee() )
		);
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
