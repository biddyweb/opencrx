PRINT 'Original script by Rick Sawtell; MCT, MCSD, MCDBA'

PRINT '**************************************'
PRINT '* Dropping Foreign Key Constraints *'
PRINT '**************************************'


SET NOCOUNT ON
DECLARE @TableNames TABLE(TableName nvarchar(256))
DECLARE @TableCount int
DECLARE @TableName nvarchar(256),
@FKName nvarchar(256)

DECLARE @FKNames TABLE(FKName nvarchar(256))
DECLARE @FKCount int

INSERT @TableNames
SELECT name
FROM sysobjects
WHERE TYPE = 'U'
AND OBJECTPROPERTY(object_id(name), 'IsTable') = 1
AND OBJECTPROPERTY(object_id(name), 'IsSystemTable') = 0
ORDER BY name



SELECT @TableCount = Count(*) FROM @TableNames

WHILE @TableCount > 0
BEGIN
SELECT @TableName = MIN(TableName)
FROM @TableNames

SET @FKName = NULL

INSERT @FKNames (FKName)
SELECT name
FROM sysobjects
WHERE TYPE = 'F'
AND parent_obj = OBJECT_ID(@TableName)
AND OBJECTPROPERTY(OBJECT_ID(name), 'IsForeignKey') = 1


SELECT @FKCount = COUNT(*)
FROM @FKNames

WHILE @FKCount > 0
BEGIN
SELECT @FKName = MIN(FKName)
FROM @FKNames

PRINT ' Dropping Constraint ' + @TableName + '.' + @FKName
EXECUTE('ALTER TABLE ' + @TableName + ' DROP CONSTRAINT ' + @FKName)

DELETE @FKNames
WHERE FKName = @FKName

SET @FKCount = @FKCount - 1
END

DELETE FROM @TableNames WHERE TableName = @TableName
SET @TableCount = @TableCount - 1
END
SET NOCOUNT OFF
GO



/******************************
DROP PKeys
******************************/
PRINT ''
PRINT ''
PRINT ''
PRINT '**************************************'

PRINT '* Dropping Primary Key Constraints *'
PRINT '**************************************'

SET NOCOUNT ON

DECLARE @TableNames TABLE(TableName nvarchar(256))
DECLARE @TableCount int
DECLARE @TableName nvarchar(256),
@PKName nvarchar(256)

DECLARE @KeyNames TABLE(KeyName nvarchar(256))
DECLARE @KeyCount int

INSERT @TableNames (TableName)
SELECT name
FROM sysobjects
WHERE TYPE = 'U'
AND OBJECTPROPERTY(object_id(name), 'IsTable') = 1
AND OBJECTPROPERTY(object_id(name), 'IsSystemTable') = 0
ORDER BY name




SELECT @TableCount = Count(*) FROM @TableNames

WHILE @TableCount > 0
BEGIN
SELECT @TableName = MIN(TableName)
FROM @TableNames

SET @PKName = NULL

INSERT @KeyNames(KeyName)
SELECT name
FROM sysobjects
WHERE parent_obj = OBJECT_ID(@TableName)
AND (OBJECTPROPERTY(OBJECT_ID(name), 'IsPrimaryKey') = 1
OR OBJECTPROPERTY(OBJECT_ID(name), 'IsUniqueCnst') = 1)

SELECT @KeyCount = COUNT(*)
FROM @KeyNames

WHILE @KeyCount > 0
BEGIN
SELECT @PKName = MIN(KeyName)
FROM @KeyNames

PRINT ' Dropping Constraint ' + @TableName + '.' + @PKName
EXECUTE('ALTER TABLE ' + @TableName + ' DROP CONSTRAINT ' + @PKName)

DELETE @KeyNames
WHERE KeyName = @PKName

SET @KeyCount = @KeyCount - 1
END

DELETE FROM @TableNames WHERE TableName = @TableName
SET @TableCount = @TableCount - 1
END

SET NOCOUNT OFF
GO



/******************************
DROP Indexes
******************************/

PRINT ''
PRINT ''
PRINT ''
PRINT '**********************'
PRINT '* Dropping Indexes *'
PRINT '**********************'
SET NOCOUNT ON

DECLARE @IndexNames TABLE(IndexName nvarchar(256))
DECLARE @IndexCount int
DECLARE @IndexName nvarchar(256)


INSERT @IndexNames (IndexName)
SELECT o.name + '.' + i.name AS IndexName
FROM sysindexes i, sysobjects o
WHERE i.indid > 0
AND i.indid < 255
AND (i.status & 64) = 0
AND i.id = o.id
AND o.type = 'U'
AND OBJECTPROPERTY(o.id, 'IsSystemTable') =0
AND OBJECTPROPERTY(o.id, 'IsConstraint') = 0


SELECT @IndexCount = Count(*) FROM @IndexNames

WHILE @IndexCount > 0
BEGIN
SET @IndexName = NULL

SELECT @IndexName = MIN(IndexName)
FROM @IndexNames

IF @IndexName <> 'NxT_Version.PK_NxT_Version'
BEGIN
EXECUTE('DROP INDEX ' + @IndexName)
PRINT ' Dropping Index ' + @IndexName
END


DELETE FROM @IndexNames WHERE IndexName = @IndexName

SET @IndexCount = @IndexCount - 1
END
SET NOCOUNT OFF
GO