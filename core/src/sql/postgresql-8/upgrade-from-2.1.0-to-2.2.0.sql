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

ALTER TABLE oocke1_activity ADD COLUMN ical_type int2 NULL;

ALTER TABLE oocke1_activitycreator ADD COLUMN ical_type int2 NULL;




ALTER TABLE oocke1_document ADD COLUMN disabled bool NULL;




ALTER TABLE oocke1_documentfolder ADD COLUMN disabled bool NULL;

CREATE TABLE oocke1_documentfolderass
(
    object_id varchar(256) NOT NULL,
    access_level_browse int2 NULL,
    access_level_delete int2 NULL,
    access_level_update int2 NULL,
    created_at timestamptz NULL,
    created_by_ int4 DEFAULT -1 NOT NULL,
    p$$parent varchar(256) NULL,
    description varchar(256) NULL,
    disabled bool NULL,
    document_folder varchar(256) NULL,
    modified_by_ int4 DEFAULT -1 NOT NULL,
    "name" varchar(256) NULL,
    owner_ int4 DEFAULT -1 NOT NULL,
    valid_from timestamptz NULL,
    valid_to timestamptz NULL,
    modified_at timestamptz NOT NULL,
    dtype varchar(256) NOT NULL,
    PRIMARY KEY (object_id)
);

CREATE TABLE oocke1_documentfolderass_
(
    object_id varchar(256) NOT NULL,
    idx int4 NOT NULL,
    created_by varchar(256) NULL,
    modified_by varchar(256) NULL,
    owner varchar(256) NULL,
    dtype varchar(256) NOT NULL,
    PRIMARY KEY (object_id,idx)
);




CREATE TABLE oocke1_contractlink
(
    object_id varchar(256) NOT NULL,
    access_level_browse int2 NULL,
    access_level_delete int2 NULL,
    access_level_update int2 NULL,
    category_ int4 DEFAULT -1 NOT NULL,
    p$$parent varchar(256) NULL,
    created_at timestamptz NULL,
    created_by_ int4 DEFAULT -1 NOT NULL,
    description varchar(256) NULL,
    disabled bool NULL,
    disabled_reason varchar(256) NULL,
    external_link_ int4 DEFAULT -1 NOT NULL,
    link_to varchar(256) NULL,
    link_type int2 NULL,
    modified_by_ int4 DEFAULT -1 NOT NULL,
    "name" varchar(256) NULL,
    owner_ int4 DEFAULT -1 NOT NULL,
    user_boolean0 bool NULL,
    user_boolean1 bool NULL,
    user_boolean2 bool NULL,
    user_boolean3 bool NULL,
    user_boolean4_ int4 DEFAULT -1 NOT NULL,
    user_code0 int2 NULL,
    user_code1 int2 NULL,
    user_code2 int2 NULL,
    user_code3 int2 NULL,
    user_code4_ int4 DEFAULT -1 NOT NULL,
    user_date0 date NULL,
    user_date1 date NULL,
    user_date2 date NULL,
    user_date3 date NULL,
    user_date4_ int4 DEFAULT -1 NOT NULL,
    user_date_time0 timestamptz NULL,
    user_date_time1 timestamptz NULL,
    user_date_time2 timestamptz NULL,
    user_date_time3 timestamptz NULL,
    user_date_time4_ int4 DEFAULT -1 NOT NULL,
    user_number0 numeric NULL,
    user_number1 numeric NULL,
    user_number2 numeric NULL,
    user_number3 numeric NULL,
    user_number4_ int4 DEFAULT -1 NOT NULL,
    user_string0 varchar(256) NULL,
    user_string1 varchar(256) NULL,
    user_string2 varchar(256) NULL,
    user_string3 varchar(256) NULL,
    user_string4_ int4 DEFAULT -1 NOT NULL,
    valid_from timestamptz NULL,
    valid_to timestamptz NULL,
    modified_at timestamptz NOT NULL,
    dtype varchar(256) NOT NULL,
    PRIMARY KEY (object_id)
);

CREATE TABLE oocke1_contractlink_
(
    object_id varchar(256) NOT NULL,
    idx int4 NOT NULL,
    category varchar(256) NULL,
    created_by varchar(256) NULL,
    external_link varchar(256) NULL,
    modified_by varchar(256) NULL,
    owner varchar(256) NULL,
    user_boolean4 bool NULL,
    user_code4 int2 NULL,
    user_date4 date NULL,
    user_date_time4 timestamptz NULL,
    user_number4 numeric NULL,
    user_string4 varchar(256) NULL,
    dtype varchar(256) NOT NULL,
    PRIMARY KEY (object_id,idx)
);




CREATE TABLE oocke1_calendarfeed
(
    object_id varchar(256) NOT NULL,
    access_level_browse int2 NULL,
    access_level_delete int2 NULL,
    access_level_update int2 NULL,
    p$$parent varchar(256) NULL,
    created_at timestamptz NULL,
    created_by_ int4 DEFAULT -1 NOT NULL,
    description varchar(256) NULL,
    back_color varchar(256) NULL,
    color varchar(256) NULL,
    is_active bool NULL,
    modified_by_ int4 DEFAULT -1 NOT NULL,
    "name" varchar(256) NULL,
    owner_ int4 DEFAULT -1 NOT NULL,
    modified_at timestamptz NOT NULL,
    dtype varchar(256) NOT NULL,
    activity_filter varchar(256) NULL,
    password varchar(256) NULL,
    url varchar(256) NULL,
    username varchar(256) NULL,
    activity_group varchar(256) NULL,
    PRIMARY KEY (object_id)
);

CREATE TABLE oocke1_calendarfeed_
(
    object_id varchar(256) NOT NULL,
    idx int4 NOT NULL,
    created_by varchar(256) NULL,
    modified_by varchar(256) NULL,
    owner varchar(256) NULL,
    dtype varchar(256) NOT NULL,
    PRIMARY KEY (object_id,idx)
);

CREATE TABLE oocke1_calendarprofile
(
    object_id varchar(256) NOT NULL,
    access_level_browse int2 NULL,
    access_level_delete int2 NULL,
    access_level_update int2 NULL,
    created_at timestamptz NULL,
    created_by_ int4 DEFAULT -1 NOT NULL,
    description varchar(256) NULL,
    modified_by_ int4 DEFAULT -1 NOT NULL,
    "name" varchar(256) NULL,
    owner_ int4 DEFAULT -1 NOT NULL,
    p$$parent varchar(256) NULL,
    modified_at timestamptz NOT NULL,
    dtype varchar(256) NOT NULL,
    PRIMARY KEY (object_id)
);

CREATE TABLE oocke1_calendarprofile_
(
    object_id varchar(256) NOT NULL,
    idx int4 NOT NULL,
    created_by varchar(256) NULL,
    modified_by varchar(256) NULL,
    owner varchar(256) NULL,
    dtype varchar(256) NOT NULL,
    PRIMARY KEY (object_id,idx)
);




ALTER TABLE oocke1_budget ADD COLUMN description varchar(256) NULL;

ALTER TABLE oocke1_budget ADD COLUMN "name" varchar(256) NULL;




ALTER TABLE oocke1_activity ADD COLUMN gateway varchar(256) NULL;
