TYPE=VIEW
query=select `b`.`OBJECT_ID` AS `building_unit`,`s`.`OBJECT_ID` AS `segment` from (`crx`.`oocke1_buildingunit` `b` join `crx`.`oocke1_segment` `s` on((`b`.`P$$PARENT` = `s`.`OBJECT_ID`))) union all select `b`.`OBJECT_ID` AS `building_unit`,`bp`.`P$$PARENT` AS `segment` from (`crx`.`oocke1_buildingunit` `b` join `crx`.`oocke1_buildingunit` `bp` on((`b`.`P$$PARENT` = `bp`.`OBJECT_ID`)))
md5=e0ee2f88f21909e52a40b96ce8400826
updatable=0
algorithm=0
definer_user=root
definer_host=localhost
suid=2
with_check_option=0
revision=1
timestamp=2008-02-23 23:21:10
create-version=1
source=SELECT
\n    b.object_id AS building_unit,
\n    s.object_id AS segment
\nFROM
\n    OOCKE1_BUILDINGUNIT b
\nINNER JOIN
\n    OOCKE1_SEGMENT s
\nON
\n    b.p$$parent = s.object_id
\n
\nUNION ALL
\n
\nSELECT
\n    b.object_id AS building_unit,
\n    bp.p$$parent AS segment
\nFROM
\n    OOCKE1_BUILDINGUNIT b
\nINNER JOIN
\n    OOCKE1_BUILDINGUNIT bp
\nON
\n    b.p$$parent = bp.object_id
