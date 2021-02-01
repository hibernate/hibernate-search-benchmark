package org.hibernate.performance.search.model.service;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.query.Query;

/**
 * A wrapper around {@link ScrollableResults} that offers type-safe results
 * and returns hits in chunks
 * to allow for batch processing to take advantage of batch loading.
 *
 * @param <T> The result type.
 */
public class Scroll<T> implements Closeable {

	private final ScrollableResults delegate;
	private final int batchSize;
	private boolean exhausted;

	public Scroll(Query<T> query, int batchSize) {
		this.delegate = query.scroll( ScrollMode.FORWARD_ONLY );
		this.batchSize = batchSize;
	}

	@Override
	public void close() {
		delegate.close();
	}

	public boolean hasNext() {
		return !exhausted && !delegate.isLast();
	}

	@SuppressWarnings("unchecked")
	public List<T> next() {
		List<T> result = new ArrayList<>( batchSize );
		for ( int i = 0; i < batchSize; i++ ) {
			if ( !delegate.next() ) {
				exhausted = true;
				break;
			}
			result.add( (T) delegate.get( 0 ) );
		}
		return result;
	}
}
