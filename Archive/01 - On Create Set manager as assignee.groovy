import com.onresolve.scriptrunner.db.DatabaseUtil
//set the reporter's manager as the assignee
// def issue = Issues.getByKey('RAYMON-19')
def status = issue.status.name.toString()
def reprter = issue.reporter.name.toString()


def qry_manager = """
SELECT[manager] FROM[Jira].[Raymon].[managerchart] where[employee] = ?
  """
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
    VALUES (?,?,?,?,?,?,?,?,getdate())

     select 1

  """


log.warn(status)

DatabaseUtil.withSql('Jira-DB') {sql ->

    def result = sql.rows(qry_manager, reprter).toString()
  result = result.substring(result.indexOf(":") + 1, result.length() - 2)
  log.warn(result)
  issue.update {
    setCustomFieldValue('Editors', '')

    setAssignee(result)
    log.warn(result)
  }
  try {
sql.rows(insert_sql,issue.id,issue.getKey(),issue.status.name.toString(),Users.getLoggedInUser().name.toString(),Users.getLoggedInUser().displayName.toString(),issue.reporter.name.toString(),issue.assignee.name.toString(),issue.assignee.getDisplayName().toString())

  }
  catch (Exception ex)
  {
    sql.rows(insert_sql,issue.id,issue.getKey(),issue.status.name.toString(),Users.getLoggedInUser().name.toString(),Users.getLoggedInUser().displayName.toString(),issue.reporter.name.toString(),result,result)


  }


}
