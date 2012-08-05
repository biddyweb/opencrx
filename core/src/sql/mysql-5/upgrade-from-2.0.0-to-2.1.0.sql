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


ALTER TABLE `OOCKE1_ACTIVITY` ADD COLUMN `LAST_APPLIED_CREATOR` VARCHAR(256) BINARY NULL;

ALTER TABLE `OOCKE1_DEPOTREPORTITEM` ADD COLUMN `BALANCE_SIMPLE_BOP` DECIMAL(17,9) NULL;

ALTER TABLE `OOCKE1_DEPOTREPORTITEM` ADD COLUMN `BALANCE_SIMPLE` DECIMAL(17,9) NULL;




CREATE TABLE `OOCKE1_EXPORTPROFILE`
(
    `OBJECT_ID` VARCHAR(256) BINARY NOT NULL,
    `ACCESS_LEVEL_BROWSE` SMALLINT NULL,
    `ACCESS_LEVEL_DELETE` SMALLINT NULL,
    `ACCESS_LEVEL_UPDATE` SMALLINT NULL,
    `CREATED_AT` DATETIME NULL,
    `CREATED_BY_` INTEGER NOT NULL DEFAULT -1,
    `DESCRIPTION` VARCHAR(256) BINARY NULL,
    `FOR_CLASS_` INTEGER NOT NULL DEFAULT -1,
    `MIME_TYPE` VARCHAR(256) BINARY NULL,
    `MODIFIED_BY_` INTEGER NOT NULL DEFAULT -1,
    `NAME` VARCHAR(256) BINARY NULL,
    `OWNER_` INTEGER NOT NULL DEFAULT -1,
    `REFERENCE_FILTER` VARCHAR(1024) BINARY NULL,
    `TEMPLATE` VARCHAR(256) BINARY NULL,
    `P$$PARENT` VARCHAR(256) BINARY NULL,
    `MODIFIED_AT` DATETIME NOT NULL,
    `DTYPE` VARCHAR(256) BINARY NOT NULL,
    PRIMARY KEY (`OBJECT_ID`)
) ENGINE=INNODB;

CREATE TABLE `OOCKE1_EXPORTPROFILE_`
(
    `OBJECT_ID` VARCHAR(256) BINARY NOT NULL,
    `IDX` INTEGER NOT NULL,
    `CREATED_BY` VARCHAR(256) BINARY NULL,
    `MODIFIED_BY` VARCHAR(256) BINARY NULL,
    `FOR_CLASS` VARCHAR(256) BINARY NULL,
    `OWNER` VARCHAR(256) BINARY NULL,
    `DTYPE` VARCHAR(256) BINARY NOT NULL,
    PRIMARY KEY (`OBJECT_ID`,`IDX`)
) ENGINE=INNODB;




ALTER TABLE `OOCKE1_FILTERPROPERTY_` ADD COLUMN `CUSTOMER` VARCHAR(256) BINARY NULL;

ALTER TABLE `OOCKE1_FILTERPROPERTY_` ADD COLUMN `PRIORITY` SMALLINT NULL;

ALTER TABLE `OOCKE1_FILTERPROPERTY_` ADD COLUMN `SUPPLIER` VARCHAR(256) BINARY NULL;

ALTER TABLE `OOCKE1_FILTERPROPERTY_` ADD COLUMN `CONTRACT_STATE` SMALLINT NULL;

ALTER TABLE `OOCKE1_FILTERPROPERTY_` ADD COLUMN `CONTRACT_TYPE` VARCHAR(256) NULL;

ALTER TABLE `OOCKE1_FILTERPROPERTY_` ADD COLUMN `TOTAL_AMOUNT` DECIMAL(17,9) NULL;

ALTER TABLE `OOCKE1_FILTERPROPERTY_` ADD COLUMN `SALES_REP` VARCHAR(256) BINARY NULL;

ALTER TABLE `OOCKE1_FILTERPROPERTY` ADD COLUMN `CONTRACT_TYPE_` INTEGER NOT NULL DEFAULT -1;

