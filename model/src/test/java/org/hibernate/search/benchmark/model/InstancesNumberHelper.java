/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.benchmark.model;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.search.benchmark.model.param.RelationshipSize;

public final class InstancesNumberHelper {

	private InstancesNumberHelper() {
	}

	private static final Map<RelationshipSize, Integer> INSTANCES_FOR_COMPANY_BY_SIZE = new HashMap<>();
	static {
		// these values depend on the topology of the employee graph (that is the organization chart).
		// Because we have auto-*, manager-*, collaborator-* and colleague-evaluations,
		// all of them determine the final count.
		INSTANCES_FOR_COMPANY_BY_SIZE.put( RelationshipSize.SMALL, 12 );
		INSTANCES_FOR_COMPANY_BY_SIZE.put( RelationshipSize.MEDIUM, 120 );
		INSTANCES_FOR_COMPANY_BY_SIZE.put( RelationshipSize.LARGE, 11880 );
	}

	public static int questionnaireInstancesForCompany(RelationshipSize relationshipSize) {
		return INSTANCES_FOR_COMPANY_BY_SIZE.get( relationshipSize );
	}
}
