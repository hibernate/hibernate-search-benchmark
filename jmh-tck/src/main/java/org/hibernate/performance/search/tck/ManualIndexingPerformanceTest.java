package org.hibernate.performance.search.tck;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.performance.search.model.application.DomainDataFiller;
import org.hibernate.performance.search.model.application.HibernateORMHelper;
import org.hibernate.performance.search.model.application.ModelService;
import org.hibernate.performance.search.model.param.RelationshipSize;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class ManualIndexingPerformanceTest {

	@Param({ "SMALL" })
	private RelationshipSize relationshipSize;

	@Param({ "100" })
	private int indexSize;

	private final ModelService modelService;
	private final SessionFactory sessionFactory;

	public ManualIndexingPerformanceTest() {
		modelService = TckBackendHelperFactory.getModelService();
		sessionFactory = HibernateORMHelper.buildSessionFactory( TckBackendHelperFactory.manualProperties() );
	}

	@Setup
	public void init() {
		DomainDataFiller domainDataFiller = new DomainDataFiller( sessionFactory, relationshipSize );
		for ( int i = 0; i < indexSize; i++ ) {
			domainDataFiller.fillData( i );
		}
	}

	@Benchmark
	public void massIndexing() throws Exception {
		try ( Session session = ( sessionFactory.openSession() ) ) {
			modelService.massIndexing( session );
		}
	}
}
