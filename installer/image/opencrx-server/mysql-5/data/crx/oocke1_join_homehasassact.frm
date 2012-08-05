TYPE=VIEW
query=select `a`.`OBJECT_ID` AS `assigned_activity`,`h0`.`OBJECT_ID` AS `user_home` from (`crx`.`oocke1_activity` `a` join `crx`.`oocke1_userhome` `h0` on((`a`.`ASSIGNED_TO` = `h0`.`CONTACT`))) union select `a`.`OBJECT_ID` AS `assigned_activity`,`h0`.`OBJECT_ID` AS `user_home` from ((`crx`.`oocke1_activity` `a` join `crx`.`oocke1_address` `adr` on((`adr`.`OBJECT_ID` = `a`.`SENDER`))) join `crx`.`oocke1_userhome` `h0` on((`adr`.`P$$PARENT` = `h0`.`CONTACT`))) union select `a`.`OBJECT_ID` AS `assigned_activity`,`h0`.`OBJECT_ID` AS `user_home` from ((`crx`.`oocke1_activityparty` `p0` join `crx`.`oocke1_userhome` `h0` on((`p0`.`PARTY` = `h0`.`CONTACT`))) join `crx`.`oocke1_activity` `a` on((`p0`.`P$$PARENT` = `a`.`OBJECT_ID`))) union select `a`.`OBJECT_ID` AS `assigned_activity`,`h0`.`OBJECT_ID` AS `user_home` from (((`crx`.`oocke1_activityparty` `p0` join `crx`.`oocke1_activity` `a` on((`p0`.`P$$PARENT` = `a`.`OBJECT_ID`))) join `crx`.`oocke1_address` `adr` on((`adr`.`OBJECT_ID` = `p0`.`PARTY`))) join `crx`.`oocke1_userhome` `h0` on((`adr`.`P$$PARENT` = `h0`.`CONTACT`)))
md5=492ef4eb1b2cef53a67647f362024dd6
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
\n    a.object_id AS assigned_activity,
\n    h0.object_id AS user_home
\nFROM
\n    OOCKE1_ACTIVITY a
\nINNER JOIN
\n    OOCKE1_USERHOME h0
\nON
\n    a.assigned_to = h0.contact
\n
\nUNION
\n
\nSELECT
\n    a.object_id AS assigned_activity,
\n    h0.object_id AS user_home
\nFROM
\n    OOCKE1_ACTIVITY a
\nINNER JOIN
\n    OOCKE1_ADDRESS adr
\nON
\n    adr.object_id = a.sender
\nINNER JOIN
\n    OOCKE1_USERHOME h0
\nON
\n    adr.p$$parent = h0.contact
\n
\nUNION
\n
\nSELECT
\n    a.object_id AS assigned_activity,
\n    h0.object_id AS user_home
\nFROM
\n    OOCKE1_ACTIVITYPARTY p0
\nINNER JOIN
\n    OOCKE1_USERHOME h0
\nON
\n    p0.party = h0.contact
\nINNER JOIN
\n    OOCKE1_ACTIVITY a
\nON
\n    p0.p$$parent = a.object_id
\n
\nUNION
\n
\nSELECT
\n    a.object_id AS assigned_activity,
\n    h0.object_id AS user_home
\nFROM
\n    OOCKE1_ACTIVITYPARTY p0
\nINNER JOIN
\n    OOCKE1_ACTIVITY a
\nON
\n    p0.p$$parent = a.object_id
\nINNER JOIN
\n    OOCKE1_ADDRESS adr
\nON
\n    adr.object_id = p0.party
\nINNER JOIN
\n    OOCKE1_USERHOME h0
\nON
\n    adr.p$$parent = h0.contact
