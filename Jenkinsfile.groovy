pipeline {
    agent any

    options {
        preserveStashes()
    }

    stages {
        stage ("build") {
            steps {
                echo 'building the app...'
            }
        }

        stage("all test") {
            parallel {
                stage("test1") {
                    steps {
                        echo 'test1'
//                        writeFile file: "test1", text: "test1"
//                        stash name: "test1", includes: "test1"
//                        script {
//                            throw new Exception("Throw to stop pipeline")
//                        }
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
                        echo 'test2'
                        writeFile file: "test2", text: "test2"
                        stash name: "test2", includes: "test2"
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
                script {
                    try {
                        echo 'try to unstash test1'
                        unstash name:"test1"
                        echo 'unstash test1 success'
                    } catch (Exception e) {
                        echo 'cant unstash test1'
                    }

                    try {
                        echo 'try to unstash test2'
                        unstash name:"test2"
                        echo 'unstash test2 success'
                    } catch (err) {
                        echo 'cant unstash test2'
                    }
                }
            }
        }

        stage("cleanup") {
            steps {
                echo 'clean up..'
            }
        }
    }
}