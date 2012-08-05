TYPE=VIEW
query=select `ac`.`ACTIVITY_GROUP` AS `activity_group`,`ac`.`OBJECT_ID` AS `activity_creator` from `crx`.`oocke1_activitycreator_` `ac`
md5=4c0963f84155f7601d9c851a7a93b8a1
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
\n    ac.activity_group AS activity_group,
\n    ac.object_id AS activity_creator
\nFROM
\n    OOCKE1_ACTIVITYCREATOR_ ac
