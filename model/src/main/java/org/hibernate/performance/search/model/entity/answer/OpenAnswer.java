package org.hibernate.performance.search.model.entity.answer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.hibernate.performance.search.model.entity.question.OpenQuestion;

@Entity
public class OpenAnswer extends Answer {

	@ManyToOne
	private OpenQuestion question;

	@Column(name = "text", columnDefinition = "LONGTEXT")
	private String text;

	private OpenAnswer() {
	}

	public OpenAnswer(QuestionnaireInstance questionnaire, OpenQuestion question) {
		super( questionnaire );
		this.question = question;
	}

	public QuestionnaireInstance getQuestionnaire() {
		return questionnaire;
	}

	// set later at questionnaire compilation time
	public void setText(String text) {
		this.text = text;
	}
}
