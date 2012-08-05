TYPE=VIEW
query=select `c`.`OBJECT_ID` AS `classifier`,`e`.`OBJECT_ID` AS `typed_element` from (`crx`.`oocke1_modelelement` `c` join `crx`.`oocke1_modelelement` `e` on((`c`.`OBJECT_ID` = `e`.`TYPE`)))
md5=bf4decf2b2e98cf83838478406a13a1f
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
\n    c.object_id AS classifier,
\n    e.object_id AS typed_element
\nFROM
\n    OOCKE1_MODELELEMENT c
\nINNER JOIN
\n    OOCKE1_MODELELEMENT e
\nON
\n    c.object_id = e.type
