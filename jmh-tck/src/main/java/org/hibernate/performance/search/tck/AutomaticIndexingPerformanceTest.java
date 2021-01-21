package org.hibernate.performance.search.tck;

import org.hibernate.performance.search.model.asset.AutomaticIndexingDeleteInsertPartitionState;
import org.hibernate.performance.search.model.asset.AutomaticIndexingState;
import org.hibernate.performance.search.model.asset.AutomaticIndexingUpdatePartitionState;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Threads;

@Fork(1)
@State(Scope.Thread)
public abstract class AutomaticIndexingPerformanceTest {

	private AutomaticIndexingState indexingState;
	private int threadIndex;

	@Setup(Level.Trial)
	public void prepareTrial() {
		indexingState.startTrial();
	}

	@TearDown(Level.Trial)
	public void tearDownTrial() {
		indexingState.stopTrial();
	}

	@Setup(Level.Iteration)
	public void prepareIteration() {
		indexingState.startIteration();
	}

	@TearDown(Level.Iteration)
	public void tearDownIteration() {
		indexingState.stopIteration();
	}

	@Benchmark
	@Threads(3)
	public void update_companyBU() {
		AutomaticIndexingUpdatePartitionState updatePartition = indexingState.getUpdatePartition( threadIndex );
		updatePartition.updateCompanyBU();
	}

	@Benchmark
	@Threads(3)
	public void update_employee() {
		AutomaticIndexingUpdatePartitionState updatePartition = indexingState.getUpdatePartition( threadIndex );
		updatePartition.updateEmployee();
	}

	@Benchmark
	@Threads(3)
	public void update_questionnaire() {
		AutomaticIndexingUpdatePartitionState updatePartition = indexingState.getUpdatePartition( threadIndex );
		updatePartition.updateQuestionnaire();
	}

	@Benchmark
	@Threads(3)
	public void update_question() {
		AutomaticIndexingUpdatePartitionState updatePartition = indexingState.getUpdatePartition( threadIndex );
		updatePartition.updateQuestion();
	}

	@Benchmark
	@Threads(3)
	public void employeesInsertDelete() {
		AutomaticIndexingDeleteInsertPartitionState partition = indexingState.getDeleteInsertPartition( threadIndex );
		partition.employeesInsertDelete();
	}

	@Benchmark
	@Threads(3)
	public void questionnaireDefinitions() {
		AutomaticIndexingDeleteInsertPartitionState partition = indexingState.getDeleteInsertPartition( threadIndex );
		partition.questionnaireDefinitions();
	}

	@Benchmark
	@Threads(3)
	public void questionnaireInstances() {
		AutomaticIndexingDeleteInsertPartitionState partition = indexingState.getDeleteInsertPartition( threadIndex );
		partition.questionnaireInstances();
	}

	@Benchmark
	@Threads(3)
	public void performanceSummaries() {
		AutomaticIndexingDeleteInsertPartitionState partition = indexingState.getDeleteInsertPartition( threadIndex );
		partition.performanceSummaries();
	}

	protected void setIndexingState(AutomaticIndexingState indexingState) {
		this.indexingState = indexingState;
	}

	protected void setThreadIndex(int threadIndex) {
		this.threadIndex = threadIndex;
	}
}
