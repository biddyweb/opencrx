TYPE=VIEW
query=select concat(replace(`a`.`OBJECT_ID`,_latin1\'account/\',_latin1\'accountMembership/\'),_latin1\'/\',replace(`ass`.`OBJECT_ID`,_latin1\'/\',_latin1\':\')) AS `object_id`,`ass`.`ACCOUNT` AS `p$$parent`,`ass`.`CREATED_AT` AS `created_at`,`ass`.`CREATED_BY_` AS `created_by_`,`ass`.`MODIFIED_AT` AS `modified_at`,`ass`.`MODIFIED_BY_` AS `modified_by_`,_utf8\'org:opencrx:kernel:account1:AccountMembership\' AS `dtype`,`ass`.`ACCESS_LEVEL_BROWSE` AS `access_level_browse`,`ass`.`ACCESS_LEVEL_UPDATE` AS `access_level_update`,`ass`.`ACCESS_LEVEL_DELETE` AS `access_level_delete`,`ass`.`OWNER_` AS `owner_`,`ass`.`NAME` AS `name`,`ass`.`DESCRIPTION` AS `description`,`ass`.`VALID_FROM` AS `valid_from`,`ass`.`VALID_TO` AS `valid_to`,`ass`.`OBJECT_ID` AS `member`,`ass`.`P$$PARENT` AS `member_of_account`,`ass`.`MEMBER_ROLE_` AS `member_role_`,`ass`.`DISABLED` AS `disabled` from (`crx`.`oocke1_accountassignment` `ass` join `crx`.`oocke1_account` `a` on(((`ass`.`ACCOUNT` = `a`.`OBJECT_ID`) and (`ass`.`DTYPE` = _latin1\'org:opencrx:kernel:account1:Member\'))))
md5=73c0b422d445e04657e5a0ebe69a4aa8
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
\n
\n
\n
\n    CONCAT( REPLACE(a.object_id, \'account/\', \'accountMembership/\') , \'/\' , REPLACE(ass.object_id, \'/\', \':\') )
\n
\n
\n
\n    AS object_id,
\n    ass.account AS p$$parent,
\n    ass.created_at,
\n    ass.created_by_,
\n    ass.modified_at,
\n    ass.modified_by_,
\n    \'org:opencrx:kernel:account1:AccountMembership\' AS dtype,
\n    ass.access_level_browse,
\n    ass.access_level_update,
\n    ass.access_level_delete,
\n    ass.owner_,
\n    ass.name,
\n    ass.description,
\n    ass.valid_from,
\n    ass.valid_to,
\n    ass.object_id AS member,
\n    ass.p$$parent AS member_of_account,
\n    ass.member_role_,
\n    ass.disabled
\nFROM
\n    OOCKE1_ACCOUNTASSIGNMENT ass
\nINNER JOIN
\n    OOCKE1_ACCOUNT a
\nON
\n    (ass.account = a.object_id) AND
\n    (ass.dtype = \'org:opencrx:kernel:account1:Member\')
