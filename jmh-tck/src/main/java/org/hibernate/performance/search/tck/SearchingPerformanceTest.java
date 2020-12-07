package org.hibernate.performance.search.tck;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.performance.search.model.application.DomainDataFiller;
import org.hibernate.performance.search.model.application.HibernateORMHelper;
import org.hibernate.performance.search.model.application.ModelService;
import org.hibernate.performance.search.model.entity.BusinessUnit;
import org.hibernate.performance.search.model.entity.Company;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

@State(Scope.Benchmark)
public class SearchingPerformanceTest {

	private final ModelService modelService;
	private final SessionFactory sessionFactory;

	public SearchingPerformanceTest() {
		modelService = TckBackendHelperFactory.getModelService();
		sessionFactory = HibernateORMHelper.buildSessionFactory( TckBackendHelperFactory.manualProperties() );
	}

	@Setup
	public void init() throws Exception {
		new DomainDataFiller( sessionFactory ).fillData( 0 );
		try ( Session session = ( sessionFactory.openSession() ) ) {
			modelService.massIndexing( session );
		}
	}

	@Benchmark
	public void searching(Blackhole blackhole) {
		try ( Session session = ( sessionFactory.openSession() ) ) {
			List<Company> companies = modelService.search( session, Company.class, "legalName", "Company0" );

			List<BusinessUnit> businessUnits = modelService.search( session, BusinessUnit.class, "name", "Unit7" );

			// This call provides a side effect preventing JIT to eliminate dependent computations
			blackhole.consume( companies );
			blackhole.consume( businessUnits );
		}
	}
}
