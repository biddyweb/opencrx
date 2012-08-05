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

ALTER TABLE OOCKE1_ACTIVITY ADD ICAL_TYPE smallint NULL;

ALTER TABLE OOCKE1_ACTIVITYCREATOR ADD ICAL_TYPE smallint NULL;




ALTER TABLE OOCKE1_DOCUMENT ADD DISABLED bit NULL;




ALTER TABLE OOCKE1_DOCUMENTFOLDER ADD DISABLED bit NULL;

CREATE TABLE OOCKE1_DOCUMENTFOLDERASS
(
    OBJECT_ID nvarchar(256) NOT NULL,
    ACCESS_LEVEL_BROWSE smallint NULL,
    ACCESS_LEVEL_DELETE smallint NULL,
    ACCESS_LEVEL_UPDATE smallint NULL,
    CREATED_AT datetime NULL,
    CREATED_BY_ int DEFAULT -1 NOT NULL,
    P$$PARENT nvarchar(256) NULL,
    DESCRIPTION nvarchar(256) NULL,
    DISABLED bit NULL,
    DOCUMENT_FOLDER nvarchar(256) NULL,
    MODIFIED_BY_ int DEFAULT -1 NOT NULL,
    "NAME" nvarchar(256) NULL,
    OWNER_ int DEFAULT -1 NOT NULL,
    VALID_FROM datetime NULL,
    VALID_TO datetime NULL,
    MODIFIED_AT datetime NOT NULL,
    DTYPE nvarchar(256) NOT NULL
);

ALTER TABLE OOCKE1_DOCUMENTFOLDERASS ADD CONSTRAINT OOCKE1_DOCUMENTFOLDERASS_PK PRIMARY KEY (OBJECT_ID);

CREATE TABLE OOCKE1_DOCUMENTFOLDERASS_
(
    OBJECT_ID nvarchar(256) NOT NULL,
    IDX int NOT NULL,
    CREATED_BY nvarchar(256) NULL,
    MODIFIED_BY nvarchar(256) NULL,
    OWNER nvarchar(256) NULL,
    DTYPE nvarchar(256) NOT NULL
);

ALTER TABLE OOCKE1_DOCUMENTFOLDERASS_ ADD CONSTRAINT OOCKE1_DOCUMENTFOLDERASS__PK PRIMARY KEY (OBJECT_ID,IDX);




CREATE TABLE OOCKE1_CONTRACTLINK
(
    OBJECT_ID nvarchar(256) NOT NULL,
    ACCESS_LEVEL_BROWSE smallint NULL,
    ACCESS_LEVEL_DELETE smallint NULL,
    ACCESS_LEVEL_UPDATE smallint NULL,
    CATEGORY_ int DEFAULT -1 NOT NULL,
    P$$PARENT nvarchar(256) NULL,
    CREATED_AT datetime NULL,
    CREATED_BY_ int DEFAULT -1 NOT NULL,
    DESCRIPTION nvarchar(256) NULL,
    DISABLED bit NULL,
    DISABLED_REASON nvarchar(256) NULL,
    EXTERNAL_LINK_ int DEFAULT -1 NOT NULL,
    LINK_TO nvarchar(256) NULL,
    LINK_TYPE smallint NULL,
    MODIFIED_BY_ int DEFAULT -1 NOT NULL,
    "NAME" nvarchar(256) NULL,
    OWNER_ int DEFAULT -1 NOT NULL,
    USER_BOOLEAN0 bit NULL,
    USER_BOOLEAN1 bit NULL,
    USER_BOOLEAN2 bit NULL,
    USER_BOOLEAN3 bit NULL,
    USER_BOOLEAN4_ int DEFAULT -1 NOT NULL,
    USER_CODE0 smallint NULL,
    USER_CODE1 smallint NULL,
    USER_CODE2 smallint NULL,
    USER_CODE3 smallint NULL,
    USER_CODE4_ int DEFAULT -1 NOT NULL,
    USER_DATE0 datetime NULL,
    USER_DATE1 datetime NULL,
    USER_DATE2 datetime NULL,
    USER_DATE3 datetime NULL,
    USER_DATE4_ int DEFAULT -1 NOT NULL,
    USER_DATE_TIME0 datetime NULL,
    USER_DATE_TIME1 datetime NULL,
    USER_DATE_TIME2 datetime NULL,
    USER_DATE_TIME3 datetime NULL,
    USER_DATE_TIME4_ int DEFAULT -1 NOT NULL,
    USER_NUMBER0 decimal(19,9) NULL,
    USER_NUMBER1 decimal(19,9) NULL,
    USER_NUMBER2 decimal(19,9) NULL,
    USER_NUMBER3 decimal(19,9) NULL,
    USER_NUMBER4_ int DEFAULT -1 NOT NULL,
    USER_STRING0 nvarchar(256) NULL,
    USER_STRING1 nvarchar(256) NULL,
    USER_STRING2 nvarchar(256) NULL,
    USER_STRING3 nvarchar(256) NULL,
    USER_STRING4_ int DEFAULT -1 NOT NULL,
    VALID_FROM datetime NULL,
    VALID_TO datetime NULL,
    MODIFIED_AT datetime NOT NULL,
    DTYPE nvarchar(256) NOT NULL
);

