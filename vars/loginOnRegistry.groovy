def call(def details) {
    echo "[LOGIN] Login on Registry"
    
    withCredentials([usernamePassword(credentialsId: 'NEXUS_CREDENTIALS', usernameVariable: 'NEXUS_USR', passwordVariable: 'NEXUS_PSW')]) {
        sh """
            docker login -u $NEXUS_USR -p $NEXUS_PSW ${details.nexus_address}:${details.registry_port}
        """
    }
}
