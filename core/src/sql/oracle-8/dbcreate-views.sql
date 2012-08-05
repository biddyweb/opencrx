/* This software is published under the BSD license                          */
/* as listed below.                                                          */
/*                                                                           */
/* Copyright (c) 2004-2008, CRIXP Corp., Switzerland                         */
/* All rights reserved.                                                      */
/*                                                                           */
/* Redistribution and use in source and binary forms, with or without        */
/* modification, are permitted provided that the following conditions        */
/* are met:                                                                  */
/*                                                                           */
/* * Redistributions of source code must retain the above copyright          */
/* notice, this list of conditions and the following disclaimer.             */
/*                                                                           */
/* * Redistributions in binary form must reproduce the above copyright       */
/* notice, this list of conditions and the following disclaimer in           */
/* the documentation and/or other materials provided with the                */
/* distribution.                                                             */
/*                                                                           */
/* * Neither the name of CRIXP Corp. nor the names of the contributors       */
/* to openCRX may be used to endorse or promote products derived             */
/* from this software without specific prior written permission              */
/*                                                                           */
/* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND                    */
/* CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,               */
/* INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF                  */
/* MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE                  */
/* DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS         */
/* BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,                  */
/* EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED           */
/* TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,             */
/* DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON         */
/* ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,           */
/* OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY            */
/* OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE                   */
/* POSSIBILITY OF SUCH DAMAGE.                                               */
/*                                                                           */
/* ------------------                                                        */
/*                                                                           */
/* This product includes software developed by the Apache Software           */
/* Foundation (http://www.apache.org/).                                      */
/*                                                                           */
/* This product includes software developed by contributors to               */
/* openMDX (http://www.openmdx.org/)                                         */
/*                                                                           */
DROP TABLE OOCKE1_JOIN_ACCTHASASSACT ;
DROP TABLE OOCKE1_JOIN_ACCTHASPROD ;
DROP TABLE OOCKE1_JOIN_ACTGCONTAINSACT ;
DROP TABLE OOCKE1_JOIN_ACTGCONTAINSFLUP ;
DROP TABLE OOCKE1_JOIN_ACTGCONTAINSNOTE ;
DROP TABLE OOCKE1_JOIN_CBHASBK ;
DROP TABLE OOCKE1_JOIN_CLFCLASSIFIESTELT ;
DROP TABLE OOCKE1_JOIN_CPOSHASPOSMOD ;
DROP TABLE OOCKE1_JOIN_DEPGCONTAINSDEP ;
DROP TABLE OOCKE1_JOIN_DEPGCONTAINSDEPG ;
DROP TABLE OOCKE1_JOIN_DEPPOSHASBK ;
DROP TABLE OOCKE1_JOIN_DEPPOSHASSBK ;
DROP TABLE OOCKE1_JOIN_ENTITYCONTAINSDEP ;
DROP TABLE OOCKE1_JOIN_FLDCONTAINSDOC ;
DROP TABLE OOCKE1_JOIN_FLDCONTAINSFLD ;
DROP TABLE OOCKE1_JOIN_HOMEHASASSACT ;
DROP TABLE OOCKE1_JOIN_IITEMHASBOOKING ;
DROP TABLE OOCKE1_JOIN_NSCONTAINSELT ;
DROP TABLE OOCKE1_JOIN_RESHASASSIGNEDACT ;
DROP TABLE OOCKE1_JOIN_SEGCONTAINSADR ;
DROP TABLE OOCKE1_JOIN_SEGCONTAINSFAC ;
DROP TABLE OOCKE1_TOBJ_ACTIVITYLINKFROM ;
DROP TABLE OOCKE1_TOBJ_ACTIVITYLINKFROM_ ;
DROP TABLE OOCKE1_TOBJ_CONTRACTROLE ;
DROP TABLE OOCKE1_TOBJ_CONTRACTROLE_ ;
DROP TABLE OOCKE1_TOBJ_ACCTMEMBERSHIP ;
DROP TABLE OOCKE1_TOBJ_ACCTMEMBERSHIP_ ;
DROP TABLE OOCKE1_TOBJ_LNKITEMLNKFROM ;
DROP TABLE OOCKE1_TOBJ_LNKITEMLNKFROM_ ;
DROP TABLE OOCKE1_TOBJ_PRICELISTENTRY ;
DROP TABLE OOCKE1_TOBJ_PRICELISTENTRY_ ;
DROP TABLE OOCKE1_TOBJ_PROPERTYSETENTRY ;
DROP TABLE OOCKE1_TOBJ_PROPERTYSETENTRY_ ;
DROP TABLE OOCKE1_TOBJ_SEARCHINDEXENTRY ;
DROP TABLE OOCKE1_TOBJ_SEARCHINDEXENTRY_ ;
DROP TABLE OOCKE1_TOBJ_WORKREPORTENTRY ;
DROP TABLE OOCKE1_TOBJ_WORKREPORTENTRY_ ;
DROP TABLE OOCKE1_JOIN_HOMEHASASSCONTR ;
DROP TABLE OOCKE1_JOIN_ACCTHASASSCONTR ;
DROP TABLE OOCKE1_JOIN_SEGCONTAINSBU ;
DROP TABLE OOCKE1_JOIN_BUHASADR ;
DROP TABLE OOCKE1_JOIN_ACTGISCREATEDBY ;
DROP TABLE OOCKE1_JOIN_DEPREPITMHASBK ;
DROP TABLE OOCKE1_JOIN_DEPREPITMHASSBK ;
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
DROP VIEW OOCKE1_JOIN_FLDCONTAINSDOC ;
DROP VIEW OOCKE1_JOIN_ENTITYCONTAINSDEP ;
DROP VIEW OOCKE1_JOIN_FLDCONTAINSFLD ;
DROP VIEW OOCKE1_JOIN_HOMEHASASSACT ;
DROP VIEW OOCKE1_JOIN_NSCONTAINSELT ;
DROP VIEW OOCKE1_TOBJ_ACTIVITYLINKFROM ;
DROP VIEW OOCKE1_JOIN_SEGCONTAINSFAC ;
DROP VIEW OOCKE1_TOBJ_ACTIVITYLINKFROM_ ;
DROP VIEW OOCKE1_TOBJ_CONTRACTROLE ;
DROP VIEW OOCKE1_TOBJ_CONTRACTROLE_ ;
DROP VIEW OOCKE1_TOBJ_ACCTMEMBERSHIP ;
DROP VIEW OOCKE1_TOBJ_ACCTMEMBERSHIP_ ;
DROP VIEW OOCKE1_TOBJ_LNKITEMLNKFROM ;
DROP VIEW OOCKE1_TOBJ_LNKITEMLNKFROM_ ;
DROP VIEW OOCKE1_TOBJ_PRICELISTENTRY ;
DROP VIEW OOCKE1_TOBJ_PRICELISTENTRY_ ;
DROP VIEW OOCKE1_TOBJ_PROPERTYSETENTRY ;
DROP VIEW OOCKE1_TOBJ_PROPERTYSETENTRY_ ;
DROP VIEW OOCKE1_TOBJ_SEARCHINDEXENTRY ;
DROP VIEW OOCKE1_TOBJ_SEARCHINDEXENTRY_ ;
DROP VIEW OOCKE1_TOBJ_WORKREPORTENTRY ;
DROP VIEW OOCKE1_TOBJ_WORKREPORTENTRY_ ;
DROP VIEW OOCKE1_JOIN_HOMEHASASSCONTR ;
DROP VIEW OOCKE1_JOIN_ACCTHASASSCONTR ;
DROP VIEW OOCKE1_JOIN_SEGCONTAINSBU ;
DROP VIEW OOCKE1_JOIN_BUHASADR ;
DROP VIEW OOCKE1_JOIN_IITEMHASBOOKING ;
DROP VIEW OOCKE1_JOIN_RESHASASSIGNEDACT ;
DROP VIEW OOCKE1_JOIN_SEGCONTAINSADR ;
DROP VIEW OOCKE1_JOIN_ACTGISCREATEDBY ;
DROP VIEW OOCSE1_TOBJ_USERS ;
DROP VIEW OOCSE1_TOBJ_ROLES ;
DROP VIEW OOCKE1_JOIN_DEPREPITMHASBK ;
DROP VIEW OOCKE1_JOIN_DEPREPITMHASSBK ;
CREATE VIEW OOCKE1_JOIN_ACCTHASASSACT AS
SELECT
    act.object_id AS assigned_activity,
    acc.object_id AS account
