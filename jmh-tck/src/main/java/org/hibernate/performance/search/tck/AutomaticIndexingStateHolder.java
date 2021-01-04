package org.hibernate.performance.search.tck;

import java.util.Properties;

import org.hibernate.performance.search.model.application.ModelServiceFactory;
import org.hibernate.performance.search.model.asset.AutomaticIndexingState;
import org.hibernate.performance.search.model.param.RelationshipSize;

import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.ThreadParams;

@State(Scope.Benchmark)
public abstract class AutomaticIndexingStateHolder {

	@Param({ "SMALL" })
	private RelationshipSize relationshipSize;

	@Param({ "100" })
	private int initialIndexSize;

	@Param({ "10" })
	private int insertInvocationSize;

	@Param({ "10" })
	private int updateInvocationSize;

	@Param({ "10" })
	private int deleteInvocationSize;

	private AutomaticIndexingState automaticIndexingState;

	@Setup(Level.Trial)
	public void setup(ThreadParams threadParams) {
		Properties properties = ModelServiceFactory.create().properties( backendHelper().automatic() );

		automaticIndexingState = new AutomaticIndexingState(
				RelationshipSize.SMALL, initialIndexSize, insertInvocationSize, updateInvocationSize,
				deleteInvocationSize, threadParams.getThreadCount(), properties
		);
	}

	public AutomaticIndexingState getAutomaticIndexingState() {
		return automaticIndexingState;
	}

	protected abstract TckBackendHelper backendHelper();

}
