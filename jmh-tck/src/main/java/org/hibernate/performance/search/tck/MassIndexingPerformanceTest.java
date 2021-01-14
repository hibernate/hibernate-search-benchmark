package org.hibernate.performance.search.tck;

import java.util.Properties;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.performance.search.model.application.DomainDataFiller;
import org.hibernate.performance.search.model.application.HibernateORMHelper;
import org.hibernate.performance.search.model.application.ModelService;
import org.hibernate.performance.search.model.application.ModelServiceFactory;
import org.hibernate.performance.search.model.param.RelationshipSize;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public abstract class MassIndexingPerformanceTest {

	@Param({ "SMALL" })
	private RelationshipSize relationshipSize;

	@Param({ "100" })
	private int initialCompanyCount;

	private final ModelService modelService;
	private final SessionFactory sessionFactory;

	public MassIndexingPerformanceTest() {
		modelService = ModelServiceFactory.create();
		sessionFactory = HibernateORMHelper.buildSessionFactory( manualProperties( modelService ) );
	}

	@Setup
	public void init() {
		DomainDataFiller domainDataFiller = new DomainDataFiller( sessionFactory, relationshipSize );
		for ( int i = 0; i < initialCompanyCount; i++ ) {
			domainDataFiller.fillData( i );
		}
	}

	@Benchmark
	public void massIndexing() throws Exception {
		try ( Session session = ( sessionFactory.openSession() ) ) {
			modelService.massIndexing( session );
		}
	}

	protected abstract Properties manualProperties(ModelService modelService);
}