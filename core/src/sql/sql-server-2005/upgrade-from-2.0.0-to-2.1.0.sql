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


ALTER TABLE OOCKE1_ACTIVITY ADD LAST_APPLIED_CREATOR nvarchar(256) NULL;

ALTER TABLE OOCKE1_DEPOTREPORTITEM ADD BALANCE_SIMPLE_BOP decimal(19,9) NULL;

ALTER TABLE OOCKE1_DEPOTREPORTITEM ADD BALANCE_SIMPLE decimal(19,9) NULL;




CREATE TABLE OOCKE1_EXPORTPROFILE
(
    OBJECT_ID nvarchar(256) NOT NULL,
    ACCESS_LEVEL_BROWSE smallint NULL,
    ACCESS_LEVEL_DELETE smallint NULL,
    ACCESS_LEVEL_UPDATE smallint NULL,
    CREATED_AT datetime NULL,
    CREATED_BY_ int DEFAULT -1 NOT NULL,
    DESCRIPTION nvarchar(256) NULL,
    FOR_CLASS_ int DEFAULT -1 NOT NULL,
    MIME_TYPE nvarchar(256) NULL,
    MODIFIED_BY_ int DEFAULT -1 NOT NULL,
    "NAME" nvarchar(256) NULL,
    OWNER_ int DEFAULT -1 NOT NULL,
    REFERENCE_FILTER nvarchar(1024) NULL,
    TEMPLATE nvarchar(256) NULL,
    P$$PARENT nvarchar(256) NULL,
    MODIFIED_AT datetime NOT NULL,
    DTYPE nvarchar(256) NOT NULL
);

ALTER TABLE OOCKE1_EXPORTPROFILE ADD CONSTRAINT OOCKE1_EXPORTPROFILE_PK PRIMARY KEY (OBJECT_ID);

CREATE TABLE OOCKE1_EXPORTPROFILE_
(
    OBJECT_ID nvarchar(256) NOT NULL,
    IDX int NOT NULL,
    CREATED_BY nvarchar(256) NULL,
    MODIFIED_BY nvarchar(256) NULL,
    FOR_CLASS nvarchar(256) NULL,
    OWNER nvarchar(256) NULL,
    DTYPE nvarchar(256) NOT NULL
);

ALTER TABLE OOCKE1_EXPORTPROFILE_ ADD CONSTRAINT OOCKE1_EXPORTPROFILE__PK PRIMARY KEY (OBJECT_ID,IDX);




ALTER TABLE OOCKE1_FILTERPROPERTY_ ADD PRIORITY smallint NULL;

ALTER TABLE OOCKE1_FILTERPROPERTY_ ADD CONTRACT_STATE smallint NULL;

ALTER TABLE OOCKE1_FILTERPROPERTY_ ADD CONTRACT_TYPE nvarchar(256) NULL;

ALTER TABLE OOCKE1_FILTERPROPERTY_ ADD TOTAL_AMOUNT decimal(19,9) NULL;

ALTER TABLE OOCKE1_FILTERPROPERTY ADD CONTRACT_TYPE_ int DEFAULT -1 NOT NULL;

ALTER TABLE OOCKE1_FILTERPROPERTY ADD TOTAL_AMOUNT_ int DEFAULT -1 NOT NULL;

ALTER TABLE OOCKE1_FILTERPROPERTY ADD PRIORITY_ int DEFAULT -1 NOT NULL;

ALTER TABLE OOCKE1_FILTERPROPERTY ADD CONTRACT_STATE_ int DEFAULT -1 NOT NULL;

ALTER TABLE OOCKE1_FILTERPROPERTY_ ADD CUSTOMER nvarchar(256) NULL;

ALTER TABLE OOCKE1_FILTERPROPERTY_ ADD SUPPLIER nvarchar(256) NULL;

ALTER TABLE OOCKE1_FILTERPROPERTY_ ADD SALES_REP nvarchar(256) NULL;

