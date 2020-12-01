package org.hibernate.performance.search.model.service;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.performance.search.model.entity.Employee;
import org.hibernate.performance.search.model.entity.Manager;
import org.hibernate.performance.search.model.entity.answer.QuestionnaireInstance;
import org.hibernate.performance.search.model.entity.question.QuestionnaireDefinition;

public final class QuestionnaireInstanceFactory {

	private QuestionnaireInstanceFactory() {
	}

	public static List<QuestionnaireInstance> createQuestionnaireInstances(Employee approval,
			QuestionnaireDefinition definition) {
		ArrayList<QuestionnaireInstance> result = new ArrayList<>();

		// create EvaluationType.MANAGER instance
		Manager manager = approval.getManager();
		if ( manager != null ) {
			result.add( new QuestionnaireInstance( definition, approval, manager,
					QuestionnaireInstance.EvaluationType.MANAGER
			) );
		}

		// create EvaluationType.COLLABORATOR instances
		List<Employee> collaborators = approval.getCollaborators();
		for ( Employee collaborator : collaborators ) {
			result.add( new QuestionnaireInstance( definition, approval, collaborator,
					QuestionnaireInstance.EvaluationType.COLLABORATOR
			) );
		}

		// create EvaluationType.SELF and .COLLEAGUE instances
		List<Employee> autoAndColleagues = approval.getSelfAndColleagues();
		for ( Employee employee : autoAndColleagues ) {
			result.add( new QuestionnaireInstance( definition, approval, employee,
					( approval.equals( employee ) ) ? QuestionnaireInstance.EvaluationType.SELF :
							QuestionnaireInstance.EvaluationType.COLLEAGUE
			) );
		}

		return result;
	}
}
