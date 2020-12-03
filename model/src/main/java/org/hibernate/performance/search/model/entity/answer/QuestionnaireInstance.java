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
	@Cascade(CascadeType.PERSIST)
	private List<ClosedAnswer> closedAnswers;

	@OneToMany(mappedBy = "questionnaire")
	@Cascade(CascadeType.PERSIST)
	private List<OpenAnswer> openAnswers;

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

	public QuestionnaireInstanceId getId() {
		return new QuestionnaireInstanceId( definition, approval, subject );
	}

	public Employee getSubject() {
		return subject;
	}

	public Integer getYear() {
		return definition.getYear();
	}

	public int getMaxScore() {
		int result = 0;
		for ( ClosedAnswer answer : closedAnswers ) {
			result += answer.getMaxScore();
		}
		return result;
	}

	public int getScore() {
		int result = 0;
		for ( ClosedAnswer answer : closedAnswers ) {
			result += answer.getScore();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private void initAnswers() {
		List<Question> questions = definition.getQuestions();
		closedAnswers = new ArrayList<>( questions.size() );
		openAnswers = new ArrayList<>( questions.size() );

		for ( Question question : questions ) {
			if ( question.isClosed() ) {
				closedAnswers.add( new ClosedAnswer( this, (ClosedQuestion) question ) );
			}
			else {
				openAnswers.add( new OpenAnswer( this, (OpenQuestion) question ) );
			}
		}
	}
}
