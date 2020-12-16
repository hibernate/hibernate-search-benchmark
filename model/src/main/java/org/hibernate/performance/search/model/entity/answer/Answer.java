package org.hibernate.performance.search.model.entity.answer;

import java.util.Objects;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class Answer {

	@Id
	@GeneratedValue
	protected Integer id;

	@ManyToOne
	protected QuestionnaireInstance questionnaire;

	protected Answer() {
	}

	public Answer(QuestionnaireInstance questionnaire) {
		this.questionnaire = questionnaire;
	}

	public Integer getId() {
		return id;
	}

	public QuestionnaireInstance getQuestionnaire() {
		return questionnaire;
	}

	@Override
	public boolean equals(Object o) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}
		Answer answer = (Answer) o;
		return Objects.equals( id, answer.id );
	}

	@Override
	public int hashCode() {
		return Objects.hash( id );
	}
}