ALTER TABLE OOCKE1_CONTRACTLINK ADD CONSTRAINT OOCKE1_CONTRACTLINK_PK PRIMARY KEY (OBJECT_ID);

CREATE TABLE OOCKE1_CONTRACTLINK_
(
    OBJECT_ID nvarchar(256) NOT NULL,
    IDX int NOT NULL,
    CATEGORY nvarchar(256) NULL,
    CREATED_BY nvarchar(256) NULL,
    EXTERNAL_LINK nvarchar(256) NULL,
    MODIFIED_BY nvarchar(256) NULL,
    OWNER nvarchar(256) NULL,
    USER_BOOLEAN4 bit NULL,
    USER_CODE4 smallint NULL,
    USER_DATE4 datetime NULL,
    USER_DATE_TIME4 datetime NULL,
    USER_NUMBER4 decimal(19,9) NULL,
    USER_STRING4 nvarchar(256) NULL,
    DTYPE nvarchar(256) NOT NULL
);

ALTER TABLE OOCKE1_CONTRACTLINK_ ADD CONSTRAINT OOCKE1_CONTRACTLINK__PK PRIMARY KEY (OBJECT_ID,IDX);




CREATE TABLE OOCKE1_CALENDARFEED
(
    OBJECT_ID nvarchar(256) NOT NULL,
    ACCESS_LEVEL_BROWSE smallint NULL,
    ACCESS_LEVEL_DELETE smallint NULL,
    ACCESS_LEVEL_UPDATE smallint NULL,
    P$$PARENT nvarchar(256) NULL,
    CREATED_AT datetime NULL,
    CREATED_BY_ int DEFAULT -1 NOT NULL,
    DESCRIPTION nvarchar(256) NULL,
    BACK_COLOR nvarchar(256) NULL,
    COLOR nvarchar(256) NULL,
    IS_ACTIVE bit NULL,
    MODIFIED_BY_ int DEFAULT -1 NOT NULL,
    "NAME" nvarchar(256) NULL,
    OWNER_ int DEFAULT -1 NOT NULL,
    MODIFIED_AT datetime NOT NULL,
    DTYPE nvarchar(256) NOT NULL,
    ACTIVITY_FILTER nvarchar(256) NULL,
    PASSWORD nvarchar(256) NULL,
    URL nvarchar(256) NULL,
    USERNAME nvarchar(256) NULL,
    ACTIVITY_GROUP nvarchar(256) NULL
);

ALTER TABLE OOCKE1_CALENDARFEED ADD CONSTRAINT OOCKE1_CALENDARFEED_PK PRIMARY KEY (OBJECT_ID);

CREATE TABLE OOCKE1_CALENDARFEED_
(
    OBJECT_ID nvarchar(256) NOT NULL,
    IDX int NOT NULL,
    CREATED_BY nvarchar(256) NULL,
    MODIFIED_BY nvarchar(256) NULL,
    OWNER nvarchar(256) NULL,
    DTYPE nvarchar(256) NOT NULL
);

ALTER TABLE OOCKE1_CALENDARFEED_ ADD CONSTRAINT OOCKE1_CALENDARFEED__PK PRIMARY KEY (OBJECT_ID,IDX);

CREATE TABLE OOCKE1_CALENDARPROFILE
(
    OBJECT_ID nvarchar(256) NOT NULL,
    ACCESS_LEVEL_BROWSE smallint NULL,
    ACCESS_LEVEL_DELETE smallint NULL,
    ACCESS_LEVEL_UPDATE smallint NULL,
    CREATED_AT datetime NULL,
    CREATED_BY_ int DEFAULT -1 NOT NULL,
    DESCRIPTION nvarchar(256) NULL,
    MODIFIED_BY_ int DEFAULT -1 NOT NULL,
    "NAME" nvarchar(256) NULL,
    OWNER_ int DEFAULT -1 NOT NULL,
    P$$PARENT nvarchar(256) NULL,
    MODIFIED_AT datetime NOT NULL,
    DTYPE nvarchar(256) NOT NULL
);

ALTER TABLE OOCKE1_CALENDARPROFILE ADD CONSTRAINT OOCKE1_CALENDARPROFILE_PK PRIMARY KEY (OBJECT_ID);

CREATE TABLE OOCKE1_CALENDARPROFILE_
(
    OBJECT_ID nvarchar(256) NOT NULL,
    IDX int NOT NULL,
    CREATED_BY nvarchar(256) NULL,
    MODIFIED_BY nvarchar(256) NULL,
    OWNER nvarchar(256) NULL,
    DTYPE nvarchar(256) NOT NULL
);

ALTER TABLE OOCKE1_CALENDARPROFILE_ ADD CONSTRAINT OOCKE1_CALENDARPROFILE__PK PRIMARY KEY (OBJECT_ID,IDX);




ALTER TABLE OOCKE1_BUDGET ADD "NAME" nvarchar(256) NULL;

ALTER TABLE OOCKE1_BUDGET ADD DESCRIPTION nvarchar(256) NULL;




ALTER TABLE OOCKE1_ACTIVITY ADD GATEWAY nvarchar(256) NULL;
