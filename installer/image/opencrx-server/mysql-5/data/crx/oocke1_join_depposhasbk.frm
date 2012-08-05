TYPE=VIEW
query=select `b`.`OBJECT_ID` AS `booking`,`b`.`POSITION` AS `depot_position` from `crx`.`oocke1_booking` `b`
md5=05d3d0d580e5ee70d2b62891a0699a15
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
\n    b.position AS depot_position
\nFROM
\n    OOCKE1_BOOKING b
