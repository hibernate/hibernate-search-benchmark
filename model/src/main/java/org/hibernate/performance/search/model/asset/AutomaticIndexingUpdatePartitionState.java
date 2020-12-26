package org.hibernate.performance.search.model.asset;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.hibernate.SessionFactory;
import org.hibernate.performance.search.model.application.StatelessDomainDataUpdater;
import org.hibernate.performance.search.model.param.RelationshipSize;

public class AutomaticIndexingUpdatePartitionState {

	private final StatelessDomainDataUpdater domainDataUpdater;
	private final RelationshipSize relationshipSize;
	private final int actualIndexSize;
	private final int numberOfThreads;
	private final int threadNumber;
	private final List<Integer> partitionIds;
	private final Integer alternativeCompanyId;
	private final Random randomFixedSource;
	private final int invocationSize;

	private int companyBUInvocation = 0;
	private int employeeInvocation = 0;
	private int questionnaireInvocation = 0;
	private int questionsInvocation = 0;

	public AutomaticIndexingUpdatePartitionState(SessionFactory sessionFactory, RelationshipSize relationshipSize,
			int actualIndexSize, int numberOfThreads, int threadNumber, int invocationSize) {
		this.domainDataUpdater = new StatelessDomainDataUpdater( sessionFactory );
		this.relationshipSize = relationshipSize;
		this.actualIndexSize = actualIndexSize;
		this.numberOfThreads = numberOfThreads;
		this.threadNumber = threadNumber;
		this.partitionIds = partitionIds();
		this.alternativeCompanyId = alternativeCompanyId();
		this.randomFixedSource = new Random( 739L );
		this.invocationSize = invocationSize;
	}

	public void updateCompanyBU() {
		for ( int i = 0; i < invocationSize; i++ ) {
			updateCompanyBUOneTime();
		}
	}

	public void updateEmployee() {
		for ( int i = 0; i < invocationSize; i++ ) {
			updateEmployeeOneTime();
		}
	}

	public void updateQuestionnaire() {
		for ( int i = 0; i < invocationSize; i++ ) {
			updateQuestionnaireOneTime();
		}
	}

	private void updateCompanyBUOneTime() {
		boolean reverse = companyBUInvocation % 2 == 1;
		int companyId = partitionIds.get( ( companyBUInvocation / 2 ) % actualIndexSize );

		int fromCompanyId = ( reverse ) ? alternativeCompanyId : companyId;
		int toCompanyId = ( reverse ) ? companyId : alternativeCompanyId;

		domainDataUpdater.doSomeChangesOnCompanyAndBusinessUnit( companyBUInvocation++, fromCompanyId, toCompanyId );
	}

