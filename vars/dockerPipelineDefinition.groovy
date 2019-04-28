def call(Map details) {
	pipeline {
		agent {
			label "${details.agentLabel}"
		}

		environment {
			environment = "${details.environment}"
		}

		triggers {
			GenericTrigger(
				genericVariables: [
					[key: 'ref', value: '$.ref'],
					[key: 'authorName', value: '$.commmits[0].author.name'],
					[key: 'authorEmail', value: '$.commmits[0].author.email'],
					[key: 'repository', regexpFilter: '[^a-z_-]', value: '$.repository.name']
				],
				causeString: 'Commit to $ref by $authorName<$authorEmail> on $repository',
				printContributedVariables: true,
				regexpFilterExpression: '$repository/$ref',
     			regexpFilterText: '$repository/refs/heads/' + env.JOB_BASE_NAME,
			)
		}

		options {
			buildDiscarder(logRotator(numToKeepStr: '10'))
			timeout(time: 30, unit:'MINUTES')
			timestamps()
		}

		stages {
			stage('Login') {
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