TYPE=VIEW
query=select `crx`.`oocke1_workrecord_`.`OBJECT_ID` AS `object_id`,`crx`.`oocke1_workrecord_`.`IDX` AS `idx`,`crx`.`oocke1_workrecord_`.`CREATED_BY` AS `created_by`,`crx`.`oocke1_workrecord_`.`MODIFIED_BY` AS `modified_by`,`crx`.`oocke1_workrecord_`.`OWNER` AS `owner`,`crx`.`oocke1_workrecord_`.`DTYPE` AS `dtype` from `crx`.`oocke1_workrecord_`
md5=b6fe26b3c7620fe8b883f011edffde23
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
\n    OOCKE1_WORKRECORD_
