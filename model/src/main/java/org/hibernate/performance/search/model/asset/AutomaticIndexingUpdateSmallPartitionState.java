package org.hibernate.performance.search.model.asset;

import org.hibernate.SessionFactory;

public class AutomaticIndexingUpdateSmallPartitionState extends AutomaticIndexingUpdatePartitionState {

	public AutomaticIndexingUpdateSmallPartitionState(SessionFactory sessionFactory,
			int initialIndexSize, int numberOfThreads, int threadNumber, int invocationSize) {
		super( sessionFactory, initialIndexSize, numberOfThreads, threadNumber, invocationSize );
	}

	@Override
	public void updateEmployeeOneTime() {
		boolean reverse = employeeInvocation % 2 == 1;
		int index = ( employeeInvocation / 2 ) % partitionIds.size();

		int managerId = partitionIds.get( index );
		int employeeId = ( index == partitionIds.size() - 1 ) ? partitionIds.get( 0 ) : partitionIds.get( index + 1 );

		if ( reverse ) {
			domainDataUpdater.removeManagerFromEmployee( employeeInvocation++, employeeId );
		} else {
			domainDataUpdater.doSomeChangesOnEmployee( employeeInvocation++, employeeId, managerId );
		}
	}

	@Override
	public void updateQuestionnaireOneTime() {
		int companyId = partitionId( questionnaireInvocation );
		// companyId == questionnaireDefinitionId for this RelationshipSize
		domainDataUpdater.updateQuestionnaire( questionnaireInvocation++, companyId );
	}

	@Override
	public void updateQuestionOneTime() {
		int companyId = partitionId( questionsInvocation );
		// companyId == questionnaireDefinitionId for this RelationshipSize
		domainDataUpdater.updateQuestionsAndAnswers( questionsInvocation++, companyId );
	}
}
