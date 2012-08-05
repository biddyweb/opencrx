TYPE=VIEW
query=select `a`.`OBJECT_ID` AS `assigned_activity`,`r`.`OBJECT_ID` AS `resource` from ((`crx`.`oocke1_activity` `a` join `crx`.`oocke1_resourceassignment` `ra` on((`ra`.`P$$PARENT` = `a`.`OBJECT_ID`))) join `crx`.`oocke1_resource` `r` on((`ra`.`RESRC` = `r`.`OBJECT_ID`)))
md5=3ee3b072bd885e791d43491d1e4e4be2
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
\n    a.object_id AS assigned_activity,
\n    r.object_id AS resource
\nFROM
\n    OOCKE1_ACTIVITY a
\nINNER JOIN
\n    OOCKE1_RESOURCEASSIGNMENT ra
\nON
\n    ra.p$$parent = a.object_id
\nINNER JOIN
\n    OOCKE1_RESOURCE r
\nON
\n    ra.resrc = r.object_id
