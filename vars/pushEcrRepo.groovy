def call(Map pipelineParams){
def projectName = "${pipelineParams.ecrRepoName}"

pipeline {
   agent any
   environment
    {
        VERSION = "${BUILD_NUMBER}"
        PROJECT = "${projectName}"
        IMAGE = "$PROJECT:$VERSION"
        ECRURL = 'https://453764757326.dkr.ecr.ap-south-1.amazonaws.com/${projectName}'
        ECRCRED = 'ecr:ap-south-1:aws_cred'
    }
    stages {
       stage("clone code") {
            steps {
                script {
                    // Let's clone the source
                    git credentialsId: 'git_cred', url: 'https://github.com/Kishore-SCM/spring3-mvc-maven-xml-hello-world.git'
                }
            }
        }
         stage('Image Build'){
             steps{
                 script{
                       docker.build('$IMAGE')
                 }
             }
         }
         stage('Push Image'){
         steps{
             script
                {

                    docker.withRegistry(ECRURL, ECRCRED)
                    {
                        docker.image(IMAGE).push()
                    }
                }
            }
         }
    }

    post
    {
        always
        {
            // make sure that the Docker image is removed
            sh "docker rmi $IMAGE | true"
        }
    }

}
}
