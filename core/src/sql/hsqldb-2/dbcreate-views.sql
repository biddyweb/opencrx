-- This software is published under the BSD license
-- as listed below.
--
-- Copyright (c) 2004-2010, CRIXP Corp., Switzerland
-- All rights reserved.
--
-- Redistribution and use in source and binary forms, with or without
-- modification, are permitted provided that the following conditions
-- are met:
--
-- * Redistributions of source code must retain the above copyright
-- notice, this list of conditions and the following disclaimer.
--
-- * Redistributions in binary form must reproduce the above copyright
-- notice, this list of conditions and the following disclaimer in
-- the documentation and/or other materials provided with the
-- distribution.
--
-- * Neither the name of CRIXP Corp. nor the names of the contributors
-- to openCRX may be used to endorse or promote products derived
-- from this software without specific prior written permission
--
-- THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
-- CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
-- INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
-- MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
-- DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS
-- BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
-- EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
-- TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES, LOSS OF USE,
-- DATA, OR PROFITS, OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
-- ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
-- OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
-- OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
-- POSSIBILITY OF SUCH DAMAGE.
--
-- ------------------
--
-- This product includes software developed by the Apache Software
-- Foundation (http://www.apache.org/).                                      
--
-- This product includes software developed by contributors to
-- openMDX (http://www.openmdx.org/)                                         
DROP VIEW OOCKE1_JOIN_ACCTHASPROD ;
DROP VIEW OOCKE1_JOIN_ACCTHASASSACT ;
DROP VIEW OOCKE1_JOIN_ACTGCONTAINSACT ;
DROP VIEW OOCKE1_JOIN_ACTGCONTAINSFLUP ;
DROP VIEW OOCKE1_JOIN_ACTGCONTAINSNOTE ;
DROP VIEW OOCKE1_JOIN_CBHASBK ;
DROP VIEW OOCKE1_JOIN_CLFCLASSIFIESTELT ;
DROP VIEW OOCKE1_JOIN_CPOSHASPOSMOD ;
DROP VIEW OOCKE1_JOIN_DEPGCONTAINSDEP ;
DROP VIEW OOCKE1_JOIN_DEPGCONTAINSDEPG ;
DROP VIEW OOCKE1_JOIN_DEPPOSHASSBK ;
DROP VIEW OOCKE1_JOIN_DEPPOSHASBK ;
DROP VIEW OOCKE1_JOIN_ENTITYCONTAINSDEP ;
DROP VIEW OOCKE1_JOIN_FLDCONTAINSFLD ;
DROP VIEW OOCKE1_JOIN_HOMEHASASSACT ;
DROP VIEW OOCKE1_JOIN_NSCONTAINSELT ;
DROP VIEW OOCKE1_TOBJ_ACTIVITYLINKFROM ;
DROP VIEW OOCKE1_JOIN_SEGCONTAINSFAC ;
DROP VIEW OOCKE1_TOBJ_ACTIVITYLINKFROM_ ;
DROP VIEW OOCKE1_TOBJ_CONTRACTROLE ;
DROP VIEW OOCKE1_TOBJ_CONTRACTROLE_ ;
DROP VIEW OOCKE1_TOBJ_DOCFLDENTRY ;
DROP VIEW OOCKE1_TOBJ_DOCFLDENTRY_ ;
DROP VIEW OOCKE1_TOBJ_ACCTMEMBERSHIP ;
DROP VIEW OOCKE1_TOBJ_ACCTMEMBERSHIP1 ;
DROP VIEW OOCKE1_TOBJ_ACCTMEMBERSHIP_D4 ;
DROP VIEW OOCKE1_TOBJ_ACCTMEMBERSHIP_D3 ;
DROP VIEW OOCKE1_TOBJ_ACCTMEMBERSHIP_D2 ;
DROP VIEW OOCKE1_TOBJ_ACCTMEMBERSHIP_D1 ;
DROP VIEW OOCKE1_TOBJ_ACCTMEMBERSHIP_ ;
DROP VIEW OOCKE1_TOBJ_CONTRACTLNKFROM ;
DROP VIEW OOCKE1_TOBJ_CONTRACTLNKFROM_ ;
DROP VIEW OOCKE1_TOBJ_LNKITEMLNKFROM ;
DROP VIEW OOCKE1_TOBJ_LNKITEMLNKFROM_ ;
DROP VIEW OOCKE1_TOBJ_PRICELISTENTRY ;
DROP VIEW OOCKE1_TOBJ_PRICELISTENTRY_ ;
DROP VIEW OOCKE1_TOBJ_PROPERTYSETENTRY ;
DROP VIEW OOCKE1_TOBJ_PROPERTYSETENTRY_ ;
DROP VIEW OOCKE1_TOBJ_SEARCHINDEXENTRY ;
DROP VIEW OOCKE1_TOBJ_SEARCHINDEXENTRY_ ;
DROP VIEW OOCKE1_JOIN_HOMEHASASSCONTR ;
DROP VIEW OOCKE1_JOIN_ACCTHASASSCONTR ;

DROP VIEW OOCKE1_JOIN_ACCTHASASSBUDGET ;

DROP VIEW OOCKE1_JOIN_SEGCONTAINSBU ;

DROP VIEW OOCKE1_JOIN_BUHASADR ;

DROP VIEW OOCKE1_JOIN_IITEMHASBOOKING ;

DROP VIEW OOCKE1_JOIN_RESHASASSIGNEDACT ;

DROP VIEW OOCKE1_JOIN_SEGCONTAINSADR ;

DROP VIEW OOCKE1_JOIN_ACTGISCREATEDBY ;

DROP VIEW OOMSE2_TOBJ_USERS ;

DROP VIEW OOMSE2_TOBJ_USERS_ALL ;

DROP VIEW OOMSE2_TOBJ_ROLES ;

DROP VIEW OOCKE1_JOIN_DEPREPITMHASBK ;

DROP VIEW OOCKE1_JOIN_DEPREPITMHASSBK ;

DROP VIEW OOCKE1_JOIN_FILTERINCLDESCONTR ;

DROP VIEW OOCKE1_JOIN_RESCONTAINSWRE ;

DROP VIEW OOCKE1_JOIN_ACTCONTAINSWRE ;

DROP VIEW OOCKE1_JOIN_ACTGCONTAINSWRE ;

DROP VIEW OOCKE1_JOIN_SEGCONTAINSWRE ;


CREATE VIEW OOCKE1_JOIN_ACCTHASASSACT AS
SELECT
    act.object_id AS assigned_activity,
    acc.object_id AS account
FROM
    OOCKE1_ACTIVITY act
INNER JOIN
    OOCKE1_ACCOUNT acc
ON
    (act.assigned_to = acc.object_id)

UNION

SELECT
    act.object_id AS assigned_activity,
    adr."P$$PARENT" AS account
FROM
    OOCKE1_ACTIVITY act
INNER JOIN
    OOCKE1_ADDRESS adr
ON
    (adr.object_id = act.sender)

UNION

SELECT
    (p0."P$$PARENT") AS assigned_activity,
    acc.object_id AS account
FROM
    OOCKE1_ACTIVITYPARTY p0
INNER JOIN
    OOCKE1_ACCOUNT acc
ON
    (acc.object_id = p0.party)

UNION

SELECT
    (p0."P$$PARENT") AS assigned_activity,
    adr."P$$PARENT" AS account
FROM
    OOCKE1_ACTIVITYPARTY p0
INNER JOIN
    OOCKE1_ADDRESS adr
ON
    (adr.object_id = p0.party)

UNION

SELECT
    act.object_id AS assigned_activity,
    c0.customer AS account
FROM
    OOCKE1_ACTIVITY_ act
INNER JOIN
    OOCKE1_CONTRACT c0
ON
    (act.contract = c0.object_id)

UNION

SELECT
    act.object_id AS assigned_activity,
    acc.object_id AS account
FROM
    OOCKE1_ACTIVITY act
INNER JOIN
    OOCKE1_ACCOUNT acc
ON
    (act.rep_contact = acc.object_id)

UNION

SELECT
    act.object_id AS assigned_activity,
    acc.object_id AS account
FROM
    OOCKE1_ACTIVITY act
INNER JOIN
    OOCKE1_ACCOUNT acc
ON
    (act.rep_acct = acc.object_id) ;



CREATE VIEW OOCKE1_JOIN_ACCTHASPROD AS
SELECT DISTINCT
    p.object_id AS product,
    a.object_id AS account
FROM
    OOCKE1_PRODUCT p, OOCKE1_ACCOUNT a, OOCKE1_CONTRACT c, OOCKE1_CONTRACTPOSITION cp
WHERE
    (cp.product = p.object_id) AND
    (cp."P$$PARENT" = c.object_id) AND
    (c.customer = a.object_id) ;


CREATE VIEW OOCKE1_JOIN_ACTGCONTAINSACT AS
SELECT DISTINCT
    (ga."P$$PARENT") AS filtered_activity,
    ga.activity_group AS activity_group
FROM
    OOCKE1_ACTIVITYGROUPASS ga ;


CREATE VIEW OOCKE1_JOIN_ACTGCONTAINSFLUP AS
SELECT DiSTINCT
    f.object_id AS follow_up,
    g.object_id AS activity_group
FROM
    OOCKE1_ACTIVITYFOLLOWUP f
INNER JOIN
    OOCKE1_ACTIVITYGROUPASS ga
ON
    (f."P$$PARENT" = ga."P$$PARENT")
INNER JOIN
    OOCKE1_ACTIVITYGROUP g
ON
    (ga.activity_group = g.object_id) ;


CREATE VIEW OOCKE1_JOIN_ACTGCONTAINSNOTE AS
SELECT DISTINCT
    n.object_id AS activity_note,
    g.object_id AS activity_group
FROM
    OOCKE1_NOTE n
INNER JOIN
    OOCKE1_ACTIVITYGROUPASS ga
ON
    (n."P$$PARENT" = ga."P$$PARENT")
INNER JOIN
    OOCKE1_ACTIVITYGROUP g
ON
    (ga.activity_group = g.object_id) ;


CREATE VIEW OOCKE1_JOIN_CBHASBK AS
SELECT
    b.object_id AS booking,
    cb.object_id AS cb
FROM
    OOCKE1_BOOKING b
INNER JOIN
    OOCKE1_COMPOUNDBOOKING cb
ON
    (b.cb = cb.object_id) ;


CREATE VIEW OOCKE1_JOIN_CLFCLASSIFIESTELT AS
SELECT
    c.object_id AS classifier,
    e.object_id AS typed_element
FROM
    OOCKE1_MODELELEMENT c
INNER JOIN
    OOCKE1_MODELELEMENT e
ON
    (c.object_id = e.type) ;


CREATE VIEW OOCKE1_JOIN_CPOSHASPOSMOD AS
SELECT
    p.object_id AS "position",
    pm.object_id AS position_modification
FROM
    OOCKE1_CONTRACTPOSITION p
INNER JOIN
    OOCKE1_CONTRACTPOSMOD pm
ON
    (p.object_id = pm.involved) ;


CREATE VIEW OOCKE1_JOIN_DEPGCONTAINSDEP AS
SELECT
    d.object_id AS depot,
    dg.object_id AS depot_group
FROM
    OOCKE1_DEPOT d
INNER JOIN
    OOCKE1_DEPOTGROUP dg
ON
    (d.depot_group = dg.object_id) ;


CREATE VIEW OOCKE1_JOIN_DEPGCONTAINSDEPG AS
SELECT
    dg.object_id AS depot_group,
    dgp.object_id AS parent
FROM
    OOCKE1_DEPOTGROUP dg
INNER JOIN
    OOCKE1_DEPOTGROUP dgp
ON
    (dg."P$$PARENT" = dgp.object_id) ;


CREATE VIEW OOCKE1_JOIN_DEPPOSHASBK AS
SELECT
    b.object_id AS booking,
    b.position AS depot_position
FROM
    OOCKE1_BOOKING b ;


CREATE VIEW OOCKE1_JOIN_DEPPOSHASSBK AS
SELECT
    b.object_id AS simple_booking,
    b.position AS depot_position
FROM
    OOCKE1_SIMPLEBOOKING b ;


CREATE VIEW OOCKE1_JOIN_ENTITYCONTAINSDEP AS
SELECT
    (dh."P$$PARENT") AS entity,
    d.object_id AS depot
FROM
    OOCKE1_DEPOT d
INNER JOIN
    OOCKE1_DEPOTHOLDER dh
ON
    (d."P$$PARENT" = dh.object_id) ;


CREATE VIEW OOCKE1_JOIN_FLDCONTAINSFLD AS
SELECT
    f.object_id AS folder,
    fp.object_id AS parent
FROM
    OOCKE1_DOCUMENTFOLDER f
INNER JOIN
    OOCKE1_DOCUMENTFOLDER fp
ON
    (f.parent = fp.object_id) ;


CREATE VIEW OOCKE1_JOIN_HOMEHASASSACT AS
SELECT
    a.object_id AS assigned_activity,
    h0.object_id AS user_home
FROM
    OOCKE1_ACTIVITY a
INNER JOIN
    OOCKE1_USERHOME h0
ON
    (a.assigned_to = h0.contact)

UNION

SELECT
    a.object_id AS assigned_activity,
    h0.object_id AS user_home
FROM
    OOCKE1_ACTIVITY a
INNER JOIN
    OOCKE1_ADDRESS adr
ON
    (adr.object_id = a.sender)
INNER JOIN
    OOCKE1_USERHOME h0
ON
    (adr."P$$PARENT" = h0.contact)

UNION

SELECT
    a.object_id AS assigned_activity,
    h0.object_id AS user_home
FROM
    OOCKE1_ACTIVITYPARTY p0
INNER JOIN
    OOCKE1_USERHOME h0
ON
    (p0.party = h0.contact)
INNER JOIN
    OOCKE1_ACTIVITY a
ON
    (p0."P$$PARENT" = a.object_id)

UNION

SELECT
    a.object_id AS assigned_activity,
    h0.object_id AS user_home
FROM
    OOCKE1_ACTIVITYPARTY p0
INNER JOIN
    OOCKE1_ACTIVITY a
ON
    (p0."P$$PARENT" = a.object_id)
INNER JOIN
    OOCKE1_ADDRESS adr
ON
    (adr.object_id = p0.party)
INNER JOIN
    OOCKE1_USERHOME h0
ON
    (adr."P$$PARENT" = h0.contact)

UNION

SELECT
    a.object_id AS assigned_activity,
    h0.object_id AS user_home
FROM
    OOCKE1_ACTIVITY a
INNER JOIN
    OOCKE1_USERHOME h0
ON
    (a.rep_contact = h0.contact)

UNION

SELECT
    a.object_id AS assigned_activity,
    h0.object_id AS user_home
FROM
    OOCKE1_ACTIVITY a
INNER JOIN
    OOCKE1_USERHOME h0
ON
    (a.rep_acct = h0.contact) ;


CREATE VIEW OOCKE1_JOIN_IITEMHASBOOKING AS
SELECT
    b.object_id AS booking,
    i.object_id AS inventory_item
FROM
    OOCKE1_INVENTORYITEM i
INNER JOIN
    OOCKE1_BOOKING b
ON
    (b.origin = i.object_id) ;


CREATE VIEW OOCKE1_JOIN_NSCONTAINSELT AS
SELECT
    n.object_id AS namespace,
    e.object_id AS content
FROM
    OOCKE1_MODELELEMENT n
INNER JOIN
    OOCKE1_MODELELEMENT e
ON
    (e.container = n.object_id) ;


CREATE VIEW OOCKE1_JOIN_RESHASASSIGNEDACT AS
SELECT
    a.object_id AS assigned_activity,
    r.object_id AS resource
FROM
    OOCKE1_ACTIVITY a
INNER JOIN
    OOCKE1_RESOURCEASSIGNMENT ra
ON
    (ra."P$$PARENT" = a.object_id)
INNER JOIN
    OOCKE1_RESOURCE r
ON
    (ra.resrc = r.object_id) ;


CREATE VIEW OOCKE1_JOIN_SEGCONTAINSADR AS
SELECT
    adr.object_id AS address,
    act."P$$PARENT" AS segment
FROM
    OOCKE1_ADDRESS adr
INNER JOIN
    OOCKE1_ACCOUNT act
ON
    (adr."P$$PARENT" = act.object_id) ;


CREATE VIEW OOCKE1_JOIN_SEGCONTAINSFAC AS
SELECT
    f.object_id AS facility,
    b."P$$PARENT" AS segment
FROM
    OOCKE1_FACILITY f
INNER JOIN
    OOCKE1_BUILDINGUNIT b
ON
    (f."P$$PARENT" = b.object_id)

UNION ALL

SELECT
    f.object_id AS facility,
    bu1."P$$PARENT" AS segment
FROM
    OOCKE1_FACILITY f
INNER JOIN
    OOCKE1_BUILDINGUNIT bu2
ON
    (f."P$$PARENT" = bu2.object_id)
INNER JOIN
    OOCKE1_BUILDINGUNIT bu1
ON
   (bu2."P$$PARENT" = bu1.object_id) ;


CREATE VIEW OOCKE1_JOIN_HOMEHASASSCONTR AS
SELECT
    c.object_id AS assigned_contract,
    h.object_id AS user_home
FROM
    OOCKE1_CONTRACT c
INNER JOIN
    OOCKE1_USERHOME h
ON
    (c.sales_rep = h.contact)

UNION

SELECT
    c.object_id AS assigned_contract,
    h.object_id AS user_home
FROM
    OOCKE1_CONTRACT c
INNER JOIN
    OOCKE1_USERHOME h
ON
    (c.customer = h.contact)

UNION

SELECT
   c.object_id AS assigned_contract,
   h.object_id AS user_home
FROM
    OOCKE1_CONTRACT c
INNER JOIN
    OOCKE1_ACCOUNTASSIGNMENT ass
ON
    (ass."P$$PARENT" = c.object_id)
INNER JOIN
    OOCKE1_USERHOME h
ON
    (ass.account = h.contact) ;


CREATE VIEW OOCKE1_JOIN_ACCTHASASSCONTR AS
SELECT
    c.object_id AS assigned_contract,
    a.object_id AS account
FROM
    OOCKE1_CONTRACT c
INNER JOIN
    OOCKE1_ACCOUNT a
ON
    (c.customer = a.object_id)

UNION

SELECT
    c.object_id AS assigned_contract,
    a.object_id AS account
FROM
    OOCKE1_CONTRACT c
INNER JOIN
    OOCKE1_ACCOUNT a
ON
    (c.sales_rep = a.object_id)

UNION

SELECT
    c.object_id AS assigned_contract,
    a.object_id AS account
FROM
    OOCKE1_CONTRACT c
INNER JOIN
    OOCKE1_ACCOUNT a
ON
    (c.supplier = a.object_id)

UNION

SELECT
   c.object_id AS assigned_contract,
   ass.account AS account
FROM
    OOCKE1_CONTRACT c
INNER JOIN
    OOCKE1_ACCOUNTASSIGNMENT ass
ON
    (ass."P$$PARENT" = c.object_id) ;


CREATE VIEW OOCKE1_JOIN_ACCTHASASSBUDGET AS
SELECT
    b.object_id AS assigned_budget,
    a.object_id AS account
FROM
    OOCKE1_BUDGET b
INNER JOIN
    OOCKE1_ACCOUNT a
ON
    (b.account = a.object_id) ;


CREATE VIEW OOCKE1_JOIN_BUHASADR AS
SELECT
    adr.object_id AS assigned_address,
    bu.object_id AS building_unit
FROM
    OOCKE1_ADDRESS adr
INNER JOIN
    OOCKE1_BUILDINGUNIT bu
ON
    (adr.building = bu.object_id) ;


CREATE VIEW OOCKE1_JOIN_SEGCONTAINSBU AS
SELECT
    b.object_id AS building_unit,
    s.object_id AS segment
FROM
    OOCKE1_BUILDINGUNIT b
INNER JOIN
    OOCKE1_SEGMENT s
ON
    (b."P$$PARENT" = s.object_id)

UNION ALL

SELECT
    b.object_id AS building_unit,
    bp."P$$PARENT" AS segment
FROM
    OOCKE1_BUILDINGUNIT b
INNER JOIN
    OOCKE1_BUILDINGUNIT bp
ON
    (b."P$$PARENT" = bp.object_id) ;


CREATE VIEW OOCKE1_JOIN_ACTGISCREATEDBY AS
SELECT
    ac.activity_group AS activity_group,
    ac.object_id AS activity_creator
FROM
    OOCKE1_ACTIVITYCREATOR_ ac ;


CREATE VIEW OOCKE1_JOIN_DEPREPITMHASBK AS
SELECT
    ip.object_id AS item_position,
    b.object_id AS single_booking
FROM
    OOCKE1_DEPOTREPORTITEM ip
INNER JOIN
    OOCKE1_DEPOTREPORT r
ON
    (ip."P$$PARENT" = r.object_id)
INNER JOIN
    OOCKE1_BOOKINGPERIOD bp
ON
    (r.booking_period = bp.object_id)
INNER JOIN
    OOCKE1_BOOKING b
ON
    (b.position = ip.position) AND
    (b.value_date >= bp.period_starts_at) AND
    ((b.value_date < bp.period_ends_at_exclusive) OR (bp.period_ends_at_exclusive IS NULL)) AND
    ((b.booking_status >= r.booking_status_threshold) OR (r.booking_status_threshold = 0) OR (r.booking_status_threshold IS NULL)) ;


CREATE VIEW OOCKE1_JOIN_DEPREPITMHASSBK AS
SELECT
    ip.object_id AS item_position,
    b.object_id AS simple_booking
FROM
    OOCKE1_DEPOTREPORTITEM ip
INNER JOIN
    OOCKE1_DEPOTREPORT r
ON
    (ip."P$$PARENT" = r.object_id)
INNER JOIN
    OOCKE1_BOOKINGPERIOD bp
ON
    (r.booking_period = bp.object_id)
INNER JOIN
    OOCKE1_SIMPLEBOOKING b
ON
    (b.position = ip.position) AND
    (b.value_date >= bp.period_starts_at) AND
    ((b.value_date < bp.period_ends_at_exclusive) OR (bp.period_ends_at_exclusive IS NULL)) AND
    ((b.booking_status >= r.booking_status_threshold) OR (r.booking_status_threshold = 0) OR (r.booking_status_threshold IS NULL)) ;


CREATE VIEW OOCKE1_JOIN_FILTERINCLDESCONTR AS
SELECT
    f.object_id AS contract_filter,
    c.object_id AS filtered_contract
FROM
    OOCKE1_FILTER f
INNER JOIN
    OOCKE1_CONTRACT c
ON
    (1=1) ;


CREATE VIEW OOCKE1_JOIN_ACTGCONTAINSWRE AS
SELECT
  ga.activity_group AS activity_group,
  wr.object_id AS work_report_entry
FROM
  OOCKE1_WORKRECORD wr
INNER JOIN
  OOCKE1_RESOURCEASSIGNMENT ra
ON
  (wr."P$$PARENT" = ra.object_id)
INNER JOIN
  OOCKE1_ACTIVITYGROUPASS ga
ON
  (ga."P$$PARENT" = ra."P$$PARENT") ;


CREATE VIEW OOCKE1_JOIN_ACTCONTAINSWRE AS
SELECT
  (ra."P$$PARENT") AS activity,
  wr.object_id AS work_report_entry
FROM
  OOCKE1_WORKRECORD wr
INNER JOIN
  OOCKE1_RESOURCEASSIGNMENT ra
ON
  (wr."P$$PARENT" = ra.object_id) ;


CREATE VIEW OOCKE1_JOIN_SEGCONTAINSWRE AS
SELECT
  (a."P$$PARENT") AS segment,
  wr.object_id AS work_report_entry
FROM
  OOCKE1_WORKRECORD wr
INNER JOIN
  OOCKE1_RESOURCEASSIGNMENT ra
ON
  (wr."P$$PARENT" = ra.object_id)
INNER JOIN
  OOCKE1_ACTIVITY a
ON
  (ra."P$$PARENT" = a.object_id) ;


CREATE VIEW OOCKE1_JOIN_RESCONTAINSWRE AS
SELECT
  ra.resrc AS resource,
  wr.object_id AS work_report_entry
FROM
  OOCKE1_WORKRECORD wr
INNER JOIN
  OOCKE1_RESOURCEASSIGNMENT ra
ON
  (wr."P$$PARENT" = ra.object_id) ;


CREATE VIEW OOCKE1_TOBJ_ACTIVITYLINKFROM AS
SELECT



    REPLACE(a.object_id, 'activity/', 'activityLinkFrom/') || '/' || REPLACE(l.object_id, '/', ':')



    AS object_id,
    a.object_id AS "P$$PARENT",
    'org:opencrx:kernel:activity1:ActivityLinkFrom'



    AS dtype,
    l.modified_at,
    l.created_at,
    l.created_by_,
    l.modified_by_,
    l.access_level_browse,
    l.access_level_update,
    l.access_level_delete,
    l.owner_,
    100-l.activity_link_type AS activity_link_type,
    l.name,
    l.description,
    l."P$$PARENT" AS link_from,
    l.object_id AS link_to
FROM
    OOCKE1_ACTIVITY a,
    OOCKE1_ACTIVITYLINK l
WHERE
    l.link_to = a.object_id AND
    l.object_id = l.object_id ;


CREATE VIEW OOCKE1_TOBJ_ACTIVITYLINKFROM_ AS
SELECT
    object_id,
    idx,
    created_by,
    modified_by,
    owner,
    dtype
FROM
    OOCKE1_ACTIVITYLINK_ ;


CREATE VIEW OOCKE1_TOBJ_CONTRACTLNKFROM AS
SELECT



    CASE
        WHEN (SUBSTRING(c.object_id,1,5) = 'lead/') THEN REPLACE(c.object_id, 'lead/', 'linkFromLead/')
        WHEN (SUBSTRING(c.object_id,1,12) = 'opportunity/') THEN REPLACE(c.object_id, 'opportunity/', 'linkFromOpportunity/')
        WHEN (SUBSTRING(c.object_id,1,6) = 'quote/') THEN REPLACE(c.object_id, 'quote/', 'linkFromQuote/')
        WHEN (SUBSTRING(c.object_id,1,11) = 'salesOrder/') THEN REPLACE(c.object_id, 'salesOrder/', 'linkFromSalesOrder/')
        WHEN (SUBSTRING(c.object_id,1,8) = 'invoice/') THEN REPLACE(c.object_id, 'invoice/', 'linkFromInvoice/')
    END || '/' || REPLACE(l.object_id, '/', ':')



    AS object_id,
    c.object_id AS "P$$PARENT",
    'org:opencrx:kernel:contract1:ContractLinkFrom'



    AS dtype,
    l.modified_at,
    l.created_at,
    l.created_by_,
    l.modified_by_,
    l.access_level_browse,
    l.access_level_update,
    l.access_level_delete,
    l.owner_,
    100-l.link_type AS link_type,
    l.name,
    l.description,
    l.valid_from AS valid_from,
    l.valid_to AS valid_to,
    l."P$$PARENT" AS link_from,
    l.object_id AS link_to
FROM
    OOCKE1_CONTRACT c,
    OOCKE1_CONTRACTLINK l
WHERE
    l.link_to = c.object_id AND
    l.object_id = l.object_id ;


CREATE VIEW OOCKE1_TOBJ_CONTRACTLNKFROM_ AS
SELECT
    object_id,
    idx,
    created_by,
    modified_by,
    owner,
    dtype
FROM
    OOCKE1_CONTRACTLINK_ ;


CREATE VIEW OOCKE1_TOBJ_CONTRACTROLE AS
SELECT



    REPLACE(c."P$$PARENT", 'contracts/', 'contractRole/') || '/' || REPLACE(dhn.object_id, '/', ':') || ':' || REPLACE(c.object_id, '/', ':')



    AS object_id,
    c."P$$PARENT" AS "P$$PARENT",
    'org:opencrx:kernel:contract1:CustomerContractRole'



    AS dtype,
    c.modified_at,
    c.modified_by_,
    c.created_at,
    c.created_by_,
    c.access_level_browse,
    c.access_level_update,
    c.access_level_delete,
    c.owner_,
    c.customer AS account,
    dhn.object_id AS contract_reference_holder,
    dhn.contract
FROM
    OOCKE1_DEPOTHOLDER_ dhn
INNER JOIN
    OOCKE1_CONTRACT c
ON
    (c.object_id = dhn.contract)
INNER JOIN
    OOCKE1_ACCOUNT a
ON
    (c.customer = a.object_id)

UNION ALL

SELECT



    REPLACE(c."P$$PARENT", 'contracts/', 'contractRole/') || '/' || REPLACE(dgn.object_id, '/', ':') || ':' || REPLACE(c.object_id, '/', ':')



    AS object_id,
    c."P$$PARENT" AS "P$$PARENT",
    'org:opencrx:kernel:contract1:CustomerContractRole'



    AS dtype,
    c.modified_at,
    c.modified_by_,
    c.created_at,
    c.created_by_,
    c.access_level_browse,
    c.access_level_update,
    c.access_level_delete,
    c.owner_,
    c.customer AS account,
    dgn.object_id AS contract_reference_holder,
    dgn.contract
FROM
    OOCKE1_DEPOTGROUP_ dgn
INNER JOIN
    OOCKE1_CONTRACT c
ON
    (c.object_id = dgn.contract)
INNER JOIN
    OOCKE1_ACCOUNT a
ON
    (c.customer = a.object_id)

UNION ALL

SELECT



    REPLACE(c."P$$PARENT", 'contracts/', 'contractRole/') || '/' || REPLACE(dn.object_id, '/', ':') || ':' || REPLACE(c.object_id, '/', ':')



    AS object_id,
    c."P$$PARENT" AS "P$$PARENT",
    'org:opencrx:kernel:contract1:CustomerContractRole'



    AS dtype,
    c.modified_at,
    c.modified_by_,
    c.created_at,
    c.created_by_,
    c.access_level_browse,
    c.access_level_update,
    c.access_level_delete,
    c.owner_,
    c.customer AS account,
    dn.object_id AS contract_reference_holder,
    dn.contract
FROM
    OOCKE1_DEPOT_ dn
INNER JOIN
    OOCKE1_CONTRACT c
ON
    (c.object_id = dn.contract)
INNER JOIN
    OOCKE1_ACCOUNT a
ON
    (c.customer = a.object_id) ;


CREATE VIEW OOCKE1_TOBJ_CONTRACTROLE_ AS
SELECT
    object_id,
    idx,
    created_by,
    modified_by,
    owner,
    'org:opencrx:kernel:contract1:CustomerContractRole'



    AS dtype
FROM
    OOCKE1_CONTRACT_ ;


CREATE VIEW OOCKE1_TOBJ_DOCFLDENTRY AS
SELECT



    REPLACE(d_.folder, 'docFolder/', 'folderEntry/') || '/' || REPLACE(d.object_id, '/', ':')



    AS object_id,
    d_.folder AS "P$$PARENT",
    'org:opencrx:kernel:document1:DocumentFolderEntry'



    AS dtype,
    d.modified_at,
    d.modified_by_,
    d.created_at,
    d.created_by_,
    d.access_level_browse,
    d.access_level_update,
    d.access_level_delete,
    d.owner_,
    d.name AS name,
    d.description AS description,
    d.active_on AS valid_from,
    d.active_until AS valid_to,
    d.disabled AS disabled,
    d.object_id AS document,
    d.object_id AS based_on
FROM
    OOCKE1_DOCUMENT_ d_
INNER JOIN
    OOCKE1_DOCUMENT d
ON
    (d_.object_id = d.object_id)

UNION

SELECT



    REPLACE(dfa.document_folder, 'docFolder/', 'folderEntry/') || '/' || REPLACE(dfa.object_id, '/', ':')



    AS object_id,
    dfa.document_folder AS "P$$PARENT",
    'org:opencrx:kernel:document1:DocumentFolderEntry'



    AS dtype,
    dfa.modified_at,
    dfa.modified_by_,
    dfa.created_at,
    dfa.created_by_,
    dfa.access_level_browse,
    dfa.access_level_update,
    dfa.access_level_delete,
    dfa.owner_,
    dfa.name AS name,
    dfa.description AS description,
    dfa.valid_from AS valid_from,
    dfa.valid_to AS valid_to,
    dfa.disabled AS disabled,
    dfa."P$$PARENT" AS document,
    dfa.object_id AS based_on
FROM
    OOCKE1_DOCUMENTFOLDERASS dfa ;


CREATE VIEW OOCKE1_TOBJ_DOCFLDENTRY_ AS
SELECT
    object_id,
    idx,
    created_by,
    modified_by,
    owner,
    'org:opencrx:kernel:document1:DocumentFolderEntry'



    AS dtype
FROM
    OOCKE1_DOCUMENT_ d_

UNION

SELECT
    object_id,
    idx,
    created_by,
    modified_by,
    owner,
    'org:opencrx:kernel:document1:DocumentFolderEntry'



    AS dtype
FROM
    OOCKE1_DOCUMENTFOLDERASS_ dfa_ ;


CREATE VIEW OOCKE1_TOBJ_ACCTMEMBERSHIP_D1 AS
SELECT
    ass.account AS account,
    ass."P$$PARENT" AS "P$$PARENT"
FROM
    OOCKE1_ACCOUNTASSIGNMENT ass

UNION

SELECT
    (ass."P$$PARENT") AS account,
    ass.account AS "P$$PARENT"
FROM
    OOCKE1_ACCOUNTASSIGNMENT ass ;


CREATE VIEW OOCKE1_TOBJ_ACCTMEMBERSHIP_D2 AS
SELECT
    (ass0."P$$PARENT") AS account,
    ass."P$$PARENT" AS "P$$PARENT"
FROM
    OOCKE1_ACCOUNTASSIGNMENT ass0
INNER JOIN
    OOCKE1_TOBJ_ACCTMEMBERSHIP_D1 ass
ON
    (ass0.account = ass.account)

UNION

SELECT
    (ass0.account) AS account,
    ass."P$$PARENT" AS "P$$PARENT"
FROM
    OOCKE1_ACCOUNTASSIGNMENT ass0
INNER JOIN
    OOCKE1_TOBJ_ACCTMEMBERSHIP_D1 ass
ON
    (ass0."P$$PARENT" = ass.account) ;


CREATE VIEW OOCKE1_TOBJ_ACCTMEMBERSHIP_D3 AS
SELECT
    (ass0."P$$PARENT") AS account,
    ass."P$$PARENT" AS "P$$PARENT"
FROM
    OOCKE1_ACCOUNTASSIGNMENT ass0
INNER JOIN
    OOCKE1_TOBJ_ACCTMEMBERSHIP_D2 ass
ON
    (ass0.account = ass.account)

UNION

SELECT
    ass0.account AS account,
    ass."P$$PARENT" AS "P$$PARENT"
FROM
    OOCKE1_ACCOUNTASSIGNMENT ass0
INNER JOIN
    OOCKE1_TOBJ_ACCTMEMBERSHIP_D2 ass
ON
    (ass0."P$$PARENT" = ass.account) ;


CREATE VIEW OOCKE1_TOBJ_ACCTMEMBERSHIP_D4 AS
SELECT
    (ass0."P$$PARENT") AS account,
    ass."P$$PARENT" AS "P$$PARENT"
FROM
    OOCKE1_ACCOUNTASSIGNMENT ass0
INNER JOIN
    OOCKE1_TOBJ_ACCTMEMBERSHIP_D3 ass
ON
    (ass0.account = ass.account)

UNION

SELECT
    ass0.account AS account,
    ass."P$$PARENT" AS "P$$PARENT"
FROM
    OOCKE1_ACCOUNTASSIGNMENT ass0
INNER JOIN
    OOCKE1_TOBJ_ACCTMEMBERSHIP_D3 ass
ON
    (ass0."P$$PARENT" = ass.account) ;


CREATE VIEW OOCKE1_TOBJ_ACCTMEMBERSHIP1 AS
SELECT DISTINCT



    (ass0."P$$PARENT") || '*' || (ass0.object_id) || '*1'



    AS object_id,
    ass0."P$$PARENT" AS "P$$PARENT",
    ass0."P$$PARENT" AS account_from,
    ass0."P$$PARENT" AS account_from_id,
    ass0.account AS account_to,
    ass0.account AS account_to_id,
    ass0.modified_at AS created_at,
    ass0.created_by_,
    CASE WHEN ass0.modified_at > acct.modified_at THEN ass0.modified_at ELSE acct.modified_at END AS modified_at,
    ass0.modified_by_,
    'org:opencrx:kernel:account1:AccountMembership'



    AS dtype,
    ass0.access_level_browse,
    ass0.access_level_update,
    ass0.access_level_delete,
    ass0.owner_,
    ass0.name,
    ass0.description,
    ass0.quality,
    ass0.for_use_by_,
    ass0.valid_from,
    ass0.valid_to,
    ass0.object_id AS member,
    ass0.disabled,
    ass0.member_role_0,
    ass0.member_role_1,
    ass0.member_role_2,
    ass0.member_role_3,
    ass0.member_role_4,
    1 AS distance
FROM
    OOCKE1_ACCOUNTASSIGNMENT ass0
INNER JOIN
    OOCKE1_ACCOUNT acct
ON
    ass0.account = acct.object_id
WHERE
    (ass0.dtype = 'org:opencrx:kernel:account1:Member')

UNION ALL

SELECT DISTINCT



    (ass0.account) || '*' || (ass0.object_id) || '*-1'



    AS object_id,
    ass0.account AS "P$$PARENT",
    ass0."P$$PARENT" AS account_from,
    ass0."P$$PARENT" AS account_from_id,
    ass0.account AS account_to,
    ass0.account AS account_to_id,
    ass0.modified_at AS created_at,
    ass0.created_by_,
    CASE WHEN ass0.modified_at > acct.modified_at THEN ass0.modified_at ELSE acct.modified_at END AS modified_at,
    ass0.modified_by_,
    'org:opencrx:kernel:account1:AccountMembership'



    AS dtype,
    ass0.access_level_browse,
    ass0.access_level_update,
    ass0.access_level_delete,
    ass0.owner_,
    ass0.name,
    ass0.description,
    ass0.quality,
    ass0.for_use_by_,
    ass0.valid_from,
    ass0.valid_to,
    ass0.object_id AS member,
    ass0.disabled,
    ass0.member_role_0,
    ass0.member_role_1,
    ass0.member_role_2,
    ass0.member_role_3,
    ass0.member_role_4,
    -1 AS distance
FROM
    OOCKE1_ACCOUNTASSIGNMENT ass0
INNER JOIN
    OOCKE1_ACCOUNT acct
ON
   ass0.account = acct.object_id
WHERE
    (ass0.dtype = 'org:opencrx:kernel:account1:Member') ;


CREATE VIEW OOCKE1_TOBJ_ACCTMEMBERSHIP AS
SELECT DISTINCT



    (ass0."P$$PARENT") || '*' || (ass0.object_id) || '*1'



    AS object_id,
    ass0."P$$PARENT" AS "P$$PARENT",
    ass0."P$$PARENT" AS account_from,
    ass0."P$$PARENT" AS account_from_id,
    ass0.account AS account_to,
    ass0.account AS account_to_id,
    ass0.created_at,
    ass0.created_by_,
    ass0.modified_at,
    ass0.modified_by_,
    'org:opencrx:kernel:account1:AccountMembership'



    AS dtype,
    ass0.access_level_browse,
    ass0.access_level_update,
    ass0.access_level_delete,
    ass0.owner_,
    ass0.name,
    ass0.description,
    ass0.quality,
    ass0.for_use_by_,
    ass0.valid_from,
    ass0.valid_to,
    ass0.object_id AS member,
    ass0.disabled,
    ass0.member_role_0,
    ass0.member_role_1,
    ass0.member_role_2,
    ass0.member_role_3,
    ass0.member_role_4,
    1 AS distance
FROM
    OOCKE1_ACCOUNTASSIGNMENT ass0
WHERE
    ass0.dtype = 'org:opencrx:kernel:account1:Member'

UNION ALL

SELECT DISTINCT



    (ass0.account) || '*' || (ass0.object_id) || '*-1'



    AS object_id,
    ass0.account AS "P$$PARENT",
    ass0."P$$PARENT" AS account_from,
    ass0."P$$PARENT" AS account_from_id,
    ass0.account AS account_to,
    ass0.account AS account_to_id,
    ass0.created_at,
    ass0.created_by_,
    ass0.modified_at,
    ass0.modified_by_,
    'org:opencrx:kernel:account1:AccountMembership'



    AS dtype,
    ass0.access_level_browse,
    ass0.access_level_update,
    ass0.access_level_delete,
    ass0.owner_,
    ass0.name,
    ass0.description,
    ass0.quality,
    ass0.for_use_by_,
    ass0.valid_from,
    ass0.valid_to,
    ass0.object_id AS member,
    ass0.disabled,
    ass0.member_role_0,
    ass0.member_role_1,
    ass0.member_role_2,
    ass0.member_role_3,
    ass0.member_role_4,
    -1 AS distance
FROM
    OOCKE1_ACCOUNTASSIGNMENT ass0
WHERE
    (ass0.dtype = 'org:opencrx:kernel:account1:Member')

UNION ALL

SELECT DISTINCT



    (ass."P$$PARENT") || '*' || (ass0.object_id) || '*2'



    AS object_id,
    ass."P$$PARENT" AS "P$$PARENT",
    ass0."P$$PARENT" AS account_from,
    ass0."P$$PARENT" As account_from_id,
    ass0.account AS account_to,
    ass0.account AS account_to_id,
    ass0.created_at,
    ass0.created_by_,
    ass0.modified_at,
    ass0.modified_by_,
    'org:opencrx:kernel:account1:AccountMembership'



    AS dtype,
    ass0.access_level_browse,
    ass0.access_level_update,
    ass0.access_level_delete,
    ass0.owner_,
    ass0.name,
    ass0.description,
    ass0.quality,
    ass0.for_use_by_,
    ass0.valid_from,
    ass0.valid_to,
    ass0.object_id AS member,
    ass0.disabled,
    ass0.member_role_0,
    ass0.member_role_1,
    ass0.member_role_2,
    ass0.member_role_3,
    ass0.member_role_4,
    2 AS distance
FROM
    OOCKE1_ACCOUNTASSIGNMENT ass0
INNER JOIN
    OOCKE1_TOBJ_ACCTMEMBERSHIP_D1 ass
ON
    (ass0."P$$PARENT" = ass."P$$PARENT") OR
    (ass0.account = ass."P$$PARENT")
WHERE
    (ass0.dtype = 'org:opencrx:kernel:account1:Member')

UNION ALL

SELECT DISTINCT



    (ass."P$$PARENT") || '*' || (ass0.object_id) || '*-2'



    AS object_id,
    ass."P$$PARENT" AS "P$$PARENT",
    ass0."P$$PARENT" AS account_from,
    ass0."P$$PARENT" AS account_from_id,
    ass0.account AS account_to,
    ass0.account AS account_to_id,
    ass0.created_at,
    ass0.created_by_,
    ass0.modified_at,
    ass0.modified_by_,
    'org:opencrx:kernel:account1:AccountMembership'



    AS dtype,
    ass0.access_level_browse,
    ass0.access_level_update,
    ass0.access_level_delete,
    ass0.owner_,
    ass0.name,
    ass0.description,
    ass0.quality,
    ass0.for_use_by_,
    ass0.valid_from,
    ass0.valid_to,
    ass0.object_id AS member,
    ass0.disabled,
    ass0.member_role_0,
    ass0.member_role_1,
    ass0.member_role_2,
    ass0.member_role_3,
    ass0.member_role_4,
    -2 AS distance
FROM
    OOCKE1_ACCOUNTASSIGNMENT ass0
INNER JOIN
    OOCKE1_TOBJ_ACCTMEMBERSHIP_D1 ass
ON
    (ass0."P$$PARENT" = ass.account) OR
    (ass0.account = ass.account)
WHERE
    (ass0.dtype = 'org:opencrx:kernel:account1:Member')




UNION ALL

SELECT DISTINCT



    (ass."P$$PARENT") || '*' || (ass0.object_id) || '*3'



    AS object_id,
    ass."P$$PARENT" AS "P$$PARENT",
    ass0."P$$PARENT" AS account_from,
    ass0."P$$PARENT" AS account_from_id,
    ass0.account AS account_to,
    ass0.account AS account_to_id,
    ass0.created_at,
    ass0.created_by_,
    ass0.modified_at,
    ass0.modified_by_,
    'org:opencrx:kernel:account1:AccountMembership'



    AS dtype,
    ass0.access_level_browse,
    ass0.access_level_update,
    ass0.access_level_delete,
    ass0.owner_,
    ass0.name,
    ass0.description,
    ass0.quality,
    ass0.for_use_by_,
    ass0.valid_from,
    ass0.valid_to,
    ass0.object_id AS member,
    ass0.disabled,
    ass0.member_role_0,
    ass0.member_role_1,
    ass0.member_role_2,
    ass0.member_role_3,
    ass0.member_role_4,
    3 AS distance
FROM
    OOCKE1_ACCOUNTASSIGNMENT ass0
INNER JOIN
    OOCKE1_TOBJ_ACCTMEMBERSHIP_D2 ass
ON
    (ass0.account = ass."P$$PARENT") OR
    (ass0."P$$PARENT" = ass."P$$PARENT")
WHERE
    (ass0.dtype = 'org:opencrx:kernel:account1:Member')

UNION ALL

SELECT DISTINCT



    (ass."P$$PARENT") || '*' || (ass0.object_id) || '*-3'



    AS object_id,
    ass."P$$PARENT" AS "P$$PARENT",
    ass0."P$$PARENT" AS account_from,
    ass0."P$$PARENT" AS account_from_id,
    ass0.account AS account_to,
    ass0.account AS account_to_id,
    ass0.created_at,
    ass0.created_by_,
    ass0.modified_at,
    ass0.modified_by_,
    'org:opencrx:kernel:account1:AccountMembership'



    AS dtype,
    ass0.access_level_browse,
    ass0.access_level_update,
    ass0.access_level_delete,
    ass0.owner_,
    ass0.name,
    ass0.description,
    ass0.quality,
    ass0.for_use_by_,
    ass0.valid_from,
    ass0.valid_to,
    ass0.object_id AS member,
    ass0.disabled,
    ass0.member_role_0,
    ass0.member_role_1,
    ass0.member_role_2,
    ass0.member_role_3,
    ass0.member_role_4,
    -3 AS distance
FROM
    OOCKE1_ACCOUNTASSIGNMENT ass0
INNER JOIN
    OOCKE1_TOBJ_ACCTMEMBERSHIP_D2 ass
ON
    (ass0."P$$PARENT" = ass.account) OR
    (ass0.account = ass.account)
WHERE
    (ass0.dtype = 'org:opencrx:kernel:account1:Member')

;


CREATE VIEW OOCKE1_TOBJ_ACCTMEMBERSHIP_ AS
SELECT
    ass_.object_id,
    ass."P$$PARENT" AS "P$$PARENT",
    ass_.idx,
    ass_.created_by,
    ass_.modified_by,
    ass_.owner,
    ass_.dtype,
    ass_.for_use_by
FROM
    OOCKE1_ACCOUNTASSIGNMENT_ ass_
INNER JOIN
    OOCKE1_ACCOUNTASSIGNMENT ass
ON
   (ass_.object_id = ass.object_id) ;


CREATE VIEW OOCKE1_TOBJ_LNKITEMLNKFROM AS
SELECT



    REPLACE(l.link_to, 'facility/', 'itemLinkFrom/') || '/' || REPLACE(l.object_id, '/', ':')



    AS object_id,
    l.link_to AS "P$$PARENT",
    l.created_at,
    l.created_by_,
    l.modified_at,
    l.modified_by_,
    'org:opencrx:kernel:building1:LinkableItemLinkFrom'



    AS dtype,
    l.access_level_browse,
    l.access_level_update,
    l.access_level_delete,
    l.owner_,
    l.disabled,
    l.disabled_reason,
    l.name,
    l.description,
    100-l.link_type AS link_type,
    l.valid_from,
    l.valid_to,
    l.object_id AS link_to,
    l."P$$PARENT" AS link_from
FROM
    OOCKE1_LINKABLEITEMLINK l
WHERE
    l.link_to LIKE 'facility/%'

UNION ALL

SELECT



    REPLACE(l.link_to, 'facility1/', 'itemLinkFrom1/') || '/' || REPLACE(l.object_id, '/', ':')



    AS object_id,
    l.link_to AS "P$$PARENT",
    l.created_at,
    l.created_by_,
    l.modified_at,
    l.modified_by_,
    'org:opencrx:kernel:building1:LinkableItemLinkFrom'



    AS dtype,
    l.access_level_browse,
    l.access_level_update,
    l.access_level_delete,
    l.owner_,
    l.disabled,
    l.disabled_reason,
    l.name,
    l.description,
    100-l.link_type AS link_type,
    l.valid_from,
    l.valid_to,
    l.object_id AS link_to,
    l."P$$PARENT" AS link_from
FROM
    OOCKE1_LINKABLEITEMLINK l
WHERE
    l.link_to LIKE 'facility1/%'

UNION ALL

SELECT



    REPLACE(l.link_to, 'facility2/', 'itemLinkFrom2/') || '/' || REPLACE(l.object_id, '/', ':')



    AS object_id,
    l.link_to AS "P$$PARENT",
    l.created_at,
    l.created_by_,
    l.modified_at,
    l.modified_by_,
    'org:opencrx:kernel:building1:LinkableItemLinkFrom'



    AS dtype,
    l.access_level_browse,
    l.access_level_update,
    l.access_level_delete,
    l.owner_,
    l.disabled,
    l.disabled_reason,
    l.name,
    l.description,
    100-l.link_type AS link_type,
    l.valid_from,
    l.valid_to,
    l.object_id AS link_to,
    l."P$$PARENT" AS link_from
FROM
    OOCKE1_LINKABLEITEMLINK l
WHERE
    l.link_to LIKE 'facility2/%'

UNION ALL

SELECT



    REPLACE(l.link_to, 'inventoryItem/', 'itemLinkFrom3/') || '/' || REPLACE(l.object_id, '/', ':')



    AS object_id,
    l.link_to AS "P$$PARENT",
    l.created_at,
    l.created_by_,
    l.modified_at,
    l.modified_by_,
    'org:opencrx:kernel:building1:LinkableItemLinkFrom'



    AS dtype,
    l.access_level_browse,
    l.access_level_update,
    l.access_level_delete,
    l.owner_,
    l.disabled,
    l.disabled_reason,
    l.name,
    l.description,
    100-l.link_type AS link_type,
    l.valid_from,
    l.valid_to,
    l.object_id AS link_to,
    l."P$$PARENT" AS link_from
FROM
    OOCKE1_LINKABLEITEMLINK l
WHERE
    l.link_to LIKE 'inventoryItem/%' ;


CREATE VIEW OOCKE1_TOBJ_LNKITEMLNKFROM_ AS
SELECT
    object_id,
    idx,
    created_by,
    modified_by,
    owner,
    dtype
FROM
    OOCKE1_LINKABLEITEMLINK_ ;


CREATE VIEW OOCKE1_TOBJ_PRICELISTENTRY AS
SELECT
    bp.object_id AS object_id,
    p."P$$PARENT" AS "P$$PARENT",
    'org:opencrx:kernel:product1:PriceListEntry'



    AS dtype,
    bp.modified_at,
    bp.modified_by_,
    bp.created_at,
    bp.created_by_,
    bp.access_level_browse,
    bp.access_level_update,
    bp.access_level_delete,
    bp.owner_,
    bp.objusage_,
    bp.price,
    bp.price_currency,
    bp.price_level_,
    bp.description,
    bp.quantity_from,
    bp.quantity_to,
    bp.discount,
    bp.discount_is_percentage,
    bp.uom AS uom,
    p.name AS product_name,
    p.description AS product_description,
    p.disabled AS product_disabled,
    p.object_id AS product,
    p.sales_tax_type,
    bp.object_id AS base_price
FROM
    OOCKE1_PRODUCTBASEPRICE bp
INNER JOIN
    OOCKE1_PRODUCT p
ON
    (bp."P$$PARENT" = p.object_id) ;


CREATE VIEW OOCKE1_TOBJ_PRICELISTENTRY_ AS
SELECT
    bp_.object_id,
    bp."P$$PARENT",
    bp_.idx,
    bp_.created_by,
    bp_.modified_by,
    bp_.owner,
    bp_.price_level,
    bp_.objusage,
    bp_.dtype
FROM
    OOCKE1_PRODUCTBASEPRICE_ bp_
INNER JOIN
    OOCKE1_PRODUCTBASEPRICE bp
ON
    (bp_.object_id = bp.object_id) ;


CREATE VIEW OOCKE1_TOBJ_PROPERTYSETENTRY AS
SELECT



    REPLACE(p.object_id, 'p2/', 'propertySetEntry1/')



    AS object_id,
    ps."P$$PARENT" AS "P$$PARENT",
    p.created_at,
    p.created_by_,
    p.modified_at,
    p.modified_by_,



    REPLACE(REPLACE(p.dtype, 'org:opencrx:kernel:base:', 'org:opencrx:kernel:generic:'), 'Property', 'PropertySetEntry')



    AS dtype,
    p.access_level_browse,
    p.access_level_update,
    p.access_level_delete,
    p.owner_,
    p.name AS property_name,
    p.description AS property_description,
    ps.name AS property_set_name,
    ps.description AS property_set_description,
    p.string_value,
    p.integer_value,
    p.boolean_value,
    p.uri_value,
    p.decimal_value,
    p.reference_value,
    p.date_value,
    p.date_time_value,
    p.object_id AS property
FROM
    OOCKE1_PROPERTY p
INNER JOIN
    OOCKE1_PROPERTYSET ps
ON
    (p."P$$PARENT" = ps.object_id)

UNION ALL

SELECT



    REPLACE(p.object_id, 'p3/', 'propertySetEntry2/')



    AS object_id,
    ps."P$$PARENT" AS "P$$PARENT",
    p.created_at,
    p.created_by_,
    p.modified_at,
    p.modified_by_,



    REPLACE(REPLACE(p.dtype, 'org:opencrx:kernel:base:', 'org:opencrx:kernel:generic:'), 'Property', 'PropertySetEntry')



    AS dtype,
    p.access_level_browse,
    p.access_level_update,
    p.access_level_delete,
    p.owner_,
    p.name AS property_name,
    p.description AS property_description,
    ps.name AS property_set_name,
    ps.description AS property_set_description,
    p.string_value,
    p.integer_value,
    p.boolean_value,
    p.uri_value,
    p.decimal_value,
    p.reference_value,
    p.date_value,
    p.date_time_value,
    p.object_id AS property
FROM
    OOCKE1_PROPERTY p
INNER JOIN
    OOCKE1_PROPERTYSET ps
ON
    (p."P$$PARENT" = ps.object_id) ;


CREATE VIEW OOCKE1_TOBJ_PROPERTYSETENTRY_ AS
SELECT
    object_id,
    idx,
    created_by,
    modified_by,
    owner,
    dtype
FROM
    OOCKE1_PROPERTY_ ;


CREATE VIEW OOCKE1_TOBJ_SEARCHINDEXENTRY AS
SELECT



    REPLACE(act."P$$PARENT", 'accounts/', 'searchIndexEntry/') || '/' || REPLACE(act.object_id, '/', ':')



    AS object_id,
    act."P$$PARENT" AS "P$$PARENT",
    'org:opencrx:kernel:account1:SearchIndexEntry'



    AS dtype,
    act.modified_at,
    act.modified_by_,
    act.created_at,
    act.created_by_,
    act.access_level_browse,
    act.access_level_update,
    act.access_level_delete,
    act.owner_,
   
        CASE WHEN act.full_name IS NULL THEN '-' ELSE act.full_name END AS account_address_index,
    act.object_id AS account
FROM
    OOCKE1_ACCOUNT act

UNION ALL

SELECT



    REPLACE(act."P$$PARENT", 'accounts/', 'searchIndexEntry/') || '/' || REPLACE(adr.object_id, '/', ':')



    AS object_id,
    act."P$$PARENT" AS "P$$PARENT",
    'org:opencrx:kernel:account1:SearchIndexEntry'



    AS dtype,
    act.modified_at,
    act.modified_by_,
    act.created_at,
    act.created_by_,
    act.access_level_browse,
    act.access_level_update,
    act.access_level_delete,
    act.owner_,
   
        CASE WHEN act.full_name IS NULL THEN '-' ELSE act.full_name END || ', ' ||
        CASE WHEN adr.email_address IS NULL THEN '-' ELSE adr.email_address END || ', ' ||
        CASE WHEN adr.phone_number_full IS NULL THEN '-' ELSE adr.phone_number_full END || ', ' ||
        CASE WHEN adr.room_number IS NULL THEN '-' ELSE adr.room_number END || ', ' ||
        CASE WHEN adr.postal_street_0 IS NULL THEN '-' ELSE adr.postal_street_0 END || ', ' ||
        CASE WHEN adr.postal_code IS NULL THEN '-' ELSE adr.postal_code END || ', ' ||
        CASE WHEN adr.postal_city IS NULL THEN '-' ELSE adr.postal_city END AS account_address_index,
    act.object_id AS account
FROM
    OOCKE1_ACCOUNT act
INNER JOIN
    OOCKE1_ADDRESS adr
ON
    (adr."P$$PARENT" = act.object_id) ;


CREATE VIEW OOCKE1_TOBJ_SEARCHINDEXENTRY_ AS
SELECT
    object_id,
    idx,
    created_by,
    modified_by,
    owner,
    dtype
FROM
    OOCKE1_ACCOUNT_ ;


CREATE VIEW OOMSE2_TOBJ_USERS_ALL AS
SELECT
    CASE
        WHEN r.name = 'Default' THEN p.name
        ELSE r.name ||



'\'

        || p.name
    END AS principal_name,
    (SELECT c1.passwd FROM OOMSE2_PRINCIPAL p1 INNER JOIN OOMSE2_REALM r1 ON p1."P$$PARENT" = r1.object_id INNER JOIN OOMSE2_CREDENTIAL c1 ON (p1.credential = c1.object_id) WHERE r1.name = 'Default' AND p.name = p1.name) AS passwd
FROM
    OOMSE2_REALM r
INNER JOIN
    OOMSE2_PRINCIPAL p
ON
    p."P$$PARENT" = r.object_id
WHERE
    r.name <> 'Root' ;


CREATE VIEW OOMSE2_TOBJ_USERS AS
SELECT * FROM
    OOMSE2_TOBJ_USERS_ALL
WHERE
    passwd IS NOT NULL ;


CREATE VIEW OOMSE2_TOBJ_ROLES AS
SELECT
    CASE
        WHEN realm.name = 'Default' THEN p.name
        ELSE realm.name ||



'\'

        || p.name
    END AS principal_name,
    r.name AS role_name
FROM
 OOMSE2_REALM realm,
    OOMSE2_PRINCIPAL_ pg,
    OOMSE2_PRINCIPAL p,
    OOMSE2_PRINCIPAL_ pn,
    OOMSE2_ROLE r
WHERE
    (p.object_id = pn.object_id) AND
    (pn.is_member_of = pg.object_id) AND
    (pg.granted_role = r.object_id) AND
    (p.object_id LIKE 'principal/%/Root/Default/%') AND
    (realm.name <> 'Root') ;
