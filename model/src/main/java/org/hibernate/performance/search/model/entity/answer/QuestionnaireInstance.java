package org.hibernate.performance.search.model.entity.answer;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.performance.search.model.entity.Employee;
import org.hibernate.performance.search.model.entity.IdEntity;
import org.hibernate.performance.search.model.entity.question.QuestionnaireDefinition;

@Entity
public class QuestionnaireInstance extends IdEntity {

	@ManyToOne
	private QuestionnaireDefinition definition;

	@ManyToOne
	private Employee approval;

	@ManyToOne
	private Employee subject;

	@OneToMany(mappedBy = "questionnaire")
	private List<Answer> answers = new ArrayList<>();

}
