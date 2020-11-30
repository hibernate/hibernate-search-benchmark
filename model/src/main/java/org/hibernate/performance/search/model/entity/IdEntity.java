package org.hibernate.performance.search.model.entity;

import java.util.Objects;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class IdEntity {

	@Id
	protected Integer id;

	protected IdEntity() {
	}

	public IdEntity(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

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
