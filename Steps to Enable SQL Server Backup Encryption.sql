--Step 1: Create a Separate Certificate for Backup Encryption
--You need a separate certificate from the one used for TDE to perform backup encryption. This ensures that even if the backup files are stolen, they cannot be restored without the correct certificate.

USE master;
GO

-- Create a new certificate for backup encryption
CREATE CERTIFICATE BackupEncryptionCert
WITH SUBJECT = 'Backup Encryption Certificate';
GO
--Step 2: Modify Your Backup Script to Include Encryption
--Modify the backup command to include the WITH ENCRYPTION option, specifying the algorithm (e.g., AES_256) and the certificate you created in Step 1 (BackupEncryptionCert).
--Here’s the modified version of your backup script with backup encryption:
DECLARE @DatabaseName NVARCHAR(128);
DECLARE @BackupPath NVARCHAR(255);
DECLARE @BackupFileName NVARCHAR(255);
DECLARE @CurrentDate NVARCHAR(20);
DECLARE @SQL NVARCHAR(MAX);

-- Get current date in Persian format
SET @CurrentDate = FORMAT(SYSDATETIME(), N'yyyy-MM-dd_HH-mm', 'fa-IR');

DECLARE db_cursor CURSOR FOR
SELECT name 
FROM sys.databases 
WHERE name NOT IN ('master', 'model', 'msdb', 'tempdb');

OPEN db_cursor;

FETCH NEXT FROM db_cursor INTO @DatabaseName;

WHILE @@FETCH_STATUS = 0
BEGIN
    -- Build the backup file name with Persian date
    SET @BackupFileName = @DatabaseName + N'_Backup_' + @CurrentDate + N'.bak';
    SET @BackupPath = N'D:\SQL Server Backup\' + @BackupFileName;

    -- Construct dynamic SQL for the BACKUP command with encryption
    SET @SQL = N'BACKUP DATABASE [' + @DatabaseName + N'] TO DISK = N''' + @BackupPath + N''' 
               WITH COMPRESSION, ENCRYPTION (ALGORITHM = AES_256, SERVER CERTIFICATE = BackupEncryptionCert), 
               INIT, NAME = N''Full Backup of ' + @DatabaseName + N''''';

    -- Execute the dynamic SQL
    EXEC sp_executesql @SQL;

    FETCH NEXT FROM db_cursor INTO @DatabaseName;
END

CLOSE db_cursor;
DEALLOCATE db_cursor;

--Explanation of Changes:
--Certificate for Backup Encryption: We use a new certificate BackupEncryptionCert to encrypt the backup. This is separate from the TDE certificate.
--WITH ENCRYPTION Option:

--ALGORITHM = AES_256: Specifies the encryption algorithm to use (AES 256-bit is a secure standard).
--SERVER CERTIFICATE = BackupEncryptionCert: Specifies that the BackupEncryptionCert certificate should be used for backup encryption
--Step 3: Backup the New Backup Encryption Certificate
--Since you are using this certificate for backup encryption, it’s important to back it up just like the TDE certificate. Here’s how:
USE master;
GO

-- Backup the backup encryption certificate
BACKUP CERTIFICATE BackupEncryptionCert
TO FILE = 'D:\SQL Server Backup\BackupEncryptionCert.cer'
WITH PRIVATE KEY (
    FILE = 'D:\SQL Server Backup\BackupEncryptionCert_PrivateKey.pvk',
    ENCRYPTION BY PASSWORD = 'YourStrongBackupPassword'
);
GO







