package org.hibernate.performance.search.tck;

import java.util.concurrent.atomic.AtomicInteger;

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
	private AtomicInteger businessUnit;

	@Setup(Level.Invocation)
	public void setup() throws Exception {
		sessionFactory = HibernateORMHelper.buildSessionFactory( TckBackendHelperFactory.autoProperties() );
		new DomainDataFiller( sessionFactory ).fillData( 0 );
		businessUnit = new AtomicInteger( 9 );
	}

	@TearDown(Level.Invocation)
	public void tearDown() {
		if ( sessionFactory != null ) {
			sessionFactory.close();
		}
	}

	@Benchmark
	public void delete() {
		new DomainDataRemover( sessionFactory ).deleteData( businessUnit.getAndDecrement() );
	}
}
