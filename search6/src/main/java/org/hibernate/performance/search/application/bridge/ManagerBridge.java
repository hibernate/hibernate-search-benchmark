package org.hibernate.performance.search.application.bridge;

import org.hibernate.performance.search.entity.Manager;
import org.hibernate.search.engine.backend.document.DocumentElement;
import org.hibernate.search.engine.backend.document.IndexFieldReference;
import org.hibernate.search.engine.backend.document.IndexObjectFieldReference;
import org.hibernate.search.engine.backend.document.model.dsl.IndexSchemaObjectField;
import org.hibernate.search.mapper.pojo.bridge.PropertyBridge;
import org.hibernate.search.mapper.pojo.bridge.binding.PropertyBindingContext;
import org.hibernate.search.mapper.pojo.bridge.mapping.programmatic.PropertyBinder;
import org.hibernate.search.mapper.pojo.bridge.runtime.PropertyBridgeWriteContext;

public class ManagerBridge implements PropertyBridge<Manager> {

	final private IndexObjectFieldReference object;
	final private IndexFieldReference<String> name;
	final private IndexFieldReference<String> surname;

	public ManagerBridge(IndexObjectFieldReference object,
			IndexFieldReference<String> name, IndexFieldReference<String> surname) {
		this.object = object;
		this.name = name;
		this.surname = surname;
	}

	@Override
	public void write(DocumentElement target, Manager manager, PropertyBridgeWriteContext context) {
		DocumentElement documentElement = target.addObject( object );
		if ( manager == null ) {
			return;
		}

		if ( manager.getName() != null ) {
			documentElement.addValue( name, manager.getName() );
		}
		if ( manager.getSurname() != null ) {
			documentElement.addValue( surname, manager.getSurname() );
		}
	}

	public static class Binder implements PropertyBinder {

		@Override
		public void bind(PropertyBindingContext context) {
			context.dependencies()
					.use( "name" )
					.use( "surname" );

			IndexSchemaObjectField manager = context.indexSchemaElement().objectField( "manager" );
			IndexFieldReference<String> name = manager.field( "name", f -> f.asString() ).toReference();
			IndexFieldReference<String> surname = manager.field( "surname", f -> f.asString() ).toReference();

			context.bridge(
					Manager.class, new ManagerBridge( manager.toReference(), name, surname )
			);
		}
	}
}