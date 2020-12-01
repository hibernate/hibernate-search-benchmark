package org.hibernate.performance.search.model.entity.answer;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.NaturalId;
import org.hibernate.performance.search.model.entity.Employee;
import org.hibernate.performance.search.model.entity.question.ClosedQuestion;
import org.hibernate.performance.search.model.entity.question.OpenQuestion;
import org.hibernate.performance.search.model.entity.question.Question;
import org.hibernate.performance.search.model.entity.question.QuestionnaireDefinition;

@Entity
@IdClass(QuestionnaireInstanceId.class)
public class QuestionnaireInstance {

	public enum EvaluationType {
		SELF, MANAGER, COLLEAGUE, COLLABORATOR
	}

	@Id
	@ManyToOne
	private QuestionnaireDefinition definition;

	@Id
	@ManyToOne
	private Employee approval;

	@Id
	@ManyToOne
	private Employee subject;

	@NaturalId
	private String uniqueCode;

	@Enumerated(EnumType.ORDINAL)
	private EvaluationType evaluationType;

	@OneToMany(mappedBy = "questionnaire")
	@Cascade( CascadeType.PERSIST )
	private List<Answer> answers;

	private QuestionnaireInstance() {
	}

	public QuestionnaireInstance(QuestionnaireDefinition definition, Employee approval, Employee subject,
			EvaluationType evaluationType) {
		this.definition = definition;
		this.approval = approval;
		this.subject = subject;
		this.uniqueCode = new QuestionnaireInstanceId( definition, approval, subject ).getUniqueCode();
		this.evaluationType = evaluationType;

		initAnswers();
	}

	@SuppressWarnings("unchecked")
	private void initAnswers() {
		List<Question> questions = definition.getQuestions();
		answers = new ArrayList<>( questions.size() );

		for ( Question question : questions ) {
			if ( question.isClosed() ) {
				answers.add( new ClosedAnswer( this, (ClosedQuestion) question ) );
			}
			else {
				answers.add( new OpenAnswer( this, (OpenQuestion) question ) );
			}
		}
	}
}
