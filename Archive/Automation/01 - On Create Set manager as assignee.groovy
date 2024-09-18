import com.onresolve.scriptrunner.db.DatabaseUtil
// def issue = Issues.getByKey('RAYMON-44')
// Log the start of the script execution
log.info("Script execution started")

// Get the issue status and reporter's username
def status = issue.status.name.toString()
def reporter = issue.reporter.name.toString()

// Log issue status and reporter details
log.info("Issue Status: $status")
log.info("Reporter: $reporter")

// SQL query to get the manager for the reporter (employee)
def qry_manager = """
SELECT [manager] 
FROM [Jira].[Raymon].[managerchart] 
WHERE [employee] = ?
"""

// SQL query to insert a log entry into the Raymon Log_history table
def insert_sql = """
INSERT INTO [Raymon].[Log_history]
           ([issue_id]
           ,[issue_key]
           ,[issue_status]
           ,[Currentuser_username]
           ,[Currnetuser_displayname]
           ,[Reporter]
           ,[Assignee_username]
           ,[Assignee_displayname]
           ,[Date_Time])
VALUES (?,?,?,?,?,?,?,?,GETDATE())

SELECT 1
"""

// Log the current issue status
log.warn("Current issue status: $status")

// Access the Jira-DB to execute the queries
DatabaseUtil.withSql('Jira-DB') { sql ->

    // Retrieve the manager for the reporter from the database
    log.info("Executing query to get manager for reporter: $reporter")
    def result = sql.rows(qry_manager, reporter).toString()
    
    // Log the query result
    log.info("Query Result: $result")

    // Extract the manager's name from the result
    result = result.substring(result.indexOf(":") + 1, result.length() - 2)
    log.warn("Assigned manager: $result")

    // Update the issue by setting the manager as the assignee
    issue.update {
        setCustomFieldValue('Editors', '') // Clear Editors field
        setAssignee(result) // Assign manager
        log.warn("Assignee set to manager: $result")
    }

    // Insert log entry into the Raymon Log_history table
    try {
        log.info("Inserting log entry with current assignee: ${issue.assignee.name.toString()}")
        sql.rows(insert_sql, 
                 issue.id, 
                 issue.getKey(), 
                 issue.status.name.toString(), 
                 Users.getLoggedInUser().name.toString(), 
                 Users.getLoggedInUser().displayName.toString(), 
                 issue.reporter.name.toString(), 
                 issue.assignee.name.toString(), 
                 issue.assignee.getDisplayName().toString())

        log.info("Log entry successfully inserted")
    } catch (Exception ex) {
        // Handle exception and retry insertion with the manager as the fallback assignee
        log.error("Error during log entry insertion, retrying with manager as fallback assignee", ex)
        sql.rows(insert_sql, 
                 issue.id, 
                 issue.getKey(), 
                 issue.status.name.toString(), 
                 Users.getLoggedInUser().name.toString(), 
                 Users.getLoggedInUser().displayName.toString(), 
                 issue.reporter.name.toString(), 
                 result, 
                 result)

        log.info("Log entry inserted with manager as fallback assignee")
    }
}

// Log the end of the script execution
log.info("Script execution completed")
