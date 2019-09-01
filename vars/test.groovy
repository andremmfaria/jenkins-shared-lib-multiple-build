def call(def details) {

	echo "[TEST] Test"

	if ( "${details.skiptests}" != 'true' ) {
		if ( "${details.engine}" == 'java' ) {
	        echo "[TEST] Java"
			sh "gradle test"
	  } 
      else {
	      error( "Engine not found: ${details.engine}")
	  }
	}
}