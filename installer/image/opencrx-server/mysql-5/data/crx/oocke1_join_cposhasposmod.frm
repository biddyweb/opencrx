TYPE=VIEW
query=select `p`.`OBJECT_ID` AS `position`,`pm`.`OBJECT_ID` AS `position_modification` from (`crx`.`oocke1_contractposition` `p` join `crx`.`oocke1_contractposmod` `pm` on((`p`.`OBJECT_ID` = `pm`.`INVOLVED`)))
md5=cca915e18eb888fae6370a5cd15d780a
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
\n    p.object_id AS position,
\n    pm.object_id AS position_modification
\nFROM
\n    OOCKE1_CONTRACTPOSITION p
\nINNER JOIN
\n    OOCKE1_CONTRACTPOSMOD pm
\nON
\n    p.object_id = pm.involved
