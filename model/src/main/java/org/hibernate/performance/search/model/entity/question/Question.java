package org.hibernate.performance.search.model.entity.question;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.hibernate.performance.search.model.entity.IdEntity;

@Entity
public class Question extends IdEntity {

	@ManyToOne
	private QuestionnaireDefinition questionnaire;

	private String text;

}
