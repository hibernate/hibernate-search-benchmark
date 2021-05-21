package org.hibernate.search.benchmark.tck;

import org.hibernate.search.benchmark.model.asset.AutomaticIndexingDeleteInsertPartitionState;
import org.hibernate.search.benchmark.model.asset.AutomaticIndexingState;
import org.hibernate.search.benchmark.model.asset.AutomaticIndexingUpdatePartitionState;

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
public abstract class AutomaticIndexingBenchmark {

	private AutomaticIndexingState indexingState;
	private int threadIndex;

	private AutomaticIndexingDeleteInsertPartitionState deleteInsertPartition;
	private AutomaticIndexingUpdatePartitionState updatePartition;

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
		deleteInsertPartition = indexingState.getDeleteInsertPartition( threadIndex );
		updatePartition = indexingState.getUpdatePartition( threadIndex );
	}

	@TearDown(Level.Iteration)
	public void tearDownIteration() {
		indexingState.stopIteration();
		deleteInsertPartition = null;
		updatePartition = null;
	}

	@Benchmark
	@Threads(3)
	public void update_companyBU() {
		updatePartition.updateCompanyBU();
	}

	@Benchmark
	@Threads(3)
	public void update_employeeManager() {
		updatePartition.updateEmployeeManager();
	}

	@Benchmark
	@Threads(3)
	public void update_employeeNames() {
		updatePartition.updateEmployeeNames();
	}

	@Benchmark
	@Threads(3)
	public void update_questionnaire() {
		updatePartition.updateQuestionnaire();
	}

	@Benchmark
	@Threads(3)
	public void update_question() {
		updatePartition.updateQuestion();
	}

	@Benchmark
	@Threads(3)
	public void employeesInsertDelete() {
		deleteInsertPartition.employeesInsertDelete();
	}

	@Benchmark
	@Threads(3)
	public void questionnaireDefinitions() {
		deleteInsertPartition.questionnaireDefinitions();
	}

	@Benchmark
	@Threads(3)
	public void questionnaireInstances() {
		deleteInsertPartition.questionnaireInstances();
	}

	@Benchmark
	@Threads(3)
	public void performanceSummaries() {
		deleteInsertPartition.performanceSummaries();
	}

	protected void setIndexingState(AutomaticIndexingState indexingState) {
		this.indexingState = indexingState;
	}

	protected void setThreadIndex(int threadIndex) {
		this.threadIndex = threadIndex;
	}
}
