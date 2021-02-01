pipeline {
    agent {
        label 'Worker'
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
        stage('Search 6 build') {
            steps {
                sh """ \
                    mvn clean install -U \
                    -DskipTests -Ddocker.skip -Dtest.elasticsearch.run.skip=true \
                """
            }
        }
        stage('Search 6 integration test') {
            steps {
                sh 'mvn verify -pl jmh-lucene'
                sh 'mvn verify -pl jmh-elasticsearch'
            }
        }
        stage('Search 5 build') {
            steps {
                sh """ \
                    mvn clean install -U -P search5 \
                    -DskipTests -Ddocker.skip -Dtest.elasticsearch.run.skip=true \
                """
            }
        }
        stage('Search 5 integration test') {
            steps {
                sh 'mvn verify -pl jmh-lucene'
                sh 'mvn verify -pl jmh-elasticsearch'
            }
        }
    }
}