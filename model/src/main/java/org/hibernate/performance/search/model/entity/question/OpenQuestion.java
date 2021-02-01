package org.hibernate.performance.search.model.entity.question;

import javax.persistence.Entity;

@Entity
public class OpenQuestion extends Question {

	OpenQuestion() {
		// For Hibernate
	}

	public OpenQuestion(Integer id, QuestionnaireDefinition questionnaire, String text) {
		super( id, questionnaire, text );
	}

	@Override
	public boolean isClosed() {
		return false;
	}
}
