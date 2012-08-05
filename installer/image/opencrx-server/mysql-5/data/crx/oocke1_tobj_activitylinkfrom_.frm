TYPE=VIEW
query=select `crx`.`oocke1_activitylink_`.`OBJECT_ID` AS `object_id`,`crx`.`oocke1_activitylink_`.`IDX` AS `idx`,`crx`.`oocke1_activitylink_`.`CREATED_BY` AS `created_by`,`crx`.`oocke1_activitylink_`.`MODIFIED_BY` AS `modified_by`,`crx`.`oocke1_activitylink_`.`OWNER` AS `owner`,`crx`.`oocke1_activitylink_`.`DTYPE` AS `dtype` from `crx`.`oocke1_activitylink_`
md5=ed1f140a7b7a50773830e045eca11f89
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
\n    object_id,
\n    idx,
\n    created_by,
\n    modified_by,
\n    owner,
\n    dtype
\nFROM
\n    OOCKE1_ACTIVITYLINK_
