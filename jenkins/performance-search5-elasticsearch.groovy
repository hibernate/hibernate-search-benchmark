/**
 * Pipeline inspired from https://github.com/hibernate/hibernate-search jenkins/performance-elasticsearch.groovy
 */
import groovy.transform.Field

/*
 * See https://github.com/hibernate/hibernate-jenkins-pipeline-helpers
 */
@Library('hibernate-jenkins-pipeline-helpers@1.2')
import org.hibernate.jenkins.pipeline.helpers.job.JobHelper

@Field final String MAVEN_TOOL = 'Apache Maven 3.6'
@Field final String JDK_TOOL = 'OpenJDK 11 Latest'

// Performance node pattern, to be used for stages involving performance tests.
@Field final String PERFORMANCE_NODE_PATTERN = 'Performance'
// Quick-use node pattern, to be used for very light, quick, and environment-independent stages,
// such as sending a notification. May include the master node in particular.
@Field final String QUICK_USE_NODE_PATTERN = 'Master||Slave||Performance'

@Field JobHelper helper

@Field EsAwsBuildEnvironment esAwsBuildEnv = new EsAwsBuildEnvironment(version: "5.6")

this.helper = new JobHelper(this)

helper.runWithNotification {

stage('Configure') {
	helper.configure {
		configurationNodePattern QUICK_USE_NODE_PATTERN
		file 'job-configuration.yaml'
		jdk {
			defaultTool JDK_TOOL
		}
		maven {
			defaultTool MAVEN_TOOL
			producedArtifactPattern "org/hibernate/search/*"
		}
	}

	properties([
			pipelineTriggers(
					[
							issueCommentTrigger('.*test Elasticsearch performance please.*')
					]
			),
			helper.generateNotificationProperty()
	])

	esAwsBuildEnv.endpointUris = env.getProperty(esAwsBuildEnv.endpointVariableName)
	if (!esAwsBuildEnv.endpointUris) {
		throw new IllegalStateException(
				"Cannot run performance test because environment variable '$esAwsBuildEnv.endpointVariableName' is not defined."
		)
	}
	esAwsBuildEnv.awsRegion = env.ES_AWS_REGION
	if (!esAwsBuildEnv.awsRegion) {
		throw new IllegalStateException(
				"Cannot run performance test because environment variable 'ES_AWS_REGION' is not defined."
		)
	}
}

lock(label: esAwsBuildEnv.lockedResourcesLabel) {
	node ('Performance') {
		stage ('Checkout') {
			checkout scm
		}

		stage ('Build') {
			helper.withMavenWorkspace {
				sh """ \
					mvn clean install -P search5 \
					-U -pl jmh-elasticsearch -am \
					-DskipTests -Ddocker.skip -Dtest.elasticsearch.run.skip=true \
			"""
				dir ('jmh-elasticsearch/target') {
					stash name:'jar', includes:'benchmarks.jar'
				}
			}
		}

		stage ('Performance test') {
			def awsCredentialsId = helper.configuration.file?.aws?.credentials
			if (!awsCredentialsId) {
				throw new IllegalStateException("Missing AWS credentials")
			}
			withCredentials([[$class: 'AmazonWebServicesCredentialsBinding',
							  credentialsId   : awsCredentialsId,
							  usernameVariable: 'AWS_ACCESS_KEY_ID',
							  passwordVariable: 'AWS_SECRET_ACCESS_KEY'
			]]) {
				helper.withMavenWorkspace { // Mainly to set the default JDK
					unstash name:'jar'
					sh 'docker stop postgresql || true && docker rm -f postgresql || true' // stop and remove any old container
					sh 'docker run --name postgresql -p 5431:5432 -e POSTGRES_USER=username -e POSTGRES_PASSWORD=password -e POSTGRES_DB=database -d postgres:10.5'
					sleep(time:10,unit:"SECONDS") // wait for postgres to be ready
					sh 'mkdir output'
					sh """ \
							java \
							-jar benchmarks.jar \
							-jvmArgsAppend -Dhibernate.search.default.elasticsearch.host=$esAwsBuildEnv.endpointUris \
							-jvmArgsAppend -Dhibernate.search.default.elasticsearch.required_index_status=yellow \
							-jvmArgsAppend -Dhibernate.search.default.elasticsearch.aws.signing.enabled=true \
							-jvmArgsAppend -Dhibernate.search.default.elasticsearch.aws.region=$esAwsBuildEnv.awsRegion \
							-jvmArgsAppend -Dhibernate.search.default.elasticsearch.aws.access_key=$AWS_ACCESS_KEY_ID \
							-jvmArgsAppend -Dhibernate.search.default.elasticsearch.aws.secret_key=$AWS_SECRET_ACCESS_KEY \
							-wi 1 -i 10 \
							-rff output/benchmark-results-search5-elasticsearch.csv \
					"""
				}
			}
			sh 'docker stop postgresql'
			sh 'docker rm -f postgresql'
			archiveArtifacts artifacts: 'output/**'
		}
	}
}

} // End of helper.runWithNotification

class EsAwsBuildEnvironment {
	String version
	String endpointUris = null
	String awsRegion = null
	String getNameEmbeddableVersion() {
		version.replaceAll('\\.', '')
	}
	String getEndpointVariableName() {
		"ES_AWS_${nameEmbeddableVersion}_ENDPOINT"
	}
	String getLockedResourcesLabel() {
		"es-aws-${nameEmbeddableVersion}"
	}
}