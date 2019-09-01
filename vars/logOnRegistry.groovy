def call(def details) {
  if(isUnix()) {
   echo "[LOGIN] Login on app public proxy registry"
 	 sh """
      docker login \
        -u $CONTAINER_REG_USR \
        -p $CONTAINER_REG_PSW \
        ${details.containerRegUrl}:${details.proxyRegistryPort} 2>&1 >> dockererror.log
    """
    
    echo "[LOGIN] Login on app registry"
    sh """
      docker login \
        -u $CONTAINER_REG_USR \
        -p $CONTAINER_REG_PSW \
        ${details.containerRegUrl}:${details.registryPort} 2>&1 >> dockererror.log
    """
  }
  else {
   echo "[LOGIN] Login on app public proxy registry"
 	 powershell """
      docker login \
        -u $CONTAINER_REG_USR \
        -p $CONTAINER_REG_PSW \
        ${details.containerRegUrl}:${details.proxyRegistryPort} 2>&1 >> dockererror.log
    """
    
    echo "[LOGIN] Login on app registry"
    powershell """
      docker login \
        -u $CONTAINER_REG_USR \
        -p $CONTAINER_REG_PSW \
        ${details.containerRegUrl}:${details.registryPort} 2>&1 >> dockererror.log
    """
  }
}
