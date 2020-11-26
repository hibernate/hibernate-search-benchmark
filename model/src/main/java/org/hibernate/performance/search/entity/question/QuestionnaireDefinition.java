package org.hibernate.performance.search.entity.question;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.hibernate.performance.search.entity.IdEntity;

@Entity
public class QuestionnaireDefinition extends IdEntity {

	private String title;

	private String description;

	private Integer year;

	@OneToMany(mappedBy = "questionnaire")
	private List<Question> questions = new ArrayList<>();

}
