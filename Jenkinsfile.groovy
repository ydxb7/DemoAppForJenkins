pipeline {
    agent any

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
                        script {
                            throw new Exception("Throw to stop pipeline")
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
                        echo 'test2'
                        script {
                            def test = 2 + 2 > 3 ? 'cool' : 'not cool'
                            echo test
                        }
                    }
                }
            }
        }

        stage("deploy") {
            steps {
                echo 'deploying the application...'
            }
        }
    }
}