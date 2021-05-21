pipeline {
    agent none
    tools {
        maven 'Apache Maven 3.8'
        jdk 'OpenJDK 11 Latest'
    }
    stages {
        stage('Build and test') {
            matrix {
                agent {
                    label 'Worker'
                }
                axes {
                    axis {
                        name 'HSEARCH_VERSION'
                        values '5', '6'
                    }
                }
                stages {
                    stage('Build and test') {
                        steps {
                            checkout scm
                            sh 'jenkins/docker-prune.sh'
                            sh """ \
                                mvn clean install -U -Dhsearch.version=${HSEARCH_VERSION}
                            """
                        }
                    }
                }
                post {
                    always {
                        sh 'jenkins/docker-prune.sh'
                    }
                }
            }
        }
    }
}