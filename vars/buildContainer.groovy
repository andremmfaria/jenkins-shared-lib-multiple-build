def call(def details) {

	echo "[BUILD] Build, engine: ${details.engine}"

    sh """
        docker build -t ${details.nexus_address}:${details.registry_port}/${details.img_name}:${details.branch} .
    """ 
}