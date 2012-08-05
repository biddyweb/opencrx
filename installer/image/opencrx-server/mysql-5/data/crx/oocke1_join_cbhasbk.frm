TYPE=VIEW
query=select `b`.`OBJECT_ID` AS `booking`,`cb`.`OBJECT_ID` AS `cb` from (`crx`.`oocke1_booking` `b` join `crx`.`oocke1_compoundbooking` `cb` on((`b`.`CB` = `cb`.`OBJECT_ID`)))
md5=071b4d3f4fca37ab7ddcf5fdd53184fe
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
\n    b.object_id AS booking,
\n    cb.object_id AS cb
\nFROM
\n    OOCKE1_BOOKING b
\nINNER JOIN
\n    OOCKE1_COMPOUNDBOOKING cb
\nON
\n    b.cb = cb.object_id
