package org.hibernate.performance.search.entity.answer;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.hibernate.performance.search.entity.question.OpenQuestion;

@Entity
public class OpenAnswer extends Answer {

	@ManyToOne
	private OpenQuestion question;

	private String text;

}
