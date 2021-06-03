package org.hibernate.search.benchmark.model.asset;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.hibernate.SessionFactory;
import org.hibernate.search.benchmark.model.application.DomainDataUpdater;
import org.hibernate.search.benchmark.model.param.RelationshipSize;

public class AutomaticIndexingUpdatePartitionState {

	private static final int LARGE_NUMBER = 100_000_000;

	private final DomainDataUpdater domainDataUpdater;
	private final int initialCompanyCount;
	private final int numberOfThreads;
	private final int threadNumber;
	private final List<Integer> partitionIds;
	private final Integer alternativeCompanyId;
	private final Random randomFixedSource;
	private final RelationshipSize relationshipSize;
	private final int alternativeManagerBaseId;
	private final int employeeBaseId;

	private int companyBUInvocation = 0;
	private int employeeManagerInvocation = 0;
	private int employeeNameInvocation = 0;
	private int questionnaireInvocation = 0;
	private int questionsInvocation = 0;

	public AutomaticIndexingUpdatePartitionState(SessionFactory sessionFactory,
			RelationshipSize relationshipSize, int initialCompanyCount,
			int numberOfThreads, int threadNumber) {
		this.domainDataUpdater = new DomainDataUpdater( sessionFactory );
		this.initialCompanyCount = initialCompanyCount;
		this.numberOfThreads = numberOfThreads;
		this.threadNumber = threadNumber;
		this.partitionIds = partitionIds();
		this.alternativeCompanyId = alternativeCompanyId();
		this.randomFixedSource = new Random( 739L );
		this.relationshipSize = relationshipSize;
		this.alternativeManagerBaseId = relationshipSize.getEmployeesPerBusinessUnit();
		this.employeeBaseId = getEmployeeBaseId( relationshipSize );
	}

	public void updateCompanyBU() {
		domainDataUpdater.inTransaction( ( (session, up) -> {
			boolean reverse = companyBUInvocation % 2 == 1;
			int companyId = partitionId( companyBUInvocation / 2 );

			int fromCompanyId = ( reverse ) ? alternativeCompanyId : companyId;
			int toCompanyId = ( reverse ) ? companyId : alternativeCompanyId;

			up.doSomeChangesOnCompanyAndBusinessUnit( session, companyBUInvocation++, fromCompanyId, toCompanyId );
		} ) );
	}

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

	private int getEmployeeBaseId(RelationshipSize relationshipSize) {
		final int employeeBaseId;
		int lastManagerBaseId = relationshipSize.getEmployeesPerCompany() -
				relationshipSize.getEmployeesPerBusinessUnit();

		// use the last manager if it is not already the alternative manager
		employeeBaseId = ( alternativeManagerBaseId == lastManagerBaseId )
				? lastManagerBaseId + 1 : lastManagerBaseId;
		return employeeBaseId;
	}

	private Integer partitionId(int index) {
		return partitionIds.get( index % partitionIds.size() );
	}

	private int getRandomOf(int lowerBoundIncluded, int upperBoundExcluded) {
		return randomFixedSource.nextInt( upperBoundExcluded - lowerBoundIncluded ) + lowerBoundIncluded;
	}

	private List<Integer> partitionIds() {
		List<Integer> result = new ArrayList<>( initialCompanyCount / numberOfThreads + 1 );
		for ( int i = 0; i < initialCompanyCount; i++ ) {
			if ( i % numberOfThreads == threadNumber ) {
				result.add( i );
			}
		}
		return result;
	}

	private Integer alternativeCompanyId() {
		return LARGE_NUMBER + threadNumber;
	}
}
