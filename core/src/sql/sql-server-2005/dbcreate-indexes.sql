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

CREATE INDEX I_OOCKE1_ACCOUNT_FULL_NAME ON OOCKE1_ACCOUNT(full_name) 
GO
CREATE INDEX I_OOCKE1_ACCOUNT_ALIAS_NAME ON OOCKE1_ACCOUNT(alias_name) 
GO
CREATE INDEX I_OOCKE1_ACCOUNT_EXTERNAL_LINK ON OOCKE1_ACCOUNT_(external_link) 
GO

CREATE INDEX I_OOCKE1_ADDRESS_PARENT ON OOCKE1_ADDRESS(p$$parent) 
GO
CREATE INDEX I_OOCKE1_ADDRESS_EMAILADDRESS ON OOCKE1_ADDRESS(email_address) 
GO

CREATE INDEX I_OOCKE1_ACTIVITY_ASSIGNED_TO ON OOCKE1_ACTIVITY(assigned_to)
GO
CREATE INDEX I_OOCKE1_ACTIVITY_PERC_COMPL ON OOCKE1_ACTIVITY(percent_complete) 
GO
CREATE INDEX I_OOCKE1_ACTIVITY_ACTIVITYNUM ON OOCKE1_ACTIVITY(activity_number) 
GO
CREATE INDEX I_OOCKE1_ACTIVITY_MOD_AT ON OOCKE1_ACTIVITY(modified_at) 
GO
CREATE INDEX I_OOCKE1_ACTIVITY_SCHEDSTART ON OOCKE1_ACTIVITY(scheduled_start) 
GO
CREATE INDEX I_OOCKE1_ACTIVITY_SCHEDEND ON OOCKE1_ACTIVITY(scheduled_end) 
GO
CREATE INDEX I_OOCKE1_ACTIVITY_CREATED_AT ON OOCKE1_ACTIVITY(created_at) 
GO
CREATE INDEX I_OOCKE1_ACTIVITY_DUE_BY ON OOCKE1_ACTIVITY(due_by) 
GO
CREATE INDEX I_OOCKE1_ACTIVITY_SENDER ON OOCKE1_ACTIVITY(sender) 
GO

CREATE INDEX I_OOCKE1_ACTIVITYASS_PARENT ON OOCKE1_ACCOUNTASSIGNMENT(p$$parent) 
GO
CREATE INDEX I_OOCKE1_ACTIVITYASS_ACCOUNT ON OOCKE1_ACCOUNTASSIGNMENT(account) 
GO

CREATE INDEX I_OOCKE1_ACTFLOLLOWUP_PARENT ON OOCKE1_ACTIVITYFOLLOWUP(p$$parent) 
GO
CREATE INDEX I_OOCKE1_ACTFOLLOWUP_MOD_AT ON OOCKE1_ACTIVITYFOLLOWUP(modified_at) 
GO
CREATE INDEX I_OOCKE1_ACTFOLLOWUP_CREAT_AT ON OOCKE1_ACTIVITYFOLLOWUP(created_at) 
GO

CREATE INDEX I_OOCKE1_ACTIVITYPARTY_PARENT ON OOCKE1_ACTIVITYPARTY(p$$parent) 
GO
CREATE INDEX I_OOCKE1_ACTIVITYPARTY_PARTY ON OOCKE1_ACTIVITYPARTY(party) 
GO

CREATE INDEX I_OOCKE1_ACTGROUPASS_PARENT ON OOCKE1_ACTIVITYGROUPASS(p$$parent) 
GO
CREATE INDEX I_OOCKE1_ACTGROUPASS_GROUP ON OOCKE1_ACTIVITYGROUPASS(activity_group) 
GO

CREATE INDEX I_OOCKE1_AUDITENTRY_CREATEDAT ON OOCKE1_AUDITENTRY(created_at) 
GO

CREATE INDEX I_OOCKE1_CONTRACT_SALES_REP ON OOCKE1_CONTRACT(sales_rep) 
GO
CREATE INDEX I_OOCKE1_CONTRACT_CUSTOMER ON OOCKE1_CONTRACT(customer) 
GO
CREATE INDEX I_OOCKE1_CONTRACT_ACTIVE_ON ON OOCKE1_CONTRACT(active_on) 
GO
CREATE INDEX I_OOCKE1_CONTRACT_EXPIRES_ON ON OOCKE1_CONTRACT(expires_on) 
GO
CREATE INDEX I_OOCKE1_CONTRACT_MODIFIEDAT ON OOCKE1_CONTRACT(modified_at) 
GO
CREATE INDEX I_OOCKE1_CONTRACT_CREATEDAT ON OOCKE1_CONTRACT(created_at) 
GO

CREATE INDEX I_OOCKE1_NOTE_PARENT ON OOCKE1_NOTE(p$$parent) 
GO

CREATE INDEX I_OOCKE1_USERHOME_CONTACT ON OOCKE1_USERHOME(contact) 
GO

CREATE INDEX I_OOCKE1_WFPROCINST_STARTED_ON ON OOCKE1_WFPROCESSINSTANCE(started_on) 
GO

CREATE INDEX I_OOCKE1_AUDITENTRY_VISITED_BY ON OOCKE1_AUDITENTRY_(visited_by) 
GO

CREATE INDEX I_OOCKE1_PRODUCT_PARENT ON OOCKE1_PRODUCT(p$$parent) 
GO
CREATE INDEX I_OOCKE1_PRODUCT_PRICE_UOM ON OOCKE1_PRODUCT_(price_uom) 
GO

CREATE INDEX I_OOCKE1_PRODBPR_PARENT ON OOCKE1_PRODUCTBASEPRICE(p$$parent) 
GO
CREATE INDEX I_OOCKE1_PRODBPR_PRICE_LEVEL ON OOCKE1_PRODUCTBASEPRICE_(price_level) 
GO

CREATE INDEX I_OOCKE1_RESASS_PARENT ON OOCKE1_RESOURCEASSIGNMENT(p$$parent) 
GO

CREATE INDEX I_OOCKE1_WORKRECORD_PARENT ON OOCKE1_WORKRECORD(p$$parent) 
GO
CREATE INDEX I_OOCKE1_WORKRECORD_STARTED_AT ON OOCKE1_WORKRECORD(started_at) ;
GO

CREATE INDEX I_OOCKE1_INDEXENTRY_CREATEDAT ON OOCKE1_INDEXENTRY(indexed_object, created_at) 
GO
CREATE INDEX I_OOCKE1_INDEXENTRY_INDEXEDOBJ ON OOCKE1_INDEXENTRY(indexed_object) 
GO

CREATE INDEX I_OOMSE2_PRINCIPAL ON OOMSE2_PRINCIPAL(name);
GO
