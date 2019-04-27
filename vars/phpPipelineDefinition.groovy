def call(Map details) {
	pipeline {
		agent {
			label "${details.agentLabel}"
		}

		environment {
			environment = "${details.environment}"
		}

		options {
			buildDiscarder(logRotator(numToKeepStr: '10'))
			timeout(time: 30, unit:'MINUTES')
			timestamps()
		}

		stages {
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

			stage('Container push to registry') {
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