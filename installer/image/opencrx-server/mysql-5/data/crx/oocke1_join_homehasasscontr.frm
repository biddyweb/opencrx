TYPE=VIEW
query=select `c`.`OBJECT_ID` AS `assigned_contract`,`h`.`OBJECT_ID` AS `user_home` from (`crx`.`oocke1_contract` `c` join `crx`.`oocke1_userhome` `h` on((`c`.`SALES_REP` = `h`.`CONTACT`))) union all select `c`.`OBJECT_ID` AS `assigned_contract`,`h`.`OBJECT_ID` AS `user_home` from (`crx`.`oocke1_contract` `c` join `crx`.`oocke1_userhome` `h` on((`c`.`CUSTOMER` = `h`.`CONTACT`)))
md5=1d3e0e3dbaf6312ae63dee615c2b68e7
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
\n    h.object_id AS user_home
\nFROM
\n    OOCKE1_CONTRACT c
\nINNER JOIN
\n    OOCKE1_USERHOME h
\nON
\n    c.sales_rep = h.contact
\n
\nUNION ALL
\n
\nSELECT
\n    c.object_id AS assigned_contract,
\n    h.object_id AS user_home
\nFROM
\n    OOCKE1_CONTRACT c
\nINNER JOIN
\n    OOCKE1_USERHOME h
\nON
\n    c.customer = h.contact
