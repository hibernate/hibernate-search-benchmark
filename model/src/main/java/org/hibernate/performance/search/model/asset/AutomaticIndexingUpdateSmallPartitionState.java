package org.hibernate.performance.search.model.asset;

import org.hibernate.SessionFactory;

public class AutomaticIndexingUpdateSmallPartitionState extends AutomaticIndexingUpdatePartitionState {

	public AutomaticIndexingUpdateSmallPartitionState(SessionFactory sessionFactory,
			int initialCompanyCount, int numberOfThreads, int threadNumber) {
		super( sessionFactory, initialCompanyCount, numberOfThreads, threadNumber );
	}

	@Override
	public void updateEmployee() {
		int index = ( employeeInvocation / 2 ) % partitionIds.size();

		int managerId = partitionId( employeeInvocation );
		int employeeId = ( index == partitionIds.size() - 1 ) ? partitionIds.get( 0 ) : partitionIds.get( index + 1 );

		domainDataUpdater.inTransaction( ( (session, up) -> up
				.doSomeChangesOnEmployee( session, employeeInvocation++, employeeId, managerId ) ) );
		domainDataUpdater.inTransaction( ( (session, up) -> up
				.removeManagerFromEmployee( session, employeeInvocation++, employeeId ) ) );
	}

	@Override
	public void updateQuestionnaire() {
		domainDataUpdater.inTransaction( ( (session, up) -> {
			int companyId = partitionId( questionnaireInvocation );
			// companyId == questionnaireDefinitionId for this RelationshipSize
			up.updateQuestionnaire( session, questionnaireInvocation++, companyId );
		} ) );
	}

	@Override
	public void updateQuestion() {
		domainDataUpdater.inTransaction( ( (session, up) -> {
			int companyId = partitionId( questionsInvocation );
			// companyId == questionnaireDefinitionId for this RelationshipSize
			up.updateQuestionsAndAnswers( session, questionsInvocation++, companyId );
		} ) );
	}
}
