pipeline{
    agent any


    environment{
        SONARQUBE_SCANNER_HOME = tool 'SonarQube Scanner'
    }

    stages{
        stage('Clone')
        {
          steps  {
                echo 'Cloning the Repo'
                git branch: 'main', url: 'https://github.com/Erik-rosol/Jenkins-Trainning.git'
            }
          }
        stage('Build')
        {
                steps {
                        script {
                            echo 'Building the project...'
                            sh 'mvn clean package -DskipTests=true'
                                }
                        }
                }
        stage('Test')
        {
            steps{
                echo 'Running test'
                sh 'mvn test -e'
            }
        }
        stage('SonarQube Analysis'){
            steps{
                withSonarQubeEnv('SonarQube'){
                sh 'mvn sonar:sonar'}
            }

        }

    }

}