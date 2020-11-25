package org.hibernate.performance.search.entity.question;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.hibernate.performance.search.entity.IdEntity;

@Entity
public class Question extends IdEntity {

	@ManyToOne
	private QuestionnaireDefinition questionnaire;

	private String text;

}
