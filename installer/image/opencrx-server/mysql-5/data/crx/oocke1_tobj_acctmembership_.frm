TYPE=VIEW
query=select `crx`.`oocke1_accountassignment_`.`OBJECT_ID` AS `object_id`,`crx`.`oocke1_accountassignment_`.`IDX` AS `idx`,`crx`.`oocke1_accountassignment_`.`CREATED_BY` AS `created_by`,`crx`.`oocke1_accountassignment_`.`MEMBER_ROLE` AS `member_role`,`crx`.`oocke1_accountassignment_`.`MODIFIED_BY` AS `modified_by`,`crx`.`oocke1_accountassignment_`.`OWNER` AS `owner`,`crx`.`oocke1_accountassignment_`.`DTYPE` AS `dtype` from `crx`.`oocke1_accountassignment_`
md5=2a5e31c773353f29c5d93d797e931990
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
\n    member_role,
\n    modified_by,
\n    owner,
\n    dtype
\nFROM
\n    OOCKE1_ACCOUNTASSIGNMENT_
