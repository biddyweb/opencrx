TYPE=VIEW
query=select `adr`.`OBJECT_ID` AS `address`,`act`.`P$$PARENT` AS `segment` from (`crx`.`oocke1_address` `adr` join `crx`.`oocke1_account` `act` on((`adr`.`P$$PARENT` = `act`.`OBJECT_ID`)))
md5=3d6ae8c35e701e95adea8b1a7d3df8ed
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
\n    adr.object_id AS address,
\n    act.p$$parent AS segment
\nFROM
\n    OOCKE1_ADDRESS adr
\nINNER JOIN
\n    OOCKE1_ACCOUNT act
\nON
\n    adr.p$$parent = act.object_id
