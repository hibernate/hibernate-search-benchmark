package org.hibernate.performance.search.model.asset;

import org.hibernate.SessionFactory;

public class AutomaticIndexingUpdateSmallPartitionState extends AutomaticIndexingUpdatePartitionState {

	public AutomaticIndexingUpdateSmallPartitionState(SessionFactory sessionFactory,
			int actualIndexSize, int numberOfThreads, int threadNumber, int invocationSize) {
		super( sessionFactory, actualIndexSize, numberOfThreads, threadNumber, invocationSize );
	}

	@Override
	public void updateEmployeeOneTime() {
		int managerId;
		int employeeId;

		if ( employeeInvocation + 1 == actualIndexSize ) {
			// last element
			managerId = partitionIds.get( employeeInvocation );
			employeeId = partitionIds.get( 0 );
		}
		else {
			// with this relationship size: companyId == employeeId
			managerId = partitionIds.get( employeeInvocation );
			employeeId = partitionIds.get( employeeInvocation + 1 );
		}

		domainDataUpdater.doSomeChangesOnEmployee( employeeInvocation++, employeeId, managerId );
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
