TYPE=VIEW
query=select `p`.`NAME` AS `principal_name`,`c`.`PASSWD` AS `passwd` from (`crx`.`oocse1_principal` `p` join `crx`.`oocse1_credential` `c` on((`p`.`CREDENTIAL` = `c`.`OBJECT_ID`)))
md5=62f66bcae32caf873be53d33601c3a70
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
\n    c.passwd
\nFROM
\n    OOCSE1_PRINCIPAL p
\nINNER JOIN
\n    OOCSE1_CREDENTIAL c
\nON
\n    p.credential = c.object_id