FROM
    OOCKE1_ACTIVITY act
INNER JOIN
    OOCKE1_ACCOUNT acc
ON
    act.assigned_to = acc.object_id

UNION

SELECT
    act.object_id AS assigned_activity,
    adr.p$$parent AS account
FROM
    OOCKE1_ACTIVITY act
INNER JOIN
    OOCKE1_ADDRESS adr
ON
    adr.object_id = act.sender

UNION

SELECT
    p0.p$$parent AS assigned_activity,
    acc.object_id AS account
FROM
    OOCKE1_ACTIVITYPARTY p0
INNER JOIN
    OOCKE1_ACCOUNT acc
ON
    acc.object_id = p0.party

UNION

SELECT
    p0.p$$parent AS assigned_activity,
    adr.p$$parent AS account
FROM
    OOCKE1_ACTIVITYPARTY p0
INNER JOIN
    OOCKE1_ADDRESS adr
ON
    adr.object_id = p0.party

UNION

SELECT
    act.object_id AS assigned_activity,
    c0.customer AS account
FROM
    OOCKE1_ACTIVITY_ act
INNER JOIN
    OOCKE1_CONTRACT c0
ON
    act.contract = c0.object_id ;
CREATE VIEW OOCKE1_JOIN_ACCTHASPROD AS
SELECT DISTINCT
    p.object_id AS product,
    a.object_id AS account
FROM
    OOCKE1_PRODUCT p, OOCKE1_ACCOUNT a, OOCKE1_CONTRACT c, OOCKE1_CONTRACTPOSITION cp
