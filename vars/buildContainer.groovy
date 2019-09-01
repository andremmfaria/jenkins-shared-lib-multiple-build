def call(def details) {

	echo "[BUILD] Build, engine: ${details.engine}"

  if (details.engine == 'docker' ) { details.'folder' = "." }
  else {details.'folder' = ".infra" }

  if(isUnix()) {
    sh """
        docker build \
          --tag ${details.containerRegUrl}:${details.registryPort}/${details.containerImgName}:${details.version} \
          --build-arg PROFILE_ENV=${details.environment} \
          --build-arg ASPNETCORE_ENVIRONMENT=${details.aspEnvironment} \
          --file ${details.folder}/Dockerfile .
    """ 
  }
  else {
    powershell """
        docker build \
          --tag ${details.containerRegUrl}:${details.registryPort}/${details.containerImgName}:${details.version} \
          --build-arg ASPNET_ENVIRONMENT=${details.aspEnvironment} \
          --file ${details.folder}/Dockerfile .
    """ 
  }
}
