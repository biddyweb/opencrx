-- This software is published under the BSD license
-- as listed below.
--
-- Copyright (c) 2004-2009, CRIXP Corp., Switzerland
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
DELETE FROM OOCKE1_ACCESSHISTORY WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_ACCESSHISTORY_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_ACCOUNT WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_ACCOUNT_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_ACCOUNTASSIGNMENT WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_ACCOUNTASSIGNMENT_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_ACTIVITY WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_ACTIVITY_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_ACTIVITYCREATOR WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_ACTIVITYCREATOR_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_ACTIVITYEFFORTESTI WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_ACTIVITYEFFORTESTI_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_ACTIVITYFOLLOWUP WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_ACTIVITYFOLLOWUP_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_ACTIVITYGROUP WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_ACTIVITYGROUP_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_ACTIVITYGROUPASS WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_ACTIVITYGROUPASS_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_ACTIVITYLINK WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_ACTIVITYLINK_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_ACTIVITYPARTY WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_ACTIVITYPARTY_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_ACTIVITYPROCACTION WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_ACTIVITYPROCACTION_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_ACTIVITYPROCESS WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_ACTIVITYPROCESS_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_ACTIVITYPROCSTATE WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_ACTIVITYPROCSTATE_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_ACTIVITYPROCTRANS WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_ACTIVITYPROCTRANS_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_ACTIVITYTYPE WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_ACTIVITYTYPE_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_ADDITIONALEXTLINK WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_ADDITIONALEXTLINK_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_ADDRESS WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_ADDRESS_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_ADDRESSGROUP WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_ADDRESSGROUP_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_ADDRESSGROUPMEMBER WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_ADDRESSGROUPMEMBER_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_ALERT WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_ALERT_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_AUDITENTRY WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_AUDITENTRY_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_BOOKING WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_BOOKING_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_BOOKINGPERIOD WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_BOOKINGPERIOD_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_BOOKINGTEXT WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_BOOKINGTEXT_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_BUDGET WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_BUDGET_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_BUILDINGUNIT WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_BUILDINGUNIT_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_CALCULATIONRULE WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_CALCULATIONRULE_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_CALENDAR WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_CALENDAR_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_CALENDARDAY WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_CALENDARDAY_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_CALENDARFEED WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_CALENDARFEED_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_CALENDARPROFILE WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_CALENDARPROFILE_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_CHART WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_CHART_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_CODEVALUECONTAINER WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_CODEVALUECONTAINER_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_CODEVALUEENTRY WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_CODEVALUEENTRY_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_COMPETITOR WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_COMPETITOR_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_COMPONENTCONFIG WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_COMPONENTCONFIG_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_COMPOUNDBOOKING WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_COMPOUNDBOOKING_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_CONTACTMEMBERSHIP WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_CONTACTMEMBERSHIP_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_CONTACTREL WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_CONTACTREL_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_CONTACTROLE WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_CONTACTROLE_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_CONTRACT WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_CONTRACT_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_CONTRACTLINK WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_CONTRACTLINK_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_CONTRACTPOSITION WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_CONTRACTPOSITION_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_CONTRACTPOSMOD WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_CONTRACTPOSMOD_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_CREDITLIMIT WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_CREDITLIMIT_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_DELIVERYINFO WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_DELIVERYINFO_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_DELIVERYREQUEST WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_DELIVERYREQUEST_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_DEPOT WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_DEPOT_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_DEPOTENTITY WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_DEPOTENTITY_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_DEPOTENTITYREL WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_DEPOTENTITYREL_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_DEPOTGROUP WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_DEPOTGROUP_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_DEPOTHOLDER WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_DEPOTHOLDER_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_DEPOTPOSITION WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_DEPOTPOSITION_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_DEPOTREFERENCE WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_DEPOTREFERENCE_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_DEPOTREPORT WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_DEPOTREPORT_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_DEPOTREPORTITEM WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_DEPOTREPORTITEM_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_DEPOTTYPE WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_DEPOTTYPE_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_DESCRIPTION WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_DESCRIPTION_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_DOCUMENT WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_DOCUMENT_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_DOCUMENTATTACHMENT WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_DOCUMENTATTACHMENT_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_DOCUMENTFOLDER WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_DOCUMENTFOLDER_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_DOCUMENTFOLDERASS WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_DOCUMENTFOLDERASS_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_DOCUMENTLINK WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_DOCUMENTLINK_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_DOCUMENTLOCK WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_DOCUMENTLOCK_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_EMAILACCOUNT WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_EMAILACCOUNT_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_EVENT WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_EVENT_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_EVENTPART WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_EVENTPART_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_EVENTSLOT WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_EVENTSLOT_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_EXPORTPROFILE WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_EXPORTPROFILE_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_FACILITY WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_FACILITY_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_FILTER WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_FILTER_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_FILTERPROPERTY WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_FILTERPROPERTY_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_INDEXENTRY WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_INDEXENTRY_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_INVENTORYITEM WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_INVENTORYITEM_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_INVOLVEDOBJECT WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_INVOLVEDOBJECT_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_LINKABLEITEMLINK WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_LINKABLEITEMLINK_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_MEDIA WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_MEDIA_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_MODELELEMENT WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_MODELELEMENT_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_NOTE WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_NOTE_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_OBJECTFINDER WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_OBJECTFINDER_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_ORGANIZATION WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_ORGANIZATION_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_ORGANIZATIONALUNIT WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_ORGANIZATIONALUNIT_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_ORGUNITRELSHIP WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_ORGUNITRELSHIP_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_PRICELEVEL WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_PRICELEVEL_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_PRICEMODIFIER WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_PRICEMODIFIER_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_PRICINGRULE WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_PRICINGRULE_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_PRODUCT WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_PRODUCT_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_PRODUCTAPPLICATION WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_PRODUCTAPPLICATION_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_PRODUCTBASEPRICE WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_PRODUCTBASEPRICE_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_PRODUCTCLASS WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_PRODUCTCLASS_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_PRODUCTCLASSREL WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_PRODUCTCLASSREL_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_PRODUCTCONFIG WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_PRODUCTCONFIG_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_PRODUCTCONFTYPESET WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_PRODUCTCONFTYPESET_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_PRODUCTPHASE WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_PRODUCTPHASE_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_PRODUCTREFERENCE WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_PRODUCTREFERENCE_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_PROPERTY WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_PROPERTY_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_PROPERTYSET WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_PROPERTYSET_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_QUICKACCESS WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_QUICKACCESS_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_RASARTIFACTCONTEXT WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_RASARTIFACTCONTEXT_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_RASARTIFACTDEP WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_RASARTIFACTDEP_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_RASCLASSIFICATIELT WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_RASCLASSIFICATIELT_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_RASDESCRIPTOR WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_RASDESCRIPTOR_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_RASPROFILE WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_RASPROFILE_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_RASSOLUTIONPART WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_RASSOLUTIONPART_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_RASVARPOINT WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_RASVARPOINT_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_RATING WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_RATING_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_RELATEDPRODUCT WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_RELATEDPRODUCT_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_RESOURCE WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_RESOURCE_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_RESOURCEASSIGNMENT WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_RESOURCEASSIGNMENT_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_REVENUEREPORT WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_REVENUEREPORT_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_SALESTAXTYPE WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_SALESTAXTYPE_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_SEGMENT WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_SEGMENT_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_SIMPLEBOOKING WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_SIMPLEBOOKING_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_SUBSCRIPTION WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_SUBSCRIPTION_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_TOPIC WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_TOPIC_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_UOM WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_UOM_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_UOMSCHEDULE WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_UOMSCHEDULE_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_USERHOME WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_USERHOME_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_VOTE WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_VOTE_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_WFACTIONLOGENTRY WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_WFACTIONLOGENTRY_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_WFPROCESS WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_WFPROCESS_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_WFPROCESSINSTANCE WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_WFPROCESSINSTANCE_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_WORKRECORD WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCKE1_WORKRECORD_ WHERE object_id LIKE '%/{provider}/{segment}/%' ;
DELETE FROM OOCSE1_PERMISSION WHERE object_id LIKE '%/{provider}/Root/{segment}/%' ;
DELETE FROM OOCSE1_PERMISSION_ WHERE object_id LIKE '%/{provider}/Root/{segment}/%' ;
DELETE FROM OOCSE1_POLICY WHERE object_id LIKE '%/{provider}/Root/{segment}/%' ;
DELETE FROM OOCSE1_POLICY_ WHERE object_id LIKE '%/{provider}/Root/{segment}/%' ;
DELETE FROM OOCSE1_PRINCIPAL WHERE object_id LIKE '%/{provider}/Root/{segment}/%' ;
DELETE FROM OOCSE1_PRINCIPAL_ WHERE object_id LIKE '%/{provider}/Root/{segment}/%' ;
DELETE FROM OOCSE1_PRIVILEGE WHERE object_id LIKE '%/{provider}/Root/{segment}/%' ;
DELETE FROM OOCSE1_PRIVILEGE_ WHERE object_id LIKE '%/{provider}/Root/{segment}/%' ;
DELETE FROM OOCSE1_REALM WHERE object_id LIKE '%/{provider}/Root/{segment}/%' ;
DELETE FROM OOCSE1_REALM_ WHERE object_id LIKE '%/{provider}/Root/{segment}/%' ;
DELETE FROM OOCSE1_ROLE WHERE object_id LIKE '%/{provider}/Root/{segment}/%' ;
DELETE FROM OOCSE1_ROLE_ WHERE object_id LIKE '%/{provider}/Root/{segment}/%' ;
