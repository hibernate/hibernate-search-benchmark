pipeline {
    agent {
        label 'Performance'
    }
    tools {
        maven 'Apache Maven 3.8'
        jdk 'OpenJDK 11 Latest'
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
                    mvn clean install -Dhsearch.version=5 \
                    -U -pl jmh-elasticsearch -am \
                    -DskipTests \
                """
                dir ('jmh-elasticsearch/target') {
                    stash name:'jar', includes:'benchmarks.jar'
                }
            }
        }
        stage('Performance test') {
            steps {
                unstash name: 'jar'
                sh 'jenkins/docker-prune.sh'
                sh 'docker run --rm=true --name elasticsearch-search5 -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" -e "xpack.security.enabled=false" -d docker.elastic.co/elasticsearch/elasticsearch-oss:5.6.16'
                sh 'docker run --rm=true --name postgresql-search5-elasticsearch -p 5431:5432 -e POSTGRES_USER=username -e POSTGRES_PASSWORD=password -e POSTGRES_DB=database -d postgres:13.3'
                sleep(time:10,unit:"SECONDS") // wait for postgres to be ready
                sh 'mkdir -p output'

                sh """ \
                    java \
                    -jar benchmarks.jar \
                    -wi 1 -i 10 \
                    -rff output/benchmark-results-search5-elasticsearch.csv \
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