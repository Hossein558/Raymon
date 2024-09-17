--Steps to Enable TDE for the jira Database:
--Step 1: Create a Master Key
--First, you need to create a master key in the master database. This is required for managing certificates.

USE master;
GO

-- Create a master key if it doesn't already exist
CREATE MASTER KEY ENCRYPTION BY PASSWORD = '147258369Wsx';
GO

USE master;
GO


--Step 2: Create a Certificate
-- Create a certificate in the master database, which will be used to protect the database encryption key.
USE master;
GO

-- Create a certificate for TDE
CREATE CERTIFICATE jiraTDECert 
WITH SUBJECT = 'TDE Certificate for jira database';
GO


--Step 3: Create a Database Encryption Key
--Now that we have the certificate, we can create a database encryption key for the jira database, which will use the certificate for encryption.

USE jira;
GO

-- Create a Database Encryption Key for the jira database
CREATE DATABASE ENCRYPTION KEY
WITH ALGORITHM = AES_256
ENCRYPTION BY SERVER CERTIFICATE jiraTDECert;
GO
--Step 4: Enable TDE on the Database
--After creating the encryption key, you can enable Transparent Data Encryption on the jira database.

USE jira;
GO

-- Enable Transparent Data Encryption (TDE) on the jira database
ALTER DATABASE jira
SET ENCRYPTION ON;
GO
--At this point, TDE is enabled, and the database will be encrypted.

--Step 5: Backup the Certificate
--It’s important to back up the certificate used for encryption. If you lose the certificate, you won’t be able to restore the database from the backup.

USE master;
GO

-- Backup the certificate and its private key
BACKUP CERTIFICATE jiraTDECert
TO FILE = 'D:\SQL Server Backup\jiraTDECert.cer'
WITH PRIVATE KEY (
    FILE = 'D:\SQL Server Backup\jiraTDECert_PrivateKey.pvk',
    ENCRYPTION BY PASSWORD = '147258369Wsx'
);
GO
--This will save the certificate and private key to D:\SQL Server Backup\.
--Step 6: Verify TDE is Enabled
--You can check if TDE is enabled on the database using the following query:

SELECT 
    name, 
    is_encrypted 
FROM sys.databases
WHERE name = 'jira';
--If is_encrypted returns 1, it means that TDE is successfully enabled.

--Step 7: Optional - Monitor Encryption Progress
--If you have a large database, encryption will occur in the background. You can monitor the progress of encryption using:

SELECT db.name, dm.encryption_state, dm.percent_complete
FROM sys.dm_database_encryption_keys dm
JOIN sys.databases db
ON db.database_id = dm.database_id;


