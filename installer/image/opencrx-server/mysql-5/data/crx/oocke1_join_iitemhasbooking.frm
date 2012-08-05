TYPE=VIEW
query=select `b`.`OBJECT_ID` AS `booking`,`i`.`OBJECT_ID` AS `inventory_item` from (`crx`.`oocke1_inventoryitem` `i` join `crx`.`oocke1_booking` `b` on((`b`.`ORIGIN` = `i`.`OBJECT_ID`)))
md5=1c40e621e8e51963134a50332b538dcc
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
\n    i.object_id AS inventory_item
\nFROM
\n    OOCKE1_INVENTORYITEM i
\nINNER JOIN
\n    OOCKE1_BOOKING b
\nON
\n    b.origin = i.object_id
