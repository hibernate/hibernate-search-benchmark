package org.hibernate.performance.search.tck;

import org.hibernate.SessionFactory;
import org.hibernate.performance.search.model.application.DomainDataFiller;
import org.hibernate.performance.search.model.application.DomainDataUpdater;
import org.hibernate.performance.search.model.application.HibernateORMHelper;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

@State(Scope.Benchmark)
public class AutomaticIndexingUpdatePerformanceTest {

	private SessionFactory sessionFactory;
	private DomainDataFiller domainDataFiller;

	public AutomaticIndexingUpdatePerformanceTest() {

	}

	@Setup
	public void setup() throws Exception {
		// this is very expensive, but we need to bring back the state to the same pre-filled state
		sessionFactory = HibernateORMHelper.buildSessionFactory( TckBackendHelperFactory.autoProperties() );
		new DomainDataFiller( sessionFactory ).fillData( 0 );
	}

	@TearDown
	public void tearDown() {
		if ( sessionFactory != null ) {
			sessionFactory.close();
		}
	}

	@Benchmark
	public void companyAndBusinessUnit() {
		new DomainDataUpdater( sessionFactory ).doSomeChangesOnCompanyAndBusinessUnit( 7, 1 );
	}
}
