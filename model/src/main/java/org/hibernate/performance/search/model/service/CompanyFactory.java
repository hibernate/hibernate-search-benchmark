package org.hibernate.performance.search.model.service;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.performance.search.model.entity.BusinessUnit;
import org.hibernate.performance.search.model.entity.Company;
import org.hibernate.performance.search.model.param.RelationshipSize;

public final class CompanyFactory {

	public final int unitPerCompany;

	public CompanyFactory(RelationshipSize relationshipSize) {
		unitPerCompany = relationshipSize.getUnitsPerCompany();
	}

	public Company createCompanyAndUnits(int companyId) {
		Company company = new Company( companyId, "Company" + companyId );
		company.setDescription( "This is a real description for the company " + companyId );
		List<BusinessUnit> units = new ArrayList<>( unitPerCompany );

		for ( int i = 0; i < unitPerCompany; i++ ) {
			int buId = unitPerCompany * companyId + i;
			units.add( new BusinessUnit( buId, "Unit" + buId, company ) );
		}
		company.setBusinessUnits( units );
		return company;
	}

}
