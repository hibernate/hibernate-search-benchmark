package org.hibernate.performance.search.model.asset;

public class InvalidThreadNumberException extends RuntimeException {

	public InvalidThreadNumberException(int threadNumber, int numberOfThreads) {
		super( "Invalid threadNumber: " + threadNumber + ". Thread range: [0 - " + ( numberOfThreads - 1 ) + "]" );
	}
}