WHERE
    cp.product = p.object_id AND
    cp.p$$parent = c.object_id AND
    c.customer = a.object_id ;
CREATE VIEW OOCKE1_JOIN_ACTGCONTAINSACT AS
SELECT
    ga.p$$parent AS filtered_activity,
    ga.activity_group AS activity_group
FROM
    OOCKE1_ACTIVITYGROUPASS ga ;
CREATE VIEW OOCKE1_JOIN_ACTGCONTAINSFLUP AS
SELECT
    f.object_id AS follow_up,
    g.object_id AS activity_group
FROM
    OOCKE1_ACTIVITYFOLLOWUP f
INNER JOIN
    OOCKE1_ACTIVITYGROUPASS ga
ON
    f.p$$parent = ga.p$$parent
INNER JOIN
    OOCKE1_ACTIVITYGROUP g
ON
    ga.activity_group = g.object_id ;
CREATE VIEW OOCKE1_JOIN_ACTGCONTAINSNOTE AS
SELECT
    n.object_id AS activity_note,
    g.object_id AS activity_group
FROM
    OOCKE1_NOTE n
INNER JOIN
    OOCKE1_ACTIVITYGROUPASS ga
ON
    n.p$$parent = ga.p$$parent
INNER JOIN
    OOCKE1_ACTIVITYGROUP g
ON
    ga.activity_group = g.object_id ;
CREATE VIEW OOCKE1_JOIN_CBHASBK AS
SELECT
    b.object_id AS booking,
    cb.object_id AS cb
FROM
    OOCKE1_BOOKING b
INNER JOIN
    OOCKE1_COMPOUNDBOOKING cb
ON
    b.cb = cb.object_id ;
CREATE VIEW OOCKE1_JOIN_CLFCLASSIFIESTELT AS
SELECT
    c.object_id AS classifier,
    e.object_id AS typed_element
FROM
    OOCKE1_MODELELEMENT c
INNER JOIN
    OOCKE1_MODELELEMENT e
ON
    c.object_id = e.type ;
CREATE VIEW OOCKE1_JOIN_CPOSHASPOSMOD AS
SELECT
    p.object_id AS position,
    pm.object_id AS position_modification
FROM
    OOCKE1_CONTRACTPOSITION p
INNER JOIN
    OOCKE1_CONTRACTPOSMOD pm
ON
    p.object_id = pm.involved ;
CREATE VIEW OOCKE1_JOIN_DEPGCONTAINSDEP AS
SELECT
    d.object_id AS depot,
    dg.object_id AS depot_group
FROM
    OOCKE1_DEPOT d
INNER JOIN
    OOCKE1_DEPOTGROUP dg
ON
    d.depot_group = dg.object_id ;
CREATE VIEW OOCKE1_JOIN_DEPGCONTAINSDEPG AS
SELECT
    dg.object_id AS depot_group,
    dgp.object_id AS parent
FROM
    OOCKE1_DEPOTGROUP dg
INNER JOIN
    OOCKE1_DEPOTGROUP dgp
ON
    dg.p$$parent = dgp.object_id ;
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
    dh.p$$parent AS entity,
    d.object_id AS depot
FROM
    OOCKE1_DEPOT d
INNER JOIN
    OOCKE1_DEPOTHOLDER dh
ON
    d.p$$parent = dh.object_id ;
CREATE VIEW OOCKE1_JOIN_FLDCONTAINSDOC AS
SELECT
    d_.object_id AS document,
    f.object_id AS folder
FROM
    OOCKE1_DOCUMENT_ d_
INNER JOIN
    OOCKE1_DOCUMENTFOLDER f
ON
    d_.folder = f.object_id ;
CREATE VIEW OOCKE1_JOIN_FLDCONTAINSFLD AS
SELECT
    f.object_id AS folder,
    fp.object_id AS parent
FROM
    OOCKE1_DOCUMENTFOLDER f
INNER JOIN
    OOCKE1_DOCUMENTFOLDER fp
ON
    f.parent = fp.object_id ;
CREATE VIEW OOCKE1_JOIN_HOMEHASASSACT AS
SELECT
    a.object_id AS assigned_activity,
    h0.object_id AS user_home
FROM
    OOCKE1_ACTIVITY a
INNER JOIN
    OOCKE1_USERHOME h0
ON
    a.assigned_to = h0.contact

UNION

SELECT
    a.object_id AS assigned_activity,
    h0.object_id AS user_home
FROM
    OOCKE1_ACTIVITY a
INNER JOIN
    OOCKE1_ADDRESS adr
ON
    adr.object_id = a.sender
INNER JOIN
    OOCKE1_USERHOME h0
ON
    adr.p$$parent = h0.contact

UNION

SELECT
    a.object_id AS assigned_activity,
    h0.object_id AS user_home
FROM
    OOCKE1_ACTIVITYPARTY p0
INNER JOIN
    OOCKE1_USERHOME h0
ON
    p0.party = h0.contact
