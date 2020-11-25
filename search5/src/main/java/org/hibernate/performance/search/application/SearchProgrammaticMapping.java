package org.hibernate.performance.search.application;

import java.lang.annotation.ElementType;

import org.hibernate.performance.search.entity.BusinessUnit;
import org.hibernate.performance.search.entity.Company;
import org.hibernate.performance.search.entity.Employee;
import org.hibernate.performance.search.entity.Manager;
import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.cfg.SearchMapping;

public final class SearchProgrammaticMapping {

	private SearchProgrammaticMapping() {
	}

	public static SearchMapping create() {
		SearchMapping mapping = new SearchMapping();
		mapping.entity( Employee.class ).indexed()
				.property( "id", ElementType.FIELD ).documentId()
				.property( "name", ElementType.FIELD ).field().analyze( Analyze.NO )
				.property( "surname", ElementType.FIELD ).field().analyze( Analyze.NO )
				.property( "socialSecurityNumber", ElementType.FIELD ).field().analyze( Analyze.NO )
				.property( "company", ElementType.FIELD ).indexEmbedded()
				.property( "businessUnit", ElementType.FIELD ).indexEmbedded()
				.property( "manager", ElementType.FIELD ).indexEmbedded().depth( 1 );

		mapping.entity( Company.class )
				.property( "legalName", ElementType.FIELD ).field().analyze( Analyze.NO );

		mapping.entity( BusinessUnit.class )
				.property( "name", ElementType.FIELD ).field().analyze( Analyze.NO );

		mapping.entity( Manager.class )
				.indexed()
				.property( "employees", ElementType.FIELD ).containedIn();

		return mapping;
	}
}
