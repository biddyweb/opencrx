/* This software is published under the BSD license                          */
/* as listed below.                                                          */
/*                                                                           */
/* Copyright (c) 2004-2015, CRIXP Corp., Switzerland                         */
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

ALTER TABLE OOCKE1_MEDIA ADD COLUMN USER_BOOLEAN0 BOOLEAN;
ALTER TABLE OOCKE1_MEDIA ADD COLUMN USER_BOOLEAN1 BOOLEAN;
ALTER TABLE OOCKE1_MEDIA ADD COLUMN USER_BOOLEAN2 BOOLEAN;
ALTER TABLE OOCKE1_MEDIA ADD COLUMN USER_BOOLEAN3 BOOLEAN;
ALTER TABLE OOCKE1_MEDIA ADD COLUMN USER_BOOLEAN4_ INTEGER DEFAULT -1;
ALTER TABLE OOCKE1_MEDIA ADD COLUMN USER_CODE0 SMALLINT;
ALTER TABLE OOCKE1_MEDIA ADD COLUMN USER_CODE1 SMALLINT;
ALTER TABLE OOCKE1_MEDIA ADD COLUMN USER_CODE2 SMALLINT;
ALTER TABLE OOCKE1_MEDIA ADD COLUMN USER_CODE3 SMALLINT;
ALTER TABLE OOCKE1_MEDIA ADD COLUMN USER_CODE4_ INTEGER DEFAULT -1;
ALTER TABLE OOCKE1_MEDIA ADD COLUMN USER_DATE0 DATE;
ALTER TABLE OOCKE1_MEDIA ADD COLUMN USER_DATE1 DATE;
ALTER TABLE OOCKE1_MEDIA ADD COLUMN USER_DATE2 DATE;
ALTER TABLE OOCKE1_MEDIA ADD COLUMN USER_DATE3 DATE;
ALTER TABLE OOCKE1_MEDIA ADD COLUMN USER_DATE4_ INTEGER DEFAULT -1;
ALTER TABLE OOCKE1_MEDIA ADD COLUMN USER_DATE_TIME0 TIMESTAMP;
ALTER TABLE OOCKE1_MEDIA ADD COLUMN USER_DATE_TIME1 TIMESTAMP;
ALTER TABLE OOCKE1_MEDIA ADD COLUMN USER_DATE_TIME2 TIMESTAMP;
ALTER TABLE OOCKE1_MEDIA ADD COLUMN USER_DATE_TIME3 TIMESTAMP;
ALTER TABLE OOCKE1_MEDIA ADD COLUMN USER_DATE_TIME4_ INTEGER DEFAULT -1;
ALTER TABLE OOCKE1_MEDIA ADD COLUMN USER_NUMBER0 NUMERIC(19,9);
ALTER TABLE OOCKE1_MEDIA ADD COLUMN USER_NUMBER1 NUMERIC(19,9);
ALTER TABLE OOCKE1_MEDIA ADD COLUMN USER_NUMBER2 NUMERIC(19,9);
ALTER TABLE OOCKE1_MEDIA ADD COLUMN USER_NUMBER3 NUMERIC(19,9);
ALTER TABLE OOCKE1_MEDIA ADD COLUMN USER_NUMBER4_ INTEGER DEFAULT -1;
ALTER TABLE OOCKE1_MEDIA ADD COLUMN USER_STRING0 VARCHAR(256);
ALTER TABLE OOCKE1_MEDIA ADD COLUMN USER_STRING1 VARCHAR(256);
ALTER TABLE OOCKE1_MEDIA ADD COLUMN USER_STRING2 VARCHAR(256);
ALTER TABLE OOCKE1_MEDIA ADD COLUMN USER_STRING3 VARCHAR(256);
ALTER TABLE OOCKE1_MEDIA ADD COLUMN USER_STRING4_ INTEGER DEFAULT -1;
ALTER TABLE OOCKE1_MEDIA_ ADD COLUMN USER_BOOLEAN4 BOOLEAN;
ALTER TABLE OOCKE1_MEDIA_ ADD COLUMN USER_CODE4 SMALLINT;
ALTER TABLE OOCKE1_MEDIA_ ADD COLUMN USER_DATE4 DATE;
ALTER TABLE OOCKE1_MEDIA_ ADD COLUMN USER_DATE_TIME4 TIMESTAMP;
ALTER TABLE OOCKE1_MEDIA_ ADD COLUMN USER_NUMBER4 NUMERIC(19,9);
ALTER TABLE OOCKE1_MEDIA_ ADD COLUMN USER_STRING4 VARCHAR(256);

ALTER TABLE OOCKE1_NOTE ADD COLUMN USER_STRING4_ INTEGER DEFAULT -1;
ALTER TABLE OOCKE1_NOTE_ ADD COLUMN USER_STRING4 VARCHAR(256);