INNER JOIN
    OOCKE1_ACTIVITY a
ON
    p0.p$$parent = a.object_id

UNION

SELECT
    a.object_id AS assigned_activity,
    h0.object_id AS user_home
FROM
    OOCKE1_ACTIVITYPARTY p0
INNER JOIN
    OOCKE1_ACTIVITY a
ON
    p0.p$$parent = a.object_id
INNER JOIN
    OOCKE1_ADDRESS adr
ON
    adr.object_id = p0.party
INNER JOIN
    OOCKE1_USERHOME h0
ON
    adr.p$$parent = h0.contact ;
CREATE VIEW OOCKE1_JOIN_IITEMHASBOOKING AS
SELECT
    b.object_id AS booking,
    i.object_id AS inventory_item
FROM
    OOCKE1_INVENTORYITEM i
INNER JOIN
    OOCKE1_BOOKING b
ON
    b.origin = i.object_id ;
CREATE VIEW OOCKE1_JOIN_NSCONTAINSELT AS
SELECT
    n.object_id AS namespace,
    e.object_id AS content
FROM
    OOCKE1_MODELELEMENT n
INNER JOIN
    OOCKE1_MODELELEMENT e
ON
    e.container = n.object_id ;
CREATE VIEW OOCKE1_JOIN_RESHASASSIGNEDACT AS
SELECT
    a.object_id AS assigned_activity,
    r.object_id AS "resource"
FROM
    OOCKE1_ACTIVITY a
INNER JOIN
    OOCKE1_RESOURCEASSIGNMENT ra
ON
    ra.p$$parent = a.object_id
INNER JOIN
    OOCKE1_RESOURCE r
ON
    ra.resrc = r.object_id ;
CREATE VIEW OOCKE1_JOIN_SEGCONTAINSADR AS
SELECT
    adr.object_id AS address,
    act.p$$parent AS segment
FROM
    OOCKE1_ADDRESS adr
INNER JOIN
    OOCKE1_ACCOUNT act
ON
    adr.p$$parent = act.object_id ;
CREATE VIEW OOCKE1_JOIN_SEGCONTAINSFAC AS
SELECT
    f.object_id AS facility,
    b.p$$parent AS segment
FROM
    OOCKE1_FACILITY f
INNER JOIN
    OOCKE1_BUILDINGUNIT b
ON
    f.p$$parent = b.object_id

UNION ALL

SELECT
    f.object_id AS facility,
    bu1.p$$parent AS segment
FROM
    OOCKE1_FACILITY f
INNER JOIN
    OOCKE1_BUILDINGUNIT bu2
ON
    f.p$$parent = bu2.object_id
INNER JOIN
    OOCKE1_BUILDINGUNIT bu1
ON
   bu2.p$$parent = bu1.object_id ;
CREATE VIEW OOCKE1_JOIN_HOMEHASASSCONTR AS
SELECT
    c.object_id AS assigned_contract,
    h.object_id AS user_home
FROM
    OOCKE1_CONTRACT c
INNER JOIN
    OOCKE1_USERHOME h
ON
    c.sales_rep = h.contact

UNION ALL

SELECT
    c.object_id AS assigned_contract,
    h.object_id AS user_home
FROM
    OOCKE1_CONTRACT c
INNER JOIN
    OOCKE1_USERHOME h
ON
    c.customer = h.contact ;
CREATE VIEW OOCKE1_JOIN_ACCTHASASSCONTR AS
SELECT
    c.object_id AS assigned_contract,
    a.object_id AS account
FROM
    OOCKE1_CONTRACT c
INNER JOIN
    OOCKE1_ACCOUNT a
ON
    c.customer = a.object_id

UNION ALL

SELECT
    c.object_id AS assigned_contract,
    a.object_id AS account
FROM
    OOCKE1_CONTRACT c
INNER JOIN
    OOCKE1_ACCOUNT a
ON
    c.sales_rep = a.object_id ;
CREATE VIEW OOCKE1_JOIN_BUHASADR AS
SELECT
    adr.object_id AS assigned_address,
    bu.object_id AS building_unit
FROM
    OOCKE1_ADDRESS adr
INNER JOIN
    OOCKE1_BUILDINGUNIT bu
ON
    adr.building = bu.object_id ;
CREATE VIEW OOCKE1_JOIN_SEGCONTAINSBU AS
SELECT
    b.object_id AS building_unit,
    s.object_id AS segment
FROM
    OOCKE1_BUILDINGUNIT b
INNER JOIN
    OOCKE1_SEGMENT s
ON
    b.p$$parent = s.object_id

UNION ALL

SELECT
    b.object_id AS building_unit,
    bp.p$$parent AS segment
FROM
    OOCKE1_BUILDINGUNIT b
INNER JOIN
    OOCKE1_BUILDINGUNIT bp
ON
    b.p$$parent = bp.object_id ;
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
    ip.p$$parent = r.object_id
INNER JOIN
    OOCKE1_BOOKINGPERIOD bp
ON
    r.booking_period = bp.object_id
