TYPE=VIEW
query=select `f`.`OBJECT_ID` AS `facility`,`b`.`P$$PARENT` AS `segment` from (`crx`.`oocke1_facility` `f` join `crx`.`oocke1_buildingunit` `b` on((`f`.`P$$PARENT` = `b`.`OBJECT_ID`))) union all select `f`.`OBJECT_ID` AS `facility`,`bu1`.`P$$PARENT` AS `segment` from ((`crx`.`oocke1_facility` `f` join `crx`.`oocke1_buildingunit` `bu2` on((`f`.`P$$PARENT` = `bu2`.`OBJECT_ID`))) join `crx`.`oocke1_buildingunit` `bu1` on((`bu2`.`P$$PARENT` = `bu1`.`OBJECT_ID`)))
md5=463f4c22a0f4a2102f9226f1f671de3e
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
\n    f.object_id AS facility,
\n    b.p$$parent AS segment
\nFROM
\n    OOCKE1_FACILITY f
\nINNER JOIN
\n    OOCKE1_BUILDINGUNIT b
\nON
\n    f.p$$parent = b.object_id
\n
\nUNION ALL
\n
\nSELECT
\n    f.object_id AS facility,
\n    bu1.p$$parent AS segment
\nFROM
\n    OOCKE1_FACILITY f
\nINNER JOIN
\n    OOCKE1_BUILDINGUNIT bu2
\nON
\n    f.p$$parent = bu2.object_id
\nINNER JOIN
\n    OOCKE1_BUILDINGUNIT bu1
\nON
\n   bu2.p$$parent = bu1.object_id
