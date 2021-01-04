package org.hibernate.performance.search.tck;

import org.hibernate.performance.search.model.asset.AutomaticIndexingDeletePartitionState;
import org.hibernate.performance.search.model.asset.AutomaticIndexingInsertPartitionState;
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

	@Setup(Level.Iteration)
	public void prepareIteration() {
		indexingState.start();
	}

	@TearDown(Level.Iteration)
	public void tearDownIteration() {
		indexingState.stop();
	}

	@Benchmark
	@Threads(1)
	public void test1_insert() {
		AutomaticIndexingInsertPartitionState insertPartition = indexingState.getInsertPartition( threadIndex );
		insertPartition.executeInsert();
	}

	@Benchmark
	@Threads(1)
	public void test2_update_companyBU() {
		AutomaticIndexingUpdatePartitionState updatePartition = indexingState.getUpdatePartition( threadIndex );
		updatePartition.updateCompanyBU();
	}

	@Benchmark
	@Threads(1)
	public void test3_update_employee() {
		AutomaticIndexingUpdatePartitionState updatePartition = indexingState.getUpdatePartition( threadIndex );
		updatePartition.updateEmployee();
	}

	@Benchmark
	@Threads(1)
	public void test4_update_questionnaire() {
		AutomaticIndexingUpdatePartitionState updatePartition = indexingState.getUpdatePartition( threadIndex );
		updatePartition.updateQuestionnaire();
	}

	@Benchmark
	@Threads(1)
	public void test5_update_question() {
		AutomaticIndexingUpdatePartitionState updatePartition = indexingState.getUpdatePartition( threadIndex );
		updatePartition.updateQuestion();
	}

	@Benchmark
	@Threads(1)
	public void test6_delete() {
		AutomaticIndexingDeletePartitionState deletePartition = indexingState.getDeletePartition( threadIndex );
		deletePartition.executeDelete();
	}

	protected void setIndexingState(AutomaticIndexingState indexingState) {
		this.indexingState = indexingState;
	}

	protected void setThreadIndex(int threadIndex) {
		this.threadIndex = threadIndex;
	}
}