INNER JOIN
    OOCKE1_BOOKING b
ON
    b.position = ip.position AND
    b.value_date >= bp.period_starts_at AND
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
    ip.p$$parent = r.object_id
INNER JOIN
    OOCKE1_BOOKINGPERIOD bp
ON
    r.booking_period = bp.object_id
INNER JOIN
    OOCKE1_SIMPLEBOOKING b
ON
    b.position = ip.position AND
    b.value_date >= bp.period_starts_at AND
    ((b.value_date < bp.period_ends_at_exclusive) OR (bp.period_ends_at_exclusive IS NULL)) AND
    ((b.booking_status >= r.booking_status_threshold) OR (r.booking_status_threshold = 0) OR (r.booking_status_threshold IS NULL)) ;
CREATE VIEW OOCKE1_TOBJ_ACTIVITYLINKFROM AS
SELECT



    REPLACE(a.object_id, 'activity/', 'activityLinkFrom/') || '/' || REPLACE(l.object_id, '/', ':')



    AS object_id,
    a.object_id AS p$$parent,
    'org:opencrx:kernel:activity1:ActivityLinkFrom' AS dtype,
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
    l.p$$parent AS link_from,
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
CREATE VIEW OOCKE1_TOBJ_CONTRACTROLE AS
SELECT



    REPLACE(c.p$$parent, 'contracts/', 'contractRole/') || '/' || REPLACE(dhn.object_id, '/', ':') || ':' || REPLACE(c.object_id, '/', ':')



    AS object_id,
    c.p$$parent AS p$$parent,
    'org:opencrx:kernel:contract1:CustomerContractRole' AS dtype,
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
    c.object_id = dhn.contract
INNER JOIN
    OOCKE1_ACCOUNT a
ON
    c.customer = a.object_id

UNION ALL

SELECT



    REPLACE(c.p$$parent, 'contracts/', 'contractRole/') || '/' || REPLACE(dgn.object_id, '/', ':') || ':' || REPLACE(c.object_id, '/', ':')



    AS object_id,
    c.p$$parent AS p$$parent,
    'org:opencrx:kernel:contract1:CustomerContractRole' AS dtype,
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
    c.object_id = dgn.contract
INNER JOIN
    OOCKE1_ACCOUNT a
ON
    c.customer = a.object_id

UNION ALL

SELECT



    REPLACE(c.p$$parent, 'contracts/', 'contractRole/') || '/' || REPLACE(dn.object_id, '/', ':') || ':' || REPLACE(c.object_id, '/', ':')



    AS object_id,
    c.p$$parent AS p$$parent,
    'org:opencrx:kernel:contract1:CustomerContractRole' AS dtype,
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
    c.object_id = dn.contract
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
    'org:opencrx:kernel:contract1:CustomerContractRole' AS dtype
FROM
    OOCKE1_CONTRACT_ ;
CREATE VIEW OOCKE1_TOBJ_ACCTMEMBERSHIP AS
SELECT



    REPLACE(a.object_id, 'account/', 'accountMembership/') || '/' || REPLACE(ass.object_id, '/', ':')



    AS object_id,
    ass.account AS p$$parent,
    ass.created_at,
    ass.created_by_,
    ass.modified_at,
    ass.modified_by_,
    'org:opencrx:kernel:account1:AccountMembership' AS dtype,
    ass.access_level_browse,
    ass.access_level_update,
    ass.access_level_delete,
    ass.owner_,
    ass.name,
    ass.description,
    ass.valid_from,
    ass.valid_to,
    ass.object_id AS member,
    ass.p$$parent AS member_of_account,
    ass.member_role_,
    ass.disabled
FROM
    OOCKE1_ACCOUNTASSIGNMENT ass
INNER JOIN
    OOCKE1_ACCOUNT a
ON
    (ass.account = a.object_id) AND
    (ass.dtype = 'org:opencrx:kernel:account1:Member') ;
CREATE VIEW OOCKE1_TOBJ_ACCTMEMBERSHIP_ AS
SELECT
    object_id,
    idx,
    created_by,
    member_role,
    modified_by,
    owner,
    dtype
FROM
    OOCKE1_ACCOUNTASSIGNMENT_ ;
CREATE VIEW OOCKE1_TOBJ_LNKITEMLNKFROM AS
SELECT



    REPLACE(l.link_to, 'facility/', 'itemLinkFrom/') || '/' || REPLACE(l.object_id, '/', ':')



    AS object_id,
    l.link_to AS p$$parent,
    l.created_at,
    l.created_by_,
    l.modified_at,
    l.modified_by_,
    'org:opencrx:kernel:building1:LinkableItemLinkFrom' AS dtype,
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
    l.p$$parent AS link_from
FROM
    OOCKE1_LINKABLEITEMLINK l
WHERE
    l.link_to LIKE 'facility/%'

UNION ALL

