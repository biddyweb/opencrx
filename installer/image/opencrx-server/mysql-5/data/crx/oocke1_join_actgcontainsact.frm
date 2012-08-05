TYPE=VIEW
query=select `ga`.`P$$PARENT` AS `filtered_activity`,`ga`.`ACTIVITY_GROUP` AS `activity_group` from `crx`.`oocke1_activitygroupass` `ga`
md5=88a89772b168e21d06c0e0fe9e3bc418
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
\n    ga.p$$parent AS filtered_activity,
\n    ga.activity_group AS activity_group
\nFROM
\n    OOCKE1_ACTIVITYGROUPASS ga
