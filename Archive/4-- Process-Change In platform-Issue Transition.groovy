import com.onresolve.scriptrunner.db.DatabaseUtil
// def issue = Issues.getByKey('RAYMON-44')
// Fetch custom field 'assign permission' (if used)
issue.getCustomFieldValue('assign permission')

// Fetch necessary issue details
def status = issue.status.name.toString()
def creator = issue.creator.username.toString()
def reporter = issue.reporter.name.toString()
def assignee = ''

// Attempt to get the assignee, handling cases where it's not set
try {
    assignee = issue.getCustomFieldValue('Assignee').toString()
    assignee = assignee.substring(1, assignee.indexOf('('))  // Extract the username
} catch (Exception ex) {
    log.warn("Assignee is not yet set")
}

// SQL query to get the manager of the reporter
def qry_manager = """
SELECT [manager] 
FROM [Jira].[Raymon].[managerchart] 
WHERE [employee] = ?
"""

// SQL query to insert log entries into Raymon Log_history
def insert_sql = """
INSERT INTO [Raymon].[Log_history]
           ([issue_id]
           ,[issue_key]
           ,[issue_status]
           ,[Currentuser_username]
           ,[Currentuser_displayname]
           ,[Reporter]
           ,[Assignee_username]
           ,[Assignee_displayname]
           ,[Date_Time])
VALUES (?,?,?,?,?,?,?,?,GETDATE())

SELECT 1
"""

// Initialize result to store the manager's username
def result = ''

// Fetch manager for the reporter
DatabaseUtil.withSql('Jira-DB') { sql ->
    result = sql.rows(qry_manager, reporter).toString()
    result = result.substring(result.indexOf(":") + 1, result.length() - 2)
    log.warn("Manager fetched: $result")
}

// Log current issue status
log.warn("Current issue status: $status")

// Business logic based on issue status
if (status == 'بررسی توسط مدیرمارکتینگ') {
    issue.update {
        setCustomFieldValue('Editors', '')
        setAssignee('s.sayadi')  // Set assignee to 's.sayadi'
    }
} else if (status == 'اصلاح درخواست') {
    issue.update {
        setCustomFieldValue('Editors', issue.reporter.name.toString())
        setAssignee(issue.reporter)  // Set assignee to reporter
    }
} else if (status == 'بررسی توسط مدیر واحد') {
    issue.update {
        setCustomFieldValue('Editors', issue.reporter.name.toString() + ',' + result)
        setAssignee(result)  // Set assignee to manager
    }
} else if (status == 'To DO') {
    issue.update {
        setCustomFieldValue('Editors', '')
        setAssignee(assignee)  // Set assignee to extracted assignee
        setCustomFieldValue('Transitioner', 's.sayadi')  // Set transitioner
    }
} else if (status == 'Under Review') {
    issue.update {
        setCustomFieldValue('Editors', '')
        setAssignee(issue.reporter)  // Set assignee to reporter
        setCustomFieldValue('Transitioner', '')  // Clear transitioner
    }
}

// Resolution settings based on issue status
if (status == 'خاتمه') {
    issue.update {
        setResolution('Done')  // Set resolution to Done
    }
} else if (status == 'انصراف از درخواست') {
    issue.update {
        setResolution('Cancelled')  // Set resolution to Cancelled
    }
}

// Insert log entry into Raymon Log_history
DatabaseUtil.withSql('Jira-DB') { sql ->
    try {
        // Insert log with current assignee
        sql.rows(insert_sql, issue.id, issue.getKey(), issue.status.name.toString(), 
                 Users.getLoggedInUser().name.toString(), Users.getLoggedInUser().displayName.toString(),
                 issue.reporter.name.toString(), issue.assignee.name.toString(), issue.assignee.getDisplayName().toString())

        log.info("Log entry inserted successfully with current assignee")
    } catch (Exception ex) {
        // Insert log with manager as fallback assignee
        log.error("Error inserting log, retrying with manager as fallback assignee", ex)
        sql.rows(insert_sql, issue.id, issue.getKey(), issue.status.name.toString(), 
                 Users.getLoggedInUser().name.toString(), Users.getLoggedInUser().displayName.toString(), 
                 issue.reporter.name.toString(), result, result)

        log.info("Log entry inserted with manager as fallback assignee")
    }
}

log.info("Script execution completed")
