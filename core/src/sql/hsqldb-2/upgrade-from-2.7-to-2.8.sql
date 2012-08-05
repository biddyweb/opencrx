/* This software is published under the BSD license                          */
/* as listed below.                                                          */
/*                                                                           */
/* Copyright (c) 2004-2010, CRIXP Corp., Switzerland                         */
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

ALTER TABLE OOCKE1_ACTIVITYPARTY ADD PARTY_STATUS SMALLINT;



ALTER TABLE OOCKE1_DOCUMENTFOLDERASS ADD ASSIGNMENT_ROLE SMALLINT;



ALTER TABLE OOCKE1_CALENDARFEED ADD DOCUMENT_FOLDER VARCHAR(256);



ALTER TABLE OOCKE1_BUDGET ADD CONTRACT_TYPE SMALLINT;
ALTER TABLE OOCKE1_BUDGET ADD DEPOT VARCHAR(256);
ALTER TABLE OOCKE1_BUDGET ADD LAST_CALCULATED_AT TIMESTAMP;
ALTER TABLE OOCKE1_CONTRACTPOSITION ADD BUDGET_ INTEGER DEFAULT -1;
ALTER TABLE OOCKE1_CONTRACTPOSITION_ ADD BUDGET VARCHAR(256);
CREATE TABLE OOCKE1_BUDGETPOSITION (OBJECT_ID VARCHAR(256) NOT NULL, ACCESS_LEVEL_BROWSE SMALLINT, ACCESS_LEVEL_DELETE SMALLINT, ACCESS_LEVEL_UPDATE SMALLINT, "P$$PARENT" VARCHAR(256), CATEGORY_ INTEGER DEFAULT -1, CONTRIBUTION_FACTOR NUMERIC, CONTRIBUTION_ROUNDING_FACTOR NUMERIC, CREATED_AT TIMESTAMP, CREATED_BY_ INTEGER DEFAULT -1, DEPOT_POSITION VARCHAR(256), DESCRIPTION VARCHAR(256), DISABLED BIT, DISABLED_REASON VARCHAR(256), EXTERNAL_LINK_ INTEGER DEFAULT -1, MODIFIED_BY_ INTEGER DEFAULT -1, NAME VARCHAR(256), OWNER_ INTEGER DEFAULT -1, TARGET_VOLUME NUMERIC, USER_BOOLEAN0 BIT, USER_BOOLEAN1 BIT, USER_BOOLEAN2 BIT, USER_BOOLEAN3 BIT, USER_BOOLEAN4_ INTEGER DEFAULT -1, USER_CODE0 SMALLINT, USER_CODE1 SMALLINT, USER_CODE2 SMALLINT, USER_CODE3 SMALLINT, USER_CODE4_ INTEGER DEFAULT -1, USER_DATE0 DATE, USER_DATE1 DATE, USER_DATE2 DATE, USER_DATE3 DATE, USER_DATE4_ INTEGER DEFAULT -1, USER_DATE_TIME0 TIMESTAMP, USER_DATE_TIME1 TIMESTAMP, USER_DATE_TIME2 TIMESTAMP, USER_DATE_TIME3 TIMESTAMP, USER_DATE_TIME4_ INTEGER DEFAULT -1, USER_NUMBER0 NUMERIC, USER_NUMBER1 NUMERIC, USER_NUMBER2 NUMERIC, USER_NUMBER3 NUMERIC, USER_NUMBER4_ INTEGER DEFAULT -1, USER_STRING0 VARCHAR(256), USER_STRING1 VARCHAR(256), USER_STRING2 VARCHAR(256), USER_STRING3 VARCHAR(256), USER_STRING4_ INTEGER DEFAULT -1, DTYPE VARCHAR(256), MODIFIED_AT TIMESTAMP, UOM VARCHAR(256), CURRENCY SMALLINT, VALUE_TYPE SMALLINT, PRIMARY KEY (OBJECT_ID));
CREATE TABLE OOCKE1_BUDGETPOSITION_ (OBJECT_ID VARCHAR(256) NOT NULL, IDX INTEGER NOT NULL, CATEGORY VARCHAR(256), CREATED_BY VARCHAR(256), EXTERNAL_LINK VARCHAR(256), MODIFIED_BY VARCHAR(256), OWNER VARCHAR(256), USER_BOOLEAN4 BIT, USER_CODE4 SMALLINT, USER_DATE4 DATE, USER_DATE_TIME4 TIMESTAMP, USER_NUMBER4 NUMERIC, USER_STRING4 VARCHAR(256), DTYPE VARCHAR(256), PRIMARY KEY (OBJECT_ID, IDX));



ALTER TABLE OOCKE1_FILTERPROPERTY ADD ACCOUNT_ INTEGER DEFAULT -1;
ALTER TABLE OOCKE1_FILTERPROPERTY_ ADD ACCOUNT VARCHAR(256);
ALTER TABLE OOCKE1_FILTER ADD ACTIVITIES_SOURCE VARCHAR(256);



ALTER TABLE OOCKE1_EMAILACCOUNT ADD DESCRIPTION VARCHAR(256);
ALTER TABLE OOCKE1_EMAILACCOUNT ADD IS_ACTIVE BIT;
ALTER TABLE OOCKE1_EMAILACCOUNT ADD NAME VARCHAR(256);
ALTER TABLE OOCKE1_EMAILACCOUNT ADD ACCESS_TOKEN VARCHAR(256);
ALTER TABLE OOCKE1_EMAILACCOUNT ADD ACCESS_TOKEN_SECRET VARCHAR(256);
ALTER TABLE OOCKE1_EMAILACCOUNT ADD CONSUMER_KEY VARCHAR(256);
ALTER TABLE OOCKE1_EMAILACCOUNT ADD CONSUMER_SECRET VARCHAR(256);
