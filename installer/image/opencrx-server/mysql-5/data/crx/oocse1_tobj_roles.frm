TYPE=VIEW
query=select `p`.`NAME` AS `principal_name`,`r`.`NAME` AS `role_name` from `crx`.`oocse1_principal_` `pg` join `crx`.`oocse1_principal` `p` join `crx`.`oocse1_principal_` `pn` join `crx`.`oocse1_role` `r` where ((`p`.`OBJECT_ID` = `pn`.`OBJECT_ID`) and (`pn`.`IS_MEMBER_OF` = `pg`.`OBJECT_ID`) and (`pg`.`GRANTED_ROLE` = `r`.`OBJECT_ID`) and (`p`.`OBJECT_ID` like _latin1\'principal/%/Root/Default/%\'))
md5=0f51a55e83ef17cc24fa6023ad12751c
updatable=1
algorithm=0
definer_user=root
definer_host=localhost
suid=2
with_check_option=0
revision=1
timestamp=2008-02-08 13:58:03
create-version=1
source=SELECT
\n    p.name AS principal_name,
\n    r.name AS role_name
\nFROM
\n    OOCSE1_PRINCIPAL_ pg,
\n    OOCSE1_PRINCIPAL p,
\n    OOCSE1_PRINCIPAL_ pn,
\n    OOCSE1_ROLE r
\nWHERE
\n    (p.object_id = pn.object_id) AND
\n    (pn.is_member_of = pg.object_id) AND
\n    (pg.granted_role = r.object_id) AND
\n    (p.object_id LIKE \'principal/%/Root/Default/%\')
