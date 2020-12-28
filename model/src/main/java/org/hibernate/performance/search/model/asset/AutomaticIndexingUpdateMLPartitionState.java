package org.hibernate.performance.search.model.asset;

import org.hibernate.SessionFactory;
import org.hibernate.performance.search.model.param.RelationshipSize;

public class AutomaticIndexingUpdateMLPartitionState extends AutomaticIndexingUpdatePartitionState {

	private final RelationshipSize relationshipSize;
	private final int alternativeManagerBaseId;
	private final int employeeBaseId;

	public AutomaticIndexingUpdateMLPartitionState(SessionFactory sessionFactory,
			RelationshipSize relationshipSize, int actualIndexSize,
			int numberOfThreads, int threadNumber, int invocationSize) {
		super( sessionFactory, actualIndexSize, numberOfThreads, threadNumber, invocationSize );
		this.relationshipSize = relationshipSize;
		this.alternativeManagerBaseId = ( RelationshipSize.MEDIUM.equals( relationshipSize ) ) ? 3 : 30;
		this.employeeBaseId = ( RelationshipSize.MEDIUM.equals( relationshipSize ) ) ? 5 : 70;
	}

	@Override
	public void updateEmployeeOneTime() {
		int employeePerCompany = relationshipSize.getEmployeesPerBusinessUnit() * relationshipSize.getUnitsPerCompany();

		boolean reverse = companyBUInvocation % 2 == 1;
		int companyId = partitionIds.get( ( companyBUInvocation / 2 ) % actualIndexSize );
		int baseEmployeeId = companyId * employeePerCompany;

		int managerId = ( reverse ) ? baseEmployeeId + alternativeManagerBaseId : baseEmployeeId;
		int employeeId = baseEmployeeId + employeeBaseId;

		domainDataUpdater.doSomeChangesOnEmployee( employeeInvocation++, employeeId, managerId );
	}

	@Override
	public void updateQuestionnaireOneTime() {
		int companyId = partitionId( questionnaireInvocation );
		int definitionsForCompany = relationshipSize.getQuestionnaireDefinitionsForCompany();

		int lowerBoundIncluded = companyId * definitionsForCompany;
		int upperBoundExcluded = lowerBoundIncluded + definitionsForCompany;

		int questionnaireDefinitionId = getRandomOf( lowerBoundIncluded, upperBoundExcluded );
		domainDataUpdater.updateQuestionnaire( questionnaireInvocation++, questionnaireDefinitionId );
	}

	@Override
	public void updateQuestionOneTime() {
		int companyId = partitionId( questionsInvocation );
		int definitionsForCompany = relationshipSize.getQuestionnaireDefinitionsForCompany();

		int lowerBoundIncluded = companyId * definitionsForCompany;
		int upperBoundExcluded = lowerBoundIncluded + definitionsForCompany;

		int questionnaireDefinitionId = getRandomOf( lowerBoundIncluded, upperBoundExcluded );
		domainDataUpdater.updateQuestionsAndAnswers( questionsInvocation++, questionnaireDefinitionId );
	}
}
