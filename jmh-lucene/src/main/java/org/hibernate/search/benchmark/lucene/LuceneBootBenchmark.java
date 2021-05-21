package org.hibernate.search.benchmark.lucene;

import java.util.Properties;

import org.hibernate.search.benchmark.model.application.BackendType;
import org.hibernate.search.benchmark.model.application.IndexingType;
import org.hibernate.search.benchmark.model.application.ModelService;
import org.hibernate.search.benchmark.tck.BootBenchmark;

public class LuceneBootBenchmark extends BootBenchmark {

	@Override
	protected Properties autoProperties(ModelService modelService) {
		return modelService.properties( BackendType.LUCENE, IndexingType.AUTOMATIC );
	}
}
