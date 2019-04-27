def call(def details) {

	echo "[BUILD] Build, engine: ${details.engine}"

  if(details.engine == 'docker') { details.'folder' = "." }
  else { details.'folder' = ".infra" }

  sh """
      docker build -t ${details.nexus_address}:${details.registry_port}/${details.img_name}:${details.branch} -f ${details.folder}/Dockerfile .
  """
}
