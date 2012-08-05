TYPE=VIEW
query=select replace(`p`.`OBJECT_ID`,_latin1\'p2/\',_latin1\'propertySetEntry1/\') AS `object_id`,`ps`.`P$$PARENT` AS `p$$parent`,`p`.`CREATED_AT` AS `created_at`,`p`.`CREATED_BY_` AS `created_by_`,`p`.`MODIFIED_AT` AS `modified_at`,`p`.`MODIFIED_BY_` AS `modified_by_`,replace(replace(`p`.`DTYPE`,_latin1\'org:opencrx:kernel:base:\',_latin1\'org:opencrx:kernel:generic:\'),_latin1\'Property\',_latin1\'PropertySetEntry\') AS `dtype`,`p`.`ACCESS_LEVEL_BROWSE` AS `access_level_browse`,`p`.`ACCESS_LEVEL_UPDATE` AS `access_level_update`,`p`.`ACCESS_LEVEL_DELETE` AS `access_level_delete`,`p`.`OWNER_` AS `owner_`,`p`.`NAME` AS `property_name`,`p`.`DESCRIPTION` AS `property_description`,`ps`.`NAME` AS `property_set_name`,`ps`.`DESCRIPTION` AS `property_set_description`,`p`.`STRING_VALUE` AS `string_value`,`p`.`INTEGER_VALUE` AS `integer_value`,`p`.`BOOLEAN_VALUE` AS `boolean_value`,`p`.`URI_VALUE` AS `uri_value`,`p`.`DECIMAL_VALUE` AS `decimal_value`,`p`.`REFERENCE_VALUE` AS `reference_value`,`p`.`DATE_VALUE` AS `date_value`,`p`.`DATE_TIME_VALUE` AS `date_time_value`,`p`.`OBJECT_ID` AS `property` from (`crx`.`oocke1_property` `p` join `crx`.`oocke1_propertyset` `ps` on((`p`.`P$$PARENT` = `ps`.`OBJECT_ID`))) union all select replace(`p`.`OBJECT_ID`,_latin1\'p3/\',_latin1\'propertySetEntry2/\') AS `object_id`,`ps`.`P$$PARENT` AS `p$$parent`,`p`.`CREATED_AT` AS `created_at`,`p`.`CREATED_BY_` AS `created_by_`,`p`.`MODIFIED_AT` AS `modified_at`,`p`.`MODIFIED_BY_` AS `modified_by_`,replace(replace(`p`.`DTYPE`,_latin1\'org:opencrx:kernel:base:\',_latin1\'org:opencrx:kernel:generic:\'),_latin1\'Property\',_latin1\'PropertySetEntry\') AS `dtype`,`p`.`ACCESS_LEVEL_BROWSE` AS `access_level_browse`,`p`.`ACCESS_LEVEL_UPDATE` AS `access_level_update`,`p`.`ACCESS_LEVEL_DELETE` AS `access_level_delete`,`p`.`OWNER_` AS `owner_`,`p`.`NAME` AS `property_name`,`p`.`DESCRIPTION` AS `property_description`,`ps`.`NAME` AS `property_set_name`,`ps`.`DESCRIPTION` AS `property_set_description`,`p`.`STRING_VALUE` AS `string_value`,`p`.`INTEGER_VALUE` AS `integer_value`,`p`.`BOOLEAN_VALUE` AS `boolean_value`,`p`.`URI_VALUE` AS `uri_value`,`p`.`DECIMAL_VALUE` AS `decimal_value`,`p`.`REFERENCE_VALUE` AS `reference_value`,`p`.`DATE_VALUE` AS `date_value`,`p`.`DATE_TIME_VALUE` AS `date_time_value`,`p`.`OBJECT_ID` AS `property` from (`crx`.`oocke1_property` `p` join `crx`.`oocke1_propertyset` `ps` on((`p`.`P$$PARENT` = `ps`.`OBJECT_ID`)))
md5=9d8acbd3e27661261bccc0c25aeac217
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
\n    REPLACE(p.object_id, \'p2/\', \'propertySetEntry1/\')
\n
\n
\n
\n    AS object_id,
\n    ps.p$$parent AS p$$parent,
\n    p.created_at,
\n    p.created_by_,
\n    p.modified_at,
\n    p.modified_by_,
\n
\n
\n
\n    REPLACE(REPLACE(p.dtype, \'org:opencrx:kernel:base:\', \'org:opencrx:kernel:generic:\'), \'Property\', \'PropertySetEntry\')
\n
\n
\n
\n    AS dtype,
\n    p.access_level_browse,
\n    p.access_level_update,
\n    p.access_level_delete,
\n    p.owner_,
\n    p.name AS property_name,
\n    p.description AS property_description,
\n    ps.name AS property_set_name,
\n    ps.description AS property_set_description,
\n    p.string_value,
\n    p.integer_value,
\n    p.boolean_value,
\n    p.uri_value,
\n    p.decimal_value,
\n    p.reference_value,
\n    p.date_value,
\n    p.date_time_value,
\n    p.object_id AS property
\nFROM
\n    OOCKE1_PROPERTY p
\nINNER JOIN
\n    OOCKE1_PROPERTYSET ps
\nON
\n    p.p$$parent = ps.object_id
\n
\nUNION ALL
\n
\nSELECT
\n
\n
\n
\n    REPLACE(p.object_id, \'p3/\', \'propertySetEntry2/\')
\n
\n
\n
\n    AS object_id,
\n    ps.p$$parent AS p$$parent,
\n    p.created_at,
\n    p.created_by_,
\n    p.modified_at,
\n    p.modified_by_,
\n
\n
\n
\n    REPLACE(REPLACE(p.dtype, \'org:opencrx:kernel:base:\', \'org:opencrx:kernel:generic:\'), \'Property\', \'PropertySetEntry\')
\n
\n
\n
\n    AS dtype,
\n    p.access_level_browse,
\n    p.access_level_update,
\n    p.access_level_delete,
\n    p.owner_,
\n    p.name AS property_name,
\n    p.description AS property_description,
\n    ps.name AS property_set_name,
\n    ps.description AS property_set_description,
\n    p.string_value,
\n    p.integer_value,
\n    p.boolean_value,
\n    p.uri_value,
\n    p.decimal_value,
\n    p.reference_value,
\n    p.date_value,
\n    p.date_time_value,
\n    p.object_id AS property
\nFROM
\n    OOCKE1_PROPERTY p
\nINNER JOIN
\n    OOCKE1_PROPERTYSET ps
\nON
\n    p.p$$parent = ps.object_id
