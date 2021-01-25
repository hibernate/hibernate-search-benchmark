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
	public void updateEmployee() {
		domainDataUpdater.inTransaction( (session, up) -> {
			int employeePerCompany = relationshipSize.getEmployeesPerCompany();

			boolean reverse = employeeInvocation % 2 == 1;
			int companyId = partitionId( employeeInvocation / 2 );
			int baseEmployeeId = companyId * employeePerCompany;

			int managerId = ( reverse ) ? baseEmployeeId + alternativeManagerBaseId : baseEmployeeId;
			int employeeId = baseEmployeeId + employeeBaseId;

			up.doSomeChangesOnEmployee( session, employeeInvocation++, employeeId, managerId );
		} );
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
