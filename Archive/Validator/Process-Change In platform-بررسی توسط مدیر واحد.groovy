import com.opensymphony.workflow.InvalidInputException
import com.atlassian.jira.component.ComponentAccessor
import com.onresolve.scriptrunner.db.DatabaseUtil
import groovy.sql.Sql
// def issue =  Issues.getByKey('RAYMON-44')
// Get the current user and issue creator
def currentUser = Users.getLoggedInUser()
def creator = issue.creator.username.toString()

// Log the issue creator's username
log.warn("Issue Creator: $creator")

// Get the user's group memberships
def userGroups = ComponentAccessor.getGroupManager().getGroupsForUser(currentUser)

// SQL query to retrieve the manager for the creator
def qry_manager = """
SELECT [managername] 
FROM [Jira].[Raymon].[managerchart] 
WHERE [employee] = ?
"""

// Check if the user is part of the "Managers" group
if (userGroups?.find { it.name == "Managers" }) {
    log.warn("Issue Key: ${issue.getKey()} | User is a manager: $creator")
} else {
    // Fetch the manager for the creator from the database
    DatabaseUtil.withSql('Jira-DB') { sql ->
        def result = sql.rows(qry_manager, creator).toString()

        // Extract the manager name from the query result
        result = result.substring(result.indexOf(":") + 1, result.length() - 2)

        // Log the message about coordination with the manager
        log.warn("${issue.reporter.displayName.toString()} تأیید درخواست با ${result} است، لطفا با ایشان هماهنگ نمایید")

        // Throw an InvalidInputException with the manager coordination message
        throw new InvalidInputException("${issue.reporter.displayName.toString()} تأیید درخواست با ${result} است، لطفا با ایشان هماهنگ نمایید")
    }
}
