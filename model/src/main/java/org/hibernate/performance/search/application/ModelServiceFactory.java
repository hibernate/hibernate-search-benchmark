package org.hibernate.performance.search.application;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.performance.search.entity.Employee;

public final class ModelServiceFactory {

	public static final String SERVICE_IMPL = ModelService.class.getName() + "Impl";

	private ModelServiceFactory() {
	}

	public static ModelService create() {
		Class<? extends ModelService> implementation;
		try {
			Class<?> loadedClass = ModelService.class.getClassLoader().loadClass( SERVICE_IMPL );
			if ( !ModelService.class.isAssignableFrom( loadedClass ) ) {
				throw new RuntimeException( "ModelService init error. Invalid class for runtime Search implementation: "
						+ loadedClass + "." );
			}

			implementation = (Class<? extends ModelService>) loadedClass;
		}
		catch (ClassNotFoundException e) {
			throw new RuntimeException( "ModelService init error. Class " + SERVICE_IMPL + " cannot be found." +
					" Please provide a runtime Search implementation module.", e );
		}

		Constructor<? extends ModelService> constructor;
		try {
			constructor = implementation.getConstructor();
		}
		catch (NoSuchMethodException e) {
			throw new RuntimeException( "ModelService init error. Cannot locate a valid contractor for " +
					implementation + ".", e );
		}

		try {
			return constructor.newInstance();
		}
		catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException( "ModelService init error. Error creating " +
					implementation + ".", e );
		}
	}

	public static SessionFactory buildSessionFactory() {
		return new Configuration().addAnnotatedClass( Employee.class ).buildSessionFactory();
	}
}
