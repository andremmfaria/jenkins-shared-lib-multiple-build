def call(Map details) {
	pipeline {
		agent {
			label "${details.agentLabel}"
		}

		environment {
			environment = "${details.environment}"
      CONTAINER_REG = credentials('CONTAINER_REG_CREDENTIALS')  
		}

    triggers{
      cron('H 2 * * *')
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
