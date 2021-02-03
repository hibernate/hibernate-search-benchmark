pipeline {
    agent {
        label 'Performance'
    }
    tools {
        maven "Apache Maven 3.6"
        jdk "OpenJDK 11 Latest"
    }
    stages {
        stage('Initialize') {
            steps {
                sh '''
                    echo "PATH = ${PATH}"
                    echo "M2_HOME = ${M2_HOME}"
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
					-U -pl jmh-lucene -am \
					-DskipTests -Ddocker.skip \
			    """
                dir('jmh-lucene/target') {
                    stash name: 'jar', includes: 'benchmarks.jar'
                }
            }
        }
        stage('Performance test') {
            steps {
                unstash name: 'jar'
                sh 'jenkins/docker-prune.sh'
                sh 'docker run --rm=true --name postgresql-search6-lucene -p 5431:5432 -e POSTGRES_USER=username -e POSTGRES_PASSWORD=password -e POSTGRES_DB=database -d postgres:10.5'
                sleep(time:10,unit:"SECONDS") // wait for postgres to be ready
                sh 'mkdir -p output'
                sh """ \
					java \
					-jar benchmarks.jar \
					-wi 1 -i 10 \
					-rff output/benchmark-results-search6-lucene.csv \
				"""
                archiveArtifacts artifacts: 'output/**'
            }
        }
    }
    post {
        always {
            sh 'jenkins/docker-prune.sh'
        }
    }
}