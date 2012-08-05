TYPE=VIEW
query=select `f`.`OBJECT_ID` AS `follow_up`,`g`.`OBJECT_ID` AS `activity_group` from ((`crx`.`oocke1_activityfollowup` `f` join `crx`.`oocke1_activitygroupass` `ga` on((`f`.`P$$PARENT` = `ga`.`P$$PARENT`))) join `crx`.`oocke1_activitygroup` `g` on((`ga`.`ACTIVITY_GROUP` = `g`.`OBJECT_ID`)))
md5=2809ec6006dce21608bc9fe774588b61
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
\n    f.object_id AS follow_up,
\n    g.object_id AS activity_group
\nFROM
\n    OOCKE1_ACTIVITYFOLLOWUP f
\nINNER JOIN
\n    OOCKE1_ACTIVITYGROUPASS ga
\nON
\n    f.p$$parent = ga.p$$parent
\nINNER JOIN
\n    OOCKE1_ACTIVITYGROUP g
\nON
\n    ga.activity_group = g.object_id
