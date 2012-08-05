TYPE=VIEW
query=select concat(replace(`a`.`P$$PARENT`,_latin1\'activities/\',_latin1\'workReportEntry1/\'),_latin1\'/\',replace(`w`.`OBJECT_ID`,_latin1\'/\',_latin1\':\')) AS `object_id`,`a`.`P$$PARENT` AS `p$$parent`,_utf8\'org:opencrx:kernel:activity1:WorkReportEntry\' AS `dtype`,`w`.`MODIFIED_AT` AS `modified_at`,`w`.`MODIFIED_BY_` AS `modified_by_`,`w`.`CREATED_AT` AS `created_at`,`w`.`CREATED_BY_` AS `created_by_`,`w`.`ACCESS_LEVEL_BROWSE` AS `access_level_browse`,`w`.`ACCESS_LEVEL_UPDATE` AS `access_level_update`,`w`.`ACCESS_LEVEL_DELETE` AS `access_level_delete`,`w`.`OWNER_` AS `owner_`,`w`.`NAME` AS `name`,`w`.`DESCRIPTION` AS `description`,`w`.`STARTED_AT` AS `started_at`,`w`.`ENDED_AT` AS `ended_at`,`w`.`DURATION_HOURS` AS `duration_hours`,`w`.`DURATION_MINUTES` AS `duration_minutes`,(`w`.`DURATION_HOURS` + (`w`.`DURATION_MINUTES` / 60.0)) AS `duration_decimal`,concat(cast(`w`.`DURATION_HOURS` as char charset utf8),_utf8\':\',cast(`w`.`DURATION_MINUTES` as char charset utf8),char(39)) AS `duration_hh_mm`,`w`.`PAUSE_DURATION_HOURS` AS `pause_duration_hours`,`w`.`PAUSE_DURATION_MINUTES` AS `pause_duration_minutes`,(`w`.`PAUSE_DURATION_HOURS` + (`w`.`PAUSE_DURATION_MINUTES` / 60.0)) AS `pause_duration_decimal`,concat(cast(`w`.`PAUSE_DURATION_HOURS` as char charset utf8),_utf8\':\',cast(`w`.`PAUSE_DURATION_MINUTES` as char charset utf8),char(39)) AS `pause_duration_hh_mm`,`w`.`BILLABLE_AMOUNT` AS `billable_amount`,`w`.`BILLING_CURRENCY` AS `billing_currency`,`a`.`ACTIVITY_NUMBER` AS `activity_number`,`a`.`OBJECT_ID` AS `activity`,`ra`.`OBJECT_ID` AS `assignment`,`w`.`OBJECT_ID` AS `work_record`,`ra`.`RESRC` AS `resrc` from ((`crx`.`oocke1_workrecord` `w` join `crx`.`oocke1_resourceassignment` `ra` on((`w`.`P$$PARENT` = `ra`.`OBJECT_ID`))) join `crx`.`oocke1_activity` `a` on((`a`.`OBJECT_ID` = `ra`.`P$$PARENT`))) union all select concat(replace(replace(replace(`ga`.`ACTIVITY_GROUP`,_latin1\'activityTracker/\',_latin1\'workReportEntry2/\'),_latin1\'activityCategory/\',_latin1\'workReportEntry2/\'),_latin1\'activityMilestone/\',_latin1\'workReportEntry2/\'),_latin1\'/\',replace(`w`.`OBJECT_ID`,_latin1\'/\',_latin1\':\')) AS `object_id`,`ga`.`ACTIVITY_GROUP` AS `p$$parent`,_utf8\'org:opencrx:kernel:activity1:WorkReportEntry\' AS `dtype`,`w`.`MODIFIED_AT` AS `modified_at`,`w`.`MODIFIED_BY_` AS `modified_by_`,`w`.`CREATED_AT` AS `created_at`,`w`.`CREATED_BY_` AS `created_by_`,`w`.`ACCESS_LEVEL_BROWSE` AS `access_level_browse`,`w`.`ACCESS_LEVEL_UPDATE` AS `access_level_update`,`w`.`ACCESS_LEVEL_DELETE` AS `access_level_delete`,`w`.`OWNER_` AS `owner_`,`w`.`NAME` AS `name`,`w`.`DESCRIPTION` AS `description`,`w`.`STARTED_AT` AS `started_at`,`w`.`ENDED_AT` AS `ended_at`,`w`.`DURATION_HOURS` AS `duration_hours`,`w`.`DURATION_MINUTES` AS `duration_minutes`,(`w`.`DURATION_HOURS` + (`w`.`DURATION_MINUTES` / 60.0)) AS `duration_decimal`,concat(cast(`w`.`DURATION_HOURS` as char charset utf8),_utf8\':\',cast(`w`.`DURATION_MINUTES` as char charset utf8),char(39)) AS `duration_hh_mm`,`w`.`PAUSE_DURATION_HOURS` AS `pause_duration_hours`,`w`.`PAUSE_DURATION_MINUTES` AS `pause_duration_minutes`,(`w`.`PAUSE_DURATION_HOURS` + (`w`.`PAUSE_DURATION_MINUTES` / 60.0)) AS `pause_duration_decimal`,concat(cast(`w`.`PAUSE_DURATION_HOURS` as char charset utf8),_utf8\':\',cast(`w`.`PAUSE_DURATION_MINUTES` as char charset utf8),char(39)) AS `pause_duration_hh_mm`,`w`.`BILLABLE_AMOUNT` AS `billable_amount`,`w`.`BILLING_CURRENCY` AS `billing_currency`,`a`.`ACTIVITY_NUMBER` AS `activity_number`,`a`.`OBJECT_ID` AS `activity`,`ra`.`OBJECT_ID` AS `assignment`,`w`.`OBJECT_ID` AS `work_record`,`ra`.`RESRC` AS `resrc` from (((`crx`.`oocke1_workrecord` `w` join `crx`.`oocke1_resourceassignment` `ra` on((`w`.`P$$PARENT` = `ra`.`OBJECT_ID`))) join `crx`.`oocke1_activity` `a` on((`a`.`OBJECT_ID` = `ra`.`P$$PARENT`))) join `crx`.`oocke1_activitygroupass` `ga` on(((`a`.`OBJECT_ID` = `ga`.`P$$PARENT`) and (`ga`.`ACTIVITY_GROUP` is not null)))) union all select concat(replace(replace(replace(`ga`.`ACTIVITY_GROUP`,_latin1\'activityTracker/\',_latin1\'workReportEntry3/\'),_latin1\'activityCategory/\',_latin1\'workReportEntry3/\'),_latin1\'activityMilestone/\',_latin1\'workReportEntry3/\'),_latin1\'/\',replace(`w`.`OBJECT_ID`,_latin1\'/\',_latin1\':\')) AS `object_id`,`ga`.`ACTIVITY_GROUP` AS `p$$parent`,_utf8\'org:opencrx:kernel:activity1:WorkReportEntry\' AS `dtype`,`w`.`MODIFIED_AT` AS `modified_at`,`w`.`MODIFIED_BY_` AS `modified_by_`,`w`.`CREATED_AT` AS `created_at`,`w`.`CREATED_BY_` AS `created_by_`,`w`.`ACCESS_LEVEL_BROWSE` AS `access_level_browse`,`w`.`ACCESS_LEVEL_UPDATE` AS `access_level_update`,`w`.`ACCESS_LEVEL_DELETE` AS `access_level_delete`,`w`.`OWNER_` AS `owner_`,`w`.`NAME` AS `name`,`w`.`DESCRIPTION` AS `description`,`w`.`STARTED_AT` AS `started_at`,`w`.`ENDED_AT` AS `ended_at`,`w`.`DURATION_HOURS` AS `duration_hours`,`w`.`DURATION_MINUTES` AS `duration_minutes`,(`w`.`DURATION_HOURS` + (`w`.`DURATION_MINUTES` / 60.0)) AS `duration_decimal`,concat(cast(`w`.`DURATION_HOURS` as char charset utf8),_utf8\':\',cast(`w`.`DURATION_MINUTES` as char charset utf8),char(39)) AS `duration_hh_mm`,`w`.`PAUSE_DURATION_HOURS` AS `pause_duration_hours`,`w`.`PAUSE_DURATION_MINUTES` AS `pause_duration_minutes`,(`w`.`PAUSE_DURATION_HOURS` + (`w`.`PAUSE_DURATION_MINUTES` / 60.0)) AS `pause_duration_decimal`,concat(cast(`w`.`PAUSE_DURATION_HOURS` as char charset utf8),_utf8\':\',cast(`w`.`PAUSE_DURATION_MINUTES` as char charset utf8),char(39)) AS `pause_duration_hh_mm`,`w`.`BILLABLE_AMOUNT` AS `billable_amount`,`w`.`BILLING_CURRENCY` AS `billing_currency`,`a`.`ACTIVITY_NUMBER` AS `activity_number`,`a`.`OBJECT_ID` AS `activity`,`ra`.`OBJECT_ID` AS `assignment`,`w`.`OBJECT_ID` AS `work_record`,`ra`.`RESRC` AS `resrc` from (((`crx`.`oocke1_workrecord` `w` join `crx`.`oocke1_resourceassignment` `ra` on((`w`.`P$$PARENT` = `ra`.`OBJECT_ID`))) join `crx`.`oocke1_activity` `a` on((`a`.`OBJECT_ID` = `ra`.`P$$PARENT`))) join `crx`.`oocke1_activitygroupass` `ga` on(((`a`.`OBJECT_ID` = `ga`.`P$$PARENT`) and (`ga`.`ACTIVITY_GROUP` is not null)))) union all select concat(replace(replace(replace(`ga`.`ACTIVITY_GROUP`,_latin1\'activityTracker/\',_latin1\'workReportEntry4/\'),_latin1\'activityCategory/\',_latin1\'workReportEntry4/\'),_latin1\'activityMilestone/\',_latin1\'workReportEntry4/\'),_latin1\'/\',replace(`w`.`OBJECT_ID`,_latin1\'/\',_latin1\':\')) AS `object_id`,`ga`.`ACTIVITY_GROUP` AS `p$$parent`,_utf8\'org:opencrx:kernel:activity1:WorkReportEntry\' AS `dtype`,`w`.`MODIFIED_AT` AS `modified_at`,`w`.`MODIFIED_BY_` AS `modified_by_`,`w`.`CREATED_AT` AS `created_at`,`w`.`CREATED_BY_` AS `created_by_`,`w`.`ACCESS_LEVEL_BROWSE` AS `access_level_browse`,`w`.`ACCESS_LEVEL_UPDATE` AS `access_level_update`,`w`.`ACCESS_LEVEL_DELETE` AS `access_level_delete`,`w`.`OWNER_` AS `owner_`,`w`.`NAME` AS `name`,`w`.`DESCRIPTION` AS `description`,`w`.`STARTED_AT` AS `started_at`,`w`.`ENDED_AT` AS `ended_at`,`w`.`DURATION_HOURS` AS `duration_hours`,`w`.`DURATION_MINUTES` AS `duration_minutes`,(`w`.`DURATION_HOURS` + (`w`.`DURATION_MINUTES` / 60.0)) AS `duration_decimal`,concat(cast(`w`.`DURATION_HOURS` as char charset utf8),_utf8\':\',cast(`w`.`DURATION_MINUTES` as char charset utf8),char(39)) AS `duration_hh_mm`,`w`.`PAUSE_DURATION_HOURS` AS `pause_duration_hours`,`w`.`PAUSE_DURATION_MINUTES` AS `pause_duration_minutes`,(`w`.`PAUSE_DURATION_HOURS` + (`w`.`PAUSE_DURATION_MINUTES` / 60.0)) AS `pause_duration_decimal`,concat(cast(`w`.`PAUSE_DURATION_HOURS` as char charset utf8),_utf8\':\',cast(`w`.`PAUSE_DURATION_MINUTES` as char charset utf8),char(39)) AS `pause_duration_hh_mm`,`w`.`BILLABLE_AMOUNT` AS `billable_amount`,`w`.`BILLING_CURRENCY` AS `billing_currency`,`a`.`ACTIVITY_NUMBER` AS `activity_number`,`a`.`OBJECT_ID` AS `activity`,`ra`.`OBJECT_ID` AS `assignment`,`w`.`OBJECT_ID` AS `work_record`,`ra`.`RESRC` AS `resrc` from (((`crx`.`oocke1_workrecord` `w` join `crx`.`oocke1_resourceassignment` `ra` on((`w`.`P$$PARENT` = `ra`.`OBJECT_ID`))) join `crx`.`oocke1_activity` `a` on((`a`.`OBJECT_ID` = `ra`.`P$$PARENT`))) join `crx`.`oocke1_activitygroupass` `ga` on(((`a`.`OBJECT_ID` = `ga`.`P$$PARENT`) and (`ga`.`ACTIVITY_GROUP` is not null))))
md5=65d5a72f468e5300a450eebb79596482
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
\n    CONCAT( REPLACE(a.p$$parent, \'activities/\', \'workReportEntry1/\') , \'/\' , REPLACE(w.object_id, \'/\', \':\') )
\n
\n
\n
\n    AS object_id,
\n    a.p$$parent AS p$$parent,
\n    \'org:opencrx:kernel:activity1:WorkReportEntry\' AS dtype,
\n    w.modified_at,
\n    w.modified_by_,
\n    w.created_at,
\n    w.created_by_,
\n    w.access_level_browse,
\n    w.access_level_update,
\n    w.access_level_delete,
\n    w.owner_,
\n    w.name,
\n    w.description,
\n    w.started_at,
\n    w.ended_at,
\n    w.duration_hours,
\n    w.duration_minutes,
\n    (w.duration_hours + (w.duration_minutes / 60.0)) AS duration_decimal,
\n    (CONCAT( CAST(w.duration_hours AS CHARACTER) , \':\' , CAST(w.duration_minutes AS CHARACTER) , CHAR(39) )) AS duration_hh_mm,
\n    w.pause_duration_hours,
\n    w.pause_duration_minutes,
\n    (w.pause_duration_hours + (w.pause_duration_minutes / 60.0)) AS pause_duration_decimal,
\n    (CONCAT( CAST(w.pause_duration_hours AS CHARACTER) , \':\' , CAST(w.pause_duration_minutes AS CHARACTER) , CHAR(39) )) AS pause_duration_hh_mm,
\n    w.billable_amount AS billable_amount,
\n    w.billing_currency AS billing_currency,
\n    a.activity_number,
\n    a.object_id AS activity,
\n    ra.object_id AS assignment,
\n    w.object_id AS work_record,
\n    ra.resrc AS resrc
\nFROM
\n   OOCKE1_WORKRECORD w
\nINNER JOIN
\n   OOCKE1_RESOURCEASSIGNMENT ra
\nON
\n   w.p$$parent = ra.object_id
\nINNER JOIN
\n   OOCKE1_ACTIVITY a
\nON
\n   a.object_id = ra.p$$parent
\n
\nUNION ALL
\n
\nSELECT
\n
\n
\n
\n    CONCAT( REPLACE(REPLACE(REPLACE(ga.activity_group, \'activityTracker/\', \'workReportEntry2/\'), \'activityCategory/\', \'workReportEntry2/\'), \'activityMilestone/\', \'workReportEntry2/\') , \'/\' , REPLACE(w.object_id, \'/\', \':\') )
\n
\n
\n
\n    AS object_id,
\n    ga.activity_group AS p$$parent,
\n    \'org:opencrx:kernel:activity1:WorkReportEntry\' AS dtype,
\n    w.modified_at,
\n    w.modified_by_,
\n    w.created_at,
\n    w.created_by_,
\n    w.access_level_browse,
\n    w.access_level_update,
\n    w.access_level_delete,
\n    w.owner_,
\n    w.name,
\n    w.description,
\n    w.started_at,
\n    w.ended_at,
\n    w.duration_hours,
\n    w.duration_minutes,
\n    (w.duration_hours + (w.duration_minutes / 60.0)) AS duration_decimal,
\n    (CONCAT( CAST(w.duration_hours AS CHARACTER) , \':\' , CAST(w.duration_minutes AS CHARACTER) , CHAR(39) )) AS duration_hh_mm,
\n    w.pause_duration_hours,
\n    w.pause_duration_minutes,
\n    (w.pause_duration_hours + (w.pause_duration_minutes / 60.0)) AS pause_duration_decimal,
\n    (CONCAT( CAST(w.pause_duration_hours AS CHARACTER) , \':\' , CAST(w.pause_duration_minutes AS CHARACTER) , CHAR(39) )) AS pause_duration_hh_mm,
\n    w.billable_amount AS billable_amount,
\n    w.billing_currency AS billing_currency,
\n    a.activity_number,
\n    a.object_id AS activity,
\n    ra.object_id AS assignment,
\n    w.object_id AS work_record,
\n    ra.resrc AS resrc
\nFROM
\n   OOCKE1_WORKRECORD w
\nINNER JOIN
\n   OOCKE1_RESOURCEASSIGNMENT ra
\nON
\n   w.p$$parent = ra.object_id
\nINNER JOIN
\n   OOCKE1_ACTIVITY a
\nON
\n   a.object_id = ra.p$$parent
\nINNER JOIN
\n    OOCKE1_ACTIVITYGROUPASS ga
\nON
\n   (a.object_id = ga.p$$parent) AND
\n   (ga.activity_group IS NOT NULL)
\n
\nUNION ALL
\n
\nSELECT
\n
\n
\n
\n    CONCAT( REPLACE(REPLACE(REPLACE(ga.activity_group, \'activityTracker/\', \'workReportEntry3/\'), \'activityCategory/\', \'workReportEntry3/\'), \'activityMilestone/\', \'workReportEntry3/\') , \'/\' , REPLACE(w.object_id, \'/\', \':\') )
\n
\n
\n
\n    AS object_id,
\n    ga.activity_group AS p$$parent,
\n    \'org:opencrx:kernel:activity1:WorkReportEntry\' AS dtype,
\n    w.modified_at,
\n    w.modified_by_,
\n    w.created_at,
\n    w.created_by_,
\n    w.access_level_browse,
\n    w.access_level_update,
\n    w.access_level_delete,
\n    w.owner_,
\n    w.name,
\n    w.description,
\n    w.started_at,
\n    w.ended_at,
\n    w.duration_hours,
\n    w.duration_minutes,
\n    (w.duration_hours + (w.duration_minutes / 60.0)) AS duration_decimal,
\n    (CONCAT( CAST(w.duration_hours AS CHARACTER) , \':\' , CAST(w.duration_minutes AS CHARACTER) , CHAR(39) )) AS duration_hh_mm,
\n    w.pause_duration_hours,
\n    w.pause_duration_minutes,
\n    (w.pause_duration_hours + (w.pause_duration_minutes / 60.0)) AS pause_duration_decimal,
\n    (CONCAT( CAST(w.pause_duration_hours AS CHARACTER) , \':\' , CAST(w.pause_duration_minutes AS CHARACTER) , CHAR(39) )) AS pause_duration_hh_mm,
\n    w.billable_amount AS billable_amount,
\n    w.billing_currency AS billing_currency,
\n    a.activity_number,
\n    a.object_id AS activity,
\n    ra.object_id AS assignment,
\n    w.object_id AS work_record,
\n    ra.resrc AS resrc
\nFROM
\n   OOCKE1_WORKRECORD w
\nINNER JOIN
\n   OOCKE1_RESOURCEASSIGNMENT ra
\nON
\n   w.p$$parent = ra.object_id
\nINNER JOIN
\n   OOCKE1_ACTIVITY a
\nON
\n   a.object_id = ra.p$$parent
\nINNER JOIN
\n    OOCKE1_ACTIVITYGROUPASS ga
\nON
\n   (a.object_id = ga.p$$parent) AND
\n   (ga.activity_group IS NOT NULL)
\n
\nUNION ALL
\n
\nSELECT
\n
\n
\n
\n    CONCAT( REPLACE(REPLACE(REPLACE(ga.activity_group, \'activityTracker/\', \'workReportEntry4/\'), \'activityCategory/\', \'workReportEntry4/\'), \'activityMilestone/\', \'workReportEntry4/\') , \'/\' , REPLACE(w.object_id, \'/\', \':\') )
\n
\n
\n
\n    AS object_id,
\n    ga.activity_group AS p$$parent,
\n    \'org:opencrx:kernel:activity1:WorkReportEntry\' AS dtype,
\n    w.modified_at,
\n    w.modified_by_,
\n    w.created_at,
\n    w.created_by_,
\n    w.access_level_browse,
\n    w.access_level_update,
\n    w.access_level_delete,
\n    w.owner_,
\n    w.name,
\n    w.description,
\n    w.started_at,
\n    w.ended_at,
\n    w.duration_hours,
\n    w.duration_minutes,
\n    (w.duration_hours + (w.duration_minutes / 60.0)) AS duration_decimal,
\n    (CONCAT( CAST(w.duration_hours AS CHARACTER) , \':\' , CAST(w.duration_minutes AS CHARACTER) , CHAR(39) )) AS duration_hh_mm,
\n    w.pause_duration_hours, w.pause_duration_minutes,
\n    (w.pause_duration_hours + (w.pause_duration_minutes / 60.0)) AS pause_duration_decimal,
\n    (CONCAT( CAST(w.pause_duration_hours AS CHARACTER) , \':\' , CAST(w.pause_duration_minutes AS CHARACTER) , CHAR(39) )) AS pause_duration_hh_mm,
\n    w.billable_amount AS billable_amount,
\n    w.billing_currency AS billing_currency,
\n    a.activity_number,
\n    a.object_id AS activity,
\n    ra.object_id AS assignment,
\n    w.object_id AS work_record,
\n    ra.resrc AS resrc
\nFROM
\n   OOCKE1_WORKRECORD w
\nINNER JOIN
\n   OOCKE1_RESOURCEASSIGNMENT ra
\nON
\n   w.p$$parent = ra.object_id
\nINNER JOIN
\n   OOCKE1_ACTIVITY a
\nON
\n   a.object_id = ra.p$$parent
\nINNER JOIN
\n    OOCKE1_ACTIVITYGROUPASS ga
\nON
\n   (a.object_id = ga.p$$parent) AND
\n   (ga.activity_group IS NOT NULL)
