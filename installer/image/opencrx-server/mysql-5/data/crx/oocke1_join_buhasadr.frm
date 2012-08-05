TYPE=VIEW
query=select `adr`.`OBJECT_ID` AS `assigned_address`,`bu`.`OBJECT_ID` AS `building_unit` from (`crx`.`oocke1_address` `adr` join `crx`.`oocke1_buildingunit` `bu` on((`adr`.`BUILDING` = `bu`.`OBJECT_ID`)))
md5=ea5134705d8902e031526d8770770aa2
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
\n    adr.object_id AS assigned_address,
\n    bu.object_id AS building_unit
\nFROM
\n    OOCKE1_ADDRESS adr
\nINNER JOIN
\n    OOCKE1_BUILDINGUNIT bu
\nON
\n    adr.building = bu.object_id
