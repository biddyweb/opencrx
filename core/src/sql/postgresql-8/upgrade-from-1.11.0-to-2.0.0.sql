/* This software is published under the BSD license                          */
/* as listed below.                                                          */
/*                                                                           */
/* Copyright (c) 2004-2007, CRIXP Corp., Switzerland                         */
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

ALTER TABLE oocke1_account_ ADD COLUMN citizenship int2;

ALTER TABLE oocke1_account_ ADD COLUMN religion int2;

ALTER TABLE oocke1_account ADD COLUMN ext_code19 int2;

ALTER TABLE oocke1_account ADD COLUMN ext_code18 int2;

ALTER TABLE oocke1_account ADD COLUMN ext_code17 int2;

ALTER TABLE oocke1_account ADD COLUMN ext_code16 int2;

ALTER TABLE oocke1_account ADD COLUMN ext_code10 int2;

ALTER TABLE oocke1_account ADD COLUMN ext_code11 int2;

ALTER TABLE oocke1_account ADD COLUMN ext_code14 int2;

ALTER TABLE oocke1_account ADD COLUMN ext_code15 int2;

ALTER TABLE oocke1_account ADD COLUMN ext_code12 int2;

ALTER TABLE oocke1_account ADD COLUMN ext_code13 int2;

ALTER TABLE oocke1_account ADD COLUMN citizenship_ int4 DEFAULT -1 NOT NULL;

ALTER TABLE oocke1_account ADD COLUMN religion_ int4 DEFAULT -1 NOT NULL;




ALTER TABLE oocke1_depotgroup ADD COLUMN contract_ int4 DEFAULT -1 NOT NULL;

ALTER TABLE oocke1_depotgroup_ ADD COLUMN contract varchar(256);




CREATE TABLE oocke1_simplebooking
(
    object_id varchar(256) NOT NULL,
    access_level_browse int2,
    access_level_delete int2,
    access_level_update int2,
    booking_date timestamptz,
    booking_status int2,
    booking_type int2,
    created_at timestamptz,
    created_by_ int4 DEFAULT -1 NOT NULL,
    description varchar(256),
    modified_by_ int4 DEFAULT -1 NOT NULL,
    "name" varchar(256),
    origin_context_params_ int4 DEFAULT -1 NOT NULL,
    origin_context_ int4 DEFAULT -1 NOT NULL,
    origin_id varchar(256),
    owner_ int4 DEFAULT -1 NOT NULL,
    "position" varchar(256),
    quantity numeric,
    quantity_uom varchar(256),
    p$$parent varchar(256),
    value_date timestamptz,
    modified_at timestamptz NOT NULL,
    dtype varchar(256) NOT NULL,
    PRIMARY KEY (object_id)
);

CREATE TABLE oocke1_simplebooking_
(
    object_id varchar(256) NOT NULL,
    idx int4 NOT NULL,
    created_by varchar(256),
    modified_by varchar(256),
    origin_context varchar(256),
    origin_context_params varchar(256),
    owner varchar(256),
    dtype varchar(256) NOT NULL,
    PRIMARY KEY (object_id,idx)
);

ALTER TABLE oocke1_depotreport ADD COLUMN booking_status_threshold int2;




CREATE TABLE oocke1_productphase
(
    object_id varchar(256) NOT NULL,
    access_level_browse int2,
    access_level_delete int2,
    access_level_update int2,
    category_ int4 DEFAULT -1 NOT NULL,
    created_at timestamptz,
    created_by_ int4 DEFAULT -1 NOT NULL,
    description varchar(256),
    disabled bool,
    disabled_reason varchar(256),
    external_link_ int4 DEFAULT -1 NOT NULL,
    modified_by_ int4 DEFAULT -1 NOT NULL,
    "name" varchar(256),
    owner_ int4 DEFAULT -1 NOT NULL,
    p$$parent varchar(256),
    product_phase_key varchar(256),
    user_boolean0 bool,
    user_boolean1 bool,
    user_boolean2 bool,
    user_boolean3 bool,
    user_boolean4_ int4 DEFAULT -1 NOT NULL,
    user_code0 int2,
    user_code1 int2,
    user_code2 int2,
    user_code3 int2,
    user_code4_ int4 DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ int4 DEFAULT -1 NOT NULL,
    user_date_time0 timestamptz,
    user_date_time1 timestamptz,
    user_date_time2 timestamptz,
    user_date_time3 timestamptz,
    user_date_time4_ int4 DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ int4 DEFAULT -1 NOT NULL,
    user_string0 varchar(256),
    user_string1 varchar(256),
    user_string2 varchar(256),
    user_string3 varchar(256),
    user_string4_ int4 DEFAULT -1 NOT NULL,
    valid_from timestamptz,
    valid_to timestamptz,
    modified_at timestamptz NOT NULL,
    dtype varchar(256) NOT NULL,
    PRIMARY KEY (object_id)
);

