USE [master];
GO

-- Create a SQL Server login
CREATE LOGIN jira 
WITH PASSWORD = '147258369Qaz';
GO

-- Create a user for the login in the master database
CREATE USER jira FOR LOGIN jira;
GO

-- Add the login to the sysadmin server role
ALTER SERVER ROLE sysadmin ADD MEMBER jira;
GO
