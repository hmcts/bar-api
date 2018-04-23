#!groovy
@Library("Reform")
import uk.gov.hmcts.Ansible
import uk.gov.hmcts.Packager
import uk.gov.hmcts.RPMTagger

def packager = new Packager(this, 'bar')
def ansible = new Ansible(this, 'bar')
RPMTagger rpmTagger = new RPMTagger(this, 'bar-api', packager.rpmName('bar-api', params.rpmVersion), 'bar-local')

def server = Artifactory.server 'artifactory.reform'
def buildInfo = Artifactory.newBuildInfo()

properties(
    [[$class: 'GithubProjectProperty', displayName: 'bar Register API', projectUrlStr: 'https://github.com/hmcts/bar-app'],
     pipelineTriggers([[$class: 'GitHubPushTrigger']])]
)

milestone()
lock(resource: "bar-app-${env.BRANCH_NAME}", inversePrecedence: true) {
    node {
        try {
            stage('Checkout') {
                deleteDir()
                checkout scm
            }

            stage('Build') {
                def rtGradle = Artifactory.newGradleBuild()
                rtGradle.tool = 'gradle-4.2'
                rtGradle.deployer repo: 'libs-release', server: server
                rtGradle.deployer.deployArtifacts = (env.BRANCH_NAME == 'master')
                rtGradle.run buildFile: 'build.gradle', tasks: 'clean build dependencyCheckAnalyze artifactoryPublish sonarqube -Dsonar.host.url=https://sonar.reform.hmcts.net/', buildInfo: buildInfo
            }

            def barApiDockerVersion
            def barDatabaseDockerVersion
//
//            stage('Build docker') {
//                barApiDockerVersion = dockerImage imageName: 'bar/bar-api'
//                barDatabaseDockerVersion = dockerImage imageName: 'bar/bar-database', context: 'docker/database'
//            }

//            stage("Trigger acceptance tests") {
//                build job: '/bar/bar-app-acceptance-tests/master', parameters: [
//                    [$class: 'StringParameterValue', name: 'barApiDockerVersion', value: barApiDockerVersion],
//                    [$class: 'StringParameterValue', name: 'barDatabaseDockerVersion', value: barDatabaseDockerVersion]
//                ]
//            }

            onMaster {
                def rpmVersion

                stage("Publish RPM") {
                    rpmVersion = packager.javaRPM('master', 'bar-api', '$(ls build/libs/bar-app.jar)', 'springboot', 'api/src/main/resources/application.properties')
                    packager.publishJavaRPM('bar-api')
                }

                stage('Deploy to Dev') {
                    ansible.runDeployPlaybook("{bar_api_version: ${rpmVersion}}", 'dev')
                    rpmTagger.tagDeploymentSuccessfulOn('dev')
                }

  //              stage("Trigger smoke tests in Dev") {
  //                  sh 'curl -f https://api.dev.bar.reform.hmcts.net:4702/health'
  //                  rpmTagger.tagTestingPassedOn('dev')
  //              }

                stage('Deploy to Test') {
                    ansible.runDeployPlaybook("{bar_api_version: ${rpmVersion}}", 'test')
                    rpmTagger.tagDeploymentSuccessfulOn('test')
                }

//                stage("Trigger smoke tests in Test") {
//                    sh 'curl -f https://api.test.bar.reform.hmcts.net:4712/health'
//                  rpmTagger.tagTestingPassedOn('test')
//                }
            }

            milestone()
        } catch (err) {
            notifyBuildFailure channel: '#bar-tech'
            throw err
        }
    }
}
