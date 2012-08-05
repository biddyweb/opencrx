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



ALTER TABLE oocke1_activity ADD COLUMN last_applied_creator varchar(256);

ALTER TABLE oocke1_depotreportitem ADD COLUMN balance_simple_bop numeric;

ALTER TABLE oocke1_depotreportitem ADD COLUMN balance_simple numeric;




CREATE TABLE oocke1_exportprofile
(
    object_id varchar(256) NOT NULL,
    access_level_browse int2,
    access_level_delete int2,
    access_level_update int2,
    created_at timestamptz,
    created_by_ int4 DEFAULT -1 NOT NULL,
    description varchar(256),
    for_class_ int4 DEFAULT -1 NOT NULL,
    mime_type varchar(256),
    modified_by_ int4 DEFAULT -1 NOT NULL,
    "name" varchar(256),
    owner_ int4 DEFAULT -1 NOT NULL,
    reference_filter varchar(1024),
    template varchar(256),
    p$$parent varchar(256),
    modified_at timestamptz NOT NULL,
    dtype varchar(256) NOT NULL,
    PRIMARY KEY (object_id)
);

CREATE TABLE oocke1_exportprofile_
(
    object_id varchar(256) NOT NULL,
    idx int4 NOT NULL,
    created_by varchar(256),
    modified_by varchar(256),
    for_class varchar(256),
    owner varchar(256),
    dtype varchar(256) NOT NULL,
    PRIMARY KEY (object_id,idx)
);




ALTER TABLE oocke1_filterproperty_ ADD COLUMN total_amount numeric;

ALTER TABLE oocke1_filterproperty_ ADD COLUMN contract_state int2;

ALTER TABLE oocke1_filterproperty_ ADD COLUMN customer varchar(256);

ALTER TABLE oocke1_filterproperty_ ADD COLUMN sales_rep varchar(256);

ALTER TABLE oocke1_filterproperty_ ADD COLUMN contract_type varchar(256);

ALTER TABLE oocke1_filterproperty_ ADD COLUMN priority int2;

ALTER TABLE oocke1_filterproperty_ ADD COLUMN supplier varchar(256);

ALTER TABLE oocke1_filterproperty ADD COLUMN total_amount_ int4 DEFAULT -1 NOT NULL;

ALTER TABLE oocke1_filterproperty ADD COLUMN contract_type_ int4 DEFAULT -1 NOT NULL;

ALTER TABLE oocke1_filterproperty ADD COLUMN sales_rep_ int4 DEFAULT -1 NOT NULL;

ALTER TABLE oocke1_filterproperty ADD COLUMN supplier_ int4 DEFAULT -1 NOT NULL;

ALTER TABLE oocke1_filterproperty ADD COLUMN contract_state_ int4 DEFAULT -1 NOT NULL;

ALTER TABLE oocke1_filterproperty ADD COLUMN priority_ int4 DEFAULT -1 NOT NULL;

ALTER TABLE oocke1_filterproperty ADD COLUMN customer_ int4 DEFAULT -1 NOT NULL;




ALTER TABLE oocke1_account ADD COLUMN vcard text;




ALTER TABLE oocke1_accountassignment_ ADD COLUMN for_use_by varchar(256);

ALTER TABLE oocke1_accountassignment ADD COLUMN for_use_by_ int4 DEFAULT -1 NOT NULL;

ALTER TABLE oocke1_accountassignment ADD COLUMN quality int2;




ALTER TABLE oocke1_account_ ADD COLUMN ext_code28 int2;

ALTER TABLE oocke1_account_ ADD COLUMN ext_code27 int2;

ALTER TABLE oocke1_account_ ADD COLUMN ext_code29 int2;

ALTER TABLE oocke1_account_ ADD COLUMN ext_code23 int2;

ALTER TABLE oocke1_account_ ADD COLUMN ext_code24 int2;

ALTER TABLE oocke1_account_ ADD COLUMN ext_code25 int2;

ALTER TABLE oocke1_account_ ADD COLUMN ext_code26 int2;

ALTER TABLE oocke1_account_ ADD COLUMN ext_code20 int2;

ALTER TABLE oocke1_account_ ADD COLUMN ext_code21 int2;

