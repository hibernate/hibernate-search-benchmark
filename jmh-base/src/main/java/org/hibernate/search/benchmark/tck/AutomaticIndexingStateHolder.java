package org.hibernate.search.benchmark.tck;

import java.util.Properties;

import org.hibernate.search.benchmark.model.application.ModelService;
import org.hibernate.search.benchmark.model.application.ModelServiceFactory;
import org.hibernate.search.benchmark.model.asset.AutomaticIndexingState;
import org.hibernate.search.benchmark.model.param.RelationshipSize;

import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.ThreadParams;

@State(Scope.Benchmark)
public abstract class AutomaticIndexingStateHolder {

	@Param({ "MEDIUM" })
	private RelationshipSize relationshipSize;

	@Param({ "100" })
	private int initialCompanyCount;

	private AutomaticIndexingState automaticIndexingState;

	@Setup(Level.Trial)
	public void setup(ThreadParams threadParams) {
		ModelService modelService = ModelServiceFactory.create();
		Properties properties = autoProperties( modelService );

		automaticIndexingState = new AutomaticIndexingState(
				relationshipSize, initialCompanyCount, threadParams.getThreadCount(), properties, modelService );
	}

	public AutomaticIndexingState getAutomaticIndexingState() {
		return automaticIndexingState;
	}

	protected abstract Properties autoProperties(ModelService modelService);

}