CREATE TABLE oocke1_productphase_
(
    object_id varchar(256) NOT NULL,
    idx int4 NOT NULL,
    category varchar(256),
    created_by varchar(256),
    external_link varchar(256),
    modified_by varchar(256),
    owner varchar(256),
    user_boolean4 bool,
    user_code4 int2,
    user_date4 date,
    user_date_time4 timestamptz,
    user_number4 numeric,
    user_string4 varchar(256),
    dtype varchar(256) NOT NULL,
    PRIMARY KEY (object_id,idx)
);

ALTER TABLE oocke1_pricelevel ADD COLUMN product_phase_key varchar(256);

ALTER TABLE oocke1_pricelevel ADD COLUMN default_offset_valid_to_hh int4;

ALTER TABLE oocke1_pricelevel ADD COLUMN default_offset_valid_from_hh int4;

ALTER TABLE oocke1_product ADD COLUMN active_on timestamptz;

ALTER TABLE oocke1_product ADD COLUMN expires_on timestamptz;




CREATE TABLE oocke1_involvedobject
(
    object_id varchar(256) NOT NULL,
    access_level_browse int2,
    access_level_delete int2,
    access_level_update int2,
    p$$parent varchar(256),
    created_at timestamptz,
    created_by_ int4 DEFAULT -1 NOT NULL,
    description varchar(256),
    involved varchar(256),
    involved_role varchar(256),
    modified_by_ int4 DEFAULT -1 NOT NULL,
    "name" varchar(256),
    owner_ int4 DEFAULT -1 NOT NULL,
    modified_at timestamptz NOT NULL,
    dtype varchar(256) NOT NULL,
    PRIMARY KEY (object_id)
);

CREATE TABLE oocke1_involvedobject_
(
    object_id varchar(256) NOT NULL,
    idx int4 NOT NULL,
    created_by varchar(256),
    modified_by varchar(256),
    owner varchar(256),
    dtype varchar(256) NOT NULL,
    PRIMARY KEY (object_id,idx)
);




ALTER TABLE oocke1_filterproperty_ ADD COLUMN contact varchar(256);

ALTER TABLE oocke1_filterproperty ADD COLUMN offset_in_hours int4;

ALTER TABLE oocke1_filterproperty ADD COLUMN contact_ int4 DEFAULT -1 NOT NULL;



CREATE TABLE oocke1_join_finderhasidxact
(
    object_finder varchar(256) NOT NULL,
    index_entry_activity varchar(256) NOT NULL,
    PRIMARY KEY (object_finder,index_entry_activity)
);

CREATE TABLE oocke1_join_finderhasidxdep
(
    object_finder varchar(256) NOT NULL,
    index_entry_depot varchar(256) NOT NULL,
    PRIMARY KEY (object_finder,index_entry_depot)
);

CREATE TABLE oocke1_join_finderhasidxbldg
(
    object_finder varchar(256) NOT NULL,
    index_entry_building varchar(256) NOT NULL,
    PRIMARY KEY (object_finder,index_entry_building)
);

CREATE TABLE oocke1_join_finderhasidxacct
(
    object_finder varchar(256) NOT NULL,
    index_entry_account varchar(256) NOT NULL,
    PRIMARY KEY (object_finder,index_entry_account)
);

CREATE TABLE oocke1_join_finderhasidxdoc
(
    object_finder varchar(256) NOT NULL,
    index_entry_document varchar(256) NOT NULL,
    PRIMARY KEY (object_finder,index_entry_document)
);

CREATE TABLE oocke1_join_finderhasidxcontr
(
    object_finder varchar(256) NOT NULL,
    index_entry_contract varchar(256) NOT NULL,
    PRIMARY KEY (object_finder,index_entry_contract)
);

CREATE TABLE oocke1_join_finderhasidxprod
(
    object_finder varchar(256) NOT NULL,
    index_entry_product varchar(256) NOT NULL,
    PRIMARY KEY (object_finder,index_entry_product)
);

