TYPE=VIEW
query=select `f`.`OBJECT_ID` AS `folder`,`fp`.`OBJECT_ID` AS `parent` from (`crx`.`oocke1_documentfolder` `f` join `crx`.`oocke1_documentfolder` `fp` on((`f`.`PARENT` = `fp`.`OBJECT_ID`)))
md5=f95c460b525f93da71a494b8b309e0fa
updatable=1
algorithm=0
definer_user=root
definer_host=localhost
suid=2
with_check_option=0
revision=1
timestamp=2008-02-23 23:21:10
create-version=1
source=SELECT
\n    f.object_id AS folder,
\n    fp.object_id AS parent
\nFROM
\n    OOCKE1_DOCUMENTFOLDER f
\nINNER JOIN
\n    OOCKE1_DOCUMENTFOLDER fp
\nON
\n    f.parent = fp.object_id
