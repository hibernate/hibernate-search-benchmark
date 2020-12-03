package org.hibernate.performance.search.model.entity.answer;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.hibernate.performance.search.model.entity.question.ClosedQuestion;

@Entity
public class ClosedAnswer extends Answer {

	private final static int MAX_VALUE = 7;

	@ManyToOne
	private ClosedQuestion question;

	private Integer choice;

	private ClosedAnswer() {
	}

	public ClosedAnswer(QuestionnaireInstance questionnaire, ClosedQuestion question) {
		super( questionnaire );
		this.question = question;
	}

	public int getMaxScore() {
		return question.getWeight() * MAX_VALUE;
	}

	public int getScore() {
		return ( choice == null ) ? 0 : choice * question.getWeight();
	}

	// set later at questionnaire compilation time
	public void setChoice(Integer choice) {
		if ( choice < 0 || choice > MAX_VALUE ) {
			throw new RuntimeException( "Wrong selection. Min:0 (included) - Max:7 (included)." );
		}
		this.choice = choice;
	}
}
