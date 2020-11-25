package org.hibernate.performance.search.entity.answer;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.hibernate.performance.search.entity.question.ClosedQuestion;

@Entity
public class ClosedAnswer extends Answer {

	@ManyToOne
	private ClosedQuestion question;

	private Integer choice;

}
