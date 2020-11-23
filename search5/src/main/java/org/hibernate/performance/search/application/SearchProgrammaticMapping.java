package org.hibernate.performance.search.application;

import java.lang.annotation.ElementType;

import org.hibernate.performance.search.entity.Employee;
import org.hibernate.search.cfg.SearchMapping;

public final class SearchProgrammaticMapping {

	private SearchProgrammaticMapping() {
	}

	public static SearchMapping create() {
		SearchMapping mapping = new SearchMapping();
		mapping.entity( Employee.class ).indexed()
				.property( "id", ElementType.FIELD ).documentId()
				.property( "name", ElementType.FIELD ).field();

		return mapping;
	}
}
