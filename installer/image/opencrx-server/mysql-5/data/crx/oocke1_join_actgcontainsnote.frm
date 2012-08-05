TYPE=VIEW
query=select `n`.`OBJECT_ID` AS `activity_note`,`g`.`OBJECT_ID` AS `activity_group` from ((`crx`.`oocke1_note` `n` join `crx`.`oocke1_activitygroupass` `ga` on((`n`.`P$$PARENT` = `ga`.`P$$PARENT`))) join `crx`.`oocke1_activitygroup` `g` on((`ga`.`ACTIVITY_GROUP` = `g`.`OBJECT_ID`)))
md5=55d1e3e299b1713eb1d0903abf1a135c
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
\n    n.object_id AS activity_note,
\n    g.object_id AS activity_group
\nFROM
\n    OOCKE1_NOTE n
\nINNER JOIN
\n    OOCKE1_ACTIVITYGROUPASS ga
\nON
\n    n.p$$parent = ga.p$$parent
\nINNER JOIN
\n    OOCKE1_ACTIVITYGROUP g
\nON
\n    ga.activity_group = g.object_id
