import com.opensymphony.workflow.InvalidInputException
// def issue = Issues.getByKey('RAYMON-14')


def currentUser = Users.getLoggedInUser().name.toString()
def reporter =issue.reporter.name.toString()
def status = issue.status.name.toString()

if(currentUser == reporter )
{
  if ( (status != "Under Review") && (reporter != 's.sayadi') )
  {
      throw new InvalidInputException("${issue.reporter.displayName.toString()} انتقال فرآیند به مرحله بعدی در اختیاری شخص دیگری است")
  }


}

log.warn(currentUser)