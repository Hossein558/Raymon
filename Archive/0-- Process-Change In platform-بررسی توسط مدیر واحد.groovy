import com.onresolve.scriptrunner.db.DatabaseUtil
//set the reporter's manager as the assignee
// def issue = Issues.getByKey('RAYMON-4')



/*
def status = issue.status.name.toString()
def creator = issue.creator.username.toString()

def qry_manager = """
SELECT[manager] FROM[Jira].[Raymon].[managerchart] where[employee] = ?
  """
log.warn(status)

DatabaseUtil.withSql('Jira-DB') {
  sql ->
    def result = sql.rows(qry_manager, creator).toString()
  result = result.substring(result.indexOf(":") + 1, result.length() - 2)
  issue.update {
    setCustomFieldValue('Editors', '')

    setAssignee(result)
    log.warn(result)
  }

}
*/