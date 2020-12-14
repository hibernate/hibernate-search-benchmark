package org.hibernate.performance.search.tck;

import org.hibernate.SessionFactory;
import org.hibernate.performance.search.model.application.DomainDataFiller;
import org.hibernate.performance.search.model.application.HibernateORMHelper;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

@State(Scope.Benchmark)
public class AutomaticIndexingInsertPerformanceTest {

	private final SessionFactory sessionFactory;
	private DomainDataFiller domainDataFiller;

	public AutomaticIndexingInsertPerformanceTest() {
		sessionFactory = HibernateORMHelper.buildSessionFactory( TckBackendHelperFactory.autoProperties() );
		domainDataFiller = new DomainDataFiller( sessionFactory );
	}

	@Setup(Level.Trial)
	public void setup() throws Exception {
		domainDataFiller.fillData( 0 );
	}

	@TearDown(Level.Trial)
	public void tearDown() {
		if ( sessionFactory != null ) {
			sessionFactory.close();
		}
	}

	@Benchmark
	public void insert() {
		domainDataFiller.fillData( 1 );
	}
}
