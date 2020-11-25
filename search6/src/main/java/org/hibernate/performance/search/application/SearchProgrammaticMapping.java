package org.hibernate.performance.search.application;

import org.hibernate.performance.search.application.bridge.ManagerBridge;
import org.hibernate.performance.search.entity.BusinessUnit;
import org.hibernate.performance.search.entity.Company;
import org.hibernate.performance.search.entity.Employee;
import org.hibernate.search.mapper.orm.mapping.HibernateOrmMappingConfigurationContext;
import org.hibernate.search.mapper.orm.mapping.HibernateOrmSearchMappingConfigurer;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.programmatic.ProgrammaticMappingConfigurationContext;
import org.hibernate.search.mapper.pojo.mapping.definition.programmatic.TypeMappingStep;

public class SearchProgrammaticMapping implements HibernateOrmSearchMappingConfigurer {

	@Override
	public void configure(HibernateOrmMappingConfigurationContext context) {
		ProgrammaticMappingConfigurationContext mapping = context.programmaticMapping();

		TypeMappingStep employee = mapping.type( Employee.class );
		employee.indexed();
		employee.property( "name" ).keywordField();
		employee.property( "surname" ).keywordField();
		employee.property( "socialSecurityNumber" ).keywordField();
		employee.property( "company" ).indexedEmbedded().indexingDependency().reindexOnUpdate( ReindexOnUpdate.NO );
		employee.property( "businessUnit" ).indexedEmbedded().indexingDependency().reindexOnUpdate( ReindexOnUpdate.NO );
		employee.property( "manager" ).binder( new ManagerBridge.Binder() );

		TypeMappingStep company = mapping.type( Company.class );
		company.property( "legalName" ).keywordField();

		TypeMappingStep businessUnit = mapping.type( BusinessUnit.class );
		businessUnit.property( "name" ).keywordField();
	}
}
