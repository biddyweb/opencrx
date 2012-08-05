TYPE=VIEW
query=select `crx`.`oocke1_property_`.`OBJECT_ID` AS `object_id`,`crx`.`oocke1_property_`.`IDX` AS `idx`,`crx`.`oocke1_property_`.`CREATED_BY` AS `created_by`,`crx`.`oocke1_property_`.`MODIFIED_BY` AS `modified_by`,`crx`.`oocke1_property_`.`OWNER` AS `owner`,`crx`.`oocke1_property_`.`DTYPE` AS `dtype` from `crx`.`oocke1_property_`
md5=afa4baec9eb8bd046ab2af7c8d14cbf3
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
\n    OOCKE1_PROPERTY_
