pipeline {
    agent any
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
					-DskipTests \
			"""
                dir('jmh-lucene/target') {
                    stash name: 'jar', includes: 'benchmarks.jar'
                }
            }
        }
        stage('Performance test') {
            steps {
                unstash name: 'jar'
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
}