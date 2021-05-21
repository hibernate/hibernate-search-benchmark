package org.hibernate.search.benchmark.model.entity.answer;

import java.util.Objects;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.SequenceGenerator;

@MappedSuperclass
public abstract class Answer {

	@Id
	@GeneratedValue(generator = "answer_seq")
	@SequenceGenerator(name = "answer_seq")
	protected Integer id;

	@ManyToOne
	protected QuestionnaireInstance questionnaire;

	Answer() {
		// For Hibernate
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