ALTER TABLE OOCKE1_FILTERPROPERTY ADD SUPPLIER_ int DEFAULT -1 NOT NULL;

ALTER TABLE OOCKE1_FILTERPROPERTY ADD SALES_REP_ int DEFAULT -1 NOT NULL;

ALTER TABLE OOCKE1_FILTERPROPERTY ADD CUSTOMER_ int DEFAULT -1 NOT NULL;




ALTER TABLE OOCKE1_ACCOUNT ADD VCARD TEXT NULL;




ALTER TABLE OOCKE1_ACCOUNTASSIGNMENT_ ADD FOR_USE_BY nvarchar(256) NULL;

ALTER TABLE OOCKE1_ACCOUNTASSIGNMENT ADD QUALITY smallint NULL;

ALTER TABLE OOCKE1_ACCOUNTASSIGNMENT ADD FOR_USE_BY_ int DEFAULT -1 NOT NULL;




ALTER TABLE OOCKE1_ACCOUNT_ ADD EXT_CODE21 smallint NULL;

ALTER TABLE OOCKE1_ACCOUNT_ ADD EXT_CODE22 smallint NULL;

ALTER TABLE OOCKE1_ACCOUNT_ ADD EXT_CODE20 smallint NULL;

ALTER TABLE OOCKE1_ACCOUNT_ ADD EXT_CODE25 smallint NULL;

ALTER TABLE OOCKE1_ACCOUNT_ ADD EXT_CODE26 smallint NULL;

ALTER TABLE OOCKE1_ACCOUNT_ ADD EXT_CODE23 smallint NULL;

ALTER TABLE OOCKE1_ACCOUNT_ ADD EXT_CODE24 smallint NULL;

ALTER TABLE OOCKE1_ACCOUNT_ ADD EXT_CODE29 smallint NULL;

ALTER TABLE OOCKE1_ACCOUNT_ ADD EXT_CODE28 smallint NULL;

ALTER TABLE OOCKE1_ACCOUNT_ ADD EXT_CODE27 smallint NULL;

ALTER TABLE OOCKE1_ACCOUNT ADD EXT_CODE26_ int DEFAULT -1 NOT NULL;

ALTER TABLE OOCKE1_ACCOUNT ADD EXT_CODE27_ int DEFAULT -1 NOT NULL;

ALTER TABLE OOCKE1_ACCOUNT ADD EXT_CODE24_ int DEFAULT -1 NOT NULL;

ALTER TABLE OOCKE1_ACCOUNT ADD EXT_CODE25_ int DEFAULT -1 NOT NULL;

ALTER TABLE OOCKE1_ACCOUNT ADD EXT_CODE22_ int DEFAULT -1 NOT NULL;

ALTER TABLE OOCKE1_ACCOUNT ADD EXT_CODE23_ int DEFAULT -1 NOT NULL;

ALTER TABLE OOCKE1_ACCOUNT ADD EXT_CODE28_ int DEFAULT -1 NOT NULL;

ALTER TABLE OOCKE1_ACCOUNT ADD EXT_CODE20_ int DEFAULT -1 NOT NULL;

ALTER TABLE OOCKE1_ACCOUNT ADD EXT_CODE29_ int DEFAULT -1 NOT NULL;

ALTER TABLE OOCKE1_ACCOUNT ADD EXT_CODE21_ int DEFAULT -1 NOT NULL;




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
UPDATE OOCKE1_ADDRESS SET email_format = 0 WHERE email_format IS NULL AND type dlike '%EMail%';
UPDATE OOCKE1_ADDRESS SET postal_freight_terms = 0 WHERE postal_freight_terms IS NULL AND dtype like '%Postal%';
UPDATE OOCKE1_ADDRESS SET postal_utc_offset = 0 WHERE postal_utc_offset IS NULL AND dtype like '%Postal%';

UPDATE OOCKE1_ACCOUNTASSIGNMENT SET quality = 5 WHERE (quality IS NULL) AND (dtype = 'org:opencrx:kernel:account1:Member');
