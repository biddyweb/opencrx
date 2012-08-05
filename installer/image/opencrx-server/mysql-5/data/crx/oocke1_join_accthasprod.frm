TYPE=VIEW
query=select distinct `p`.`OBJECT_ID` AS `product`,`a`.`OBJECT_ID` AS `account` from `crx`.`oocke1_product` `p` join `crx`.`oocke1_account` `a` join `crx`.`oocke1_contract` `c` join `crx`.`oocke1_contractposition` `cp` where ((`cp`.`PRODUCT` = `p`.`OBJECT_ID`) and (`cp`.`P$$PARENT` = `c`.`OBJECT_ID`) and (`c`.`CUSTOMER` = `a`.`OBJECT_ID`))
md5=0cbd4c51417712daabad70d61a666d40
updatable=0
algorithm=0
definer_user=root
definer_host=localhost
suid=2
with_check_option=0
revision=1
timestamp=2008-02-23 23:21:10
create-version=1
source=SELECT DISTINCT
\n    p.object_id AS product,
\n    a.object_id AS account
\nFROM
\n    OOCKE1_PRODUCT p, OOCKE1_ACCOUNT a, OOCKE1_CONTRACT c, OOCKE1_CONTRACTPOSITION cp
\nWHERE
\n    cp.product = p.object_id AND
\n    cp.p$$parent = c.object_id AND
\n    c.customer = a.object_id
