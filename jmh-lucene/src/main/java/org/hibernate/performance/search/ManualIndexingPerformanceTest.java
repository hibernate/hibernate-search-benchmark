package org.hibernate.performance.search;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.performance.search.application.ModelService;
import org.hibernate.performance.search.application.ModelServiceFactory;
import org.hibernate.performance.search.entity.Employee;
import org.hibernate.performance.search.helper.TransactionHelper;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class ManualIndexingPerformanceTest {

	private final ModelService modelService;
	private final SessionFactory sessionFactory;

	public ManualIndexingPerformanceTest() {
		modelService = ModelServiceFactory.create();
		sessionFactory = ModelServiceFactory.buildSessionFactory( modelService.properties( true ) );
	}

	@Setup
	public void init() {
		for (int i=0; i<10; i++) {
			TransactionHelper.inTransaction( sessionFactory, (session) -> {
				for (int j=0; j<10; j++) {
					session.persist( new Employee() );
				}
			} );
		}
	}

	@Benchmark
	public void massIndexing() throws Exception {
		try ( Session session = ( sessionFactory.openSession() ) ) {
			modelService.massIndexing( session, Employee.class );
		}
	}
}
