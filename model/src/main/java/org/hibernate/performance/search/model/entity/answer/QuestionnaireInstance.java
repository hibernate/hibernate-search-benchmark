package org.hibernate.performance.search.model.entity.answer;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
public class QuestionnaireInstance {

	public enum EvaluationType {
		SELF, MANAGER, COLLEAGUE, COLLABORATOR
	}

	@EmbeddedId
	private QuestionnaireInstanceId id;

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
		this.id = new QuestionnaireInstanceId( definition, approval, subject );
		this.uniqueCode = id.getUniqueCode();
		this.evaluationType = evaluationType;

		initAnswers();
	}

	public Employee getSubject() {
		return id.getSubject();
	}

	public Integer getYear() {
		return id.getDefinition().getYear();
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
		List<Question> questions = id.getDefinition().getQuestions();
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
