--Step 2: Set the Database to Single-User Mode
--Run the following SQL command to set the jira database into single-user mode:
ALTER DATABASE jira 
SET SINGLE_USER 
WITH ROLLBACK IMMEDIATE;
--WITH ROLLBACK IMMEDIATE: This option ensures that any open transactions are immediately rolled back, and the database goes into single-user mode right away.
--Step 3: Verify the Mode
--You can verify if the database is in single-user mode by running the following query:
SELECT name, state_desc, user_access_desc 
FROM sys.databases 
WHERE name = 'jira';
--This should show SINGLE_USER under the user_access_desc column for the jira database.
--Step 4: Perform Your Tasks
--With the jira database in single-user mode, you can perform any maintenance or changes you need.
--Step 5: Return the Database to Multi-User Mode
--Once you're done with the maintenance or changes, make sure to switch the database back to multi-user mode to allow normal access:
ALTER DATABASE jira 
SET MULTI_USER;


