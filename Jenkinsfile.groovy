pipeline {
    agent any

    options {
        preserveStashes()
    }

    stages {
        stage("build") {
            steps {
                echo 'building the app...'
            }
        }

        stage("test1 and test2") {
            parallel {
                stage("test1") {
                    steps {
                        script {
                            try {
                                echo "try to unstash test1"
                                unstash name: "test1"
                                echo "test1 already exist, skip testing it again"
                            } catch (Exception e) {
                                echo "testing test1"
                                writeFile file: "test1", text: "test1"
                                stash name: "test1", includes: "test1"
                            }
                        }
                    }
                    post {
                        success {
                            echo 'I succeeded!'
                        }
                        failure {
                            echo 'I failed :('
                        }
                        always {
                            echo 'always run me..'
                        }
                    }
                }

                stage("test2") {
                    steps {
                        testWithCheck("test2") {
                            writeFile file: "test2", text: "test2"
                            stash name: "test2", includes: "test2"
                        }
                        script {
                            def test = 2 + 2 > 3 ? 'cool' : 'not cool'
                            echo test
                        }
                    }
                }
            }
        }

        stage("check files") {
            steps {
                echo 'checking files...'
                unstash name: "test1"
                unstash name: "test2"
            }
        }

        stage("cleanup") {
            steps {
                echo 'clean up..'
            }
        }
    }
}

def testWithCheck(String blockName, Closure closure) {
    try {
        echo "try to unstash ${blockName}"
        unstash name: "${blockName}"
        echo "${blockName} already exist, skip testing it again"
    } catch (Exception e) {
        echo "testing ${blockName}"
        closure.call()
    }

}