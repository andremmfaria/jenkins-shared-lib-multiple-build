def call(def details) {
  echo "[DEPLOY] Deploy Registry"
  if(isUnix()) { 
    sh """
      docker push ${details.containerRegUrl}:${details.registryPort}/${details.containerImgName}:${details.version}
    """
  }
  else {
    powershell """
      docker push ${details.containerRegUrl}:${details.registryPort}/${details.containerImgName}:${details.version}
    """
  }
}
