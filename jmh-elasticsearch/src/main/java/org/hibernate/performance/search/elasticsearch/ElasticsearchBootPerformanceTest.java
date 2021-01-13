package org.hibernate.performance.search.elasticsearch;

import java.util.Properties;

import org.hibernate.performance.search.model.application.ModelService;
import org.hibernate.performance.search.tck.BootPerformanceTest;

public class ElasticsearchBootPerformanceTest extends BootPerformanceTest {

	@Override
	protected Properties autoProperties(ModelService modelService) {
		return modelService.properties( ModelService.Kind.ELASTICSEARCH_AUTOMATIC_INDEXING );
	}
}
