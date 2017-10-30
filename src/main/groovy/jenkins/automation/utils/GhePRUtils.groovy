package jenkins.automation.utils

/**
 * Utility class to provide nicer, terser DSL for common tasks
 */
class GhePrUtils {

/***
 *
 * Utility method to create a multiscm block from a list of repos.
 * @see <a href="https://github.com/cfpb/jenkins-automation/blob/gh-pages/docs/examples.md#using-multiscm-utility" target="_blank">example</a>
 * @param context A reference to the job object being modified
 * @param gheProject A reference to the GHE project as 'username/reponame'
 * @param gheHostname The GHE hostname, i.e. github.org.tld
 * @param ghePrCron for use when ghePrHooks is false. Uses standard cron syntax
 * @param ghePrHooks whether or not to install webhooks in the remote repo
 * @param ghePrOrgList a string of organizations to whitelist for PRs
 * @param gheAuthId the Jenkins credential ID to authenticate with
 */
    static void GhePrBuilder(
      context,
      String gheProject,
      String gheHostname,
      ghePrCron = null,
      Boolean ghePrHooks = true
      String ghePrOrgsList = 'jenkins',
      String gheAuthId,
    ) {
        context.with {
            scm {
                git {
                    remote {
                        github(gheProject, "https", gheHostname)
                        refspec("+refs/pull/*:refs/remotes/origin/pr/*")
                    }
                    branch('${sha1}')
                }
            }
            configure { node ->
                node / 'triggers' / 'org.jenkinsci.plugins.ghprb.GhprbTrigger'( plugin: 'ghprb') {
                    spec(ghePrCron)
                    cron(ghePrCron)
                    configVersion('3')
                    allowMembersOfWhitelistedOrgsAsAdmin(true)
                    orgslist(ghePrOrgsList)
                    permitAll(true)
                    useGitHubHooks(ghePrHooks)
                    gitHubAuthId(gheAuthId)
                }
            }
        }
    }
}
