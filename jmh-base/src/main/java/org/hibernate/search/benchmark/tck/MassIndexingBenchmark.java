package org.hibernate.search.benchmark.tck;

import java.util.Properties;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.search.benchmark.model.application.DomainDataInitializer;
import org.hibernate.search.benchmark.model.application.HibernateORMHelper;
import org.hibernate.search.benchmark.model.application.ModelService;
import org.hibernate.search.benchmark.model.application.ModelServiceFactory;
import org.hibernate.search.benchmark.model.param.RelationshipSize;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;

@State(Scope.Benchmark)
public abstract class MassIndexingBenchmark {

	@Param({ "MEDIUM" })
	private RelationshipSize relationshipSize;

	@Param({ "100" })
	private int initialCompanyCount;

	private final ModelService modelService;
	private final SessionFactory sessionFactory;

	public MassIndexingBenchmark() {
		modelService = ModelServiceFactory.create();
		sessionFactory = HibernateORMHelper.buildSessionFactory( manualProperties( modelService ) );
	}

	@Setup
	public void init() {
		DomainDataInitializer domainDataInitializer = new DomainDataInitializer( modelService, sessionFactory, relationshipSize );
		for ( int i = 0; i < initialCompanyCount; i++ ) {
			domainDataInitializer.initAllCompanyData( i );
		}
	}

	@Benchmark
	public void massIndexing() throws Exception {
		try ( Session session = ( sessionFactory.openSession() ) ) {
			modelService.massIndexing( session );
		}
	}

	@TearDown
	public void tearDown() {
		if ( sessionFactory != null ) {
			sessionFactory.close();
		}
	}

	protected abstract Properties manualProperties(ModelService modelService);
}
