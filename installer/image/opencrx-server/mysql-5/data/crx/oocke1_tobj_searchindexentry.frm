TYPE=VIEW
query=select concat(replace(`act`.`P$$PARENT`,_latin1\'accounts/\',_latin1\'searchIndexEntry/\'),_latin1\'/\',replace(`act`.`OBJECT_ID`,_latin1\'/\',_latin1\':\')) AS `object_id`,`act`.`P$$PARENT` AS `p$$parent`,_utf8\'org:opencrx:kernel:account1:SearchIndexEntry\' AS `dtype`,`act`.`MODIFIED_AT` AS `modified_at`,`act`.`MODIFIED_BY_` AS `modified_by_`,`act`.`CREATED_AT` AS `created_at`,`act`.`CREATED_BY_` AS `created_by_`,`act`.`ACCESS_LEVEL_BROWSE` AS `access_level_browse`,`act`.`ACCESS_LEVEL_UPDATE` AS `access_level_update`,`act`.`ACCESS_LEVEL_DELETE` AS `access_level_delete`,`act`.`OWNER_` AS `owner_`,concat((case when isnull(`act`.`LAST_NAME`) then _utf8\'-\' else `act`.`LAST_NAME` end),_latin1\', \',(case when isnull(`act`.`FIRST_NAME`) then _utf8\'-\' else `act`.`FIRST_NAME` end)) AS `account_address_index`,`act`.`OBJECT_ID` AS `account` from `crx`.`oocke1_account` `act` union all select concat(replace(`act`.`P$$PARENT`,_latin1\'accounts/\',_latin1\'searchIndexEntry/\'),_latin1\'/\',replace(`adr`.`OBJECT_ID`,_latin1\'/\',_latin1\':\')) AS `object_id`,`act`.`P$$PARENT` AS `p$$parent`,_utf8\'org:opencrx:kernel:account1:SearchIndexEntry\' AS `dtype`,`act`.`MODIFIED_AT` AS `modified_at`,`act`.`MODIFIED_BY_` AS `modified_by_`,`act`.`CREATED_AT` AS `created_at`,`act`.`CREATED_BY_` AS `created_by_`,`act`.`ACCESS_LEVEL_BROWSE` AS `access_level_browse`,`act`.`ACCESS_LEVEL_UPDATE` AS `access_level_update`,`act`.`ACCESS_LEVEL_DELETE` AS `access_level_delete`,`act`.`OWNER_` AS `owner_`,concat((case when isnull(`act`.`LAST_NAME`) then _utf8\'-\' else `act`.`LAST_NAME` end),_latin1\', \',(case when isnull(`act`.`FIRST_NAME`) then _utf8\'-\' else `act`.`FIRST_NAME` end),_latin1\', \',(case when isnull(`adr`.`EMAIL_ADDRESS`) then _utf8\'-\' else `adr`.`EMAIL_ADDRESS` end),_latin1\', \',(case when isnull(`adr`.`PHONE_NUMBER_FULL`) then _utf8\'-\' else `adr`.`PHONE_NUMBER_FULL` end),_latin1\', \',(case when isnull(`adr`.`ROOM_NUMBER`) then _utf8\'-\' else `adr`.`ROOM_NUMBER` end),_latin1\', \',(case when isnull(`adr`.`POSTAL_STREET_0`) then _utf8\'-\' else `adr`.`POSTAL_STREET_0` end),_latin1\', \',(case when isnull(`adr`.`POSTAL_CITY`) then _utf8\'-\' else `adr`.`POSTAL_CITY` end)) AS `account_address_index`,`act`.`OBJECT_ID` AS `account` from (`crx`.`oocke1_account` `act` join `crx`.`oocke1_address` `adr` on((`adr`.`P$$PARENT` = `act`.`OBJECT_ID`)))
md5=13f86b206d2ab42884e9945f7faa9a00
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
\n
\n
\n
\n    CONCAT( REPLACE(act.p$$parent, \'accounts/\', \'searchIndexEntry/\') , \'/\' , REPLACE(act.object_id, \'/\', \':\') )
\n
\n
\n
\n    AS object_id,
\n    act.p$$parent AS p$$parent,
\n    \'org:opencrx:kernel:account1:SearchIndexEntry\' AS dtype,
\n    act.modified_at,
\n    act.modified_by_,
\n    act.created_at,
\n    act.created_by_,
\n    act.access_level_browse,
\n    act.access_level_update,
\n    act.access_level_delete,
\n    act.owner_,
\n    CONCAT(
\n        CASE WHEN act.last_name IS NULL THEN \'-\' ELSE act.last_name END , \', \' ,
\n        CASE WHEN act.first_name IS NULL THEN \'-\' ELSE act.first_name END ) AS account_address_index,
\n    act.object_id AS account
\nFROM
\n    OOCKE1_ACCOUNT act
\n
\nUNION ALL
\n
\nSELECT
\n
\n
\n
\n    CONCAT( REPLACE(act.p$$parent, \'accounts/\', \'searchIndexEntry/\') , \'/\' , REPLACE(adr.object_id, \'/\', \':\') )
\n
\n
\n
\n    AS object_id,
\n    act.p$$parent AS p$$parent,
\n    \'org:opencrx:kernel:account1:SearchIndexEntry\' AS dtype,
\n    act.modified_at,
\n    act.modified_by_,
\n    act.created_at,
\n    act.created_by_,
\n    act.access_level_browse,
\n    act.access_level_update,
\n    act.access_level_delete,
\n    act.owner_,
\n    CONCAT(
\n        CASE WHEN act.last_name IS NULL THEN \'-\' ELSE act.last_name END , \', \' ,
\n        CASE WHEN act.first_name IS NULL THEN \'-\' ELSE act.first_name END , \', \' ,
\n        CASE WHEN adr.email_address IS NULL THEN \'-\' ELSE adr.email_address END , \', \' ,
\n        CASE WHEN adr.phone_number_full IS NULL THEN \'-\' ELSE adr.phone_number_full END , \', \' ,
\n        CASE WHEN adr.room_number IS NULL THEN \'-\' ELSE adr.room_number END , \', \' ,
\n        CASE WHEN adr.postal_street_0 IS NULL THEN \'-\' ELSE adr.postal_street_0 END , \', \' ,
\n        CASE WHEN adr.postal_city IS NULL THEN \'-\' ELSE adr.postal_city END ) AS account_address_index,
\n    act.object_id AS account
\nFROM
\n    OOCKE1_ACCOUNT act
\nINNER JOIN
\n    OOCKE1_ADDRESS adr
\nON
\n    adr.p$$parent = act.object_id
