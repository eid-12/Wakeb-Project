pipeline{
    agent any

  

    stages{
        stage('Clone')
        {
          steps  {
                echo 'Cloning the Repo'
                git branch: 'main', url: 'https://github.com/eid-12/Wakeb-Project.git'
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

    }

}
