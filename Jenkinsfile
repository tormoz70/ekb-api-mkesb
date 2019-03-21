pipeline {

	agent any

	// using the Timestamper plugin we can add timestamps to the console log
	options {
		timestamps()
		// keep only last 10 builds
		buildDiscarder(logRotator(numToKeepStr: '10'))
        // timeout job after 60 minutes
		timeout(time: 60, unit: 'MINUTES')
	}

	tools { 
		maven "maven-3.5.4" 
		jdk "jdk8" 
	} 

	environment {
		//Use Pipeline Utility Steps plugin to read information from pom.xml into env variables
		IMAGE = readMavenPom().getArtifactId()
		VERSION = readMavenPom().getVersion()
	}

	stages {
		stage('Prepare') {
			steps {
/*				configFileProvider([configFile(fileId: 'my-maven-settings', variable: 'MY_MAVEN_SETTINGS')]) {
					sh 'mvn -s $MY_MAVEN_SETTINGS clean'
				}

				withMaven(mavenSettingsConfig : "my-maven-settings") {
					sh "mvn clean"
				}
*/
				echo 'Some preparing ...'
				sh 'rm -f -r ${WORKSPACE}/*'
				sh 'mkdir -p ${WORKSPACE}/bio4j-distribution'
			}
		}
		
		stage('Pull') {
/*			when { 
				// skip this stage unless on Dev branch 
				branch "dev" 
			}
*/
			steps {
				git(url: 'http://192.168.70.200/ekb-2/ekb-server-mkesb.git', branch: 'master', credentialsId: 'jenkins')

				checkout([$class: 'GitSCM', branches: [[name: '*/master']], 
//					doGenerateSubmoduleConfigurations: false, 
					extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'bio4j-distribution']], 
//					submoduleCfg: [], 
					userRemoteConfigs: [[credentialsId: 'jenkins', url: 'http://192.168.70.200/bio4j-ng/bio4j-distribution.git']]])

			}
		}
		
		stage('Build') {
			steps {
/*
				configFileProvider([configFile(fileId: 'nexus-maven-settings', variable: 'MAVEN_SETTINGS')]) {
					sh 'mvn -s $MAVEN_SETTINGS clean install'
				}
*/				
				sh 'mvn clean install'
			}
		}
		stage('Publish') {
/*			when { 
				// skip this stage unless on Dev branch
				branch "dev" 
			}
*/
			steps {
				sh 'echo QWE'
				
				sh 'cd ${WORKSPACE}/deploy/bio-as/bio-as;zip -r ../../../ekb-as-mkesb-$(date +%Y%m%d) *'
				sh 'sshpass -p "W38t46S2" scp ekb-as-mkesb-$(date +%Y%m%d).zip root@192.168.70.233:/www'
				sh 'sshpass -p "W38t46S2" ssh root@192.168.70.233 "cd /www; systemctl stop ekb-mkesb; rm -rf ekb-as; unzip ekb-as-mkesb-$(date +%Y%m%d).zip -d ekb-as; chown -R felix /www/ekb-as; systemctl start ekb-mkesb;"'

			}
		}
	}
	
	post {
		/*
		* These steps will run at the end of the pipeline based on the condition.
		* Post conditions run in order regardless of their place in pipeline
		* 1. always - always run
		* 2. changed - run if something changed from last run
		* 3. aborted, success, unstable or failure - depending on status
		*/
	        always {
        	    echo "I AM ALWAYS first"
	        }
        	changed {
	            echo "CHANGED is run second"
        	}
	        aborted {
        	  echo "SUCCESS, FAILURE, UNSTABLE, or ABORTED are exclusive of each other"
	        }
        	success {
	            echo "SUCCESS, FAILURE, UNSTABLE, or ABORTED runs last"
/*
        	  // we only worry about archiving the jar file if the build steps are successful
	          archiveArtifacts(artifacts: '*.jar', allowEmptyArchive: true) 
*/
	        }
        	unstable {
	          echo "SUCCESS, FAILURE, UNSTABLE, or ABORTED runs last"
        	}
	        failure {
        	    echo "SUCCESS, FAILURE, UNSTABLE, or ABORTED runs last"
/*
		// notify users when the Pipeline fails
		mail to: 'team@example.com',
			subject: "Failed Pipeline: ${currentBuild.fullDisplayName}",
			body: "Something is wrong with ${env.BUILD_URL}"
*/
        	}
	}
}