SELECT



    REPLACE(l.link_to, 'facility1/', 'itemLinkFrom1/') || '/' || REPLACE(l.object_id, '/', ':')



    AS object_id,
    l.link_to AS p$$parent,
    l.created_at,
    l.created_by_,
    l.modified_at,
    l.modified_by_,
    'org:opencrx:kernel:building1:LinkableItemLinkFrom' AS dtype,
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
    l.p$$parent AS link_from
FROM
    OOCKE1_LINKABLEITEMLINK l
WHERE
    l.link_to LIKE 'facility1/%'

UNION ALL

SELECT



    REPLACE(l.link_to, 'facility2/', 'itemLinkFrom2/') || '/' || REPLACE(l.object_id, '/', ':')



    AS object_id,
    l.link_to AS p$$parent,
    l.created_at,
    l.created_by_,
    l.modified_at,
    l.modified_by_,
    'org:opencrx:kernel:building1:LinkableItemLinkFrom' AS dtype,
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
    l.p$$parent AS link_from
FROM
    OOCKE1_LINKABLEITEMLINK l
WHERE
    l.link_to LIKE 'facility2/%'

UNION ALL

SELECT



    REPLACE(l.link_to, 'inventoryItem/', 'itemLinkFrom3/') || '/' || REPLACE(l.object_id, '/', ':')



    AS object_id,
    l.link_to AS p$$parent,
    l.created_at,
    l.created_by_,
    l.modified_at,
    l.modified_by_,
    'org:opencrx:kernel:building1:LinkableItemLinkFrom' AS dtype,
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
    l.p$$parent AS link_from
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



    REPLACE(p.p$$parent, 'products/', 'priceListEntry/') || '/' || REPLACE(bp.object_id, '/', ':')



    AS object_id,
    p.p$$parent AS p$$parent,
    'org:opencrx:kernel:product1:PriceListEntry' AS dtype,
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
    p.object_id AS product,
    p.sales_tax_type,
    bp.object_id AS base_price
FROM
    OOCKE1_PRODUCTBASEPRICE bp
INNER JOIN
    OOCKE1_PRODUCT p
ON
    bp.p$$parent = p.object_id

UNION ALL

SELECT



    REPLACE(pp.p$$parent, 'products/', 'priceListEntry/') || '/' || REPLACE(bp.object_id, '/', ':')



    AS object_id,
    pp.p$$parent AS p$$parent,
    'org:opencrx:kernel:product1:PriceListEntry' AS dtype,
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
    p.object_id AS product,
    p.sales_tax_type,
    bp.object_id AS base_price
FROM
    OOCKE1_PRODUCTBASEPRICE bp
INNER JOIN
    OOCKE1_PRODUCT p
ON
    bp.p$$parent = p.object_id
INNER JOIN
    OOCKE1_PRODUCT pp
ON
    p.p$$parent = pp.object_id ;
CREATE VIEW OOCKE1_TOBJ_PRICELISTENTRY_ AS
SELECT
    object_id,
    idx,
    created_by,
    modified_by,
    owner,
    price_level,
    objusage,
    dtype
FROM
    OOCKE1_PRODUCTBASEPRICE_ ;
CREATE VIEW OOCKE1_TOBJ_PROPERTYSETENTRY AS
SELECT



    REPLACE(p.object_id, 'p2/', 'propertySetEntry1/')



    AS object_id,
    ps.p$$parent AS p$$parent,
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
    p.p$$parent = ps.object_id

UNION ALL

SELECT



    REPLACE(p.object_id, 'p3/', 'propertySetEntry2/')



    AS object_id,
    ps.p$$parent AS p$$parent,
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
    p.p$$parent = ps.object_id ;
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



    REPLACE(act.p$$parent, 'accounts/', 'searchIndexEntry/') || '/' || REPLACE(act.object_id, '/', ':')



    AS object_id,
    act.p$$parent AS p$$parent,
    'org:opencrx:kernel:account1:SearchIndexEntry' AS dtype,
    act.modified_at,
    act.modified_by_,
    act.created_at,
    act.created_by_,
    act.access_level_browse,
    act.access_level_update,
    act.access_level_delete,
    act.owner_,
   
        CASE WHEN act.last_name IS NULL THEN '-' ELSE act.last_name END || ', ' ||
        CASE WHEN act.first_name IS NULL THEN '-' ELSE act.first_name END AS account_address_index,
    act.object_id AS account
FROM
    OOCKE1_ACCOUNT act

UNION ALL

SELECT



    REPLACE(act.p$$parent, 'accounts/', 'searchIndexEntry/') || '/' || REPLACE(adr.object_id, '/', ':')



    AS object_id,
    act.p$$parent AS p$$parent,
    'org:opencrx:kernel:account1:SearchIndexEntry' AS dtype,
    act.modified_at,
    act.modified_by_,
    act.created_at,
    act.created_by_,
    act.access_level_browse,
    act.access_level_update,
    act.access_level_delete,
    act.owner_,
   
        CASE WHEN act.last_name IS NULL THEN '-' ELSE act.last_name END || ', ' ||
        CASE WHEN act.first_name IS NULL THEN '-' ELSE act.first_name END || ', ' ||
        CASE WHEN adr.email_address IS NULL THEN '-' ELSE adr.email_address END || ', ' ||
        CASE WHEN adr.phone_number_full IS NULL THEN '-' ELSE adr.phone_number_full END || ', ' ||
        CASE WHEN adr.room_number IS NULL THEN '-' ELSE adr.room_number END || ', ' ||
        CASE WHEN adr.postal_street_0 IS NULL THEN '-' ELSE adr.postal_street_0 END || ', ' ||
        CASE WHEN adr.postal_city IS NULL THEN '-' ELSE adr.postal_city END AS account_address_index,
    act.object_id AS account
