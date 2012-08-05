TYPE=VIEW
query=select concat(replace(`a`.`OBJECT_ID`,_latin1\'activity/\',_latin1\'activityLinkFrom/\'),_latin1\'/\',replace(`l`.`OBJECT_ID`,_latin1\'/\',_latin1\':\')) AS `object_id`,`a`.`OBJECT_ID` AS `p$$parent`,_utf8\'org:opencrx:kernel:activity1:ActivityLinkFrom\' AS `dtype`,`l`.`MODIFIED_AT` AS `modified_at`,`l`.`CREATED_AT` AS `created_at`,`l`.`CREATED_BY_` AS `created_by_`,`l`.`MODIFIED_BY_` AS `modified_by_`,`l`.`ACCESS_LEVEL_BROWSE` AS `access_level_browse`,`l`.`ACCESS_LEVEL_UPDATE` AS `access_level_update`,`l`.`ACCESS_LEVEL_DELETE` AS `access_level_delete`,`l`.`OWNER_` AS `owner_`,(100 - `l`.`ACTIVITY_LINK_TYPE`) AS `activity_link_type`,`l`.`NAME` AS `name`,`l`.`DESCRIPTION` AS `description`,`l`.`P$$PARENT` AS `link_from`,`l`.`OBJECT_ID` AS `link_to` from `crx`.`oocke1_activity` `a` join `crx`.`oocke1_activitylink` `l` where ((`l`.`LINK_TO` = `a`.`OBJECT_ID`) and (`l`.`OBJECT_ID` = `l`.`OBJECT_ID`))
md5=ad70275832af104435ab472a79ade3a6
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
\n    CONCAT( REPLACE(a.object_id, \'activity/\', \'activityLinkFrom/\') , \'/\' , REPLACE(l.object_id, \'/\', \':\') )
\n
\n
\n
\n    AS object_id,
\n    a.object_id AS p$$parent,
\n    \'org:opencrx:kernel:activity1:ActivityLinkFrom\' AS dtype,
\n    l.modified_at,
\n    l.created_at,
\n    l.created_by_,
\n    l.modified_by_,
\n    l.access_level_browse,
\n    l.access_level_update,
\n    l.access_level_delete,
\n    l.owner_,
\n    100-l.activity_link_type AS activity_link_type,
\n    l.name,
\n    l.description,
\n    l.p$$parent AS link_from,
\n    l.object_id AS link_to
\nFROM
\n    OOCKE1_ACTIVITY a,
\n    OOCKE1_ACTIVITYLINK l
\nWHERE
\n    l.link_to = a.object_id AND
\n    l.object_id = l.object_id
