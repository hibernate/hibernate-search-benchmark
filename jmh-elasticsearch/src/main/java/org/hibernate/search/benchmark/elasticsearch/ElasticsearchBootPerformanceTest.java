package org.hibernate.search.benchmark.elasticsearch;

import java.util.Properties;

import org.hibernate.search.benchmark.model.application.BackendType;
import org.hibernate.search.benchmark.model.application.IndexingType;
import org.hibernate.search.benchmark.model.application.ModelService;
import org.hibernate.search.benchmark.tck.BootPerformanceTest;

public class ElasticsearchBootPerformanceTest extends BootPerformanceTest {

	@Override
	protected Properties autoProperties(ModelService modelService) {
		return modelService.properties( BackendType.ELASTICSEARCH, IndexingType.AUTOMATIC );
	}
}
