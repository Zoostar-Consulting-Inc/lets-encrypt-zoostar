pipeline {
	agent any
	
   	stages {
        stage('Verify') {
			steps {
				script {
					if("opened" == "$action" || "synchronize" == "$action" || "edited" == "$action" || "create" == "$action") {
						bat 'mvn -U -B clean verify -Duser.name=%BUILD_NUMBER%'
					}
				}
			}
		}
		
        stage('Install') {
			steps {
				script {
					if("closed" == "$action" && "support" == "$target") {
						bat 'mvn -B clean install -Duser.name="%BUILD_NUMBER%" -Dmaven.tomcat.skip="install"'
					}
				}
			}
		}
		
        stage('Deploy') {
			steps {
				script {
					if("closed" == "$action" &&
							("test" == "$target" || "develop" == "$target")) {
						bat 'mvn -B clean deploy -Duser.name="%BUILD_NUMBER%" -Dmaven.test.skip=true -Dmaven.tomcat.skip="install"'
					}
				}
			}
		}
    }
    
}