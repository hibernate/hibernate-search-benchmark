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
					mvn clean install -P search5 \
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
                sh 'docker stop postgresql || true && docker rm -f postgresql || true' // stop and remove any old container
                sh 'docker run --name postgresql -p 5431:5432 -e POSTGRES_USER=username -e POSTGRES_PASSWORD=password -e POSTGRES_DB=database -d postgres:10.5'
                sleep(time:10,unit:"SECONDS") // wait for postgres to be ready
                sh 'mkdir -p output'
                sh """ \
					java \
					-jar benchmarks.jar \
					-wi 1 -i 3 \
					-rff output/benchmark-results-search5-lucene.csv \
			"""
                sh 'docker stop postgresql'
                sh 'docker rm -f postgresql'
                archiveArtifacts artifacts: 'output/**'
            }
        }
    }
}