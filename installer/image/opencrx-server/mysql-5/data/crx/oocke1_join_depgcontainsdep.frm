TYPE=VIEW
query=select `d`.`OBJECT_ID` AS `depot`,`dg`.`OBJECT_ID` AS `depot_group` from (`crx`.`oocke1_depot` `d` join `crx`.`oocke1_depotgroup` `dg` on((`d`.`DEPOT_GROUP` = `dg`.`OBJECT_ID`)))
md5=1356994191d5213344988697f6d374e1
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
\n    d.object_id AS depot,
\n    dg.object_id AS depot_group
\nFROM
\n    OOCKE1_DEPOT d
\nINNER JOIN
\n    OOCKE1_DEPOTGROUP dg
\nON
\n    d.depot_group = dg.object_id
