TYPE=VIEW
query=select concat(replace(`l`.`LINK_TO`,_latin1\'facility/\',_latin1\'itemLinkFrom/\'),_latin1\'/\',replace(`l`.`OBJECT_ID`,_latin1\'/\',_latin1\':\')) AS `object_id`,`l`.`LINK_TO` AS `p$$parent`,`l`.`CREATED_AT` AS `created_at`,`l`.`CREATED_BY_` AS `created_by_`,`l`.`MODIFIED_AT` AS `modified_at`,`l`.`MODIFIED_BY_` AS `modified_by_`,_utf8\'org:opencrx:kernel:building1:LinkableItemLinkFrom\' AS `dtype`,`l`.`ACCESS_LEVEL_BROWSE` AS `access_level_browse`,`l`.`ACCESS_LEVEL_UPDATE` AS `access_level_update`,`l`.`ACCESS_LEVEL_DELETE` AS `access_level_delete`,`l`.`OWNER_` AS `owner_`,`l`.`DISABLED` AS `disabled`,`l`.`DISABLED_REASON` AS `disabled_reason`,`l`.`NAME` AS `name`,`l`.`DESCRIPTION` AS `description`,(100 - `l`.`LINK_TYPE`) AS `link_type`,`l`.`VALID_FROM` AS `valid_from`,`l`.`VALID_TO` AS `valid_to`,`l`.`OBJECT_ID` AS `link_to`,`l`.`P$$PARENT` AS `link_from` from `crx`.`oocke1_linkableitemlink` `l` where (`l`.`LINK_TO` like _latin1\'facility/%\') union all select concat(replace(`l`.`LINK_TO`,_latin1\'facility1/\',_latin1\'itemLinkFrom1/\'),_latin1\'/\',replace(`l`.`OBJECT_ID`,_latin1\'/\',_latin1\':\')) AS `object_id`,`l`.`LINK_TO` AS `p$$parent`,`l`.`CREATED_AT` AS `created_at`,`l`.`CREATED_BY_` AS `created_by_`,`l`.`MODIFIED_AT` AS `modified_at`,`l`.`MODIFIED_BY_` AS `modified_by_`,_utf8\'org:opencrx:kernel:building1:LinkableItemLinkFrom\' AS `dtype`,`l`.`ACCESS_LEVEL_BROWSE` AS `access_level_browse`,`l`.`ACCESS_LEVEL_UPDATE` AS `access_level_update`,`l`.`ACCESS_LEVEL_DELETE` AS `access_level_delete`,`l`.`OWNER_` AS `owner_`,`l`.`DISABLED` AS `disabled`,`l`.`DISABLED_REASON` AS `disabled_reason`,`l`.`NAME` AS `name`,`l`.`DESCRIPTION` AS `description`,(100 - `l`.`LINK_TYPE`) AS `link_type`,`l`.`VALID_FROM` AS `valid_from`,`l`.`VALID_TO` AS `valid_to`,`l`.`OBJECT_ID` AS `link_to`,`l`.`P$$PARENT` AS `link_from` from `crx`.`oocke1_linkableitemlink` `l` where (`l`.`LINK_TO` like _latin1\'facility1/%\') union all select concat(replace(`l`.`LINK_TO`,_latin1\'facility2/\',_latin1\'itemLinkFrom2/\'),_latin1\'/\',replace(`l`.`OBJECT_ID`,_latin1\'/\',_latin1\':\')) AS `object_id`,`l`.`LINK_TO` AS `p$$parent`,`l`.`CREATED_AT` AS `created_at`,`l`.`CREATED_BY_` AS `created_by_`,`l`.`MODIFIED_AT` AS `modified_at`,`l`.`MODIFIED_BY_` AS `modified_by_`,_utf8\'org:opencrx:kernel:building1:LinkableItemLinkFrom\' AS `dtype`,`l`.`ACCESS_LEVEL_BROWSE` AS `access_level_browse`,`l`.`ACCESS_LEVEL_UPDATE` AS `access_level_update`,`l`.`ACCESS_LEVEL_DELETE` AS `access_level_delete`,`l`.`OWNER_` AS `owner_`,`l`.`DISABLED` AS `disabled`,`l`.`DISABLED_REASON` AS `disabled_reason`,`l`.`NAME` AS `name`,`l`.`DESCRIPTION` AS `description`,(100 - `l`.`LINK_TYPE`) AS `link_type`,`l`.`VALID_FROM` AS `valid_from`,`l`.`VALID_TO` AS `valid_to`,`l`.`OBJECT_ID` AS `link_to`,`l`.`P$$PARENT` AS `link_from` from `crx`.`oocke1_linkableitemlink` `l` where (`l`.`LINK_TO` like _latin1\'facility2/%\') union all select concat(replace(`l`.`LINK_TO`,_latin1\'inventoryItem/\',_latin1\'itemLinkFrom3/\'),_latin1\'/\',replace(`l`.`OBJECT_ID`,_latin1\'/\',_latin1\':\')) AS `object_id`,`l`.`LINK_TO` AS `p$$parent`,`l`.`CREATED_AT` AS `created_at`,`l`.`CREATED_BY_` AS `created_by_`,`l`.`MODIFIED_AT` AS `modified_at`,`l`.`MODIFIED_BY_` AS `modified_by_`,_utf8\'org:opencrx:kernel:building1:LinkableItemLinkFrom\' AS `dtype`,`l`.`ACCESS_LEVEL_BROWSE` AS `access_level_browse`,`l`.`ACCESS_LEVEL_UPDATE` AS `access_level_update`,`l`.`ACCESS_LEVEL_DELETE` AS `access_level_delete`,`l`.`OWNER_` AS `owner_`,`l`.`DISABLED` AS `disabled`,`l`.`DISABLED_REASON` AS `disabled_reason`,`l`.`NAME` AS `name`,`l`.`DESCRIPTION` AS `description`,(100 - `l`.`LINK_TYPE`) AS `link_type`,`l`.`VALID_FROM` AS `valid_from`,`l`.`VALID_TO` AS `valid_to`,`l`.`OBJECT_ID` AS `link_to`,`l`.`P$$PARENT` AS `link_from` from `crx`.`oocke1_linkableitemlink` `l` where (`l`.`LINK_TO` like _latin1\'inventoryItem/%\')
md5=d266452291927c9a82915dacb89e2a23
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
\n    CONCAT( REPLACE(l.link_to, \'facility/\', \'itemLinkFrom/\') , \'/\' , REPLACE(l.object_id, \'/\', \':\') )
\n
\n
\n
\n    AS object_id,
\n    l.link_to AS p$$parent,
\n    l.created_at,
\n    l.created_by_,
\n    l.modified_at,
\n    l.modified_by_,
\n    \'org:opencrx:kernel:building1:LinkableItemLinkFrom\' AS dtype,
\n    l.access_level_browse,
\n    l.access_level_update,
\n    l.access_level_delete,
\n    l.owner_,
\n    l.disabled,
\n    l.disabled_reason,
\n    l.name,
\n    l.description,
\n    100-l.link_type AS link_type,
\n    l.valid_from,
\n    l.valid_to,
\n    l.object_id AS link_to,
\n    l.p$$parent AS link_from
\nFROM
\n    OOCKE1_LINKABLEITEMLINK l
\nWHERE
\n    l.link_to LIKE \'facility/%\'
\n
\nUNION ALL
\n
\nSELECT
\n
\n
\n
\n    CONCAT( REPLACE(l.link_to, \'facility1/\', \'itemLinkFrom1/\') , \'/\' , REPLACE(l.object_id, \'/\', \':\') )
\n
\n
\n
\n    AS object_id,
\n    l.link_to AS p$$parent,
\n    l.created_at,
\n    l.created_by_,
\n    l.modified_at,
\n    l.modified_by_,
\n    \'org:opencrx:kernel:building1:LinkableItemLinkFrom\' AS dtype,
\n    l.access_level_browse,
\n    l.access_level_update,
\n    l.access_level_delete,
\n    l.owner_,
\n    l.disabled,
\n    l.disabled_reason,
\n    l.name,
\n    l.description,
\n    100-l.link_type AS link_type,
\n    l.valid_from,
\n    l.valid_to,
\n    l.object_id AS link_to,
\n    l.p$$parent AS link_from
\nFROM
\n    OOCKE1_LINKABLEITEMLINK l
\nWHERE
\n    l.link_to LIKE \'facility1/%\'
\n
\nUNION ALL
\n
\nSELECT
\n
\n
\n
\n    CONCAT( REPLACE(l.link_to, \'facility2/\', \'itemLinkFrom2/\') , \'/\' , REPLACE(l.object_id, \'/\', \':\') )
\n
\n
\n
\n    AS object_id,
\n    l.link_to AS p$$parent,
\n    l.created_at,
\n    l.created_by_,
\n    l.modified_at,
\n    l.modified_by_,
\n    \'org:opencrx:kernel:building1:LinkableItemLinkFrom\' AS dtype,
\n    l.access_level_browse,
\n    l.access_level_update,
\n    l.access_level_delete,
\n    l.owner_,
\n    l.disabled,
\n    l.disabled_reason,
\n    l.name,
\n    l.description,
\n    100-l.link_type AS link_type,
\n    l.valid_from,
\n    l.valid_to,
\n    l.object_id AS link_to,
\n    l.p$$parent AS link_from
\nFROM
\n    OOCKE1_LINKABLEITEMLINK l
\nWHERE
\n    l.link_to LIKE \'facility2/%\'
\n
\nUNION ALL
\n
\nSELECT
\n
\n
\n
\n    CONCAT( REPLACE(l.link_to, \'inventoryItem/\', \'itemLinkFrom3/\') , \'/\' , REPLACE(l.object_id, \'/\', \':\') )
\n
\n
\n
\n    AS object_id,
\n    l.link_to AS p$$parent,
\n    l.created_at,
\n    l.created_by_,
\n    l.modified_at,
\n    l.modified_by_,
\n    \'org:opencrx:kernel:building1:LinkableItemLinkFrom\' AS dtype,
\n    l.access_level_browse,
\n    l.access_level_update,
\n    l.access_level_delete,
\n    l.owner_,
\n    l.disabled,
\n    l.disabled_reason,
\n    l.name,
\n    l.description,
\n    100-l.link_type AS link_type,
\n    l.valid_from,
\n    l.valid_to,
\n    l.object_id AS link_to,
\n    l.p$$parent AS link_from
\nFROM
\n    OOCKE1_LINKABLEITEMLINK l
\nWHERE
\n    l.link_to LIKE \'inventoryItem/%\'
