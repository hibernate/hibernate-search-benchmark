package org.hibernate.performance.search.entity;

import java.util.Objects;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class IdEntity {

	@Id
	@GeneratedValue
	private Integer id;

	@Override
	public boolean equals(Object o) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}
		IdEntity idEntity = (IdEntity) o;
		return Objects.equals( id, idEntity.id );
	}

	@Override
	public int hashCode() {
		return Objects.hash( id );
	}
}
