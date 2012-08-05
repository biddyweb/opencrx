TYPE=VIEW
query=select `c`.`OBJECT_ID` AS `assigned_contract`,`a`.`OBJECT_ID` AS `account` from (`crx`.`oocke1_contract` `c` join `crx`.`oocke1_account` `a` on((`c`.`CUSTOMER` = `a`.`OBJECT_ID`))) union all select `c`.`OBJECT_ID` AS `assigned_contract`,`a`.`OBJECT_ID` AS `account` from (`crx`.`oocke1_contract` `c` join `crx`.`oocke1_account` `a` on((`c`.`SALES_REP` = `a`.`OBJECT_ID`)))
md5=eb9849868c44a395ac0381f10d0aa386
updatable=0
algorithm=0
definer_user=root
definer_host=localhost
suid=2
with_check_option=0
revision=1
timestamp=2008-02-23 23:21:10
create-version=1
source=SELECT
\n    c.object_id AS assigned_contract,
\n    a.object_id AS account
\nFROM
\n    OOCKE1_CONTRACT c
\nINNER JOIN
\n    OOCKE1_ACCOUNT a
\nON
\n    c.customer = a.object_id
\n
\nUNION ALL
\n
\nSELECT
\n    c.object_id AS assigned_contract,
\n    a.object_id AS account
\nFROM
\n    OOCKE1_CONTRACT c
\nINNER JOIN
\n    OOCKE1_ACCOUNT a
\nON
\n    c.sales_rep = a.object_id
