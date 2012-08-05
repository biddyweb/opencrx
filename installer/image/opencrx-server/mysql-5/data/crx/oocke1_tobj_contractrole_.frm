TYPE=VIEW
query=select `crx`.`oocke1_contract_`.`OBJECT_ID` AS `object_id`,`crx`.`oocke1_contract_`.`IDX` AS `idx`,`crx`.`oocke1_contract_`.`CREATED_BY` AS `created_by`,`crx`.`oocke1_contract_`.`MODIFIED_BY` AS `modified_by`,`crx`.`oocke1_contract_`.`OWNER` AS `owner`,_utf8\'org:opencrx:kernel:contract1:CustomerContractRole\' AS `dtype` from `crx`.`oocke1_contract_`
md5=06e839fa116f3bfc8211881bb5d07d9d
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
\n    object_id,
\n    idx,
\n    created_by,
\n    modified_by,
\n    owner,
\n    \'org:opencrx:kernel:contract1:CustomerContractRole\' AS dtype
\nFROM
\n    OOCKE1_CONTRACT_
