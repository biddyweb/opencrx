TYPE=VIEW
query=select `n`.`OBJECT_ID` AS `namespace`,`e`.`OBJECT_ID` AS `content` from (`crx`.`oocke1_modelelement` `n` join `crx`.`oocke1_modelelement` `e` on((`e`.`CONTAINER` = `n`.`OBJECT_ID`)))
md5=b460774c4735fed067e54f4691c66a34
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
\n    n.object_id AS namespace,
\n    e.object_id AS content
\nFROM
\n    OOCKE1_MODELELEMENT n
\nINNER JOIN
\n    OOCKE1_MODELELEMENT e
\nON
\n    e.container = n.object_id
