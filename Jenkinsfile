void setBuildStatus(String message, String state) {
  step([
      $class: "GitHubCommitStatusSetter",
      reposSource: [$class: "ManuallyEnteredRepositorySource", url: "https://github.com/Night-Foxx/LunaChat-fork"],
      contextSource: [$class: "ManuallyEnteredCommitContextSource", context: "ci/jenkins/build-status"],
      errorHandlers: [[$class: "ChangingBuildStatusErrorHandler", result: "UNSTABLE"]],
      statusResultSource: [ $class: "ConditionalStatusResultSource", results: [[$class: "AnyBuildResult", message: message, state: state]] ]
  ]);
}

pipeline {
    agent any

    triggers {
        githubPush()
    }

    tools {
        maven "Maven-3.8.5"
    }

    stages {
        stage('Build') {
            steps {
		echo "java -version"
                sh "mvn clean package"

            }

            post {
                success {
                    archiveArtifacts 'target/*.jar'
		            setBuildStatus("Build succeeded", "SUCCESS");
                }

                failure {
        	        setBuildStatus("Build failed", "FAILURE");
                }
            }

        }

        stage('Deploy to reposilite') {
            steps {
                sh "mvn deploy"

            }

            post {
                success {
		            setBuildStatus("Push succeeded", "SUCCESS");
                }

                failure {
        	        setBuildStatus("Push failed", "FAILURE");
                }
            }

        }

    }

}
