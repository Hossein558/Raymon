
import com.onresolve.scriptrunner.db.DatabaseUtil


// def issue = Issues.getByKey('RAYMON-13')
issue.getCustomFieldValue('assign permission')

def status = issue.status.name.toString()
def creator = issue.creator.username.toString()
def assignee = issue.getCustomFieldValue('Assignee').toString()
def reprter = issue.reporter.name.toString()

try{
assignee =issue.getCustomFieldValue('Assignee').toString()
assignee= assignee.substring(1,assignee.indexOf('('))
}
catch (Exception ex)
{
  log.warn("Not yet set assignee" )



}




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

  def result = ''
DatabaseUtil.withSql('Jira-DB') {  sql ->


    result = sql.rows(qry_manager, reprter).toString()
  result = result.substring(result.indexOf(":") + 1, result.length() - 2)

  log.warn(result)

}
log.warn(status)


log.warn(status)

if (status == 'بررسی توسط مدیرمارکتینگ') {
  issue.update {
    setCustomFieldValue('Editors','')
    setAssignee('s.sayadi')

    
  }
}
else if (status == 'اصلاح درخواست') {
  issue.update {
    setCustomFieldValue('Editors',  issue.reporter.name.toString() )
    setAssignee(issue.reporter)
  }
} 
else if (status == 'بررسی توسط مدیر واحد') {
  issue.update {
    setCustomFieldValue('Editors', issue.reporter.name.toString() +','+result)
    setAssignee(result)
  }
} 
else if (status == 'To DO') {
  issue.update {
    setCustomFieldValue('Editors', '')
    setAssignee(assignee)
    setCustomFieldValue('Transitioner','s.sayadi')
  }
} 
else if (status == 'Under Review') {
  issue.update {
    setCustomFieldValue('Editors', '')
    setAssignee(issue.reporter)
    setCustomFieldValue('Transitioner','')
  }




} 

if (issue.status.name.toString() == 'خاتمه')
{
  issue.update { 
    setResolution('Done')
   }


}
else if (issue.status.name.toString() == 'انصراف از درخواست') {
    issue.update { 
    setResolution('Cancelled')
   }



}

DatabaseUtil.withSql('Jira-DB') {  sql ->
  try {
sql.rows(insert_sql,issue.id,issue.getKey(),issue.status.name.toString(),Users.getLoggedInUser().name.toString(),Users.getLoggedInUser().displayName.toString(),issue.reporter.name.toString(),issue.assignee.name.toString(),issue.assignee.getDisplayName().toString())

  }
  catch (Exception ex)
  {
    sql.rows(insert_sql,issue.id,issue.getKey(),issue.status.name.toString(),Users.getLoggedInUser().name.toString(),Users.getLoggedInUser().displayName.toString(),issue.reporter.name.toString(),result,result)


  }





}






