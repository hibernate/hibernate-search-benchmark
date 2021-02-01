package org.hibernate.performance.search.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Properties;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.performance.search.model.application.DomainDataInitializer;
import org.hibernate.performance.search.model.application.HibernateORMHelper;
import org.hibernate.performance.search.model.application.ModelService;
import org.hibernate.performance.search.model.entity.Employee;
import org.hibernate.performance.search.model.param.RelationshipSize;
import org.hibernate.performance.search.model.service.EmployeeRepository;

import org.junit.jupiter.api.Test;

public class BiDirectionalRelationshipCheckIT {

	@Test
	public void test() {
		try ( SessionFactory sessionFactory = HibernateORMHelper.buildSessionFactory( new Properties() ) ) {
			ModelService modelService = new NoIndexingModelService();
			new DomainDataInitializer( modelService, sessionFactory, RelationshipSize.MEDIUM ).initAllCompanyData( 0 );

			try ( Session session = sessionFactory.openSession() ) {
				EmployeeRepository repository = new EmployeeRepository( session );

				List<Employee> employees = repository.getEmployees( 0 );
				assertThat( employees ).hasSize( RelationshipSize.MEDIUM.getEmployeesPerBusinessUnit() );

				Employee employee = employees.get( 0 );

				// verify that employee#performanceSummaries is filled
				assertThat( employee.getPerformanceSummaries() ).hasSize(
						RelationshipSize.MEDIUM.getQuestionnaireDefinitionsForCompany() );

				// verify that employee#questionnaires is filled
				assertThat( employee.getQuestionnaires() ).hasSize(
						// 4 is the scale size for MEDIUM relationship size
						4 * RelationshipSize.MEDIUM.getQuestionnaireDefinitionsForCompany() );
			}
		}
	}

}
