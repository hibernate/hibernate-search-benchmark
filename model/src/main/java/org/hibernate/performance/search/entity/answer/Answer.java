package org.hibernate.performance.search.entity.answer;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.hibernate.performance.search.entity.IdEntity;

@Entity
public class Answer extends IdEntity {

	@ManyToOne
	private QuestionnaireInstance questionnaire;

}
