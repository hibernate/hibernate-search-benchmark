pipeline {
    agent any
    tools {
        maven "Apache Maven 3.6"
        jdk "OpenJDK 11 Latest"
    }
    stages {
        stage('Initialize') {
            steps {
                // I don't find ES_AWS_71_ENDPOINT in the env, so I'm using the 78
                sh '''
                    echo "PATH = ${PATH}"
                    echo "M2_HOME = ${M2_HOME}"
                    echo "ES_AWS_78_ENDPOINT = ${ES_AWS_78_ENDPOINT}"
                    echo "ES_AWS_REGION" = ${ES_AWS_REGION}
                '''
            }
        }
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Build') {
            steps {
                sh """ \
					mvn clean install \
					-U -pl jmh-elasticsearch -am \
					-DskipTests -Ddocker.skip -Dtest.elasticsearch.run.skip=true \
			"""
                dir('jmh-elasticsearch/target') {
                    stash name: 'jar', includes: 'benchmarks.jar'
                }
            }
        }
        stage('Performance test') {
            steps {
                lock(resource: 'es-aws-78') {
                    unstash name: 'jar'
                    sh 'docker run --name postgresql -p 5431:5432 -e POSTGRES_USER=username -e POSTGRES_PASSWORD=password -e POSTGRES_DB=database -d postgres:10.5'
                    sh 'mkdir -p output'
                    sh """ \
					java \
					-jar benchmarks.jar \
					-jvmArgsAppend -Dhosts=vpc-ci-elasticsearch-78-r5-membawptnueqlpmzkr677lwzoa.us-east-1.es.amazonaws.com \
                    -jvmArgsAppend -Dprotocol=https \
                    -jvmArgsAppend -Daws.signing.enabled=true \
                    -jvmArgsAppend -Daws.region=$ES_AWS_REGION \
                    -jvmArgsAppend -Daws.credentials.type=static \
                    -jvmArgsAppend -Daws.credentials.access_key_id=$AWS_ACCESS_KEY_ID \
                    -jvmArgsAppend -Daws.credentials.secret_access_key=$AWS_SECRET_ACCESS_KEY \
					-wi 1 -i 10 \
					-rff output/benchmark-results-search6-elasticsearch.csv \
			"""
                    sh 'docker stop postgresql'
                    sh 'docker rm -f postgresql'
                    archiveArtifacts artifacts: 'output/**'
                }
            }
        }
    }
}