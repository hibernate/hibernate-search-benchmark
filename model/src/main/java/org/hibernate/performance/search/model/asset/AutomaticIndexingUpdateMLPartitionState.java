package org.hibernate.performance.search.model.asset;

import org.hibernate.SessionFactory;
import org.hibernate.performance.search.model.param.RelationshipSize;

public class AutomaticIndexingUpdateMLPartitionState extends AutomaticIndexingUpdatePartitionState {

	private final RelationshipSize relationshipSize;
	private final int alternativeManagerBaseId;
	private final int employeeBaseId;

	public AutomaticIndexingUpdateMLPartitionState(SessionFactory sessionFactory,
			RelationshipSize relationshipSize, int initialCompanyCount,
			int numberOfThreads, int threadNumber) {
		super( sessionFactory, initialCompanyCount, numberOfThreads, threadNumber );
		this.relationshipSize = relationshipSize;
		this.alternativeManagerBaseId = ( RelationshipSize.MEDIUM.equals( relationshipSize ) ) ? 3 : 30;
		this.employeeBaseId = ( RelationshipSize.MEDIUM.equals( relationshipSize ) ) ? 5 : 70;
	}

	@Override
	public void updateEmployeeManager() {
		int employeePerCompany = relationshipSize.getEmployeesPerCompany();

		boolean reverse = employeeManagerInvocation % 2 == 1;
		int companyId = partitionId( employeeManagerInvocation / 2 );
		int baseEmployeeId = companyId * employeePerCompany;

		int managerId = ( reverse ) ? baseEmployeeId + alternativeManagerBaseId : baseEmployeeId;
		int employeeId = baseEmployeeId + employeeBaseId;

		domainDataUpdater.inTransaction( (session, up) -> {
			up.assignNewManager( session, employeeId, managerId );
		} );

		employeeManagerInvocation++;
	}

	@Override
	public void updateEmployeeNames() {
		int employeePerCompany = relationshipSize.getEmployeesPerCompany();

		boolean reverse = employeeNameInvocation % 2 == 1;
		int companyId = partitionId( employeeNameInvocation / 2 );
		int baseEmployeeId = companyId * employeePerCompany;

		int managerId = ( reverse ) ? baseEmployeeId + alternativeManagerBaseId : baseEmployeeId;
		int employeeId = baseEmployeeId + employeeBaseId;

		domainDataUpdater.inTransaction( (session, up) -> {
			up.changeEmployeeName( session, employeeId, employeeNameInvocation );
			up.changeEmployeeName( session, managerId, employeeNameInvocation );
		} );

		employeeNameInvocation++;
	}

	@Override
	public void updateQuestionnaire() {
		domainDataUpdater.inTransaction( (session, up) -> {
			int companyId = partitionId( questionnaireInvocation );
			int definitionsForCompany = relationshipSize.getQuestionnaireDefinitionsForCompany();

			int lowerBoundIncluded = companyId * definitionsForCompany;
			int upperBoundExcluded = lowerBoundIncluded + definitionsForCompany;

			int questionnaireDefinitionId = getRandomOf( lowerBoundIncluded, upperBoundExcluded );
			up.updateQuestionnaire( session, questionnaireInvocation++, questionnaireDefinitionId );
		} );
	}

	@Override
	public void updateQuestion() {
		domainDataUpdater.inTransaction( (session, up) -> {
			int companyId = partitionId( questionsInvocation );
			int definitionsForCompany = relationshipSize.getQuestionnaireDefinitionsForCompany();

			int lowerBoundIncluded = companyId * definitionsForCompany;
			int upperBoundExcluded = lowerBoundIncluded + definitionsForCompany;

			int questionnaireDefinitionId = getRandomOf( lowerBoundIncluded, upperBoundExcluded );
			up.updateQuestionsAndAnswers( session, questionsInvocation++, questionnaireDefinitionId );
		} );
	}
}
