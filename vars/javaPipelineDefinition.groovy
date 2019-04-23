def call(Map details) {
	pipeline {
		agent {
			label "${details.agentLabel}"
		}

		environment {
			environment = "${details.environment}"
			SONAR_URL = "${details.sonarqube_url}"
			SONAR_LOGIN = "${details.sonarqube_login}"
		}

		options {
			buildDiscarder(logRotator(numToKeepStr: '10'))
			timeout(time: 30, unit:'MINUTES')
			timestamps()
		}

		stages {
			stage('Application Build') {
				steps {
					build(details)
				}
			}
/*
			stage('Test') {
				steps {
					test(details)
				}
			}

			stage('Analysis') {
				steps {
					sonar(details)
				}
			}
*/
			stage('Login on registry') {
				steps {
					loginOnRegistry(details)
				}
			}

			stage('Container build') {
				when {
					expression {
						currentBuild.result == null ||
						currentBuild.result == 'SUCCESS' ||
						currentBuild.result == 'UNSTABLE'
					}
				}
				steps {
					buildContainer(details)
				}
			}

			stage('Deploy') {
				when {
					expression {
						currentBuild.result == null ||
						currentBuild.result == 'SUCCESS' ||
						currentBuild.result == 'UNSTABLE'
					}
				}
				steps {
					deployToRegistry(details)
				}
			}
		}

		post {
			success {
				echo "SUCCESS!"
			}
			failure {
				echo "FAIL!"
			}
		}
	}
}