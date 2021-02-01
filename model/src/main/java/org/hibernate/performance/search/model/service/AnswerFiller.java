package org.hibernate.performance.search.model.service;

import static org.hibernate.performance.search.model.application.HibernateORMHelper.inTransaction;

import java.util.Random;

import org.hibernate.SessionFactory;
import org.hibernate.performance.search.model.application.ModelService;
import org.hibernate.performance.search.model.entity.answer.ClosedAnswer;
import org.hibernate.performance.search.model.entity.answer.OpenAnswer;

public class AnswerFiller {

	public final static String[] OPEN_ANSWER_RESPONSE_TYPES = {
			"Full text search engines like Apache Lucene are very powerful technologies to add efficient free text search capabilities to applications. However, Lucene suffers several mismatches when dealing with object domain models. Amongst other things indexes have to be kept up to date and mismatches between index structure and domain model as well as query mismatches have to be avoided.",
			// 0
			"Hibernate Search addresses these shortcomings - it indexes your domain model with the help of a few annotations, takes care of database/index synchronization and brings back regular managed objects from free text queries. To achieve this Hibernate Search is combining the power of Hibernate ORM and Apache Lucene/Elasticsearch.",
			// 1
			"In particular, the group IDs changed from org.hibernate to org.hibernate.search, most of the artifact IDs changed to reflect the new mapper/backend design, and the Lucene integration now requires an explicit dependency instead of being available by default. Read Dependencies for more information.",
			// 2
			"One thing to keep in mind, though: Spring Boot automatically sets the version of dependencies without your knowledge. While this is ordinarily a good thing, from time to time Spring Boot dependencies will be a little out of date. Thus, it is recommended to override Spring Boot’s defaults at least for some key dependencies.",
			// 3
			"If, after setting the properties above, you still have problems (e.g. NoClassDefFoundError) with some of Hibernate Search’s dependencies, look for the version of that dependency in Spring Boot’s POM and Hibernate Search’s POM: there will probably be a mistmatch, and generally overriding Spring Boot’s version to match Hibernate Search’s version will work fine.",
			// 4
			"To make these entities searchable, you will need to map them to an index structure. The mapping can be defined using annotations, or using a programmatic API; this getting started guide will show you a simple annotation mapping. For more details, refer to Mapping Hibernate ORM entities to indexes.",
			// 5
			"Here, the Author class defines a single indexed field, name. Thus adding @IndexedEmbedded to the authors property of Book will add a single field named authors.name to the Book index. This field will be populated automatically based on the content of the authors property, and the books will be reindexed automatically whenever the name property of their author changes. See Mapping associated elements with @IndexedEmbedded for more information.",
			// 6
			"The following code will prepare a search query targeting the index for the Book entity, filtering the results so that at least one field among title and authors.name contains the string refactoring. The matches are implicitly on words (\"tokens\") instead of the full string, and are case-insensitive: that’s because the targeted fields are full-text fields.",
			// 7
	};
	public static final int BATCH_SIZE = 100;

	private final ModelService modelService;
	private final SessionFactory sessionFactory;
	private final Random random;

	public AnswerFiller(ModelService modelService, SessionFactory sessionFactory) {
		this.modelService = modelService;
		this.sessionFactory = sessionFactory;
		this.random = new Random( 739 ); // fixed seed so all the tests will have the same results
	}

	public void fillAllAnswers(Integer companyId) {
		inTransaction( sessionFactory, session -> {
			EmployeeRepository repository = new EmployeeRepository( session );
			try ( Scroll<ClosedAnswer> closedAnswers = repository.findAllClosedAnswersInNaturalOrder( companyId, BATCH_SIZE ) ) {
				while ( closedAnswers.hasNext() ) {
					for ( ClosedAnswer answer : closedAnswers.next() ) {
						answer.setChoice( random.nextInt( 8 ) );
					}
					modelService.flushOrmAndIndexesAndClear( session );
				}
			}
			try ( Scroll<OpenAnswer> openAnswers = repository.findAllOpenAnswersInNaturalOrder( companyId, BATCH_SIZE ) ) {
				while ( openAnswers.hasNext() ) {
					for ( OpenAnswer answer : openAnswers.next() ) {
						answer.setText( OPEN_ANSWER_RESPONSE_TYPES[random.nextInt( 8 )] );
					}
					modelService.flushOrmAndIndexesAndClear( session );
				}
			}
		} );
	}

}