CREATE TABLE oocke1_objectfinder
(
    object_id varchar(256) NOT NULL,
    access_level_browse int2,
    access_level_delete int2,
    access_level_update int2,
    all_words varchar(256),
    at_least_one_of_the_words varchar(256),
    created_at timestamptz,
    created_by_ int4 DEFAULT -1 NOT NULL,
    description varchar(256),
    modified_by_ int4 DEFAULT -1 NOT NULL,
    "name" varchar(256),
    owner_ int4 DEFAULT -1 NOT NULL,
    p$$parent varchar(256),
    without_words varchar(256),
    modified_at timestamptz NOT NULL,
    dtype varchar(256) NOT NULL,
    PRIMARY KEY (object_id)
);

CREATE TABLE oocke1_objectfinder_
(
    object_id varchar(256) NOT NULL,
    idx int4 NOT NULL,
    created_by varchar(256),
    modified_by varchar(256),
    owner varchar(256),
    dtype varchar(256) NOT NULL,
    PRIMARY KEY (object_id,idx)
);




ALTER TABLE oocke1_media ADD COLUMN "version" varchar(256);

ALTER TABLE oocke1_media ADD COLUMN author varchar(256);

ALTER TABLE oocke1_media ADD COLUMN "name" varchar(256);

CREATE TABLE oocke1_documentlink_
(
    object_id varchar(256) NOT NULL,
    idx int4 NOT NULL,
    created_by varchar(256),
    modified_by varchar(256),
    owner varchar(256),
    dtype varchar(256) NOT NULL,
    PRIMARY KEY (object_id,idx)
);

CREATE TABLE oocke1_documentlink
(
    object_id varchar(256) NOT NULL,
    access_level_browse int2,
    access_level_delete int2,
    access_level_update int2,
    created_at timestamptz,
    created_by_ int4 DEFAULT -1 NOT NULL,
    description varchar(256),
    p$$parent varchar(256),
    link_uri varchar(256),
    modified_by_ int4 DEFAULT -1 NOT NULL,
    "name" varchar(256),
    owner_ int4 DEFAULT -1 NOT NULL,
    modified_at timestamptz NOT NULL,
    dtype varchar(256) NOT NULL,
    PRIMARY KEY (object_id)
);

ALTER TABLE oocke1_document ADD COLUMN cms_translation int4;

ALTER TABLE oocke1_document ADD COLUMN qualified_name varchar(256);

ALTER TABLE oocke1_document ADD COLUMN description varchar(256);

ALTER TABLE oocke1_document ADD COLUMN "name" varchar(256);

ALTER TABLE oocke1_document ADD COLUMN parent varchar(256);

ALTER TABLE oocke1_document ADD COLUMN cms_template varchar(256);

ALTER TABLE oocke1_document ADD COLUMN cms_class text;

ALTER TABLE oocke1_document ADD COLUMN cms_meta varchar(256);

ALTER TABLE oocke1_document ADD COLUMN cms_default_language varchar(256);

ALTER TABLE oocke1_document ADD COLUMN cms_language varchar(256);

CREATE TABLE oocke1_documentlock_
(
    object_id varchar(256) NOT NULL,
    idx int4 NOT NULL,
    created_by varchar(256),
    locked_by varchar(256),
    modified_by varchar(256),
    owner varchar(256),
    dtype varchar(256) NOT NULL,
    PRIMARY KEY (object_id,idx)
);

CREATE TABLE oocke1_documentlock
(
    object_id varchar(256) NOT NULL,
    access_level_browse int2,
    access_level_delete int2,
    access_level_update int2,
    created_at timestamptz,
    created_by_ int4 DEFAULT -1 NOT NULL,
    description varchar(256),
    p$$parent varchar(256),
    locked_at timestamptz,
    locked_by_ int4 DEFAULT -1 NOT NULL,
    modified_by_ int4 DEFAULT -1 NOT NULL,
    "name" varchar(256),
    owner_ int4 DEFAULT -1 NOT NULL,
    modified_at timestamptz NOT NULL,
    dtype varchar(256) NOT NULL,
    PRIMARY KEY (object_id)
);




ALTER TABLE oocke1_activitygroup ADD COLUMN default_creator varchar(256);

ALTER TABLE oocke1_activity ADD COLUMN ical text;
