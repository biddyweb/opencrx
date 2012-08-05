TYPE=VIEW
query=select concat(replace(`p`.`P$$PARENT`,_latin1\'products/\',_latin1\'priceListEntry/\'),_latin1\'/\',replace(`bp`.`OBJECT_ID`,_latin1\'/\',_latin1\':\')) AS `object_id`,`p`.`P$$PARENT` AS `p$$parent`,_utf8\'org:opencrx:kernel:product1:PriceListEntry\' AS `dtype`,`bp`.`MODIFIED_AT` AS `modified_at`,`bp`.`MODIFIED_BY_` AS `modified_by_`,`bp`.`CREATED_AT` AS `created_at`,`bp`.`CREATED_BY_` AS `created_by_`,`bp`.`ACCESS_LEVEL_BROWSE` AS `access_level_browse`,`bp`.`ACCESS_LEVEL_UPDATE` AS `access_level_update`,`bp`.`ACCESS_LEVEL_DELETE` AS `access_level_delete`,`bp`.`OWNER_` AS `owner_`,`bp`.`OBJUSAGE_` AS `objusage_`,`bp`.`PRICE` AS `price`,`bp`.`PRICE_CURRENCY` AS `price_currency`,`bp`.`PRICE_LEVEL_` AS `price_level_`,`bp`.`DESCRIPTION` AS `description`,`bp`.`QUANTITY_FROM` AS `quantity_from`,`bp`.`QUANTITY_TO` AS `quantity_to`,`bp`.`DISCOUNT` AS `discount`,`bp`.`DISCOUNT_IS_PERCENTAGE` AS `discount_is_percentage`,`bp`.`UOM` AS `uom`,`p`.`NAME` AS `product_name`,`p`.`DESCRIPTION` AS `product_description`,`p`.`OBJECT_ID` AS `product`,`p`.`SALES_TAX_TYPE` AS `sales_tax_type`,`bp`.`OBJECT_ID` AS `base_price` from (`crx`.`oocke1_productbaseprice` `bp` join `crx`.`oocke1_product` `p` on((`bp`.`P$$PARENT` = `p`.`OBJECT_ID`))) union all select concat(replace(`pp`.`P$$PARENT`,_latin1\'products/\',_latin1\'priceListEntry/\'),_latin1\'/\',replace(`bp`.`OBJECT_ID`,_latin1\'/\',_latin1\':\')) AS `object_id`,`pp`.`P$$PARENT` AS `p$$parent`,_utf8\'org:opencrx:kernel:product1:PriceListEntry\' AS `dtype`,`bp`.`MODIFIED_AT` AS `modified_at`,`bp`.`MODIFIED_BY_` AS `modified_by_`,`bp`.`CREATED_AT` AS `created_at`,`bp`.`CREATED_BY_` AS `created_by_`,`bp`.`ACCESS_LEVEL_BROWSE` AS `access_level_browse`,`bp`.`ACCESS_LEVEL_UPDATE` AS `access_level_update`,`bp`.`ACCESS_LEVEL_DELETE` AS `access_level_delete`,`bp`.`OWNER_` AS `owner_`,`bp`.`OBJUSAGE_` AS `objusage_`,`bp`.`PRICE` AS `price`,`bp`.`PRICE_CURRENCY` AS `price_currency`,`bp`.`PRICE_LEVEL_` AS `price_level_`,`bp`.`DESCRIPTION` AS `description`,`bp`.`QUANTITY_FROM` AS `quantity_from`,`bp`.`QUANTITY_TO` AS `quantity_to`,`bp`.`DISCOUNT` AS `discount`,`bp`.`DISCOUNT_IS_PERCENTAGE` AS `discount_is_percentage`,`bp`.`UOM` AS `uom`,`p`.`NAME` AS `product_name`,`p`.`DESCRIPTION` AS `product_description`,`p`.`OBJECT_ID` AS `product`,`p`.`SALES_TAX_TYPE` AS `sales_tax_type`,`bp`.`OBJECT_ID` AS `base_price` from ((`crx`.`oocke1_productbaseprice` `bp` join `crx`.`oocke1_product` `p` on((`bp`.`P$$PARENT` = `p`.`OBJECT_ID`))) join `crx`.`oocke1_product` `pp` on((`p`.`P$$PARENT` = `pp`.`OBJECT_ID`)))
md5=7675541f0c60c930c1ac2b196a924cd2
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
\n    CONCAT( REPLACE(p.p$$parent, \'products/\', \'priceListEntry/\') , \'/\' , REPLACE(bp.object_id, \'/\', \':\') )
\n
\n
\n
\n    AS object_id,
\n    p.p$$parent AS p$$parent,
\n    \'org:opencrx:kernel:product1:PriceListEntry\' AS dtype,
\n    bp.modified_at,
\n    bp.modified_by_,
\n    bp.created_at,
\n    bp.created_by_,
\n    bp.access_level_browse,
\n    bp.access_level_update,
\n    bp.access_level_delete,
\n    bp.owner_,
\n    bp.objusage_,
\n    bp.price,
\n    bp.price_currency,
\n    bp.price_level_,
\n    bp.description,
\n    bp.quantity_from,
\n    bp.quantity_to,
\n    bp.discount,
\n    bp.discount_is_percentage,
\n    bp.uom AS uom,
\n    p.name AS product_name,
\n    p.description AS product_description,
\n    p.object_id AS product,
\n    p.sales_tax_type,
\n    bp.object_id AS base_price
\nFROM
\n    OOCKE1_PRODUCTBASEPRICE bp
\nINNER JOIN
\n    OOCKE1_PRODUCT p
\nON
\n    bp.p$$parent = p.object_id
\n
\nUNION ALL
\n
\nSELECT
\n
\n
\n
\n    CONCAT( REPLACE(pp.p$$parent, \'products/\', \'priceListEntry/\') , \'/\' , REPLACE(bp.object_id, \'/\', \':\') )
\n
\n
\n
\n    AS object_id,
\n    pp.p$$parent AS p$$parent,
\n    \'org:opencrx:kernel:product1:PriceListEntry\' AS dtype,
\n    bp.modified_at,
\n    bp.modified_by_,
\n    bp.created_at,
\n    bp.created_by_,
\n    bp.access_level_browse,
\n    bp.access_level_update,
\n    bp.access_level_delete,
\n    bp.owner_,
\n    bp.objusage_,
\n    bp.price,
\n    bp.price_currency,
\n    bp.price_level_,
\n    bp.description,
\n    bp.quantity_from,
\n    bp.quantity_to,
\n    bp.discount,
\n    bp.discount_is_percentage,
\n    bp.uom AS uom,
\n    p.name AS product_name,
\n    p.description AS product_description,
\n    p.object_id AS product,
\n    p.sales_tax_type,
\n    bp.object_id AS base_price
\nFROM
\n    OOCKE1_PRODUCTBASEPRICE bp
\nINNER JOIN
\n    OOCKE1_PRODUCT p
\nON
\n    bp.p$$parent = p.object_id
\nINNER JOIN
\n    OOCKE1_PRODUCT pp
\nON
\n    p.p$$parent = pp.object_id
