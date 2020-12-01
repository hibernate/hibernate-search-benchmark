package org.hibernate.performance.search.model.entity.answer;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.hibernate.performance.search.model.entity.question.ClosedQuestion;

@Entity
public class ClosedAnswer extends Answer {

	@ManyToOne
	private ClosedQuestion question;

	private Integer choice;

	private ClosedAnswer() {
	}

	public ClosedAnswer(QuestionnaireInstance questionnaire, ClosedQuestion question) {
		super( questionnaire );
		this.question = question;
	}

	// set later at questionnaire compilation time
	public void setChoice(Integer choice) {
		this.choice = choice;
	}
}