FROM
    OOCKE1_ACCOUNT act
INNER JOIN
    OOCKE1_ADDRESS adr
ON
    adr.p$$parent = act.object_id ;
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
CREATE VIEW OOCKE1_TOBJ_WORKREPORTENTRY AS
SELECT



    REPLACE(a.p$$parent, 'activities/', 'workReportEntry1/') || '/' || REPLACE(w.object_id, '/', ':')



    AS object_id,
    a.p$$parent AS p$$parent,
    'org:opencrx:kernel:activity1:WorkReportEntry' AS dtype,
    w.modified_at,
    w.modified_by_,
    w.created_at,
    w.created_by_,
    w.access_level_browse,
    w.access_level_update,
    w.access_level_delete,
    w.owner_,
    w.name,
    w.description,
    w.started_at,
    w.ended_at,
    w.duration_hours,
    w.duration_minutes,
    (w.duration_hours + (w.duration_minutes / 60.0)) AS duration_decimal,
    ( TO_CHAR(w.duration_hours) || ':' || TO_CHAR(w.duration_minutes, '00') || CHR(39) ) AS duration_hh_mm,
    w.pause_duration_hours,
    w.pause_duration_minutes,
    (w.pause_duration_hours + (w.pause_duration_minutes / 60.0)) AS pause_duration_decimal,
    ( TO_CHAR(w.pause_duration_hours) || ':' || TO_CHAR(w.pause_duration_minutes, '00') || CHR(39) ) AS pause_duration_hh_mm,
    w.billable_amount AS billable_amount,
    w.billing_currency AS billing_currency,
    a.activity_number,
    a.object_id AS activity,
    ra.object_id AS assignment,
    w.object_id AS work_record,
    ra.resrc AS resrc
FROM
   OOCKE1_WORKRECORD w
INNER JOIN
   OOCKE1_RESOURCEASSIGNMENT ra
ON
   w.p$$parent = ra.object_id
INNER JOIN
   OOCKE1_ACTIVITY a
ON
   a.object_id = ra.p$$parent

UNION ALL

SELECT



    REPLACE(REPLACE(REPLACE(ga.activity_group, 'activityTracker/', 'workReportEntry2/'), 'activityCategory/', 'workReportEntry2/'), 'activityMilestone/', 'workReportEntry2/') || '/' || REPLACE(w.object_id, '/', ':')



    AS object_id,
    ga.activity_group AS p$$parent,
    'org:opencrx:kernel:activity1:WorkReportEntry' AS dtype,
    w.modified_at,
    w.modified_by_,
    w.created_at,
    w.created_by_,
    w.access_level_browse,
    w.access_level_update,
    w.access_level_delete,
    w.owner_,
    w.name,
    w.description,
    w.started_at,
    w.ended_at,
    w.duration_hours,
    w.duration_minutes,
    (w.duration_hours + (w.duration_minutes / 60.0)) AS duration_decimal,
    ( TO_CHAR(w.duration_hours) || ':' || TO_CHAR(w.duration_minutes, '00') || CHR(39) ) AS duration_hh_mm,
    w.pause_duration_hours,
    w.pause_duration_minutes,
    (w.pause_duration_hours + (w.pause_duration_minutes / 60.0)) AS pause_duration_decimal,
    ( TO_CHAR(w.pause_duration_hours) || ':' || TO_CHAR(w.pause_duration_minutes, '00') || CHR(39) ) AS pause_duration_hh_mm,
    w.billable_amount AS billable_amount,
    w.billing_currency AS billing_currency,
    a.activity_number,
    a.object_id AS activity,
    ra.object_id AS assignment,
    w.object_id AS work_record,
    ra.resrc AS resrc
FROM
   OOCKE1_WORKRECORD w
INNER JOIN
   OOCKE1_RESOURCEASSIGNMENT ra
ON
   w.p$$parent = ra.object_id
INNER JOIN
   OOCKE1_ACTIVITY a
ON
   a.object_id = ra.p$$parent
INNER JOIN
    OOCKE1_ACTIVITYGROUPASS ga
ON
   (a.object_id = ga.p$$parent) AND
   (ga.activity_group IS NOT NULL)

UNION ALL

