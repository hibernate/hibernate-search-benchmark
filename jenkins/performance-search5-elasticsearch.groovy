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
					mvn clean install -P search5 \
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
                    lock('es-aws-56') {
                        sh """ \
							java \
							-jar benchmarks.jar \
							-jvmArgsAppend -Dhibernate.search.default.elasticsearch.host=$ES_AWS_56_ENDPOINT \
							-jvmArgsAppend -Dhibernate.search.default.elasticsearch.required_index_status=yellow \
							-jvmArgsAppend -Dhibernate.search.default.elasticsearch.aws.signing.enabled=true \
							-jvmArgsAppend -Dhibernate.search.default.elasticsearch.aws.region=$ES_AWS_REGION \
							-jvmArgsAppend -Dhibernate.search.default.elasticsearch.aws.access_key=$AWS_ACCESS_KEY_ID \
							-jvmArgsAppend -Dhibernate.search.default.elasticsearch.aws.secret_key=$AWS_SECRET_ACCESS_KEY \
							-wi 1 -i 10 \
							-rff output/benchmark-results-search5-elasticsearch.csv \
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