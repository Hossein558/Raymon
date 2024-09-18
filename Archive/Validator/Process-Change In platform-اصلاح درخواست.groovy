import com.opensymphony.workflow.InvalidInputException
import com.atlassian.jira.component.ComponentAccessor
import com.onresolve.scriptrunner.db.DatabaseUtil
import groovy.sql.Sql

// def issue = Issues.getByKey('RAYMON-44')
// Get the current user and issue creator
def currentUser = Users.getLoggedInUser()
def creator = issue.creator.username.toString()
log.warn("Issue Creator: $creator")

// Get the groups the current user belongs to
def userGroups = ComponentAccessor.getGroupManager().getGroupsForUser(currentUser)

// SQL query to get the manager for the creator
def qry_manager = """
  SELECT [managername] 
  FROM [Jira].[Raymon].[managerchart] 
  WHERE [employee] = ?
"""

// Check if the user is part of the "Managers" group
if (userGroups?.find { it.name == "Managers" }) {
    log.warn("Issue Key: ${issue.getKey()} | User is a manager: $creator")
} else {
    // Fetch the manager for the creator from the database and throw an exception
    DatabaseUtil.withSql('Jira-DB') { sql ->
        def result = sql.rows(qry_manager, creator).toString()

        // Extract the manager name from the query result
        result = result.substring(result.indexOf(":") + 1, result.length() - 2)
        log.warn("Manager for creator: $result")

        // Throw an exception with a customized message in Persian
        throw new InvalidInputException(
            "${issue.reporter.displayName.toString()}، این وظیفه در انتظار تعیین وضعیت است. در صورت نیاز به اصلاح ${result} آن را در وضعیت اصلاح قرار خواهد داد."
        )
    }
}
