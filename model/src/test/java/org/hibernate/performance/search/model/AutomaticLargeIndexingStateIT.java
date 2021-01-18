package org.hibernate.performance.search.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Properties;

import org.hibernate.Session;
import org.hibernate.performance.search.model.asset.AutomaticIndexingDeletePartitionState;
import org.hibernate.performance.search.model.asset.AutomaticIndexingInsertPartitionState;
import org.hibernate.performance.search.model.asset.AutomaticIndexingState;
import org.hibernate.performance.search.model.asset.AutomaticIndexingUpdatePartitionState;
import org.hibernate.performance.search.model.entity.BusinessUnit;
import org.hibernate.performance.search.model.entity.Company;
import org.hibernate.performance.search.model.entity.Employee;
import org.hibernate.performance.search.model.entity.Manager;
import org.hibernate.performance.search.model.entity.answer.QuestionnaireInstance;
import org.hibernate.performance.search.model.entity.performance.PerformanceSummary;
import org.hibernate.performance.search.model.entity.question.ClosedQuestion;
import org.hibernate.performance.search.model.entity.question.OpenQuestion;
import org.hibernate.performance.search.model.entity.question.Question;
import org.hibernate.performance.search.model.entity.question.QuestionnaireDefinition;
import org.hibernate.performance.search.model.param.RelationshipSize;
import org.hibernate.performance.search.model.service.EmployeeRepository;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

public class AutomaticLargeIndexingStateIT {

	private static final int INITIAL_INDEX_SIZE = 1;
	private static final int INSERT_INVOCATION_SIZE = 1;
	private static final int UPDATE_INVOCATION_SIZE = 10;
	private static final int DELETE_INVOCATION_SIZE = 2;
	private static final int NUMBER_OF_THREADS = 1;

	private static final int[] BEFORE_INSERT_SIZES = { 1, 10, 200, 100, 100, 10, 10, 100, 11880, 118800, 118800, 1000 };
	private static final int[] AFTER_INSERT_SIZES = { 2, 20, 400, 200, 200, 20, 20, 200, 23760, 237600, 237600, 2000 };
	private static final int[] AFTER_UPDATE_SIZES = { 3, 20, 400, 200, 200, 20, 20, 200, 23760, 237600, 237600, 2000 };
	private static final int[] AFTER_DELETE_SIZES = { 3, 20, 400, 200, 200, 18, 18, 180, 21340, 213400, 213400, 1800 };

	private static final AutomaticIndexingState indexingState = new AutomaticIndexingState(
			RelationshipSize.LARGE, INITIAL_INDEX_SIZE, INSERT_INVOCATION_SIZE, UPDATE_INVOCATION_SIZE,
			DELETE_INVOCATION_SIZE, NUMBER_OF_THREADS, new Properties(), null
	);

	@Test
	public void test() {
		indexingState.startTrial();
		checkTheSize( BEFORE_INSERT_SIZES );

		for ( int i = 0; i < NUMBER_OF_THREADS; i++ ) {
			AutomaticIndexingInsertPartitionState partition = indexingState.getInsertPartition( i );
			partition.executeInsert();
		}

		checkTheSize( AFTER_INSERT_SIZES );

		for ( int i = 0; i < NUMBER_OF_THREADS; i++ ) {
			AutomaticIndexingUpdatePartitionState partition = indexingState.getUpdatePartition( i );

			partition.updateCompanyBU();
			assertThat( partition.getCompanyBUInvocation() ).isEqualTo( UPDATE_INVOCATION_SIZE );

			partition.updateEmployee();
			assertThat( partition.getEmployeeInvocation() ).isEqualTo( UPDATE_INVOCATION_SIZE );

			partition.updateQuestionnaire();
			assertThat( partition.getQuestionnaireInvocation() ).isEqualTo( UPDATE_INVOCATION_SIZE );

			partition.updateQuestion();
			assertThat( partition.getQuestionsInvocation() ).isEqualTo( UPDATE_INVOCATION_SIZE );
		}

		checkTheSize( AFTER_UPDATE_SIZES );

		for ( int i = 0; i < NUMBER_OF_THREADS; i++ ) {
			AutomaticIndexingDeletePartitionState partition = indexingState.getDeletePartition( i );
			partition.executeDelete();
		}

		checkTheSize( AFTER_DELETE_SIZES );
	}

	@AfterAll
	public static void afterAll() {
		indexingState.stopTrial();
	}

	private void checkTheSize(int[] sizes) {
		try ( Session session = indexingState.getSessionFactory().openSession() ) {
			EmployeeRepository repository = new EmployeeRepository( session );
			assertThat( repository.count( Company.class ) ).isEqualTo( sizes[0] );

			assertThat( repository.count( QuestionnaireDefinition.class ) ).isEqualTo( sizes[1] );
			assertThat( repository.count( Question.class ) ).isEqualTo( sizes[2] );
			assertThat( repository.count( ClosedQuestion.class ) ).isEqualTo( sizes[3] );
			assertThat( repository.count( OpenQuestion.class ) ).isEqualTo( sizes[4] );

			assertThat( repository.count( BusinessUnit.class ) ).isEqualTo( sizes[5] );
			assertThat( repository.count( Manager.class ) ).isEqualTo( sizes[6] );
			assertThat( repository.count( Employee.class ) ).isEqualTo( sizes[7] );

			assertThat( repository.count( QuestionnaireInstance.class ) ).isEqualTo( sizes[8] );
			assertThat( repository.countFilledClosedAnswer() ).isEqualTo( sizes[9] );
			assertThat( repository.countFilledOpenAnswer() ).isEqualTo( sizes[10] );
			assertThat( repository.count( PerformanceSummary.class ) ).isEqualTo( sizes[11] );
		}
	}

}
