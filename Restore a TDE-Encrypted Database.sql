--Step 1: Restore the Certificate
--If you're restoring the database on a different SQL Server instance, first restore the certificate and private key used for TDE:

USE master;
GO

-- Restore the TDE certificate
CREATE CERTIFICATE jiraTDECert
FROM FILE = 'D:\SQL Server Backup\jiraTDECert.cer'
WITH PRIVATE KEY (
    FILE = 'D:\SQL Server Backup\jiraTDECert_PrivateKey.pvk',
    DECRYPTION BY PASSWORD = '147258369Wsx'
);
GO
--Note: Use the certificate file (jiraTDECert.cer) and private key file (jiraTDECert_PrivateKey.pvk) that you backed up when setting up TDE.
--Step 2: Restore the Encrypted Database
--Once the certificate is restored, you can restore the TDE-encrypted database like any other database:

--USE master;
GO

-- Restore the encrypted database
RESTORE DATABASE jira
FROM DISK = 'D:\SQL Server Backup\jira_backup.bak'
WITH MOVE 'jira_Data' TO 'D:\SQL Server Data\jira.mdf',--path for Data
     MOVE 'jira_Log' TO 'D:\SQL Server Data\jira.ldf';--path for log
GO
--Step 3: Verify the Database is Restored
--You can verify the database is restored and still encrypted by running the following query:
SELECT name, is_encrypted
FROM sys.databases
WHERE name = 'jira';
--Important Notes:
--Backup the Certificate: Always keep a backup of your TDE certificate and private key in a safe location, as you will need it to restore your database. Without it, you won't be able to restore any encrypted backups.
--Transferring Certificates: When migrating a TDE-encrypted database to a new server, you must transfer the certificate before attempting to restore the database.