ALTER TABLE oocke1_account_ ADD COLUMN ext_code22 int2;

ALTER TABLE oocke1_account ADD COLUMN ext_code23_ int4 DEFAULT -1 NOT NULL;

ALTER TABLE oocke1_account ADD COLUMN ext_code24_ int4 DEFAULT -1 NOT NULL;

ALTER TABLE oocke1_account ADD COLUMN ext_code25_ int4 DEFAULT -1 NOT NULL;

ALTER TABLE oocke1_account ADD COLUMN ext_code26_ int4 DEFAULT -1 NOT NULL;

ALTER TABLE oocke1_account ADD COLUMN ext_code27_ int4 DEFAULT -1 NOT NULL;

ALTER TABLE oocke1_account ADD COLUMN ext_code28_ int4 DEFAULT -1 NOT NULL;

ALTER TABLE oocke1_account ADD COLUMN ext_code20_ int4 DEFAULT -1 NOT NULL;

ALTER TABLE oocke1_account ADD COLUMN ext_code29_ int4 DEFAULT -1 NOT NULL;

ALTER TABLE oocke1_account ADD COLUMN ext_code21_ int4 DEFAULT -1 NOT NULL;

ALTER TABLE oocke1_account ADD COLUMN ext_code22_ int4 DEFAULT -1 NOT NULL;




UPDATE OOCKE1_DOCUMENT SET literature_type = 0 WHERE literature_type IS NULL;
UPDATE OOCKE1_DOCUMENT SET document_state = 0 WHERE document_state IS NULL;
UPDATE OOCKE1_DOCUMENT SET document_type = 0 WHERE document_type IS NULL;
UPDATE OOCKE1_DOCUMENT SET content_language = 0 WHERE content_language IS NULL;

UPDATE OOCKE1_MEDIA SET name = content_name WHERE name IS NULL;
UPDATE OOCKE1_MEDIA SET name = description WHERE name IS NULL;

UPDATE OOCKE1_ACCOUNT SET education = 0 WHERE education IS NULL AND dtype = 'org:opencrx:kernel:account1:Contact';
UPDATE OOCKE1_ACCOUNT SET preferred_spoken_language = 0 WHERE preferred_spoken_language IS NULL AND dtype = 'org:opencrx:kernel:account1:Contact';
UPDATE OOCKE1_ACCOUNT SET preferred_written_language = 0 WHERE preferred_written_language IS NULL AND dtype = 'org:opencrx:kernel:account1:Contact';
UPDATE OOCKE1_ACCOUNT SET family_status = 0 WHERE family_status IS NULL AND dtype = 'org:opencrx:kernel:account1:Contact';
UPDATE OOCKE1_ACCOUNT SET gender = 0 WHERE gender IS NULL AND dtype = 'org:opencrx:kernel:account1:Contact';
UPDATE OOCKE1_ACCOUNT SET salutation_code = 0 WHERE salutation_code IS NULL AND dtype = 'org:opencrx:kernel:account1:Contact';
UPDATE OOCKE1_ACCOUNT SET preferred_contact_method = 0 WHERE preferred_contact_method IS NULL AND dtype = 'org:opencrx:kernel:account1:Contact';
UPDATE OOCKE1_ACCOUNT SET account_rating = 0 WHERE account_rating IS NULL;
UPDATE OOCKE1_ACCOUNT SET account_state = 0 WHERE account_state IS NULL;

UPDATE OOCKE1_ADDRESS SET email_type = 0 WHERE email_type IS NULL AND dtype like '%EMail%';
UPDATE OOCKE1_ADDRESS SET email_format = 0 WHERE email_format IS NULL AND dtype like '%EMail%';
UPDATE OOCKE1_ADDRESS SET postal_freight_terms = 0 WHERE postal_freight_terms IS NULL AND dtype like '%Postal%';
UPDATE OOCKE1_ADDRESS SET postal_utc_offset = 0 WHERE postal_utc_offset IS NULL AND dtype like '%Postal%';

UPDATE OOCKE1_ACCOUNTASSIGNMENT SET quality = 5 WHERE (quality IS NULL) AND (dtype = 'org:opencrx:kernel:account1:Member');