	private void updateEmployeeOneTime() {
		if ( RelationshipSize.SMALL.equals( relationshipSize ) ) {
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
		else if ( RelationshipSize.MEDIUM.equals( relationshipSize ) ) {
			boolean reverse = companyBUInvocation % 2 == 1;
			int companyId = partitionIds.get( ( companyBUInvocation / 2 ) % actualIndexSize );
			int baseEmployeeId = companyId * 6;

			int managerId = ( reverse ) ? baseEmployeeId + 3 : baseEmployeeId;
			int employeeId = baseEmployeeId + 5;

			domainDataUpdater.doSomeChangesOnEmployee( employeeInvocation++, employeeId, managerId );
		}
		else if ( RelationshipSize.LARGE.equals( relationshipSize ) ) {
			boolean reverse = companyBUInvocation % 2 == 1;
			int companyId = partitionIds.get( ( companyBUInvocation / 2 ) % actualIndexSize );
			int baseEmployeeId = companyId * 100;

			int managerId = ( reverse ) ? baseEmployeeId + 30 : baseEmployeeId;
			int employeeId = baseEmployeeId + 70;

			domainDataUpdater.doSomeChangesOnEmployee( employeeInvocation++, employeeId, managerId );
		}
	}

	private void updateQuestionnaireOneTime() {
		if ( RelationshipSize.SMALL.equals( relationshipSize ) ) {
			int companyId = partitionId( questionnaireInvocation );
			// companyId == questionnaireDefinitionId for this RelationshipSize
			domainDataUpdater.updateQuestionnaire( questionnaireInvocation++, companyId );
		}
		else if ( RelationshipSize.MEDIUM.equals( relationshipSize ) ) {
			int companyId = partitionId( questionnaireInvocation );
			int definitionsForCompany = RelationshipSize.MEDIUM.getQuestionnaireDefinitionsForCompany();

			int lowerBoundIncluded = companyId * definitionsForCompany;
			int upperBoundExcluded = lowerBoundIncluded + definitionsForCompany;

			int questionnaireDefinitionId = getRandomOf( lowerBoundIncluded, upperBoundExcluded );
			domainDataUpdater.updateQuestionnaire( questionnaireInvocation++, questionnaireDefinitionId );
		}
		else if ( RelationshipSize.LARGE.equals( relationshipSize ) ) {
			int companyId = partitionId( questionnaireInvocation );
			int definitionsForCompany = RelationshipSize.LARGE.getQuestionnaireDefinitionsForCompany();

			int lowerBoundIncluded = companyId * definitionsForCompany;
			int upperBoundExcluded = lowerBoundIncluded + definitionsForCompany;

			int questionnaireDefinitionId = getRandomOf( lowerBoundIncluded, upperBoundExcluded );
			domainDataUpdater.updateQuestionnaire( questionnaireInvocation++, questionnaireDefinitionId );
		}
	}

	public void updateQuestion() {
		if ( RelationshipSize.SMALL.equals( relationshipSize ) ) {
			int companyId = partitionId( questionnaireInvocation );
			// companyId == questionnaireDefinitionId for this RelationshipSize
			domainDataUpdater.updateQuestionsAndAnswers( questionsInvocation++, companyId );
		}
		else if ( RelationshipSize.MEDIUM.equals( relationshipSize ) ) {
			int companyId = partitionId( questionnaireInvocation );
			int definitionsForCompany = RelationshipSize.MEDIUM.getQuestionnaireDefinitionsForCompany();

			int lowerBoundIncluded = companyId * definitionsForCompany;
			int upperBoundExcluded = lowerBoundIncluded + definitionsForCompany;

			int questionnaireDefinitionId = getRandomOf( lowerBoundIncluded, upperBoundExcluded );
			domainDataUpdater.updateQuestionsAndAnswers( questionsInvocation++, questionnaireDefinitionId );
		}
		else if ( RelationshipSize.LARGE.equals( relationshipSize ) ) {
			int companyId = partitionId( questionnaireInvocation );
			int definitionsForCompany = RelationshipSize.LARGE.getQuestionnaireDefinitionsForCompany();

			int lowerBoundIncluded = companyId * definitionsForCompany;
			int upperBoundExcluded = lowerBoundIncluded + definitionsForCompany;

			int questionnaireDefinitionId = getRandomOf( lowerBoundIncluded, upperBoundExcluded );
			domainDataUpdater.updateQuestionsAndAnswers( questionsInvocation++, questionnaireDefinitionId );
		}
	}

	private List<Integer> partitionIds() {
		List<Integer> result = new ArrayList<>( actualIndexSize / numberOfThreads + 1 );
		for ( int i = 0; i < actualIndexSize; i++ ) {
			if ( i % threadNumber == 0 ) {
				result.add( i );
			}
		}
		return result;
	}

	private Integer alternativeCompanyId() {
		return actualIndexSize + threadNumber;
	}

	private Integer partitionId(int index) {
		return partitionIds.get( index % partitionIds.size() );
	}

	private int getRandomOf(int lowerBoundIncluded, int upperBoundExcluded) {
		return randomFixedSource.nextInt( upperBoundExcluded - lowerBoundIncluded ) + lowerBoundIncluded;
	}
}
