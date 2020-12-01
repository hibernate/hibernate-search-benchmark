package org.hibernate.performance.search.model.entity.answer;

import java.io.Serializable;
import java.util.Objects;

import org.hibernate.performance.search.model.entity.Employee;
import org.hibernate.performance.search.model.entity.question.QuestionnaireDefinition;

public class QuestionnaireInstanceId implements Serializable {

	private QuestionnaireDefinition definition;

	private Employee approval;

	private Employee subject;

	public QuestionnaireInstanceId(
			QuestionnaireDefinition definition, Employee approval,
			Employee subject) {
		this.definition = definition;
		this.approval = approval;
		this.subject = subject;
	}

	private QuestionnaireInstanceId() {
	}

	@Override
	public boolean equals(Object o) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}
		QuestionnaireInstanceId that = (QuestionnaireInstanceId) o;
		return Objects.equals( definition, that.definition ) &&
				Objects.equals( approval, that.approval ) &&
				Objects.equals( subject, that.subject );
	}

	@Override
	public int hashCode() {
		return Objects.hash( definition, approval, subject );
	}

	public String getUniqueCode() {
		return definition.getId() + ":" + approval.getId() + ":" + subject.getId();
	}
}
