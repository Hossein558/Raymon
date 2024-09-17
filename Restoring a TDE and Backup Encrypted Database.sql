--Step-by-Step Guide to Restoring a TDE and Backup Encrypted Database:
--Step 1: Restore the TDE Certificate
--The first step is to restore the TDE certificate (jiraTDECert) that was used to encrypt the database. This is necessary to decrypt the TDE-encrypted database files.

--Since you've already restored the TDE certificate with the following command, there's no need to repeat this step:

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
--Step 2: Restore the Backup Encryption Certificate
--Since your backup was also encrypted with a Backup Encryption certificate (BackupEncryptionCert), you need to restore this certificate and its private key before restoring the backup.
--Here’s how to restore the backup encryption certificate:

USE master;
GO

-- Restore the Backup Encryption certificate
CREATE CERTIFICATE BackupEncryptionCert
FROM FILE = 'D:\SQL Server Backup\BackupEncryptionCert.cer'
WITH PRIVATE KEY (
    FILE = 'D:\SQL Server Backup\BackupEncryptionCert_PrivateKey.pvk',
    DECRYPTION BY PASSWORD = '147258369Wsx'
);
GO
--Step 3: Restore the Encrypted Database Backup
--Now that both the TDE certificate and the Backup Encryption certificate have been restored, you can proceed to restore the database backup.

USE master;
GO

-- Restore the encrypted database
RESTORE DATABASE jira
FROM DISK = 'D:\SQL Server Backup\jira_backup.bak'
WITH MOVE 'jira_Data' TO 'D:\SQL Server Data\jira.mdf',  --path for Data
     MOVE 'jira_Log' TO 'D:\SQL Server Data\jira.ldf';    --path for Log
GO
--This will restore the database and decrypt it using the restored TDE certificate and Backup Encryption certificate.

--Step 4: Verify the Restoration
--Once the restoration is complete, you can verify that the jira database has been restored and that TDE is still enabled:

SELECT name, is_encrypted
FROM sys.databases
WHERE name = 'jira';
--If is_encrypted returns 1, it means that TDE is still active and the restored database is encrypted.

--Summary:
--Restore the TDE certificate (jiraTDECert) - You already did this.
--Restore the Backup Encryption certificate (BackupEncryptionCert).
--Restore the database using the encrypted backup file.
--Verify that the database is restored and still encrypted.