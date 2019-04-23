def call(def details) {

	echo "[BUILD] Build, engine: ${details.engine}"

	if ( "${details.engine}" == 'java' ) {
        	echo "[BUILD] Build java"
        	sh "gradle clean build test"
	}
    	else {
		error( "[BUILD] Engine not found: ${details.engine}")
	}
}