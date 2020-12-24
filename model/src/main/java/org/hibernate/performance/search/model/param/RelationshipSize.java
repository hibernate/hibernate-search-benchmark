package org.hibernate.performance.search.model.param;

public enum RelationshipSize {

	SMALL( 1, 1, 1, new int[] { 3 }, 1 ),
	MEDIUM( 3, 2, 3, new int[] { 7, 3 }, 2 ),
	LARGE( 10, 10, 10, new int[] { 7, 3, 9, 3, 3, 9, 7, 7, 7, 9 }, 10 );

	private final int unitPerCompany;
	private final int employeePerBusinessUnit;
	private final int questionnaireDefinitionsForCompany;
	private final int[] closedQuestionsWeightsForQuestionnaire;
	private final int openQuestionsForQuestionnaire;

	RelationshipSize(int unitPerCompany, int employeePerBusinessUnit, int questionnaireDefinitionsForCompany,
			int[] closedQuestionsWeightsForQuestionnaire, int openQuestionsForQuestionnaire) {
		this.unitPerCompany = unitPerCompany;
		this.employeePerBusinessUnit = employeePerBusinessUnit;
		this.questionnaireDefinitionsForCompany = questionnaireDefinitionsForCompany;
		this.closedQuestionsWeightsForQuestionnaire = closedQuestionsWeightsForQuestionnaire;
		this.openQuestionsForQuestionnaire = openQuestionsForQuestionnaire;
	}

	public int getUnitPerCompany() {
		return unitPerCompany;
	}

	public int getEmployeePerBusinessUnit() {
		return employeePerBusinessUnit;
	}

	public int getQuestionnaireDefinitionsForCompany() {
		return questionnaireDefinitionsForCompany;
	}

	public int[] getClosedQuestionsWeightsForQuestionnaire() {
		return closedQuestionsWeightsForQuestionnaire;
	}

	public int getOpenQuestionsForQuestionnaire() {
		return openQuestionsForQuestionnaire;
	}
}
