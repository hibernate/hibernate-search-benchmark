package org.hibernate.performance.search.model.param;

public enum RelationshipSize {

	SMALL( 1, 1, 1, new int[] { 3 }, 1 ),
	MEDIUM( 2, 3, 2, new int[] { 7, 3 }, 2 ),
	LARGE( 10, 10, 10, new int[] { 7, 3, 9, 3, 3, 9, 7, 7, 7, 9 }, 10 );

	private final int unitsPerCompany;
	private final int employeesPerBusinessUnit;
	private final int questionnaireDefinitionsForCompany;
	private final int[] closedQuestionsWeightsForQuestionnaire;
	private final int openQuestionsForQuestionnaire;

	RelationshipSize(int unitsPerCompany, int employeesPerBusinessUnit, int questionnaireDefinitionsForCompany,
			int[] closedQuestionsWeightsForQuestionnaire, int openQuestionsForQuestionnaire) {
		this.unitsPerCompany = unitsPerCompany;
		this.employeesPerBusinessUnit = employeesPerBusinessUnit;
		this.questionnaireDefinitionsForCompany = questionnaireDefinitionsForCompany;
		this.closedQuestionsWeightsForQuestionnaire = closedQuestionsWeightsForQuestionnaire;
		this.openQuestionsForQuestionnaire = openQuestionsForQuestionnaire;
	}

	public int getUnitsPerCompany() {
		return unitsPerCompany;
	}

	public int getEmployeesPerBusinessUnit() {
		return employeesPerBusinessUnit;
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
