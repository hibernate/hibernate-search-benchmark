package org.hibernate.performance.search.model.entity.answer;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.hibernate.performance.search.model.entity.question.OpenQuestion;

@Entity
public class OpenAnswer extends Answer {

	@ManyToOne
	private OpenQuestion question;

	private String text;

}
