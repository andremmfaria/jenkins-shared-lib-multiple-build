def call(Map details) {
	pipeline {
		agent {
			label "${details.agentLabel}"
		}

		environment {
      BASH_ENV = "${details.bashEnv}"
			environment = "${details.environment}"
     	CONTAINER_REG = credentials('CONTAINER_REG_CREDENTIALS')  
     	BINARY_REPO = credentials('BINARY_REPO_CREDENTIALS')  
     	SONAR_CRED = credentials('SONAR_CREDENTIALS')  
		}

		options {
			buildDiscarder(logRotator(numToKeepStr: '10'))
			timeout(time: 15, unit:'MINUTES')
			timestamps()
			office365ConnectorWebhooks(
				[
					[
						name: "${details.msTeamsName}", 
						notifyAborted: true, 
						notifyBackToNormal: true, 
						notifyFailure: true, 
						notifyNotBuilt: true, 
						notifyRepeatedFailure: true, 
						notifySuccess: true, 
						notifyUnstable: true, 
						startNotification: true, 
						url: "${details.msTeamsUrl}"
					]
				]
			)
		}

		stages {
			stage('Login on registry') {
				steps {
					logOnRegistry(details)
				}
			}
			
			stage('Build application') {
				steps {
					buildApplication(details)
				}
			}

			stage('Static analysis') {
				steps {
					staticScan(details)
				}
			}
			
     	stage('Build container') {
				steps {
					buildContainer(details)
				}
			}

			stage('Push to registry') {
				steps {
					pushToRegistry(details)
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
