TYPE=VIEW
query=select `crx`.`oocke1_productbaseprice_`.`OBJECT_ID` AS `object_id`,`crx`.`oocke1_productbaseprice_`.`IDX` AS `idx`,`crx`.`oocke1_productbaseprice_`.`CREATED_BY` AS `created_by`,`crx`.`oocke1_productbaseprice_`.`MODIFIED_BY` AS `modified_by`,`crx`.`oocke1_productbaseprice_`.`OWNER` AS `owner`,`crx`.`oocke1_productbaseprice_`.`PRICE_LEVEL` AS `price_level`,`crx`.`oocke1_productbaseprice_`.`OBJUSAGE` AS `objusage`,`crx`.`oocke1_productbaseprice_`.`DTYPE` AS `dtype` from `crx`.`oocke1_productbaseprice_`
md5=83e271b1c8cd2204a468e9f6816a8ff0
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
\n    price_level,
\n    objusage,
\n    dtype
\nFROM
\n    OOCKE1_PRODUCTBASEPRICE_
