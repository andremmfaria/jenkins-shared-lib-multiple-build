def call(Map config) {

	def details = [:]

	details.'type' = "${config.type}"
	details.'engine' = "${config.engine}"
  details.'bashEnv' = "${env.BASH_ENV}"
	details.'testDir' = "${config.testDir}"
	details.'buildDir' = "${config.buildDir}"
  details.'buildNumber' = "${env.BUILD_NUMBER}"
  details.'solutionFile' = "${config.solutionFile}"
  details.'proxyRegistryPort' = "${env.PROXY_REGISTRY_PORT}"
	details.'branch' = "${env.JOB_BASE_NAME.replace("%2F","/")}"
	details.'version' = "${details.branch.replace("/","-")}"
	details.'application' = "${env.JOB_NAME.toLowerCase().split('/')[-2]}"
	details.'productName' = "${env.JOB_NAME.toLowerCase().split('/')[-3]}"
	details.'containerRegUrl' = "${env.CONTAINER_REGISTRY_URL.split('/')[-1]}"

  details.'sonarServer'	= "${env.SONAR_SERVER_URL}"
  details.'sonarSources' = "${config.sonarSources}"
  details.'sonarProjectKey'	= "${details.application}"
  details.'sonarProjectName' = "${details.application}"
  details.'sonarExclusions' = "${config.sonarExclusions}"
	
  details.'msTeamsUrl' = "${config.msTeamsHookUrl}"
	details.'msTeamsName' = "${config.msTeamsConnName}"

  if ("${details.type}" == 'app') {
	  details.'containerImgName' = "${details.application.replace("--","-")}"
		switch("${details.branch}") {
			case 'master':
				details.'environment' = 'PRD'
				details.'aspEnvironment' = 'Production'
				details.'registryPort' = "${env.PRD_REGISTRY_PORT}"
				break
			case ~/^(release|hotfix).*$/:
				details.'environment' = 'HML'
				details.'aspEnvironment' = 'Staging'
				details.'registryPort' = "${env.HML_REGISTRY_PORT}"
				break
			case ~/^(feature|develop).*$/:
				details.'environment' = 'DEV'
				details.'aspEnvironment' = 'Development'
				details.'registryPort' = "${env.DEV_REGISTRY_PORT}"
				break
			default:
				return 'Undefined environment'
				break
		}
	}
	else {
		details.'environment' = 'INE'
		details.'aspEnvironment' = 'Infrastructure'
		details.'registryPort' = "${env.INE_REGISTRY_PORT}"
    details.'sonarProjectKey' = "${details.application}"
	  details.'containerImgName' = "${details.application.split("--")[1]}"
	  details.'tech'= "${details.application.split("--")[1].split("-")[0]}"
    echo "Technology: ${details.tech.toUpperCase()}"
	}

	echo "Product Name: ${details.productName}"
	echo "Repository: ${details.application}"
	echo "Branch: ${details.branch}"
	echo "Environment: ${details.environment}"

	if( "${details.environment}" == 'INE' ){
		if ( "${details.engine}" == 'docker' ) {
      if ( "${details.tech}" == 'windows' || "${details.tech}" == 'netfull' ) { 
        details.'agentLabel' = "windows-bancopan-dind" 
      }
      else if ( "${details.tech}" == 'linux' || "${details.tech}" == 'nodejs' || "${details.tech}" == 'angular' || "${details.tech}" == 'java' ||  "${details.tech}" == 'netcore' ) { 
        details.'agentLabel' = "linux-bancopan-dind" 
      }
			dockerPipelineDefinition(details)
		}
    else {
		  error( "[DeliveryPipeline] Pipeline not found for engine ${details.engine}" )
    }
  }
	else if( "${details.environment}" == 'DEV' || "${details.environment}" == 'HML' || "${details.environment}" == 'PRD' ) {
		if ( "${details.engine}" == 'java' || "${details.engine}" == 'angular' || "${details.engine}" == 'nodejs' || "${details.engine}" == 'netcore' || "${details.engine}" == 'netfull' ) {
	    details.'agentLabel' = "${details.engine}-build-image"
		  appPipelineDefinition(details)
    }
    else {
		  error( "[DeliveryPipeline] Pipeline not found for engine ${details.engine}" )
    }
	} 
	else {
		error( "[DeliveryPipeline] Pipeline not found for environment ${details.environment}" )
	}
}
