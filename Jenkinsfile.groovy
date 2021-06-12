pipeline {
    agent any

    options {
        timeout(time: 4, unit: 'HOURS')
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
                        testWithCheck("test1") {
                            runTest("test1")
                        }
                    }
                    post {
                        success {
                            stash name: "test1", includes: "test3.txt"
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
    def needTest = true
    catchError(message: 'check previous build status', stageResult:'SUCCESS', buildResult: 'SUCCESS') {
        echo "try to unstash ${blockName}"
        unstash name:"${blockName}"
        needTest = false
        echo "${blockName} already exist, skip testing it again"
    }

    if (needTest) {
        closure.call()
        echo "testing ${blockName}"
    }
}

def runTest(String testName) {
    Random rnd = new Random()
    def fail = rnd.nextInt(4) % 2  == 0 ? "fail" : "success"
    if (fail == "success") {
        echo "testing ${testName}"
        writeFile file: "${testName}.txt", text: "${testName}"
    } else {
        throw new Exception("Throw to stop pipeline")
    }
}