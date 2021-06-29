#!groovy
def withCheck(String blockName, Closure closure) {
    script {
        def buildStage = true
//        catchError(message: 'check previous build status', stageResult:'SUCCESS', buildResult: 'SUCCESS') {
//            unstash name:"${blockName}"
//            buildStage = false
//        }
        try {
            unstash name:"${blockName}"
        } catch (AbortException exception) {
            buildStage = false
            echo "check previous build status: ${exception}"
        }


        if (buildStage) {
            closure.call()
            writeFile file: "${blockName}", text: "1"
            stash name: "${blockName}", includes: "${blockName}"
        }
    }
}

pipeline {
    agent none

    options {
        preserveStashes()
    }

    stages {
        stage("Build and test") {
            parallel() {
                stage("Build/Test Win64") {
                    agent {
                        label 'master'
                    }
                    steps {
                        withCheck("build-deploy-Win64") {
                            echo "test"
                        }

                        withCheck("test-Win64") {
                            echo "test"
                        }
                    }
                }

                stage("Build/Test Win32") {
                    agent {
                        label 'master'
                    }
                    steps {
                        withCheck("build-deploy-Win32") {
                            echo "test"
                        }

                        withCheck("test-Win32") {
                            echo "test"
                        }
                    }
                }
            }
        }

        stage("Deploy") {
            agent {
                label 'master'
            }
            steps {
                withCheck("build-deploy-win64") {
                    echo "test"
                }

                withCheck("test-win64") {
                    echo "test"
                }
            }
        }
    }
}