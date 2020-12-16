package org.hibernate.performance.search.model.entity.question;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.hibernate.performance.search.model.entity.IdEntity;

@Entity
public abstract class Question extends IdEntity {

	@ManyToOne
	private QuestionnaireDefinition questionnaire;

	private String text;

	protected Question() {
	}

	protected Question(Integer id, QuestionnaireDefinition questionnaire, String text) {
		super( id );
		this.questionnaire = questionnaire;
		this.text = text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public abstract boolean isClosed();
}
