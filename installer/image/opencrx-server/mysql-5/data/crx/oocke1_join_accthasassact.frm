TYPE=VIEW
query=select `act`.`OBJECT_ID` AS `assigned_activity`,`acc`.`OBJECT_ID` AS `account` from (`crx`.`oocke1_activity` `act` join `crx`.`oocke1_account` `acc` on((`act`.`ASSIGNED_TO` = `acc`.`OBJECT_ID`))) union select `act`.`OBJECT_ID` AS `assigned_activity`,`adr`.`P$$PARENT` AS `account` from (`crx`.`oocke1_activity` `act` join `crx`.`oocke1_address` `adr` on((`adr`.`OBJECT_ID` = `act`.`SENDER`))) union select `p0`.`P$$PARENT` AS `assigned_activity`,`acc`.`OBJECT_ID` AS `account` from (`crx`.`oocke1_activityparty` `p0` join `crx`.`oocke1_account` `acc` on((`acc`.`OBJECT_ID` = `p0`.`PARTY`))) union select `p0`.`P$$PARENT` AS `assigned_activity`,`adr`.`P$$PARENT` AS `account` from (`crx`.`oocke1_activityparty` `p0` join `crx`.`oocke1_address` `adr` on((`adr`.`OBJECT_ID` = `p0`.`PARTY`))) union select `act`.`OBJECT_ID` AS `assigned_activity`,`c0`.`CUSTOMER` AS `account` from (`crx`.`oocke1_activity_` `act` join `crx`.`oocke1_contract` `c0` on((`act`.`CONTRACT` = `c0`.`OBJECT_ID`)))
md5=bbe9e557c75c24d0c571f96ceaad61d2
updatable=0
algorithm=0
definer_user=root
definer_host=localhost
suid=2
with_check_option=0
revision=1
timestamp=2008-02-23 23:21:10
create-version=1
source=SELECT
\n    act.object_id AS assigned_activity,
\n    acc.object_id AS account
\nFROM
\n    OOCKE1_ACTIVITY act
\nINNER JOIN
\n    OOCKE1_ACCOUNT acc
\nON
\n    act.assigned_to = acc.object_id
\n
\nUNION
\n
\nSELECT
\n    act.object_id AS assigned_activity,
\n    adr.p$$parent AS account
\nFROM
\n    OOCKE1_ACTIVITY act
\nINNER JOIN
\n    OOCKE1_ADDRESS adr
\nON
\n    adr.object_id = act.sender
\n
\nUNION
\n
\nSELECT
\n    p0.p$$parent AS assigned_activity,
\n    acc.object_id AS account
\nFROM
\n    OOCKE1_ACTIVITYPARTY p0
\nINNER JOIN
\n    OOCKE1_ACCOUNT acc
\nON
\n    acc.object_id = p0.party
\n
\nUNION
\n
\nSELECT
\n    p0.p$$parent AS assigned_activity,
\n    adr.p$$parent AS account
\nFROM
\n    OOCKE1_ACTIVITYPARTY p0
\nINNER JOIN
\n    OOCKE1_ADDRESS adr
\nON
\n    adr.object_id = p0.party
\n
\nUNION
\n
\nSELECT
\n    act.object_id AS assigned_activity,
\n    c0.customer AS account
\nFROM
\n    OOCKE1_ACTIVITY_ act
\nINNER JOIN
\n    OOCKE1_CONTRACT c0
\nON
\n    act.contract = c0.object_id
