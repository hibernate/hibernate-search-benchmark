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

import org.junit.jupiter.api.Test;

public class AutomaticMediumIndexingStateIT {

	private static final int INITIAL_INDEX_SIZE = 50;
	private static final int INSERT_INVOCATION_SIZE = 5;
	private static final int UPDATE_INVOCATION_SIZE = 0;
	private static final int DELETE_INVOCATION_SIZE = 10;
	private static final int NUMBER_OF_THREADS = 3;

	private static final int[] BEFORE_INSERT_SIZES = { 50, 100, 400, 200, 200, 100, 100, 300, 2400, 4800, 4800, 600 };
	private static final int[] AFTER_INSERT_SIZES = { 65, 130, 520, 260, 260, 130, 130, 390, 3120, 6240, 6240, 780 };
	private static final int[] AFTER_UPDATE_SIZES = { 65, 130, 520, 260, 260, 130, 130, 390, 3120, 6240, 6240, 780 };
	private static final int[] AFTER_DELETE_SIZES = { 65, 130, 520, 260, 260, 100, 100, 300, 2220, 4440, 4440, 600 };

	private static final AutomaticIndexingState indexingState = new AutomaticIndexingState(
			RelationshipSize.MEDIUM, INITIAL_INDEX_SIZE, INSERT_INVOCATION_SIZE, UPDATE_INVOCATION_SIZE,
			DELETE_INVOCATION_SIZE, NUMBER_OF_THREADS, new Properties()
	);

	@Test
	public void test() {
		indexingState.start();
		checkTheSize( BEFORE_INSERT_SIZES );

		int expectedIndexSize = INITIAL_INDEX_SIZE + INSERT_INVOCATION_SIZE * NUMBER_OF_THREADS;
		for ( int i = 0; i < NUMBER_OF_THREADS; i++ ) {
			AutomaticIndexingInsertPartitionState partition = indexingState.getInsertPartition( i );
			partition.executeInsert();
			assertThat( partition.actualIndexSize() ).isEqualTo( expectedIndexSize );
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
