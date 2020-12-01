package org.hibernate.performance.search.model.entity.question;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.performance.search.model.entity.Company;
import org.hibernate.performance.search.model.entity.IdEntity;

@Entity
public class QuestionnaireDefinition extends IdEntity {

	private String title;

	private String description;

	private Integer year;

	@ManyToOne
	private Company company;

	@OneToMany(mappedBy = "questionnaire")
	@Cascade( CascadeType.PERSIST )
	private List<Question> questions;

	private QuestionnaireDefinition() {
	}

	public QuestionnaireDefinition(Integer id, String title, String description, Integer year, Company company) {
		super( id );
		this.title = title;
		this.description = description;
		this.year = year;
		this.company = company;
	}

	public String getTitle() {
		return title;
	}

	public void setQuestions(List<Question> questions) {
		this.questions = questions;
	}
}
