package org.hibernate.performance.search.model.entity.question;

import javax.persistence.Entity;

@Entity
public class ClosedQuestion extends Question {

	private Integer weight;

	ClosedQuestion() {
		// For Hibernate
	}

	public ClosedQuestion(Integer id, QuestionnaireDefinition questionnaire, String text, Integer weight) {
		super( id, questionnaire, text );
		this.weight = weight;
	}

	@Override
	public boolean isClosed() {
		return true;
	}

	public Integer getWeight() {
		return weight;
	}
}
