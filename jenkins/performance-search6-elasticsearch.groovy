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
					-U -pl jmh-elasticsearch -am \
					-DskipTests -Ddocker.skip -Dtest.elasticsearch.run.skip=true \
			"""
                dir ('jmh-elasticsearch/target') {
                    stash name:'jar', includes:'benchmarks.jar'
                }
            }
        }
        stage('Performance test') {
            steps {
                unstash name: 'jar'
                sh 'docker run --name postgresql -p 5431:5432 -e POSTGRES_USER=username -e POSTGRES_PASSWORD=password -e POSTGRES_DB=database -d postgres:10.5'
                sleep(time:10,unit:"SECONDS") // wait for postgres to be ready
                sh 'mkdir -p output'

                withCredentials([[$class: 'AmazonWebServicesCredentialsBinding',
                                  credentialsId   : 'JenkinsSlaveServicesConsumer.amazonaws.com',
                                  usernameVariable: 'AWS_ACCESS_KEY_ID',
                                  passwordVariable: 'AWS_SECRET_ACCESS_KEY'
                                 ]]) {
                    lock('es-aws-78') {
                        sh """ \
							java \
							-jar benchmarks.jar \
							-jvmArgsAppend -Dhibernate.search.backend.uris=$ES_AWS_78_ENDPOINT \
							-jvmArgsAppend -Dhibernate.search.backend.aws.signing.enabled=true \
							-jvmArgsAppend -Dhibernate.search.backend.aws.region=$ES_AWS_REGION \
							-jvmArgsAppend -Dhibernate.search.backend.aws.credentials.type=static \
							-jvmArgsAppend -Dhibernate.search.backend.aws.credentials.access_key_id=$AWS_ACCESS_KEY_ID \
							-jvmArgsAppend -Dhibernate.search.backend.aws.credentials.secret_access_key=$AWS_SECRET_ACCESS_KEY \
							-wi 1 -i 3 \
							-rff output/benchmark-results-search6-elasticsearch.csv \
					    """
                    }
                }

                archiveArtifacts artifacts: 'output/**'
            }
        }
    }
    post {
        always {
            // stop and remove any created container
            sh 'docker stop postgresql || true && docker rm -f postgresql || true'
        }
    }
}