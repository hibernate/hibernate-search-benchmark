package org.hibernate.performance.search.tck;

import org.hibernate.performance.search.model.application.ModelService;

public interface TckBackendHelper {

	ModelService.Kind automatic();

	ModelService.Kind manual();

}
