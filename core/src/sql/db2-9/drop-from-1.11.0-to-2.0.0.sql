-- This software is published under the BSD license
-- as listed below.
--
-- Copyright (c) 2004-2008, CRIXP Corp., Switzerland
-- All rights reserved.
--
-- Redistribution and use in source and binary forms, with or without
-- modification, are permitted provided that the following conditions
-- are met:
--
-- * Redistributions of source code must retain the above copyright
-- notice, this list of conditions and the following disclaimer.
--
-- * Redistributions in binary form must reproduce the above copyright
-- notice, this list of conditions and the following disclaimer in
-- the documentation and/or other materials provided with the
-- distribution.
--
-- * Neither the name of CRIXP Corp. nor the names of the contributors
-- to openCRX may be used to endorse or promote products derived
-- from this software without specific prior written permission
--
-- THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
-- CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
-- INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
-- MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
-- DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS
-- BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
-- EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
-- TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES, LOSS OF USE,
-- DATA, OR PROFITS, OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
-- ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
-- OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
-- OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
-- POSSIBILITY OF SUCH DAMAGE.
--
-- ------------------
--
-- This product includes software developed by the Apache Software
-- Foundation (http://www.apache.org/).                                      
--
-- This product includes software developed by contributors to
-- openMDX (http://www.openmdx.org/)                                         
ALTER TABLE OOCKE1_DEPOTREPORTITEM
  DROP
     BOOKING_DATE
  ;
REORG TABLE OOCKE1_DEPOTREPORTITEM ;
ALTER TABLE OOCKE1_DEPOTREPORTITEM
  DROP
     CB
  ;
REORG TABLE OOCKE1_DEPOTREPORTITEM ;
ALTER TABLE OOCKE1_DEPOTREPORTITEM
  DROP
     QUANTITY_DEBIT
  ;
REORG TABLE OOCKE1_DEPOTREPORTITEM ;
ALTER TABLE OOCKE1_DEPOTREPORTITEM
  DROP
     QUANTITY_CREDIT
  ;
REORG TABLE OOCKE1_DEPOTREPORTITEM ;
DROP TABLE OOCKE1_TOBJ_SELECTABLEITEM ;
DROP TABLE OOCKE1_TOBJ_SELECTABLEITEM_ ;
DROP TABLE OOCKE1_TEMPLATEREPL ;
DROP TABLE OOCKE1_TEMPLATEREPL_ ;
ALTER TABLE OOCKE1_PRODUCT
  DROP
     TYPE
  ;
REORG TABLE OOCKE1_PRODUCT ;
ALTER TABLE OOCKE1_CONTRACT
  DROP
     IS_TEMPLATE
  ;
REORG TABLE OOCKE1_CONTRACT ;
ALTER TABLE OOCKE1_CONTRACT
  DROP
     TEMPLATE_REFERENCE_FILTER
  ;
REORG TABLE OOCKE1_CONTRACT ;
ALTER TABLE OOCKE1_DEPOTHOLDER
  DROP
     IS_TEMPLATE
  ;
REORG TABLE OOCKE1_DEPOTHOLDER ;
ALTER TABLE OOCKE1_DEPOTHOLDER
  DROP
     TEMPLATE_REFERENCE_FILTER
  ;
REORG TABLE OOCKE1_DEPOTHOLDER ;
