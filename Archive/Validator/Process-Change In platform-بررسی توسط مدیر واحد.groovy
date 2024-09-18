import org.ofbiz.core.entity.jdbc.DatabaseUtil
import com.opensymphony.workflow.InvalidInputException
import com.atlassian.jira.component.ComponentAccessor
import com.onresolve.scriptrunner.db.DatabaseUtil
import groovy.sql.Sql

// def issue = Issues.getByKey('RAYMON-2')

def currentUser = Users.getLoggedInUser()
def creator = issue.creator.username.toString()
log.warn(creator)
def isingroup = ComponentAccessor.getGroupManager().getGroupsForUser(currentUser)

def qry_manager = """
SELECT[managername] FROM[Jira].[Raymon].[managerchart] where[employee] = ?

"""

if (isingroup?.find {
    (it.name == "Managers")
  }) {
  log.warn("issuekey " + issue.getKey() + " IS manager " + creator)

} else {
  DatabaseUtil.withSql('Jira-DB') {
    sql ->
      def result = sql.rows(qry_manager, creator).toString()
    result = result.substring(result.indexOf(":") + 1, result.length() - 2)
    log.warn("${issue.reporter.displayName.toString()}  تأیید درخواست با ${result} است لطفا با ایشان هماهنگ نمایید")
    throw new InvalidInputException("${issue.reporter.displayName.toString()}  تأیید درخواست با ${result} است لطفا با ایشان هماهنگ نمایید")

  }

}