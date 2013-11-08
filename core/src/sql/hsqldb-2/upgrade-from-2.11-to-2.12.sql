/* This software is published under the BSD license                          */
/* as listed below.                                                          */
/*                                                                           */
/* Copyright (c) 2004-2013, CRIXP Corp., Switzerland                         */
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

ALTER TABLE OOCKE1_BOOKING ADD COLUMN ORIGIN_CONTEXT_PARAMS_ INTEGER DEFAULT -1;
ALTER TABLE OOCKE1_BOOKING_ ADD COLUMN ORIGIN_CONTEXT_PARAMS VARCHAR(256);
ALTER TABLE OOCKE1_BOOKING ADD COLUMN ORIGIN_CONTEXT_ INTEGER DEFAULT -1;
ALTER TABLE OOCKE1_BOOKING_ ADD COLUMN ORIGIN_CONTEXT VARCHAR(256);


ALTER TABLE OOCKE1_INVOLVEDOBJECT ADD COLUMN VALID_FROM TIMESTAMP;
ALTER TABLE OOCKE1_INVOLVEDOBJECT ADD COLUMN VALID_TO TIMESTAMP;


ALTER TABLE OOCKE1_ACTIVITYGROUP ADD COLUMN ACTIVITY_GROUP_TYPE SMALLINT;
ALTER TABLE OOCKE1_ACTIVITYGROUP ADD COLUMN MAIN_ACTIVITY VARCHAR(256);
ALTER TABLE OOCKE1_ACTIVITYGROUP ADD COLUMN TARGET_GROUP_ACCOUNTS VARCHAR(256);
ALTER TABLE OOCKE1_ACTIVITYGROUPASS ADD COLUMN DESCRIPTION VARCHAR(256);
ALTER TABLE OOCKE1_ACTIVITYGROUPASS ADD COLUMN NAME VARCHAR(256);
ALTER TABLE OOCKE1_ACTIVITYGROUPASS ADD COLUMN RELATIONSHIP_TYPE SMALLINT;


ALTER TABLE OOCKE1_DOCUMENT ADD COLUMN CONTENT_LANGUAGE_ INTEGER DEFAULT -1;
ALTER TABLE OOCKE1_DOCUMENT_ ADD COLUMN CONTENT_LANGUAGE SMALLINT;


ALTER TABLE OOCKE1_FILTERPROPERTY ADD COLUMN CONTENT_LANGUAGE_ INTEGER DEFAULT -1;
ALTER TABLE OOCKE1_FILTERPROPERTY_ ADD COLUMN CONTENT_LANGUAGE SMALLINT;
ALTER TABLE OOCKE1_CALENDARFEED ADD COLUMN DOCUMENT_FILTER VARCHAR(256);
ALTER TABLE OOCKE1_FILTERPROPERTY ADD COLUMN NAME_PATTERN VARCHAR(256);
ALTER TABLE OOCKE1_FILTERPROPERTY ADD COLUMN DOCUMENT_STATE_ INTEGER DEFAULT -1;
ALTER TABLE OOCKE1_FILTERPROPERTY ADD COLUMN DOCUMENT_TYPE_ INTEGER DEFAULT -1;
ALTER TABLE OOCKE1_FILTERPROPERTY ADD COLUMN DOCUMENT_FOLDER_ INTEGER DEFAULT -1;
ALTER TABLE OOCKE1_FILTERPROPERTY_ ADD COLUMN DOCUMENT_STATE SMALLINT;
ALTER TABLE OOCKE1_FILTERPROPERTY_ ADD COLUMN DOCUMENT_TYPE SMALLINT;
ALTER TABLE OOCKE1_FILTERPROPERTY_ ADD COLUMN DOCUMENT_FOLDER VARCHAR(256);


ALTER TABLE OOCKE1_ADDITIONALEXTLINK ADD COLUMN DESCRIPTION VARCHAR(256);
ALTER TABLE OOCKE1_ADDITIONALEXTLINK ADD COLUMN NAME VARCHAR(256);
ALTER TABLE OOCKE1_DOCUMENTATTACHMENT ADD COLUMN NAME VARCHAR(256);
ALTER TABLE OOCKE1_RATING ADD COLUMN NAME VARCHAR(256);
