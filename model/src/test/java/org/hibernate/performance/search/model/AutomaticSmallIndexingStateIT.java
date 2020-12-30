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

public class AutomaticSmallIndexingStateIT {

	private static final int INITIAL_INDEX_SIZE = 100;
	private static final int INVOCATION_SIZE = 10;
	private static final int NUMBER_OF_THREADS = 3;

	private static final AutomaticIndexingState indexingState = new AutomaticIndexingState( RelationshipSize.SMALL,
			INITIAL_INDEX_SIZE, INVOCATION_SIZE, INVOCATION_SIZE, INVOCATION_SIZE, NUMBER_OF_THREADS, new Properties()
	);

	@Test
	public void test() {
		indexingState.start();

		checkTheSize( INITIAL_INDEX_SIZE, INITIAL_INDEX_SIZE, INITIAL_INDEX_SIZE );

		int expectedIndexSize = INITIAL_INDEX_SIZE + INVOCATION_SIZE * NUMBER_OF_THREADS;
		for ( int i = 0; i < NUMBER_OF_THREADS; i++ ) {
			AutomaticIndexingInsertPartitionState partition = indexingState.getInsertPartition( i );
			partition.executeInsert();
			assertThat( partition.actualIndexSize() ).isEqualTo( expectedIndexSize );
		}

		checkTheSize( expectedIndexSize, expectedIndexSize, expectedIndexSize );

		for ( int i = 0; i < NUMBER_OF_THREADS; i++ ) {
			AutomaticIndexingUpdatePartitionState partition = indexingState.getUpdatePartition( i );

			partition.updateCompanyBU();
			assertThat( partition.getCompanyBUInvocation() ).isEqualTo( INVOCATION_SIZE );

			partition.updateEmployee();
			assertThat( partition.getEmployeeInvocation() ).isEqualTo( INVOCATION_SIZE );

			partition.updateQuestionnaire();
			assertThat( partition.getQuestionnaireInvocation() ).isEqualTo( INVOCATION_SIZE );

			partition.updateQuestion();
			assertThat( partition.getQuestionsInvocation() ).isEqualTo( INVOCATION_SIZE );
		}

		// created NUMBER_OF_THREADS extra companies
		checkTheSize( expectedIndexSize + NUMBER_OF_THREADS, expectedIndexSize, expectedIndexSize );

		for ( int i = 0; i < NUMBER_OF_THREADS; i++ ) {
			AutomaticIndexingDeletePartitionState partition = indexingState.getDeletePartition( i );
			partition.executeDelete();
		}

		checkTheSize(
				expectedIndexSize + NUMBER_OF_THREADS, expectedIndexSize,
				expectedIndexSize - NUMBER_OF_THREADS * INVOCATION_SIZE
		);
	}

	private void checkTheSize(int companySize, int questionSize, int otherSize) {
		try ( Session session = indexingState.getSessionFactory().openSession() ) {
			EmployeeRepository repository = new EmployeeRepository( session );
			assertThat( repository.count( Company.class ) ).isEqualTo( companySize );

			assertThat( repository.count( QuestionnaireDefinition.class ) ).isEqualTo( questionSize );
			assertThat( repository.count( Question.class ) ).isEqualTo( questionSize * 2 );
			assertThat( repository.count( ClosedQuestion.class ) ).isEqualTo( questionSize );
			assertThat( repository.count( OpenQuestion.class ) ).isEqualTo( questionSize );

			assertThat( repository.count( BusinessUnit.class ) ).isEqualTo( otherSize );
			assertThat( repository.count( Manager.class ) ).isEqualTo( otherSize );
			assertThat( repository.count( Employee.class ) ).isEqualTo( otherSize );

			assertThat( repository.count( QuestionnaireInstance.class ) ).isEqualTo( otherSize );
			assertThat( repository.countFilledClosedAnswer() ).isEqualTo( otherSize );
			assertThat( repository.countFilledOpenAnswer() ).isEqualTo( otherSize );
			assertThat( repository.count( PerformanceSummary.class ) ).isEqualTo( otherSize );
		}
	}

	@AfterAll
	public static void afterAll() {
		indexingState.stop();
	}

}
