TYPE=VIEW
query=select `dg`.`OBJECT_ID` AS `depot_group`,`dgp`.`OBJECT_ID` AS `parent` from (`crx`.`oocke1_depotgroup` `dg` join `crx`.`oocke1_depotgroup` `dgp` on((`dg`.`P$$PARENT` = `dgp`.`OBJECT_ID`)))
md5=f4f5c7badf5ad3348c6d6c9051f3abe5
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
\n    dg.object_id AS depot_group,
\n    dgp.object_id AS parent
\nFROM
\n    OOCKE1_DEPOTGROUP dg
\nINNER JOIN
\n    OOCKE1_DEPOTGROUP dgp
\nON
\n    dg.p$$parent = dgp.object_id