SELECT



    REPLACE(REPLACE(REPLACE(ga.activity_group, 'activityTracker/', 'workReportEntry3/'), 'activityCategory/', 'workReportEntry3/'), 'activityMilestone/', 'workReportEntry3/') || '/' || REPLACE(w.object_id, '/', ':')



    AS object_id,
    ga.activity_group AS p$$parent,
    'org:opencrx:kernel:activity1:WorkReportEntry' AS dtype,
    w.modified_at,
    w.modified_by_,
    w.created_at,
    w.created_by_,
    w.access_level_browse,
    w.access_level_update,
    w.access_level_delete,
    w.owner_,
    w.name,
    w.description,
    w.started_at,
    w.ended_at,
    w.duration_hours,
    w.duration_minutes,
    (w.duration_hours + (w.duration_minutes / 60.0)) AS duration_decimal,
    ( TO_CHAR(w.duration_hours) || ':' || TO_CHAR(w.duration_minutes, '00') || CHR(39) ) AS duration_hh_mm,
    w.pause_duration_hours,
    w.pause_duration_minutes,
    (w.pause_duration_hours + (w.pause_duration_minutes / 60.0)) AS pause_duration_decimal,
    ( TO_CHAR(w.pause_duration_hours) || ':' || TO_CHAR(w.pause_duration_minutes, '00') || CHR(39) ) AS pause_duration_hh_mm,
    w.billable_amount AS billable_amount,
    w.billing_currency AS billing_currency,
    a.activity_number,
    a.object_id AS activity,
    ra.object_id AS assignment,
    w.object_id AS work_record,
    ra.resrc AS resrc
FROM
   OOCKE1_WORKRECORD w
INNER JOIN
   OOCKE1_RESOURCEASSIGNMENT ra
ON
   w.p$$parent = ra.object_id
INNER JOIN
   OOCKE1_ACTIVITY a
ON
   a.object_id = ra.p$$parent
INNER JOIN
    OOCKE1_ACTIVITYGROUPASS ga
ON
   (a.object_id = ga.p$$parent) AND
   (ga.activity_group IS NOT NULL)

UNION ALL

SELECT



    REPLACE(REPLACE(REPLACE(ga.activity_group, 'activityTracker/', 'workReportEntry4/'), 'activityCategory/', 'workReportEntry4/'), 'activityMilestone/', 'workReportEntry4/') || '/' || REPLACE(w.object_id, '/', ':')



    AS object_id,
    ga.activity_group AS p$$parent,
    'org:opencrx:kernel:activity1:WorkReportEntry' AS dtype,
    w.modified_at,
    w.modified_by_,
    w.created_at,
    w.created_by_,
    w.access_level_browse,
    w.access_level_update,
    w.access_level_delete,
    w.owner_,
    w.name,
    w.description,
    w.started_at,
    w.ended_at,
    w.duration_hours,
    w.duration_minutes,
    (w.duration_hours + (w.duration_minutes / 60.0)) AS duration_decimal,
    ( TO_CHAR(w.duration_hours) || ':' || TO_CHAR(w.duration_minutes, '00') || CHR(39) ) AS duration_hh_mm,
    w.pause_duration_hours, w.pause_duration_minutes,
    (w.pause_duration_hours + (w.pause_duration_minutes / 60.0)) AS pause_duration_decimal,
    ( TO_CHAR(w.pause_duration_hours) || ':' || TO_CHAR(w.pause_duration_minutes, '00') || CHR(39) ) AS pause_duration_hh_mm,
    w.billable_amount AS billable_amount,
    w.billing_currency AS billing_currency,
    a.activity_number,
    a.object_id AS activity,
    ra.object_id AS assignment,
    w.object_id AS work_record,
    ra.resrc AS resrc
FROM
   OOCKE1_WORKRECORD w
INNER JOIN
   OOCKE1_RESOURCEASSIGNMENT ra
ON
   w.p$$parent = ra.object_id
INNER JOIN
   OOCKE1_ACTIVITY a
ON
   a.object_id = ra.p$$parent
INNER JOIN
    OOCKE1_ACTIVITYGROUPASS ga
ON
   (a.object_id = ga.p$$parent) AND
   (ga.activity_group IS NOT NULL) ;
CREATE VIEW OOCKE1_TOBJ_WORKREPORTENTRY_ AS
SELECT
    object_id,
    idx,
    created_by,
    modified_by,
    owner,
    dtype
FROM
    OOCKE1_WORKRECORD_ ;
CREATE VIEW OOCSE1_TOBJ_USERS AS
SELECT
    p.name AS principal_name,
    c.passwd
FROM
    OOCSE1_PRINCIPAL p
INNER JOIN
    OOCSE1_CREDENTIAL c
ON
    p.credential = c.object_id ;
CREATE VIEW OOCSE1_TOBJ_ROLES AS
SELECT
    p.name AS principal_name,
    r.name AS role_name
FROM
    OOCSE1_PRINCIPAL_ pg,
    OOCSE1_PRINCIPAL p,
    OOCSE1_PRINCIPAL_ pn,
    OOCSE1_ROLE r
WHERE
    (p.object_id = pn.object_id) AND
    (pn.is_member_of = pg.object_id) AND
    (pg.granted_role = r.object_id) AND
    (p.object_id LIKE 'principal/%/Root/Default/%') ;
