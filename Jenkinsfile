#!groovy
@Library("Reform")
import uk.gov.hmcts.Ansible
import uk.gov.hmcts.Packager
import uk.gov.hmcts.RPMTagger

def packager = new Packager(this, 'bar')
def ansible = new Ansible(this, 'ccfr')
RPMTagger rpmTagger = new RPMTagger(this, 'bar-api', packager.rpmName('bar-api', params.rpmVersion), 'bar-local')

def server = Artifactory.server 'artifactory.reform'
def buildInfo = Artifactory.newBuildInfo()

properties(
    [[$class: 'GithubProjectProperty', displayName: 'bar Register API', projectUrlStr: 'https://git.reform.hmcts.net/bar/bar-app'],
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

            def artifactVersion = readFile('version.txt').trim()
            def versionAlreadyPublished = checkJavaVersionPublished group: 'bar', artifact: 'bar-app', version: artifactVersion

            onPR {
                if (versionAlreadyPublished) {
                    print "Artifact version already exists. Please bump it."
                    error "Artifact version already exists. Please bump it."
                }
            }

            stage('Build') {
                def descriptor = Artifactory.mavenDescriptor()
                descriptor.version = artifactVersion
                descriptor.transform()

                def rtMaven = Artifactory.newMavenBuild()
                rtMaven.tool = 'apache-maven-3.3.9'
                rtMaven.deployer releaseRepo: 'libs-release', snapshotRepo: 'libs-snapshot', server: server
                rtMaven.deployer.deployArtifacts = (env.BRANCH_NAME == 'master') && !versionAlreadyPublished
                rtMaven.run pom: 'pom.xml', goals: 'clean install sonar:sonar', buildInfo: buildInfo
            }

            def barApiDockerVersion
            def barDatabaseDockerVersion

            stage('Build docker') {
                barApiDockerVersion = dockerImage imageName: 'bar/bar-api'
                barDatabaseDockerVersion = dockerImage imageName: 'bar/bar-database', context: 'docker/database'
            }

            stage("Trigger acceptance tests") {
                build job: '/bar/bar-app-acceptance-tests/master', parameters: [
                    [$class: 'StringParameterValue', name: 'barApiDockerVersion', value: barApiDockerVersion],
                    [$class: 'StringParameterValue', name: 'barDatabaseDockerVersion', value: barDatabaseDockerVersion]
                ]
            }

            onMaster {
                stage('Publish JAR') {
                    server.publishBuildInfo buildInfo
                }

                def rpmVersion

                stage("Publish RPM") {
                    rpmVersion = packager.javaRPM('master', 'bar-api', '$(ls api/target/bar-api-*.jar)', 'springboot', 'api/src/main/resources/application.properties')
                    packager.publishJavaRPM('bar-api')
                }

                stage('Deploy to Dev') {
                    ansible.runDeployPlaybook("{bar_register_api_version: ${rpmVersion}}", 'dev')
                    rpmTagger.tagDeploymentSuccessfulOn('dev')
                }

                stage("Trigger smoke tests in Dev") {
                    sh 'curl -f https://dev.bar.reform.hmcts.net:4411/health'
                    rpmTagger.tagTestingPassedOn('dev')
                }

                stage('Deploy to Test') {
                    ansible.runDeployPlaybook("{bar_register_api_version: ${rpmVersion}}", 'test')
                    rpmTagger.tagDeploymentSuccessfulOn('test')
                }

                stage("Trigger smoke tests in Test") {
                    sh 'curl -f https://test.bar.reform.hmcts.net:4431/health'
                    rpmTagger.tagTestingPassedOn('test')
                }
            }

            milestone()
        } catch (err) {
            notifyBuildFailure channel: '#cc-payments-tech'
            throw err
        }
    }
}
