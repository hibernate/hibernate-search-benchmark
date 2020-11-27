package org.hibernate.performance.search.util;

import org.hibernate.performance.search.application.ModelService;

public interface TckBackendHelper {

	ModelService.Kind automatic();

	ModelService.Kind manual();

}
