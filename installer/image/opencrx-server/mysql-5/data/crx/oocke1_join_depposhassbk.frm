TYPE=VIEW
query=select `b`.`OBJECT_ID` AS `simple_booking`,`b`.`POSITION` AS `depot_position` from `crx`.`oocke1_simplebooking` `b`
md5=75301e7b0738910322841f19cdf66ab8
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
\n    b.object_id AS simple_booking,
\n    b.position AS depot_position
\nFROM
\n    OOCKE1_SIMPLEBOOKING b
