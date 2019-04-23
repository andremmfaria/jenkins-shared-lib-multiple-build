def call(def details) {
    echo "[DEPLOY] Deploy Registry"
    
    sh """
        docker push ${details.nexus_address}:${details.registry_port}/${details.img_name}:${details.branch}
    """
}
