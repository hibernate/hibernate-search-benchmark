package org.hibernate.performance.search.tck;

import org.hibernate.SessionFactory;
import org.hibernate.performance.search.model.application.DomainDataFiller;
import org.hibernate.performance.search.model.application.DomainDataRemover;
import org.hibernate.performance.search.model.application.HibernateORMHelper;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

@State(Scope.Benchmark)
public class AutomaticIndexingDeletePerformanceTest {

	private SessionFactory sessionFactory;

	@Setup(Level.Iteration)
	public void setup() throws Exception {
		sessionFactory = HibernateORMHelper.buildSessionFactory( TckBackendHelperFactory.autoProperties() );
		new DomainDataFiller( sessionFactory ).fillData( 0 );
	}

	@TearDown(Level.Iteration)
	public void tearDown() {
		if ( sessionFactory != null ) {
			sessionFactory.close();
		}
	}

	@Benchmark
	public void delete() throws Exception {
		new DomainDataRemover( sessionFactory ).deleteData( 9 );
	}
}
