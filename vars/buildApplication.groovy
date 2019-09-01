def call(def details) {

	echo "[BUILD] Build, engine: ${details.engine}"

  if ( "${details.engine}" == 'java' ) {
		echo "[BUILD] Build java"

		sh """
      gradle clean build test
    """
	}
	else if ( "${details.engine}" == 'angular' ) {
		echo "[BUILD] Build Javascript Angular"

		sh """
      npm install --no-progress 
      export PATH=\$PWD/node_modules/.bin:\$PATH
      npm run build-${details.environment.toLowerCase()} --no-progress  
    """
	}
	else if ( "${details.engine}" == 'nodejs' ) {
		echo "[BUILD] Build Javascript NodeJs"

		sh """
      npm install --no-progress
      export PATH=\$PWD/node_modules/.bin:\$PATH
      gulp
    """
	}
  else if ( "${details.engine}" == 'netcore' ) {
		echo "[BUILD] Build ASP.NET Core"

		sh """
      PREV=\$PWD
      cd ${details.buildDir}
      
      dotnet restore
      dotnet clean
      dotnet build
      
      npm install --no-progress
      export PATH=\$PWD/node_modules/.bin:\$PATH

      dotnet publish -c Release -o out
      
      mv out \$PREV
      cd \$PREV
    """
	}
	else if ( "${details.engine}" == 'netfull' ) {
		echo "[BUILD] Build ASP.NET Framework"

    powershell """
      nuget restore ${details.solutionFile}
      
      \$prev=(Get-Location).Path
      Set-Location -Path ${details.buildDir}

      msbuild /t:Package /p:DeployIisAppPath=\"Default Web Site\" /p:Configuration=Release /p:PackageLocation=\"output/publish.zip\" /p:OutputPath="\$PWD/output"
      Move-Item -Path "\$PWD/output/_PublishedWebsites/${details.buildDir}" -Destination "\$prev/out" -Force

      Set-Location -Path \$prev
    """
  }
	else {
		error( "[BUILD] Engine not found: ${details.engine}")
	}
}
