import com.opensymphony.workflow.InvalidInputException

// Get the current user, reporter, and issue status
def currentUser = Users.getLoggedInUser().name.toString()
def reporter = issue.reporter.name.toString()
def status = issue.status.name.toString()

// Log the current user for traceability
log.warn("Current User: $currentUser")

// Check if the current user is the reporter
if (currentUser == reporter) {
    // Additional condition: status must not be 'Under Review' and reporter must not be 's.sayadi'
    if (status != "Under Review" && reporter != 's.sayadi') {
        // Throw an exception with a customized message in Persian
        throw new InvalidInputException("${issue.reporter.displayName.toString()} انتقال فرآیند به مرحله بعدی در اختیاری شخص دیگری است")
    }
}

// Log a warning if the conditions are passed
log.warn("Process check completed for user: $currentUser")
