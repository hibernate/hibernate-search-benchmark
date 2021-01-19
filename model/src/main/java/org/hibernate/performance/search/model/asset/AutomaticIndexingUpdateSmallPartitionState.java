package org.hibernate.performance.search.model.asset;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.performance.search.model.application.DomainDataUpdater;

public class AutomaticIndexingUpdateSmallPartitionState extends AutomaticIndexingUpdatePartitionState {

	public AutomaticIndexingUpdateSmallPartitionState(SessionFactory sessionFactory,
			int initialIndexSize, int numberOfThreads, int threadNumber, int invocationSize) {
		super( sessionFactory, initialIndexSize, numberOfThreads, threadNumber, invocationSize );
	}

	@Override
	public void updateEmployeeOneTime(Session session, DomainDataUpdater up) {
		boolean reverse = employeeInvocation % 2 == 1;
		int index = ( employeeInvocation / 2 ) % partitionIds.size();

		int managerId = partitionIds.get( index );
		int employeeId = ( index == partitionIds.size() - 1 ) ? partitionIds.get( 0 ) : partitionIds.get( index + 1 );

		if ( reverse ) {
			up.removeManagerFromEmployee( session, employeeInvocation++, employeeId );
		}
		else {
			up.doSomeChangesOnEmployee( session, employeeInvocation++, employeeId, managerId );
		}
	}

	@Override
	public void updateQuestionnaireOneTime(Session session, DomainDataUpdater up) {
		int companyId = partitionId( questionnaireInvocation );
		// companyId == questionnaireDefinitionId for this RelationshipSize
		up.updateQuestionnaire( session, questionnaireInvocation++, companyId );
	}

	@Override
	public void updateQuestionOneTime(Session session, DomainDataUpdater up) {
		int companyId = partitionId( questionsInvocation );
		// companyId == questionnaireDefinitionId for this RelationshipSize
		up.updateQuestionsAndAnswers( session, questionsInvocation++, companyId );
	}
}
