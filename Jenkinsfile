pipeline {
    agent any

    stages {
        stage("build") {
            steps {
                echo 'building the application...'
            }
        }

        stage("test") {
            steps {
                echo 'testing the application...'
                script {
                    def test = 2 + 2 > 3 ? 'cool' : 'not cool'
                    echo test
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