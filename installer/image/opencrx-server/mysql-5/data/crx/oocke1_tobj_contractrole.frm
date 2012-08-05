TYPE=VIEW
query=select concat(replace(`c`.`P$$PARENT`,_latin1\'contracts/\',_latin1\'contractRole/\'),_latin1\'/\',replace(`dhn`.`OBJECT_ID`,_latin1\'/\',_latin1\':\'),_latin1\':\',replace(`c`.`OBJECT_ID`,_latin1\'/\',_latin1\':\')) AS `object_id`,`c`.`P$$PARENT` AS `p$$parent`,_utf8\'org:opencrx:kernel:contract1:CustomerContractRole\' AS `dtype`,`c`.`MODIFIED_AT` AS `modified_at`,`c`.`MODIFIED_BY_` AS `modified_by_`,`c`.`CREATED_AT` AS `created_at`,`c`.`CREATED_BY_` AS `created_by_`,`c`.`ACCESS_LEVEL_BROWSE` AS `access_level_browse`,`c`.`ACCESS_LEVEL_UPDATE` AS `access_level_update`,`c`.`ACCESS_LEVEL_DELETE` AS `access_level_delete`,`c`.`OWNER_` AS `owner_`,`c`.`CUSTOMER` AS `account`,`dhn`.`OBJECT_ID` AS `contract_reference_holder`,`dhn`.`CONTRACT` AS `contract` from ((`crx`.`oocke1_depotholder_` `dhn` join `crx`.`oocke1_contract` `c` on((`c`.`OBJECT_ID` = `dhn`.`CONTRACT`))) join `crx`.`oocke1_account` `a` on((`c`.`CUSTOMER` = `a`.`OBJECT_ID`))) union all select concat(replace(`c`.`P$$PARENT`,_latin1\'contracts/\',_latin1\'contractRole/\'),_latin1\'/\',replace(`dgn`.`OBJECT_ID`,_latin1\'/\',_latin1\':\'),_latin1\':\',replace(`c`.`OBJECT_ID`,_latin1\'/\',_latin1\':\')) AS `object_id`,`c`.`P$$PARENT` AS `p$$parent`,_utf8\'org:opencrx:kernel:contract1:CustomerContractRole\' AS `dtype`,`c`.`MODIFIED_AT` AS `modified_at`,`c`.`MODIFIED_BY_` AS `modified_by_`,`c`.`CREATED_AT` AS `created_at`,`c`.`CREATED_BY_` AS `created_by_`,`c`.`ACCESS_LEVEL_BROWSE` AS `access_level_browse`,`c`.`ACCESS_LEVEL_UPDATE` AS `access_level_update`,`c`.`ACCESS_LEVEL_DELETE` AS `access_level_delete`,`c`.`OWNER_` AS `owner_`,`c`.`CUSTOMER` AS `account`,`dgn`.`OBJECT_ID` AS `contract_reference_holder`,`dgn`.`CONTRACT` AS `contract` from ((`crx`.`oocke1_depotgroup_` `dgn` join `crx`.`oocke1_contract` `c` on((`c`.`OBJECT_ID` = `dgn`.`CONTRACT`))) join `crx`.`oocke1_account` `a` on((`c`.`CUSTOMER` = `a`.`OBJECT_ID`))) union all select concat(replace(`c`.`P$$PARENT`,_latin1\'contracts/\',_latin1\'contractRole/\'),_latin1\'/\',replace(`dn`.`OBJECT_ID`,_latin1\'/\',_latin1\':\'),_latin1\':\',replace(`c`.`OBJECT_ID`,_latin1\'/\',_latin1\':\')) AS `object_id`,`c`.`P$$PARENT` AS `p$$parent`,_utf8\'org:opencrx:kernel:contract1:CustomerContractRole\' AS `dtype`,`c`.`MODIFIED_AT` AS `modified_at`,`c`.`MODIFIED_BY_` AS `modified_by_`,`c`.`CREATED_AT` AS `created_at`,`c`.`CREATED_BY_` AS `created_by_`,`c`.`ACCESS_LEVEL_BROWSE` AS `access_level_browse`,`c`.`ACCESS_LEVEL_UPDATE` AS `access_level_update`,`c`.`ACCESS_LEVEL_DELETE` AS `access_level_delete`,`c`.`OWNER_` AS `owner_`,`c`.`CUSTOMER` AS `account`,`dn`.`OBJECT_ID` AS `contract_reference_holder`,`dn`.`CONTRACT` AS `contract` from ((`crx`.`oocke1_depot_` `dn` join `crx`.`oocke1_contract` `c` on((`c`.`OBJECT_ID` = `dn`.`CONTRACT`))) join `crx`.`oocke1_account` `a` on((`c`.`CUSTOMER` = `a`.`OBJECT_ID`)))
md5=d141755f320fc8480bb2faedee2e6fed
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
\n    CONCAT( REPLACE(c.p$$parent, \'contracts/\', \'contractRole/\') , \'/\' , REPLACE(dhn.object_id, \'/\', \':\') , \':\' , REPLACE(c.object_id, \'/\', \':\') )
\n
\n
\n
\n    AS object_id,
\n    c.p$$parent AS p$$parent,
\n    \'org:opencrx:kernel:contract1:CustomerContractRole\' AS dtype,
\n    c.modified_at,
\n    c.modified_by_,
\n    c.created_at,
\n    c.created_by_,
\n    c.access_level_browse,
\n    c.access_level_update,
\n    c.access_level_delete,
\n    c.owner_,
\n    c.customer AS account,
\n    dhn.object_id AS contract_reference_holder,
\n    dhn.contract
\nFROM
\n    OOCKE1_DEPOTHOLDER_ dhn
\nINNER JOIN
\n    OOCKE1_CONTRACT c
\nON
\n    c.object_id = dhn.contract
\nINNER JOIN
\n    OOCKE1_ACCOUNT a
\nON
\n    c.customer = a.object_id
\n
\nUNION ALL
\n
\nSELECT
\n
\n
\n
\n    CONCAT( REPLACE(c.p$$parent, \'contracts/\', \'contractRole/\') , \'/\' , REPLACE(dgn.object_id, \'/\', \':\') , \':\' , REPLACE(c.object_id, \'/\', \':\') )
\n
\n
\n
\n    AS object_id,
\n    c.p$$parent AS p$$parent,
\n    \'org:opencrx:kernel:contract1:CustomerContractRole\' AS dtype,
\n    c.modified_at,
\n    c.modified_by_,
\n    c.created_at,
\n    c.created_by_,
\n    c.access_level_browse,
\n    c.access_level_update,
\n    c.access_level_delete,
\n    c.owner_,
\n    c.customer AS account,
\n    dgn.object_id AS contract_reference_holder,
\n    dgn.contract
\nFROM
\n    OOCKE1_DEPOTGROUP_ dgn
\nINNER JOIN
\n    OOCKE1_CONTRACT c
\nON
\n    c.object_id = dgn.contract
\nINNER JOIN
\n    OOCKE1_ACCOUNT a
\nON
\n    c.customer = a.object_id
\n
\nUNION ALL
\n
\nSELECT
\n
\n
\n
\n    CONCAT( REPLACE(c.p$$parent, \'contracts/\', \'contractRole/\') , \'/\' , REPLACE(dn.object_id, \'/\', \':\') , \':\' , REPLACE(c.object_id, \'/\', \':\') )
\n
\n
\n
\n    AS object_id,
\n    c.p$$parent AS p$$parent,
\n    \'org:opencrx:kernel:contract1:CustomerContractRole\' AS dtype,
\n    c.modified_at,
\n    c.modified_by_,
\n    c.created_at,
\n    c.created_by_,
\n    c.access_level_browse,
\n    c.access_level_update,
\n    c.access_level_delete,
\n    c.owner_,
\n    c.customer AS account,
\n    dn.object_id AS contract_reference_holder,
\n    dn.contract
\nFROM
\n    OOCKE1_DEPOT_ dn
\nINNER JOIN
\n    OOCKE1_CONTRACT c
\nON
\n    c.object_id = dn.contract
\nINNER JOIN
\n    OOCKE1_ACCOUNT a
\nON
\n    (c.customer = a.object_id)
