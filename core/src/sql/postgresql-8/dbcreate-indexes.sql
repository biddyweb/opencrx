/* This software is published under the BSD license                          */
/* as listed below.                                                          */
/*                                                                           */
/* Copyright (c) 2004-2007, CRIXP Corp., Switzerland                         */
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
CREATE INDEX IDX_Act_full_name ON OOCKE1_Account(full_name) ;
CREATE INDEX IDX_Act_alias_name ON OOCKE1_Account(alias_name) ;
CREATE INDEX IDX_Act_external_link ON OOCKE1_Account_(external_link) ;
CREATE INDEX IDX_Adr_parent ON OOCKE1_Address(p$$parent) ;
CREATE INDEX IDX_Adr_emailaddress ON OOCKE1_Address(email_address) ;
CREATE INDEX IDX_Activity_assigned_to ON OOCKE1_Activity(assigned_to) ;
CREATE INDEX IDX_Activity_percent_complete ON OOCKE1_Activity(percent_complete) ;
CREATE INDEX IDX_Act_activityNumber ON OOCKE1_Activity(activity_number) ;
CREATE INDEX IDX_Activity_modifiedAt ON OOCKE1_Activity(modified_at) ;
CREATE INDEX IDX_ActivityFollowUp_parent ON OOCKE1_ActivityFollowUp(p$$parent) ;
CREATE INDEX IDX_ActFollowUp_modified_at ON OOCKE1_ActivityFollowUp(modified_at) ;
CREATE INDEX IDX_ActivityParty_parent ON OOCKE1_ActivityParty(p$$parent) ;
CREATE INDEX IDX_ActivityParty_party ON OOCKE1_ActivityParty(party) ;
CREATE INDEX IDX_ActivityGrpAss_parent ON OOCKE1_ActivityGroupAss(p$$parent) ;
CREATE INDEX IDX_ActivityGrpAss_group ON OOCKE1_ActivityGroupAss(activity_group) ;
CREATE INDEX IDX_AuditEntry_created_at ON OOCKE1_AuditEntry(created_at) ;
CREATE INDEX IDX_Contract_sales_rep ON OOCKE1_Contract(sales_rep) ;
CREATE INDEX IDX_Contract_customer ON OOCKE1_Contract(customer) ;
CREATE INDEX IDX_ModelElement_qualifiedName ON OOCKE1_ModelElement(qualified_name) ;
CREATE INDEX IDX_ModelElement_container ON OOCKE1_ModelElement(container) ;
CREATE INDEX IDX_Note_parent ON OOCKE1_Note(p$$parent) ;
CREATE INDEX IDX_UserHome_contact ON OOCKE1_UserHome(contact) ;
CREATE INDEX IDX_WfProcInst_started_on ON OOCKE1_WfProcessInstance(started_on) ;
CREATE INDEX IDX_AuditEntry_visited_by ON OOCKE1_AuditEntry_(visited_by) ;
CREATE INDEX IDX_AuditEntry_dtype ON OOCKE1_AuditEntry(DTYPE) ;
CREATE INDEX IDX_Product_dtype ON OOCKE1_Product(DTYPE) ;
CREATE INDEX IDX_Product_parent ON OOCKE1_Product(p$$parent) ;
CREATE INDEX IDX_Product_price_uom ON OOCKE1_Product_(price_uom) ;
CREATE INDEX IDX_ProductBasePrice_parent ON OOCKE1_ProductBasePrice(p$$parent) ;
CREATE INDEX IDX_ProdBasePrice_price_level ON OOCKE1_ProductBasePrice_(price_level) ;
CREATE INDEX IDX_ProductBasePrice_owner_ref ON OOCKE1_ProductBasePrice_(owner) ;
CREATE INDEX IDX_ResourceAssignment_parent ON OOCKE1_ResourceAssignment(p$$parent) ;
CREATE INDEX IDX_WorkRecord_parent ON OOCKE1_WorkRecord(p$$parent) ;
CREATE INDEX IDX_INDEXEDOBJ_CREATEDAT ON OOCKE1_INDEXENTRY(INDEXED_OBJECT, CREATED_AT);
