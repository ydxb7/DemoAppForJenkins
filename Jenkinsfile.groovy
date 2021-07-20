def runTest1 = false
def runTest2 = false

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
                        script {
                            runTest1 = runIfStashIsNotExist("test1", "test1")
                            if (runTest1) {
                                echo "run test1"
                            } else {
                                echo "skip test1"
                            }
                        }
                    }
                    post {
                        success {
                            stash name: "test1", includes: "test1.txt"
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
                        script {
                            runTest2 = runIfStashIsNotExist("test2", "test2")
                            if (runTest2) {
                                echo "run test2"
                                writeFile file: "test2.txt", text: "test2"
                                stash name: "test2", includes: "test2.txt"
                            } else {
                                echo "skip test2"
                            }
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

def runIfStashIsNotExist(String stashName, String testName) {
    catchError(message: "check previous build status of ${testName}", stageResult:'SUCCESS', buildResult: 'SUCCESS') {
        unstash name:"${stashName}"
        echo "${testName} already passed last time, skip ${testName}."
        return false
    }

    echo "${stashName} does not exist, start ${testName}..."
    return true
}

//def runTest(String testName) {
//    Random rnd = new Random()
//    def fail = rnd.nextInt(4) % 2  == 0 ? "fail" : "success"
//    if (fail == "success") {
//        echo "testing ${testName}"
//        writeFile file: "${testName}.txt", text: "${testName}"
//    } else {
//        throw new Exception("Throw to stop pipeline")
//    }
//}