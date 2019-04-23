def call(def details) {
  try {
  	echo "[SONAR] SONAR Analysis"

    def sonarParam = [:]

    sonarParam.'sonarServer' = "${details.sonarServer}"
    sonarParam.'engine' = "${details.engine}"
    sonarParam.'sonarProjectKey' = "${details.sonarProjectKey}"
    sonarParam.'buildNumber' = "${details.buildNumber}"
    sonarParam.'application' = "${details.application}"
    sonarParam.'sonarSources' = "${details.sonarSources}"
    sonarParam.'sonarExclusions' = "${details.sonarExclusions}"
    sonarParam.'testDir' = "${details.testDir}"
    sonarParam.'buildDir' = "${details.buildDir}"
    sonarParam.'root' = "${details.root}"
    sonarParam.'environment' = "${details.environment}"
    sonarParam.'branch' = "${details.branch}"

    echo "sonarServer...................: ${sonarParam.sonarServer}"
    echo "engine........................: ${sonarParam.engine}"
    echo "application...................: ${sonarParam.application}"
    echo "sonarProjectKey...............: ${sonarParam.sonarProjectKey}"
    echo "buildNumber...................: ${sonarParam.buildNumber}"
    echo "root..........................: ${sonarParam.root}"
    echo "buildDir......................: ${sonarParam.buildDir}"
    echo "sonarSources..................: ${sonarParam.sonarSources}"
    echo "testDir.......................: ${sonarParam.testDir}"
    echo "sonarExclusions...............: ${sonarParam.sonarExclusions}"

    withSonarQubeEnv("${sonarParam.sonarServer}") {
      if ( "${sonarParam.engine}" == 'java' ) {
        echo "[SONAR] Java SONAR SCANNER"
  			sh "gradle --info sonarqube \
          -Dsonar.projectKey=${sonarParam.sonarProjectKey} \
          -Dsonar.projectVersion=${sonarParam.buildNumber} \
          -Dsonar.exclusions=${sonarParam.sonarExclusions}"
      }
      else {
        error( "Compiler not found: ${sonarParam.compiler}")
      }
    }
  } catch (Exception e) {
    echo "Problemas ao executar o sonar-scanner: ${e.message}"
    currentBuild.result = 'UNSTABLE'
  }
}