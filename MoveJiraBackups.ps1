#To automate the process of moving .zip files from C:\Atlassian\Jira Home\export\backups to D:\Jira XML Backups, you can use a PowerShell script and set it up as a scheduled task to run at specific intervals.
#Step 1: Create the PowerShell Script
#Open a text editor like Notepad, and paste the following PowerShell script:
# Define source and destination directories
$sourcePath = "C:\Atlassian\Jira Home\export\backups"
$destinationPath = "D:\Jira XML Backups"

# Get all .zip files from the source directory
$zipFiles = Get-ChildItem -Path $sourcePath -Filter *.zip

# Check if destination folder exists, if not, create it
If (!(Test-Path -Path $destinationPath)) {
    New-Item -ItemType Directory -Path $destinationPath
}

# Move each .zip file to the destination directory
foreach ($file in $zipFiles) {
    $destinationFile = Join-Path -Path $destinationPath -ChildPath $file.Name
    Move-Item -Path $file.FullName -Destination $destinationFile
}

# Output success message
Write-Host "All .zip files have been moved to $destinationPath"
#Save the file as MoveJiraBackups.ps1 in a location of your choice.
#Step 2: Test the Script
#To ensure the script works:
#Open PowerShell as Administrator.
#Navigate to the folder where you saved the script:
#cd "C:\Path\To\Your\Script"
#Run the script:
#.\MoveJiraBackups.ps1
#This should move all .zip files from C:\Atlassian\Jira Home\export\backups to D:\Jira XML Backups.
#Step 3: Automate with Task Scheduler
#Open Task Scheduler from the Windows Start menu.
#Click on Create Task.
#In the General tab:
#Name the task (e.g., "Move Jira Backups").
#Select Run with highest privileges.
#In the Triggers tab:
#Click New, and set the schedule for how often you want the task to run (e.g., daily at 2:00 AM).
#In the Actions tab:
#Click New, and for the Action, choose Start a program.
#In the Program/script box, type powershell.
#In the Add arguments box, type:
#-ExecutionPolicy Bypass -File "C:\Path\To\Your\Script\MoveJiraBackups.ps1"
#Click OK to save the task.
#Step 4: Test the Scheduled Task
#You can test the scheduled task by right-clicking it in Task Scheduler and selecting Run. It should execute the PowerShell script and move the .zip files to D:\Jira XML Backups.















