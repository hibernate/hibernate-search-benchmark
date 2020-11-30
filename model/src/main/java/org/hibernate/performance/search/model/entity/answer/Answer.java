package org.hibernate.performance.search.model.entity.answer;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.hibernate.performance.search.model.entity.IdEntity;

@Entity
public class Answer extends IdEntity {

	@ManyToOne
	private QuestionnaireInstance questionnaire;

}
