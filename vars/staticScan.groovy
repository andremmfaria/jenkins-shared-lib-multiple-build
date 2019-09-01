def call(def details) {
 	echo "[STATIC TESTS] Static tests scan"

  def sonarParam = [:]

  sonarParam.'branch' = "${details.branch}"
  sonarParam.'engine' = "${details.engine}"
  sonarParam.'version' = "${details.version}"
  sonarParam.'testDir' = "${details.testDir}"
  sonarParam.'buildDir' = "${details.buildDir}"
  sonarParam.'environment' = "${details.environment}"
  sonarParam.'buildNumber' = "${details.buildNumber}"
  sonarParam.'application' = "${details.application}"
  sonarParam.'sonarServer' = "${details.sonarServer}"
  sonarParam.'sonarSources' = "${details.sonarSources}"
  sonarParam.'sonarExclusions' = "${details.sonarExclusions}"
  sonarParam.'sonarProjectKey' = "${details.sonarProjectKey}"
  sonarParam.'sonarProjectName' = "${details.sonarProjectName}"

  echo "engine........................: ${sonarParam.engine}"
  echo "branch........................: ${sonarParam.branch}"
  echo "testDir.......................: ${sonarParam.testDir}"
  echo "buildDir......................: ${sonarParam.buildDir}"
  echo "sonarServer...................: ${sonarParam.sonarServer}"
  echo "application...................: ${sonarParam.application}"
  echo "sonarProjectKey...............: ${sonarParam.sonarProjectKey}"
  echo "sonarProjectName..............: ${sonarParam.sonarProjectName}"
  echo "sonarExclusions...............: ${sonarParam.sonarExclusions}"
  
  if ( "${sonarParam.engine}" == 'java' ) {
    sh """
      gradle --info sonarqube \
        -Dsonar.host.url=${sonarParam.sonarServer} \
        -Dsonar.projectName=${sonarParam.sonarProjectName} \
        -Dsonar.projectKey=${sonarParam.sonarProjectKey} \
        -Dsonar.projectVersion=${sonarParam.version} \
        -Dsonar.exclusions=${sonarParam.sonarExclusions} \
        -Dsonar.branch.name=${sonarParam.branch} \
        -Dsonar.login=$SONAR_CRED_USR \
        -Dsonar.password=$SONAR_CRED_PSW
    """
  }
  else if ( "${sonarParam.engine}" == 'nodejs' ) {
    sh """
      sonar-scanner \
        -Dsonar.host.url=${sonarParam.sonarServer} \
        -Dsonar.projectName=${sonarParam.sonarProjectName} \
        -Dsonar.projectKey=${sonarParam.sonarProjectKey} \
        -Dsonar.projectVersion=${sonarParam.version} \
        -Dsonar.exclusions=${sonarParam.sonarExclusions} \
        -Dsonar.sources=${sonarParam.sonarSources} \
        -Dsonar.branch.name=${sonarParam.branch} \
        -Dsonar.login=$SONAR_CRED_USR \
        -Dsonar.password=$SONAR_CRED_PSW
    """
  }
  else if ( "${sonarParam.engine}" == 'angular' ) {
    sh """
      sonar-scanner \
        -Dsonar.host.url=${sonarParam.sonarServer} \
        -Dsonar.projectName=${sonarParam.sonarProjectName} \
        -Dsonar.projectKey=${sonarParam.sonarProjectKey} \
        -Dsonar.projectVersion=${sonarParam.version} \
        -Dsonar.exclusions=${sonarParam.sonarExclusions} \
        -Dsonar.sources=${sonarParam.sonarSources} \
        -Dsonar.branch.name=${sonarParam.branch} \
        -Dsonar.login=$SONAR_CRED_USR \
        -Dsonar.password=$SONAR_CRED_PSW
    """
  }
  else if ( "${sonarParam.engine}" == 'netcore' ) {
    sh """
      PREV=\$PWD
      cd ${details.buildDir}
      
      dotnet /opt/dotnet-sonar-scanner/SonarScanner.MSBuild.dll begin \
        /k:${sonarParam.sonarProjectKey} \
        /n:${sonarParam.sonarProjectName} \
        /v:${sonarParam.version} \
        /d:sonar.host.url=${sonarParam.sonarServer} \
        /d:sonar.exclusions=${sonarParam.sonarExclusions} \
        /d:sonar.branch.name=${sonarParam.branch} \
        /d:sonar.login=$SONAR_CRED_USR \
        /d:sonar.password=$SONAR_CRED_PSW
      
      dotnet build 
      
      dotnet /opt/dotnet-sonar-scanner/SonarScanner.MSBuild.dll end \
        /d:sonar.login=$SONAR_CRED_USR \
        /d:sonar.password=$SONAR_CRED_PSW
      
      cd \$PREV
    """
  }
  else if ( "${sonarParam.engine}" == 'netfull' ) {
    powershell """
      nuget restore ${details.solutionFile}
      
      \$prev=(Get-Location).Path
      Set-Location -Path ${details.buildDir}
      
      SonarScanner.MSBuild.exe begin \
        /k:${sonarParam.sonarProjectKey} \
        /n:${sonarParam.sonarProjectName} \
        /v:${sonarParam.version} \
        /d:sonar.host.url=${sonarParam.sonarServer} \
        /d:sonar.exclusions=${sonarParam.sonarExclusions} \
        /d:sonar.branch.name=${sonarParam.branch} \
        /d:sonar.login=$SONAR_CRED_USR \
        /d:sonar.password=$SONAR_CRED_PSW

      msbuild /t:"Clean;Rebuild"

      SonarScanner.MSBuild.exe end \
        /d:sonar.login=$SONAR_CRED_USR \
        /d:sonar.password=$SONAR_CRED_PSW

      Set-Location -Path \$prev
    """
  }
}
