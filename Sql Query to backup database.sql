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

    -- Construct dynamic SQL for the BACKUP command
    SET @SQL = N'BACKUP DATABASE [' + @DatabaseName + N'] TO DISK = N''' + @BackupPath + N''' WITH COMPRESSION, INIT, NAME = N''Full Backup of ' + @DatabaseName + N'''';

    -- Execute the dynamic SQL
    EXEC sp_executesql @SQL;

    FETCH NEXT FROM db_cursor INTO @DatabaseName;
END

CLOSE db_cursor;
DEALLOCATE db_cursor;