ALTER TABLE `OOCKE1_FILTERPROPERTY` ADD COLUMN `TOTAL_AMOUNT_` INTEGER NOT NULL DEFAULT -1;

ALTER TABLE `OOCKE1_FILTERPROPERTY` ADD COLUMN `PRIORITY_` INTEGER NOT NULL DEFAULT -1;

ALTER TABLE `OOCKE1_FILTERPROPERTY` ADD COLUMN `SUPPLIER_` INTEGER NOT NULL DEFAULT -1;

ALTER TABLE `OOCKE1_FILTERPROPERTY` ADD COLUMN `SALES_REP_` INTEGER NOT NULL DEFAULT -1;

ALTER TABLE `OOCKE1_FILTERPROPERTY` ADD COLUMN `CONTRACT_STATE_` INTEGER NOT NULL DEFAULT -1;

ALTER TABLE `OOCKE1_FILTERPROPERTY` ADD COLUMN `CUSTOMER_` INTEGER NOT NULL DEFAULT -1;




ALTER TABLE `OOCKE1_ACCOUNT` ADD COLUMN `VCARD` MEDIUMTEXT NULL;




ALTER TABLE `OOCKE1_ACCOUNTASSIGNMENT_` ADD COLUMN `FOR_USE_BY` VARCHAR(256) BINARY NULL;

ALTER TABLE `OOCKE1_ACCOUNTASSIGNMENT` ADD COLUMN `QUALITY` SMALLINT NULL;

ALTER TABLE `OOCKE1_ACCOUNTASSIGNMENT` ADD COLUMN `FOR_USE_BY_` INTEGER NOT NULL DEFAULT -1;




ALTER TABLE OOCKE1_ACCOUNT_ ADD EXT_CODE21 SMALLINT;

ALTER TABLE OOCKE1_ACCOUNT_ ADD EXT_CODE22 SMALLINT;

ALTER TABLE OOCKE1_ACCOUNT_ ADD EXT_CODE20 SMALLINT;

ALTER TABLE OOCKE1_ACCOUNT_ ADD EXT_CODE25 SMALLINT;

ALTER TABLE OOCKE1_ACCOUNT_ ADD EXT_CODE26 SMALLINT;

ALTER TABLE OOCKE1_ACCOUNT_ ADD EXT_CODE23 SMALLINT;

ALTER TABLE OOCKE1_ACCOUNT_ ADD EXT_CODE24 SMALLINT;

ALTER TABLE OOCKE1_ACCOUNT_ ADD EXT_CODE29 SMALLINT;

ALTER TABLE OOCKE1_ACCOUNT_ ADD EXT_CODE28 SMALLINT;

ALTER TABLE OOCKE1_ACCOUNT_ ADD EXT_CODE27 SMALLINT;

ALTER TABLE OOCKE1_ACCOUNT ADD EXT_CODE26_ INTEGER DEFAULT -1 NOT NULL;

ALTER TABLE OOCKE1_ACCOUNT ADD EXT_CODE27_ INTEGER DEFAULT -1 NOT NULL;

ALTER TABLE OOCKE1_ACCOUNT ADD EXT_CODE24_ INTEGER DEFAULT -1 NOT NULL;

ALTER TABLE OOCKE1_ACCOUNT ADD EXT_CODE25_ INTEGER DEFAULT -1 NOT NULL;

ALTER TABLE OOCKE1_ACCOUNT ADD EXT_CODE22_ INTEGER DEFAULT -1 NOT NULL;

ALTER TABLE OOCKE1_ACCOUNT ADD EXT_CODE23_ INTEGER DEFAULT -1 NOT NULL;

ALTER TABLE OOCKE1_ACCOUNT ADD EXT_CODE28_ INTEGER DEFAULT -1 NOT NULL;

ALTER TABLE OOCKE1_ACCOUNT ADD EXT_CODE20_ INTEGER DEFAULT -1 NOT NULL;

ALTER TABLE OOCKE1_ACCOUNT ADD EXT_CODE29_ INTEGER DEFAULT -1 NOT NULL;

ALTER TABLE OOCKE1_ACCOUNT ADD EXT_CODE21_ INTEGER DEFAULT -1 NOT NULL;




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
