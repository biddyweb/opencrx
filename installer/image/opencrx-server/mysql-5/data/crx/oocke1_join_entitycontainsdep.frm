TYPE=VIEW
query=select `dh`.`P$$PARENT` AS `entity`,`d`.`OBJECT_ID` AS `depot` from (`crx`.`oocke1_depot` `d` join `crx`.`oocke1_depotholder` `dh` on((`d`.`P$$PARENT` = `dh`.`OBJECT_ID`)))
md5=8b2c70897b43d28249d25d9ac69038fc
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
\n    dh.p$$parent AS entity,
\n    d.object_id AS depot
\nFROM
\n    OOCKE1_DEPOT d
\nINNER JOIN
\n    OOCKE1_DEPOTHOLDER dh
\nON
\n    d.p$$parent = dh.object_id
