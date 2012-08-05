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
CREATE TABLE prefs_Preference(
    object_rid varchar(100) NOT NULL,
    object_oid varchar(200) NOT NULL,
    object_idx integer NOT NULL,
    object__class varchar(100) NULL,
    name varchar(100) NULL,
    description varchar(100) NULL,
    absolute_path varchar(200) NULL,
    parent varchar(100) NULL,
    string_value text NULL,
    integer_value integer NULL,
    boolean_value varchar(10) NULL,
    uri_value varchar(200) NULL,
    decimal_value decimal(18,9) NULL,
    PRIMARY KEY (object_rid, object_oid, object_idx)
);
CREATE SEQUENCE OOCKE1_activity_number_SEQ INCREMENT 1 START 1000000 ;
CREATE SEQUENCE OOCKE1_position_number_SEQ INCREMENT 1 START 1000000 ;

--
-- Name: oocke1_accesshistory; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_accesshistory (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    modified_by_ integer DEFAULT -1 NOT NULL,
    owner_ integer DEFAULT -1 NOT NULL,
    reference character varying(256),
    "p$$parent" character varying(256),
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_accesshistory_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_accesshistory_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    created_by character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_account; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_account (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    account_category_ integer DEFAULT -1 NOT NULL,
    account_rating smallint,
    account_state smallint,
    account_type_ integer DEFAULT -1 NOT NULL,
    alias_name character varying(256),
    business_type_ integer DEFAULT -1 NOT NULL,
    category_ integer DEFAULT -1 NOT NULL,
    contact_ integer DEFAULT -1 NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    disabled boolean,
    disabled_reason character varying(256),
    external_link_ integer DEFAULT -1 NOT NULL,
    full_name character varying(256),
    master character varying(256),
    modified_by_ integer DEFAULT -1 NOT NULL,
    owner_ integer DEFAULT -1 NOT NULL,
    partner_ integer DEFAULT -1 NOT NULL,
    "p$$parent" character varying(256),
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL,
    industry smallint,
    name character varying(256),
    number_of_employees integer,
    number_of_employees_category smallint,
    org_unit character varying(256),
    stock_exchange character varying(256),
    ticker_symbol character varying(256),
    anniversary timestamp with time zone,
    annual_income_amount numeric,
    annual_income_currency smallint,
    assistant character varying(256),
    birthdate timestamp with time zone,
    children_names_ integer DEFAULT -1 NOT NULL,
    department character varying(256),
    deputy character varying(256),
    do_not_bulk_postal_mail boolean,
    do_not_e_mail boolean,
    do_not_fax boolean,
    do_not_phone boolean,
    do_not_postal_mail boolean,
    education smallint,
    family_status smallint,
    first_name character varying(256),
    gender smallint,
    government_id character varying(256),
    job_role character varying(256),
    job_title character varying(256),
    last_name character varying(256),
    middle_name character varying(256),
    nick_name character varying(256),
    number_of_children smallint,
    organization character varying(256),
    ou_membership_ integer DEFAULT -1 NOT NULL,
    picture character varying(256),
    preferred_contact_method smallint,
    preferred_spoken_language smallint,
    preferred_written_language smallint,
    reports_to character varying(256),
    salutation character varying(256),
    salutation_code smallint,
    suffix character varying(256),
    ext_boolean0 boolean,
    ext_boolean1 boolean,
    ext_boolean2 boolean,
    ext_boolean3 boolean,
    ext_boolean4 boolean,
    ext_boolean5 boolean,
    ext_boolean6 boolean,
    ext_boolean7 boolean,
    ext_boolean8 boolean,
    ext_boolean9 boolean,
    ext_code0 smallint,
    ext_code1 smallint,
    ext_code2 smallint,
    ext_code3 smallint,
    ext_code4 smallint,
    ext_code5 smallint,
    ext_code6 smallint,
    ext_code7 smallint,
    ext_code8 smallint,
    ext_code9 smallint,
    ext_date0 date,
    ext_date1 date,
    ext_date2 date,
    ext_date3 date,
    ext_date4 date,
    ext_date5 date,
    ext_date6 date,
    ext_date7 date,
    ext_date8 date,
    ext_date9 date,
    ext_date_time0 timestamp with time zone,
    ext_date_time1 timestamp with time zone,
    ext_date_time2 timestamp with time zone,
    ext_date_time3 timestamp with time zone,
    ext_date_time4 timestamp with time zone,
    ext_date_time5 timestamp with time zone,
    ext_date_time6 timestamp with time zone,
    ext_date_time7 timestamp with time zone,
    ext_date_time8 timestamp with time zone,
    ext_date_time9 timestamp with time zone,
    ext_number0 numeric,
    ext_number1 numeric,
    ext_number2 numeric,
    ext_number3 numeric,
    ext_number4 numeric,
    ext_number5 numeric,
    ext_number6 numeric,
    ext_number7 numeric,
    ext_number8 numeric,
    ext_number9 numeric,
    ext_string0 character varying(256),
    ext_string1 character varying(256),
    ext_string2 character varying(256),
    ext_string3 character varying(256),
    ext_string4 character varying(256),
    ext_string5 character varying(256),
    ext_string6 character varying(256),
    ext_string7 character varying(256),
    ext_string8 character varying(256),
    ext_string9 character varying(256),
    ext_code19 smallint,
    ext_code18 smallint,
    ext_code17 smallint,
    ext_code16 smallint,
    ext_code10 smallint,
    ext_code11 smallint,
    ext_code14 smallint,
    ext_code15 smallint,
    ext_code12 smallint,
    ext_code13 smallint,
    citizenship_ integer DEFAULT -1 NOT NULL,
    religion_ integer DEFAULT -1 NOT NULL
);


--
-- Name: oocke1_account_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_account_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    account_category smallint,
    account_type smallint,
    business_type smallint,
    category character varying(256),
    contact character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    partner character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL,
    children_names character varying(256),
    ou_membership character varying(256),
    citizenship smallint,
    religion smallint
);


--
-- Name: oocke1_accountassignment; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_accountassignment (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    account character varying(256),
    account_role smallint,
    category_ integer DEFAULT -1 NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    disabled boolean,
    disabled_reason character varying(256),
    external_link_ integer DEFAULT -1 NOT NULL,
    "p$$parent" character varying(256),
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    valid_from timestamp with time zone,
    valid_to timestamp with time zone,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL,
    discount numeric,
    discount_is_percentage boolean,
    member_role_ integer DEFAULT -1 NOT NULL
);


--
-- Name: oocke1_accountassignment_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_accountassignment_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL,
    member_role smallint
);


--
-- Name: oocke1_activity; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_activity (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    activity_number character varying(256),
    activity_state smallint,
    activity_type character varying(256),
    actual_end timestamp with time zone,
    actual_start timestamp with time zone,
    assigned_to character varying(256),
    category_ integer DEFAULT -1 NOT NULL,
    contract_ integer DEFAULT -1 NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    detailed_description text,
    disabled boolean,
    disabled_reason character varying(256),
    due_by timestamp with time zone,
    external_link_ integer DEFAULT -1 NOT NULL,
    last_transition character varying(256),
    misc1 character varying(256),
    misc2 character varying(256),
    misc3 character varying(256),
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    original_scheduled_end timestamp with time zone,
    owner_ integer DEFAULT -1 NOT NULL,
    percent_complete smallint,
    priority smallint,
    process_state character varying(256),
    rep_acct character varying(256),
    rep_contact character varying(256),
    scheduled_end timestamp with time zone,
    scheduled_start timestamp with time zone,
    "p$$parent" character varying(256),
    total_votes integer,
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL,
    case_origin smallint,
    case_type smallint,
    customer_satisfaction smallint,
    reproducibility smallint,
    severity smallint,
    document character varying(256),
    sender_mailing character varying(256),
    "template" character varying(256),
    sender character varying(256),
    competitor_ integer DEFAULT -1 NOT NULL,
    is_all_day_event boolean,
    "location" character varying(256),
    delivery_receipt_requested boolean,
    message_body text,
    message_subject character varying(256),
    read_receipt_requested boolean,
    send_date timestamp with time zone,
    sender_email character varying(256),
    reference character varying(256),
    sender_mms character varying(256),
    sender_sms character varying(256),
    ical text
);


--
-- Name: oocke1_activity_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_activity_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    contract character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL,
    competitor character varying(256)
);


--
-- Name: oocke1_activitycreator; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_activitycreator (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    activity_group_ integer DEFAULT -1 NOT NULL,
    activity_type character varying(256),
    base_date timestamp with time zone,
    category_ integer DEFAULT -1 NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    disabled boolean,
    disabled_reason character varying(256),
    due_by timestamp with time zone,
    external_link_ integer DEFAULT -1 NOT NULL,
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    priority smallint,
    resrc_ integer DEFAULT -1 NOT NULL,
    scheduled_end timestamp with time zone,
    scheduled_start timestamp with time zone,
    "p$$parent" character varying(256),
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_activitycreator_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_activitycreator_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    activity_group character varying(256),
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    resrc character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_activityeffortesti; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_activityeffortesti (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    "p$$parent" character varying(256),
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    estimate_effort_hours integer,
    estimate_effort_minutes integer,
    estimate_max_deviation integer,
    is_main boolean,
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    selector_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_activityeffortesti_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_activityeffortesti_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    created_by character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    selector character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_activityfollowup; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_activityfollowup (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    activity character varying(256),
    assigned_to character varying(256),
    category_ integer DEFAULT -1 NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    disabled boolean,
    disabled_reason character varying(256),
    external_link_ integer DEFAULT -1 NOT NULL,
    modified_by_ integer DEFAULT -1 NOT NULL,
    owner_ integer DEFAULT -1 NOT NULL,
    "p$$parent" character varying(256),
    text text,
    title character varying(256),
    transition character varying(256),
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_activityfollowup_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_activityfollowup_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_activitygroup; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_activitygroup (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    category_ integer DEFAULT -1 NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    disabled boolean,
    disabled_reason character varying(256),
    estimate_effort_hours integer,
    estimate_effort_minutes integer,
    estimate_max_deviation integer,
    external_link_ integer DEFAULT -1 NOT NULL,
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    "p$$parent" character varying(256),
    sum_estimate_effort_hours integer,
    sum_estimate_effort_minutes integer,
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    welcome text,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL,
    scheduled_date timestamp with time zone,
    default_creator character varying(256)
);


--
-- Name: oocke1_activitygroup_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_activitygroup_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_activitygroupass; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_activitygroupass (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    "p$$parent" character varying(256),
    activity_group character varying(256),
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    modified_by_ integer DEFAULT -1 NOT NULL,
    owner_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_activitygroupass_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_activitygroupass_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    created_by character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_activitylink; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_activitylink (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    "p$$parent" character varying(256),
    activity_link_type smallint,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    link_to character varying(256),
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_activitylink_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_activitylink_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    created_by character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_activityparty; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_activityparty (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    modified_by_ integer DEFAULT -1 NOT NULL,
    owner_ integer DEFAULT -1 NOT NULL,
    party_type smallint,
    "p$$parent" character varying(256),
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL,
    party character varying(256)
);


--
-- Name: oocke1_activityparty_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_activityparty_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    created_by character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_activityprocaction; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_activityprocaction (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    category_ integer DEFAULT -1 NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    disabled boolean,
    disabled_reason character varying(256),
    external_link_ integer DEFAULT -1 NOT NULL,
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    "p$$parent" character varying(256),
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL,
    activity_link_type smallint,
    transition character varying(256),
    transition_text character varying(256),
    transition_title character varying(256),
    reset_to_null boolean,
    wf_process character varying(256),
    activity_creator character varying(256),
    activity_description character varying(256),
    activity_name character varying(256),
    contact_feature_name character varying(256),
    resource_order smallint,
    resource_role smallint
);


--
-- Name: oocke1_activityprocaction_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_activityprocaction_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_activityprocess; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_activityprocess (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    category_ integer DEFAULT -1 NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    disabled boolean,
    disabled_reason character varying(256),
    external_link_ integer DEFAULT -1 NOT NULL,
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    "p$$parent" character varying(256),
    start_state character varying(256),
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_activityprocess_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_activityprocess_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_activityprocstate; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_activityprocstate (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    category_ integer DEFAULT -1 NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    disabled boolean,
    disabled_reason character varying(256),
    external_link_ integer DEFAULT -1 NOT NULL,
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    "p$$parent" character varying(256),
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_activityprocstate_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_activityprocstate_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_activityproctrans; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_activityproctrans (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    category_ integer DEFAULT -1 NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    disabled boolean,
    disabled_reason character varying(256),
    err_state character varying(256),
    external_link_ integer DEFAULT -1 NOT NULL,
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    new_activity_state smallint,
    new_percent_complete smallint,
    next_state character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    prev_state character varying(256),
    "p$$parent" character varying(256),
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_activityproctrans_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_activityproctrans_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_activitytype; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_activitytype (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    activity_class smallint,
    activity_class_name character varying(256),
    category_ integer DEFAULT -1 NOT NULL,
    controlled_by character varying(256),
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    disabled boolean,
    disabled_reason character varying(256),
    external_link_ integer DEFAULT -1 NOT NULL,
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    "p$$parent" character varying(256),
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    work_bt character varying(256),
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_activitytype_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_activitytype_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_additionalextlink; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_additionalextlink (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    "p$$parent" character varying(256),
    external_link character varying(256),
    modified_by_ integer DEFAULT -1 NOT NULL,
    owner_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_additionalextlink_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_additionalextlink_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    created_by character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_address; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_address (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    "p$$parent" character varying(256),
    building character varying(256),
    category_ integer DEFAULT -1 NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    disabled boolean,
    disabled_reason character varying(256),
    external_link_ integer DEFAULT -1 NOT NULL,
    is_main boolean,
    modified_by_ integer DEFAULT -1 NOT NULL,
    owner_ integer DEFAULT -1 NOT NULL,
    objusage_ integer DEFAULT -1 NOT NULL,
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL,
    web_url character varying(256),
    automatic_parsing boolean,
    phone_city_area character varying(256),
    phone_country_prefix smallint,
    phone_extension character varying(256),
    phone_local_number character varying(256),
    phone_number_full character varying(256),
    address character varying(256),
    email_address character varying(256),
    email_format smallint,
    email_type smallint,
    picture character varying(256),
    postal_address_line_0 character varying(256),
    postal_address_line_1 character varying(256),
    postal_address_line_2 character varying(256),
    postal_address_line_3 character varying(256),
    postal_address_line_4 character varying(256),
    postal_city character varying(256),
    postal_code character varying(256),
    postal_country smallint,
    postal_county character varying(256),
    postal_freight_terms smallint,
    postal_latitude numeric,
    postal_longitude numeric,
    postal_state character varying(256),
    postal_street_number character varying(256),
    postal_street_0 character varying(256),
    postal_street_1 character varying(256),
    postal_street_2 character varying(256),
    postal_street_3 character varying(256),
    postal_street_4 character varying(256),
    postal_utc_offset smallint,
    room_number character varying(256),
    valid_to timestamp with time zone,
    valid_from timestamp with time zone
);


--
-- Name: oocke1_address_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_address_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    created_by character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    dtype character varying(256) NOT NULL,
    user_string4 character varying(256),
    user_number4 numeric,
    user_code4 smallint,
    user_date4 date,
    objusage smallint,
    category character varying(256),
    user_date_time4 timestamp with time zone,
    external_link character varying(256),
    user_boolean4 boolean
);


--
-- Name: oocke1_addressgroup; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_addressgroup (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    category_ integer DEFAULT -1 NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    disabled boolean,
    disabled_reason character varying(256),
    external_link_ integer DEFAULT -1 NOT NULL,
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    "p$$parent" character varying(256),
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_addressgroup_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_addressgroup_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_addressgroupmember; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_addressgroupmember (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    address character varying(256),
    "p$$parent" character varying(256),
    category_ integer DEFAULT -1 NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    disabled boolean,
    disabled_reason character varying(256),
    external_link_ integer DEFAULT -1 NOT NULL,
    modified_by_ integer DEFAULT -1 NOT NULL,
    owner_ integer DEFAULT -1 NOT NULL,
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_addressgroupmember_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_addressgroupmember_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_alert; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_alert (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    alert_state smallint,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description text,
    importance smallint,
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    reference character varying(256),
    "p$$parent" character varying(256),
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_alert_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_alert_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    created_by character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_auditentry; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_auditentry (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    auditee character varying(256),
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    modified_by_ integer DEFAULT -1 NOT NULL,
    owner_ integer DEFAULT -1 NOT NULL,
    "p$$parent" character varying(256),
    unit_of_work character varying(256),
    visited_by_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL,
    before_image text,
    modified_features character varying(300)
);


--
-- Name: oocke1_auditentry_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_auditentry_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    created_by character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    visited_by character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_booking; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_booking (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    booking_date timestamp with time zone,
    booking_status smallint,
    booking_type smallint,
    category_ integer DEFAULT -1 NOT NULL,
    cb character varying(256),
    config_type character varying(256),
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    disabled boolean,
    disabled_reason character varying(256),
    external_link_ integer DEFAULT -1 NOT NULL,
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    origin character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    "position" character varying(256),
    "p$$parent" character varying(256),
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    value_date timestamp with time zone,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL,
    quantity_credit numeric,
    quantity_debit numeric
);


--
-- Name: oocke1_booking_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_booking_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_bookingperiod; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_bookingperiod (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    category_ integer DEFAULT -1 NOT NULL,
    closing_booking_type_threshold smallint,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    "p$$parent" character varying(256),
    description character varying(256),
    disabled boolean,
    disabled_reason character varying(256),
    external_link_ integer DEFAULT -1 NOT NULL,
    is_closed boolean,
    is_final boolean,
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    period_ends_at_exclusive timestamp with time zone,
    period_starts_at timestamp with time zone,
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_bookingperiod_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_bookingperiod_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_bookingtext; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_bookingtext (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    category_ integer DEFAULT -1 NOT NULL,
    cb_name_infix1 character varying(256),
    cb_name_infix2 character varying(256),
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    credit_booking_name_infix character varying(256),
    credit_first boolean,
    debit_booking_name_infix character varying(256),
    "p$$parent" character varying(256),
    description character varying(256),
    disabled boolean,
    disabled_reason character varying(256),
    external_link_ integer DEFAULT -1 NOT NULL,
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_bookingtext_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_bookingtext_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_budget; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_budget (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    account character varying(256),
    actual_value numeric,
    budget_type smallint,
    category_ integer DEFAULT -1 NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    disabled boolean,
    disabled_reason character varying(256),
    ending_at timestamp with time zone,
    external_link_ integer DEFAULT -1 NOT NULL,
    modified_by_ integer DEFAULT -1 NOT NULL,
    owner_ integer DEFAULT -1 NOT NULL,
    "p$$parent" character varying(256),
    starting_from timestamp with time zone,
    target_value numeric,
    fulfil_org_unit character varying(256),
    underlying character varying(256),
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_budget_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_budget_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_budgetmilestone; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_budgetmilestone (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    actual_value numeric,
    "p$$parent" character varying(256),
    category_ integer DEFAULT -1 NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    disabled boolean,
    disabled_reason character varying(256),
    external_link_ integer DEFAULT -1 NOT NULL,
    modified_by_ integer DEFAULT -1 NOT NULL,
    owner_ integer DEFAULT -1 NOT NULL,
    target_value numeric,
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_budgetmilestone_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_budgetmilestone_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_buildingunit; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_buildingunit (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    building_cmplx character varying(256),
    category_ integer DEFAULT -1 NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    depot character varying(256),
    description character varying(256),
    disabled boolean,
    disabled_reason character varying(256),
    external_link_ integer DEFAULT -1 NOT NULL,
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    picture character varying(256),
    "p$$parent" character varying(256),
    size_in_cubic_meter numeric,
    size_in_square_meter numeric,
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL,
    parent character varying(256),
    building_unit_type smallint
);


--
-- Name: oocke1_buildingunit_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_buildingunit_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_calculationrule; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_calculationrule (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    category_ integer DEFAULT -1 NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    disabled boolean,
    disabled_reason character varying(256),
    external_link_ integer DEFAULT -1 NOT NULL,
    get_contract_amounts_script text,
    get_position_amounts_script text,
    is_default boolean,
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    "p$$parent" character varying(256),
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_calculationrule_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_calculationrule_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_calendar; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_calendar (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    base_calendar character varying(256),
    category_ integer DEFAULT -1 NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    disabled boolean,
    disabled_reason character varying(256),
    external_link_ integer DEFAULT -1 NOT NULL,
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    "p$$parent" character varying(256),
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_calendar_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_calendar_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_calendarday; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_calendarday (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    "p$$parent" character varying(256),
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    date_of_day date,
    is_working_day boolean,
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    work_duration_hours smallint,
    work_duration_minutes smallint,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL,
    day_of_week smallint
);


--
-- Name: oocke1_calendarday_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_calendarday_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    created_by character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_chart; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_chart (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    chart bytea,
    chart_mime_type character varying(256),
    chart_name character varying(256),
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    modified_by_ integer DEFAULT -1 NOT NULL,
    owner_ integer DEFAULT -1 NOT NULL,
    "p$$parent" character varying(256),
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_chart_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_chart_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    created_by character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_codevaluecontainer; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_codevaluecontainer (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    modified_by_ integer DEFAULT -1 NOT NULL,
    name_ integer DEFAULT -1 NOT NULL,
    owner_ integer DEFAULT -1 NOT NULL,
    "p$$parent" character varying(256),
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_codevaluecontainer_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_codevaluecontainer_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    created_by character varying(256),
    modified_by character varying(256),
    name character varying(256),
    "owner" character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_codevalueentry; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_codevalueentry (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    entry_value character varying(256),
    modified_by_ integer DEFAULT -1 NOT NULL,
    owner_ integer DEFAULT -1 NOT NULL,
    valid_from timestamp with time zone,
    valid_to timestamp with time zone,
    "p$$parent" character varying(256),
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL,
    back_color character varying(256),
    color character varying(256),
    font_weight character varying(256),
    icon_key character varying(256),
    long_text_ integer DEFAULT -1 NOT NULL,
    short_text_ integer DEFAULT -1 NOT NULL
);


--
-- Name: oocke1_codevalueentry_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_codevalueentry_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    created_by character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    dtype character varying(256) NOT NULL,
    long_text character varying(256),
    short_text character varying(256)
);


--
-- Name: oocke1_competitor; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_competitor (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    account_ integer DEFAULT -1 NOT NULL,
    category_ integer DEFAULT -1 NOT NULL,
    competitor_state smallint,
    contact_ integer DEFAULT -1 NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    disabled boolean,
    disabled_reason character varying(256),
    external_link_ integer DEFAULT -1 NOT NULL,
    key_product character varying(256),
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    opportunities character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    "p$$parent" character varying(256),
    strengths character varying(256),
    threats character varying(256),
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    weaknesses character varying(256),
    win_percentage numeric,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_competitor_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_competitor_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    account character varying(256),
    category character varying(256),
    contact character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_componentconfig; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_componentconfig (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    "p$$parent" character varying(256),
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_componentconfig_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_componentconfig_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    created_by character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_compoundbooking; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_compoundbooking (
    object_id character varying(256) NOT NULL,
    accepted_by_ integer DEFAULT -1 NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    booking_date timestamp with time zone,
    booking_status smallint,
    booking_type smallint,
    category_ integer DEFAULT -1 NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    disabled boolean,
    disabled_reason character varying(256),
    external_link_ integer DEFAULT -1 NOT NULL,
    is_locked boolean,
    lock_modified_at timestamp with time zone,
    locking_reason smallint,
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    reversal_of character varying(256),
    "p$$parent" character varying(256),
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    wf_process character varying(256),
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_compoundbooking_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_compoundbooking_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    accepted_by character varying(256),
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_contactmembership; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_contactmembership (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    category_ integer DEFAULT -1 NOT NULL,
    contact character varying(256),
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    disabled boolean,
    disabled_reason character varying(256),
    effective_on timestamp with time zone,
    external_link_ integer DEFAULT -1 NOT NULL,
    modified_by_ integer DEFAULT -1 NOT NULL,
    "p$$parent" character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL,
    employment_contract_state smallint,
    employment_position smallint
);


--
-- Name: oocke1_contactmembership_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_contactmembership_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_contactrel; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_contactrel (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    category_ integer DEFAULT -1 NOT NULL,
    complimentary_close character varying(256),
    "p$$parent" character varying(256),
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    disabled boolean,
    disabled_reason character varying(256),
    external_link_ integer DEFAULT -1 NOT NULL,
    modified_by_ integer DEFAULT -1 NOT NULL,
    owner_ integer DEFAULT -1 NOT NULL,
    salutation character varying(256),
    to_contact character varying(256),
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_contactrel_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_contactrel_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_contactrole; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_contactrole (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    account character varying(256),
    category_ integer DEFAULT -1 NOT NULL,
    contact_role smallint,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    disabled boolean,
    disabled_reason character varying(256),
    "p$$parent" character varying(256),
    external_link_ integer DEFAULT -1 NOT NULL,
    modified_by_ integer DEFAULT -1 NOT NULL,
    owner_ integer DEFAULT -1 NOT NULL,
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_contactrole_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_contactrole_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_contract; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_contract (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    active_on timestamp with time zone,
    activity_ integer DEFAULT -1 NOT NULL,
    broker character varying(256),
    calc_rule character varying(256),
    cancel_on timestamp with time zone,
    carrier character varying(256),
    category_ integer DEFAULT -1 NOT NULL,
    close_probability smallint,
    closed_on timestamp with time zone,
    competitor_ integer DEFAULT -1 NOT NULL,
    contact_ integer DEFAULT -1 NOT NULL,
    contract_currency smallint,
    contract_language smallint,
    contract_number character varying(256),
    contract_state smallint,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    customer character varying(256),
    description character varying(256),
    disabled boolean,
    disabled_reason character varying(256),
    estimated_close_date timestamp with time zone,
    estimated_value numeric,
    expires_on timestamp with time zone,
    external_link_ integer DEFAULT -1 NOT NULL,
    gift_message character varying(256),
    inventory_cb_ integer DEFAULT -1 NOT NULL,
    is_gift boolean,
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    next_step character varying(256),
    opportunity_rating smallint,
    opportunity_source smallint,
    origin character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    payment_terms smallint,
    pricing_date timestamp with time zone,
    pricing_rule character varying(256),
    pricing_state smallint,
    priority smallint,
    quote_ integer DEFAULT -1 NOT NULL,
    sales_rep character varying(256),
    "p$$parent" character varying(256),
    shipping_instructions character varying(256),
    shipping_method smallint,
    shipping_tracking_number character varying(256),
    supplier character varying(256),
    total_amount numeric,
    total_amount_including_tax numeric,
    total_base_amount numeric,
    total_discount_amount numeric,
    total_sales_commission numeric,
    total_tax_amount numeric,
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL,
    estimated_sales_commission numeric,
    lead_source smallint,
    opportunity_ integer DEFAULT -1 NOT NULL,
    lead_rating smallint,
    freight_terms smallint,
    sales_order_ integer DEFAULT -1 NOT NULL,
    submit_date timestamp with time zone,
    submit_status smallint,
    invoice_ integer DEFAULT -1 NOT NULL,
    submit_status_description character varying(256)
);


--
-- Name: oocke1_contract_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_contract_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    activity character varying(256),
    category character varying(256),
    competitor character varying(256),
    contact character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    inventory_cb character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    sales_order character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL,
    "quote" character varying(256),
    opportunity character varying(256),
    invoice character varying(256)
);


--
-- Name: oocke1_contractposition; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_contractposition (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    calc_rule character varying(256),
    carrier character varying(256),
    category_ integer DEFAULT -1 NOT NULL,
    contact_ integer DEFAULT -1 NOT NULL,
    contract_position_state smallint,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    disabled boolean,
    disabled_reason character varying(256),
    discount numeric,
    discount_description character varying(256),
    discount_is_percentage boolean,
    external_link_ integer DEFAULT -1 NOT NULL,
    gift_message character varying(256),
    "p$$parent" character varying(256),
    is_gift boolean,
    line_item_number bigint,
    list_price character varying(256),
    max_quantity numeric,
    min_max_quantity_handling smallint,
    min_quantity numeric,
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    offset_quantity numeric,
    owner_ integer DEFAULT -1 NOT NULL,
    position_number character varying(256),
    price_level character varying(256),
    price_per_unit numeric,
    price_uom character varying(256),
    pricing_date timestamp with time zone,
    pricing_rule character varying(256),
    pricing_state smallint,
    quantity numeric,
    sales_commission numeric,
    sales_commission_is_percentage boolean,
    sales_tax_type character varying(256),
    shipping_instructions character varying(256),
    shipping_method smallint,
    shipping_tracking_number character varying(256),
    uom character varying(256),
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL,
    based_on character varying(256),
    config_type character varying(256),
    current_config character varying(256),
    parent_position character varying(256),
    product character varying(256),
    product_serial_number_ integer DEFAULT -1 NOT NULL,
    estimated_close_date timestamp with time zone,
    close_probability smallint
);


--
-- Name: oocke1_contractposition_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_contractposition_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    contact character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL,
    product_serial_number character varying(256)
);


--
-- Name: oocke1_contractposmod; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_contractposmod (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    "p$$parent" character varying(256),
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    involved character varying(256),
    modified_by_ integer DEFAULT -1 NOT NULL,
    owner_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL,
    involved_pty character varying(256),
    property_value character varying(256),
    quantity numeric
);


--
-- Name: oocke1_contractposmod_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_contractposmod_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    created_by character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_creditlimit; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_creditlimit (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    category_ integer DEFAULT -1 NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    credit_currency smallint,
    credit_limit_amount numeric,
    current_credit_usage_amount numeric,
    disabled boolean,
    disabled_reason character varying(256),
    expected_credit_usage_amount1 numeric,
    expected_credit_usage_amount2 numeric,
    expected_credit_usage_amount3 numeric,
    external_link_ integer DEFAULT -1 NOT NULL,
    is_credit_on_hold boolean,
    "p$$parent" character varying(256),
    modified_by_ integer DEFAULT -1 NOT NULL,
    owner_ integer DEFAULT -1 NOT NULL,
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_creditlimit_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_creditlimit_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_deliveryinfo; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_deliveryinfo (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    actual_delivery_on timestamp with time zone,
    category_ integer DEFAULT -1 NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    "p$$parent" character varying(256),
    disabled boolean,
    disabled_reason character varying(256),
    external_link_ integer DEFAULT -1 NOT NULL,
    modified_by_ integer DEFAULT -1 NOT NULL,
    owner_ integer DEFAULT -1 NOT NULL,
    quantity_shipped numeric,
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_deliveryinfo_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_deliveryinfo_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_deliveryrequest; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_deliveryrequest (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    category_ integer DEFAULT -1 NOT NULL,
    "comment" character varying(256),
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    "p$$parent" character varying(256),
    disabled boolean,
    disabled_reason character varying(256),
    earliest_delivery_at timestamp with time zone,
    external_link_ integer DEFAULT -1 NOT NULL,
    latest_delivery_at timestamp with time zone,
    modified_by_ integer DEFAULT -1 NOT NULL,
    owner_ integer DEFAULT -1 NOT NULL,
    state smallint,
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_deliveryrequest_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_deliveryrequest_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_depot; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_depot (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    allow_position_auto_create boolean,
    category_ integer DEFAULT -1 NOT NULL,
    closing_date timestamp with time zone,
    contract_ integer DEFAULT -1 NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    depot_group character varying(256),
    "p$$parent" character varying(256),
    depot_number character varying(256),
    depot_type_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    disabled boolean,
    disabled_reason character varying(256),
    external_link_ integer DEFAULT -1 NOT NULL,
    is_default boolean,
    is_locked boolean,
    latest_report character varying(256),
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    opening_date timestamp with time zone,
    owner_ integer DEFAULT -1 NOT NULL,
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_depot_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_depot_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    contract character varying(256),
    created_by character varying(256),
    depot_type character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_depotentity; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_depotentity (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    category_ integer DEFAULT -1 NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    depot_entity_number character varying(256),
    description character varying(256),
    disabled boolean,
    disabled_reason character varying(256),
    external_link_ integer DEFAULT -1 NOT NULL,
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    "p$$parent" character varying(256),
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_depotentity_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_depotentity_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_depotentityrel; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_depotentityrel (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    category_ integer DEFAULT -1 NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    disabled boolean,
    disabled_reason character varying(256),
    entity1 character varying(256),
    entity2 character varying(256),
    external_link_ integer DEFAULT -1 NOT NULL,
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    relationship_type smallint,
    "p$$parent" character varying(256),
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_depotentityrel_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_depotentityrel_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_depotgroup; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_depotgroup (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    category_ integer DEFAULT -1 NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    disabled boolean,
    disabled_reason character varying(256),
    "p$$parent" character varying(256),
    external_link_ integer DEFAULT -1 NOT NULL,
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    parent character varying(256),
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL,
    contract_ integer DEFAULT -1 NOT NULL
);


--
-- Name: oocke1_depotgroup_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_depotgroup_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL,
    contract character varying(256)
);


--
-- Name: oocke1_depotholder; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_depotholder (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    category_ integer DEFAULT -1 NOT NULL,
    contract_ integer DEFAULT -1 NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    depot_holder_number character varying(256),
    description character varying(256),
    disabled boolean,
    disabled_reason character varying(256),
    "p$$parent" character varying(256),
    external_link_ integer DEFAULT -1 NOT NULL,
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_depotholder_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_depotholder_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    contract character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_depotposition; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_depotposition (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    category_ integer DEFAULT -1 NOT NULL,
    closing_date timestamp with time zone,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    "p$$parent" character varying(256),
    description character varying(256),
    disabled boolean,
    disabled_reason character varying(256),
    external_link_ integer DEFAULT -1 NOT NULL,
    is_locked boolean,
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    opening_date timestamp with time zone,
    owner_ integer DEFAULT -1 NOT NULL,
    qualifier character varying(256),
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL,
    based_on character varying(256),
    parent_position character varying(256),
    product character varying(256)
);


--
-- Name: oocke1_depotposition_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_depotposition_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_depotreference; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_depotreference (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    depot character varying(256),
    "p$$parent" character varying(256),
    depot_usage smallint,
    description character varying(256),
    holder_qualifies_position boolean,
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_depotreference_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_depotreference_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    created_by character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_depotreport; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_depotreport (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    booking_period character varying(256),
    category_ integer DEFAULT -1 NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    "p$$parent" character varying(256),
    description character varying(256),
    disabled boolean,
    disabled_reason character varying(256),
    external_link_ integer DEFAULT -1 NOT NULL,
    is_draft boolean,
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL,
    booking_status_threshold smallint
);


--
-- Name: oocke1_depotreport_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_depotreport_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_depotreportitem; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_depotreportitem (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    balance numeric,
    balance_bop numeric,
    balance_credit numeric,
    balance_credit_bop numeric,
    balance_debit numeric,
    balance_debit_bop numeric,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    modified_by_ integer DEFAULT -1 NOT NULL,
    owner_ integer DEFAULT -1 NOT NULL,
    "position" character varying(256),
    position_name character varying(256),
    "p$$parent" character varying(256),
    value_date timestamp with time zone,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL,
    booking character varying(256)
);


--
-- Name: oocke1_depotreportitem_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_depotreportitem_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    created_by character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_depottype; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_depottype (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    allow_credit_bookings boolean,
    allow_debit_bookings boolean,
    category_ integer DEFAULT -1 NOT NULL,
    compatible_to_ integer DEFAULT -1 NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    "p$$parent" character varying(256),
    description character varying(256),
    disabled boolean,
    disabled_reason character varying(256),
    external_link_ integer DEFAULT -1 NOT NULL,
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_depottype_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_depottype_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    compatible_to character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_description; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_description (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    "p$$parent" character varying(256),
    detailed_description text,
    "language" smallint,
    modified_by_ integer DEFAULT -1 NOT NULL,
    owner_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_description_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_description_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    created_by character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_document; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_document (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    active_on timestamp with time zone,
    active_until timestamp with time zone,
    author character varying(256),
    content_language smallint,
    content_length integer,
    content_type character varying(256),
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    document_abstract character varying(256),
    document_number character varying(256),
    document_state smallint,
    document_type smallint,
    folder_ integer DEFAULT -1 NOT NULL,
    head_revision character varying(256),
    keywords character varying(256),
    literature_type smallint,
    "location" character varying(256),
    modified_by_ integer DEFAULT -1 NOT NULL,
    owner_ integer DEFAULT -1 NOT NULL,
    search_text character varying(256),
    "p$$parent" character varying(256),
    title character varying(256),
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL,
    cms_translation integer,
    qualified_name character varying(256),
    description character varying(256),
    name character varying(256),
    parent character varying(256),
    cms_template character varying(256),
    cms_class character varying(256),
    cms_meta character varying(256),
    cms_default_language character varying(256),
    cms_language character varying(256)
);


--
-- Name: oocke1_document_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_document_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    created_by character varying(256),
    folder character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_documentattachment; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_documentattachment (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    "p$$parent" character varying(256),
    description character varying(256),
    document character varying(256),
    modified_by_ integer DEFAULT -1 NOT NULL,
    owner_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_documentattachment_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_documentattachment_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    created_by character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_documentfolder; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_documentfolder (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    parent character varying(256),
    "p$$parent" character varying(256),
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_documentfolder_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_documentfolder_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    created_by character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_documentlink; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_documentlink (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    "p$$parent" character varying(256),
    link_uri character varying(256),
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_documentlink_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_documentlink_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    created_by character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_documentlock; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_documentlock (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    "p$$parent" character varying(256),
    locked_at timestamp with time zone,
    locked_by_ integer DEFAULT -1 NOT NULL,
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_documentlock_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_documentlock_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    created_by character varying(256),
    locked_by character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_emailaccount; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_emailaccount (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    e_mail_address character varying(256),
    incoming_mail_service_name character varying(256),
    is_default boolean,
    modified_by_ integer DEFAULT -1 NOT NULL,
    outgoing_mail_service_name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    reply_e_mail_address character varying(256),
    "p$$parent" character varying(256),
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_emailaccount_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_emailaccount_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    created_by character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_event; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_event (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    category_ integer DEFAULT -1 NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    disabled boolean,
    disabled_reason character varying(256),
    external_link_ integer DEFAULT -1 NOT NULL,
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    "p$$parent" character varying(256),
    slice_uom character varying(256),
    slot_uom character varying(256),
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_event_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_event_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_eventpart; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_eventpart (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    category_ integer DEFAULT -1 NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    disabled boolean,
    disabled_reason character varying(256),
    "p$$parent" character varying(256),
    external_link_ integer DEFAULT -1 NOT NULL,
    filling character varying(256),
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_eventpart_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_eventpart_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_eventslot; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_eventslot (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    booked_by character varying(256),
    category_ integer DEFAULT -1 NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    disabled boolean,
    disabled_reason character varying(256),
    external_link_ integer DEFAULT -1 NOT NULL,
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    number character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    "p$$parent" character varying(256),
    sold character varying(256),
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_eventslot_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_eventslot_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_facility; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_facility (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    "p$$parent" character varying(256),
    category_ integer DEFAULT -1 NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    disabled boolean,
    disabled_reason character varying(256),
    external_link_ integer DEFAULT -1 NOT NULL,
    facility_type smallint,
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    picture character varying(256),
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_facility_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_facility_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_filter; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_filter (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    "p$$parent" character varying(256),
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL,
    user_string0 character varying(256),
    user_string2 character varying(256),
    user_string1 character varying(256),
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_string3 character varying(256),
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date4_ integer DEFAULT -1 NOT NULL,
    external_link_ integer DEFAULT -1 NOT NULL,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_string4_ integer DEFAULT -1 NOT NULL,
    category_ integer DEFAULT -1 NOT NULL,
    user_number3 numeric,
    user_code3 smallint,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_code1 smallint,
    user_code2 smallint,
    user_date3 date,
    user_date2 date,
    user_date0 date,
    user_date1 date,
    user_code0 smallint,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    disabled_reason character varying(256),
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean0 boolean,
    user_date_time0 timestamp with time zone,
    user_boolean1 boolean,
    user_date_time1 timestamp with time zone,
    disabled boolean
);


--
-- Name: oocke1_filter_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_filter_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    created_by character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    dtype character varying(256) NOT NULL,
    user_string4 character varying(256),
    user_number4 numeric,
    user_code4 smallint,
    user_date4 date,
    category character varying(256),
    user_date_time4 timestamp with time zone,
    external_link character varying(256),
    user_boolean4 boolean
);


--
-- Name: oocke1_filterproperty; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_filterproperty (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    "p$$parent" character varying(256),
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    modified_by_ integer DEFAULT -1 NOT NULL,
    owner_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL,
    activity_type_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    filter_operator smallint,
    filter_quantor smallint,
    is_active boolean,
    name character varying(256),
    process_state_ integer DEFAULT -1 NOT NULL,
    activity_state_ integer DEFAULT -1 NOT NULL,
    disabled boolean,
    scheduled_end_ integer DEFAULT -1 NOT NULL,
    boolean_param_ integer DEFAULT -1 NOT NULL,
    clause character varying(4000),
    date_param_ integer DEFAULT -1 NOT NULL,
    date_time_param_ integer DEFAULT -1 NOT NULL,
    decimal_param_ integer DEFAULT -1 NOT NULL,
    integer_param_ integer DEFAULT -1 NOT NULL,
    string_param_ integer DEFAULT -1 NOT NULL,
    activity_number_ integer DEFAULT -1 NOT NULL,
    scheduled_start_ integer DEFAULT -1 NOT NULL,
    sales_tax_type_ integer DEFAULT -1 NOT NULL,
    price_uom_ integer DEFAULT -1 NOT NULL,
    category_ integer DEFAULT -1 NOT NULL,
    classification_ integer DEFAULT -1 NOT NULL,
    is_main boolean,
    address_type_ integer DEFAULT -1 NOT NULL,
    objusage_ integer DEFAULT -1 NOT NULL,
    account_type_ integer DEFAULT -1 NOT NULL,
    account_category_ integer DEFAULT -1 NOT NULL,
    offset_in_hours integer,
    contact_ integer DEFAULT -1 NOT NULL
);


--
-- Name: oocke1_filterproperty_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_filterproperty_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    created_by character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    dtype character varying(256) NOT NULL,
    scheduled_start timestamp with time zone,
    scheduled_end timestamp with time zone,
    process_state character varying(256),
    activity_state smallint,
    activity_type character varying(256),
    activity_number character varying(256),
    boolean_param boolean,
    date_param date,
    date_time_param timestamp with time zone,
    decimal_param numeric,
    integer_param integer,
    string_param character varying(256),
    objusage smallint,
    category character varying(256),
    address_type smallint,
    account_category smallint,
    account_type smallint,
    price_uom character varying(256),
    sales_tax_type character varying(256),
    classification character varying(256),
    contact character varying(256)
);


--
-- Name: oocke1_forecast; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_forecast (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    category_ integer DEFAULT -1 NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    disabled boolean,
    disabled_reason character varying(256),
    external_link_ integer DEFAULT -1 NOT NULL,
    modified_by_ integer DEFAULT -1 NOT NULL,
    organizational_unit character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    "p$$parent" character varying(256),
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_forecast_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_forecast_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_forecastperiod; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_forecastperiod (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    category_ integer DEFAULT -1 NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    disabled boolean,
    disabled_reason character varying(256),
    external_link_ integer DEFAULT -1 NOT NULL,
    "p$$parent" character varying(256),
    modified_by_ integer DEFAULT -1 NOT NULL,
    owner_ integer DEFAULT -1 NOT NULL,
    person character varying(256),
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_forecastperiod_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_forecastperiod_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_indexentry; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_indexentry (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    "p$$parent" character varying(256),
    indexed_object character varying(256),
    keywords text,
    modified_by_ integer DEFAULT -1 NOT NULL,
    owner_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_indexentry_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_indexentry_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    created_by character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_inventoryitem; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_inventoryitem (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    category_ integer DEFAULT -1 NOT NULL,
    config_type character varying(256),
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    current_config character varying(256),
    description character varying(256),
    disabled boolean,
    disabled_reason character varying(256),
    external_link_ integer DEFAULT -1 NOT NULL,
    inventory_item_type smallint,
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    product character varying(256),
    product_serial_number_ integer DEFAULT -1 NOT NULL,
    "p$$parent" character varying(256),
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_inventoryitem_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_inventoryitem_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    product_serial_number character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_involvedobject; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_involvedobject (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    "p$$parent" character varying(256),
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    involved character varying(256),
    involved_role character varying(256),
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_involvedobject_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_involvedobject_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    created_by character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_join_accthasassact; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW oocke1_join_accthasassact AS
    (((SELECT act.object_id AS assigned_activity, acc.object_id AS account FROM (oocke1_activity act JOIN oocke1_account acc ON (((act.assigned_to)::text = (acc.object_id)::text))) UNION SELECT act.object_id AS assigned_activity, adr."p$$parent" AS account FROM (oocke1_activity act JOIN oocke1_address adr ON (((adr.object_id)::text = (act.sender)::text)))) UNION SELECT p0."p$$parent" AS assigned_activity, acc.object_id AS account FROM (oocke1_activityparty p0 JOIN oocke1_account acc ON (((acc.object_id)::text = (p0.party)::text)))) UNION SELECT p0."p$$parent" AS assigned_activity, adr."p$$parent" AS account FROM (oocke1_activityparty p0 JOIN oocke1_address adr ON (((adr.object_id)::text = (p0.party)::text)))) UNION SELECT act.object_id AS assigned_activity, c0.customer AS account FROM (oocke1_activity_ act JOIN oocke1_contract c0 ON (((act.contract)::text = (c0.object_id)::text)));


--
-- Name: oocke1_join_accthasasscontr; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW oocke1_join_accthasasscontr AS
    SELECT c.object_id AS assigned_contract, a.object_id AS account FROM (oocke1_contract c JOIN oocke1_account a ON (((c.customer)::text = (a.object_id)::text))) UNION ALL SELECT c.object_id AS assigned_contract, a.object_id AS account FROM (oocke1_contract c JOIN oocke1_account a ON (((c.sales_rep)::text = (a.object_id)::text)));


--
-- Name: oocke1_product; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_product (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    allow_modification boolean,
    allow_removal boolean,
    alternate_product_number_ integer DEFAULT -1 NOT NULL,
    category_ integer DEFAULT -1 NOT NULL,
    classification_ integer DEFAULT -1 NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    default_positions integer,
    def_price_level character varying(256),
    default_quantity numeric,
    default_uom character varying(256),
    description character varying(256),
    detailed_description character varying(256),
    disabled boolean,
    disabled_reason character varying(256),
    discount numeric,
    discount_is_percentage boolean,
    external_link_ integer DEFAULT -1 NOT NULL,
    item_number bigint,
    max_positions integer,
    max_quantity numeric,
    min_max_quantity_handling smallint,
    min_positions integer,
    min_quantity numeric,
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    offset_quantity numeric,
    override_price boolean,
    owner_ integer DEFAULT -1 NOT NULL,
    price_uom_ integer DEFAULT -1 NOT NULL,
    pricing_rule character varying(256),
    product_number character varying(256),
    product_state smallint,
    product_usage character varying(256),
    sales_tax_type character varying(256),
    "p$$parent" character varying(256),
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL,
    config_type character varying(256),
    picture character varying(256),
    gross_weight_kilogram numeric,
    net_weight_kilogram numeric,
    product_dimension character varying(256),
    version character varying(256),
    profile character varying(256),
    is_stock_item boolean,
    product_serial_number_ integer DEFAULT -1 NOT NULL,
    current_config character varying(256),
    product character varying(256),
    parent character varying(256),
    active_on timestamp with time zone,
    expires_on timestamp with time zone
);


--
-- Name: oocke1_join_accthasprod; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW oocke1_join_accthasprod AS
    SELECT DISTINCT p.object_id AS product, a.object_id AS account FROM oocke1_product p, oocke1_account a, oocke1_contract c, oocke1_contractposition cp WHERE ((((cp.product)::text = (p.object_id)::text) AND ((cp."p$$parent")::text = (c.object_id)::text)) AND ((c.customer)::text = (a.object_id)::text)) ORDER BY p.object_id, a.object_id;


--
-- Name: oocke1_join_actcontainswre; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_join_actcontainswre (
    activity character varying(256) NOT NULL,
    work_report_entry character varying(256) NOT NULL
);


--
-- Name: oocke1_join_actgcontainsact; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW oocke1_join_actgcontainsact AS
    SELECT ga."p$$parent" AS filtered_activity, ga.activity_group FROM oocke1_activitygroupass ga;


--
-- Name: oocke1_join_actgcontainsflup; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW oocke1_join_actgcontainsflup AS
    SELECT f.object_id AS follow_up, g.object_id AS activity_group FROM ((oocke1_activityfollowup f JOIN oocke1_activitygroupass ga ON (((f."p$$parent")::text = (ga."p$$parent")::text))) JOIN oocke1_activitygroup g ON (((ga.activity_group)::text = (g.object_id)::text)));


--
-- Name: oocke1_note; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_note (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    "p$$parent" character varying(256),
    modified_by_ integer DEFAULT -1 NOT NULL,
    owner_ integer DEFAULT -1 NOT NULL,
    text text,
    title character varying(256),
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_join_actgcontainsnote; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW oocke1_join_actgcontainsnote AS
    SELECT n.object_id AS activity_note, g.object_id AS activity_group FROM ((oocke1_note n JOIN oocke1_activitygroupass ga ON (((n."p$$parent")::text = (ga."p$$parent")::text))) JOIN oocke1_activitygroup g ON (((ga.activity_group)::text = (g.object_id)::text)));


--
-- Name: oocke1_join_actgcontainswre; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_join_actgcontainswre (
    activity_group character varying(256) NOT NULL,
    work_report_entry character varying(256) NOT NULL
);


--
-- Name: oocke1_join_actgiscreatedby; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW oocke1_join_actgiscreatedby AS
    SELECT ac.activity_group, ac.object_id AS activity_creator FROM oocke1_activitycreator_ ac;


--
-- Name: oocke1_join_buhasadr; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW oocke1_join_buhasadr AS
    SELECT adr.object_id AS assigned_address, bu.object_id AS building_unit FROM (oocke1_address adr JOIN oocke1_buildingunit bu ON (((adr.building)::text = (bu.object_id)::text)));


--
-- Name: oocke1_join_cbhasbk; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW oocke1_join_cbhasbk AS
    SELECT b.object_id AS booking, cb.object_id AS cb FROM (oocke1_booking b JOIN oocke1_compoundbooking cb ON (((b.cb)::text = (cb.object_id)::text)));


--
-- Name: oocke1_modelelement; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_modelelement (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    annotation character varying(3000),
    category_ integer DEFAULT -1 NOT NULL,
    container character varying(256),
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    disabled boolean,
    disabled_reason character varying(256),
    element_order integer,
    external_link_ integer DEFAULT -1 NOT NULL,
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    qualified_name character varying(256),
    "p$$parent" character varying(256),
    stereotype_ integer DEFAULT -1 NOT NULL,
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL,
    exposed_end character varying(256),
    is_changeable boolean,
    multiplicity smallint,
    referenced_end character varying(256),
    scope smallint,
    "type" character varying(256),
    upper_bound integer,
    visibility smallint,
    exception_ integer DEFAULT -1 NOT NULL,
    is_query boolean,
    semantics character varying(256),
    is_derived boolean,
    max_length integer,
    evaluation_policy smallint,
    expression character varying(256),
    "language" character varying(256),
    tag_value character varying(256),
    is_abstract boolean,
    supertype_ integer DEFAULT -1 NOT NULL,
    const_value character varying(256),
    is_singleton boolean,
    is_clustered boolean,
    namespace character varying(256),
    label_ integer DEFAULT -1 NOT NULL,
    direction smallint,
    aggregation smallint,
    is_navigable boolean,
    qualifier_name_ integer DEFAULT -1 NOT NULL,
    qualifier_type_ integer DEFAULT -1 NOT NULL
);


--
-- Name: oocke1_join_clfclassifiestelt; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW oocke1_join_clfclassifiestelt AS
    SELECT c.object_id AS classifier, e.object_id AS typed_element FROM (oocke1_modelelement c JOIN oocke1_modelelement e ON (((c.object_id)::text = (e."type")::text)));


--
-- Name: oocke1_join_cposhasposmod; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW oocke1_join_cposhasposmod AS
    SELECT p.object_id AS "position", pm.object_id AS position_modification FROM (oocke1_contractposition p JOIN oocke1_contractposmod pm ON (((p.object_id)::text = (pm.involved)::text)));


--
-- Name: oocke1_join_depgcontainsdep; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW oocke1_join_depgcontainsdep AS
    SELECT d.object_id AS depot, dg.object_id AS depot_group FROM (oocke1_depot d JOIN oocke1_depotgroup dg ON (((d.depot_group)::text = (dg.object_id)::text)));


--
-- Name: oocke1_join_depgcontainsdepg; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW oocke1_join_depgcontainsdepg AS
    SELECT dg.object_id AS depot_group, dgp.object_id AS parent FROM (oocke1_depotgroup dg JOIN oocke1_depotgroup dgp ON (((dg."p$$parent")::text = (dgp.object_id)::text)));


--
-- Name: oocke1_join_depposhasbk; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW oocke1_join_depposhasbk AS
    SELECT b.object_id AS booking, b."position" AS depot_position FROM oocke1_booking b;


--
-- Name: oocke1_simplebooking; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_simplebooking (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    booking_date timestamp with time zone,
    booking_status smallint,
    booking_type smallint,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    origin_context_params_ integer DEFAULT -1 NOT NULL,
    origin_context_ integer DEFAULT -1 NOT NULL,
    origin_id character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    "position" character varying(256),
    quantity numeric,
    quantity_uom character varying(256),
    "p$$parent" character varying(256),
    value_date timestamp with time zone,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_join_depposhassbk; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW oocke1_join_depposhassbk AS
    SELECT b.object_id AS simple_booking, b."position" AS depot_position FROM oocke1_simplebooking b;


--
-- Name: oocke1_join_deprepitmhasbk; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW oocke1_join_deprepitmhasbk AS
    SELECT ip.object_id AS item_position, b.object_id AS single_booking FROM (((oocke1_depotreportitem ip JOIN oocke1_depotreport r ON (((ip."p$$parent")::text = (r.object_id)::text))) JOIN oocke1_bookingperiod bp ON (((r.booking_period)::text = (bp.object_id)::text))) JOIN oocke1_booking b ON ((((((b."position")::text = (ip."position")::text) AND (b.value_date >= bp.period_starts_at)) AND ((b.value_date < bp.period_ends_at_exclusive) OR (bp.period_ends_at_exclusive IS NULL))) AND (((b.booking_status >= r.booking_status_threshold) OR (r.booking_status_threshold = 0)) OR (r.booking_status_threshold IS NULL)))));


--
-- Name: oocke1_join_deprepitmhassbk; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW oocke1_join_deprepitmhassbk AS
    SELECT ip.object_id AS item_position, b.object_id AS simple_booking FROM (((oocke1_depotreportitem ip JOIN oocke1_depotreport r ON (((ip."p$$parent")::text = (r.object_id)::text))) JOIN oocke1_bookingperiod bp ON (((r.booking_period)::text = (bp.object_id)::text))) JOIN oocke1_simplebooking b ON ((((((b."position")::text = (ip."position")::text) AND (b.value_date >= bp.period_starts_at)) AND ((b.value_date < bp.period_ends_at_exclusive) OR (bp.period_ends_at_exclusive IS NULL))) AND (((b.booking_status >= r.booking_status_threshold) OR (r.booking_status_threshold = 0)) OR (r.booking_status_threshold IS NULL)))));


--
-- Name: oocke1_join_entitycontainsdep; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW oocke1_join_entitycontainsdep AS
    SELECT dh."p$$parent" AS entity, d.object_id AS depot FROM (oocke1_depot d JOIN oocke1_depotholder dh ON (((d."p$$parent")::text = (dh.object_id)::text)));


--
-- Name: oocke1_join_fcperiodhaslead; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_join_fcperiodhaslead (
    forecast_period character varying(256) NOT NULL,
    lead character varying(256) NOT NULL
);


--
-- Name: oocke1_join_fcperiodhasopty; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_join_fcperiodhasopty (
    forecast_period character varying(256) NOT NULL,
    opportunity character varying(256) NOT NULL
);


--
-- Name: oocke1_join_fcperiodhasquote; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_join_fcperiodhasquote (
    forecast_period character varying(256) NOT NULL,
    "quote" character varying(256) NOT NULL
);


--
-- Name: oocke1_join_filterincludesacct; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_join_filterincludesacct (
    account_filter character varying(256) NOT NULL,
    filtered_account character varying(256) NOT NULL
);


--
-- Name: oocke1_join_filterincludesact; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_join_filterincludesact (
    activity_filter character varying(256) NOT NULL,
    filtered_activity character varying(256) NOT NULL
);


--
-- Name: oocke1_join_filterincludesaddr; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_join_filterincludesaddr (
    address_filter character varying(256) NOT NULL,
    filtered_address character varying(256) NOT NULL
);


--
-- Name: oocke1_join_filterincludesprod; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_join_filterincludesprod (
    product_filter character varying(256) NOT NULL,
    filtered_product character varying(256) NOT NULL
);


--
-- Name: oocke1_join_finderhasidxacct; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_join_finderhasidxacct (
    object_finder character varying(256) NOT NULL,
    index_entry_account character varying(256) NOT NULL
);


--
-- Name: oocke1_join_finderhasidxact; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_join_finderhasidxact (
    object_finder character varying(256) NOT NULL,
    index_entry_activity character varying(256) NOT NULL
);


--
-- Name: oocke1_join_finderhasidxbldg; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_join_finderhasidxbldg (
    object_finder character varying(256) NOT NULL,
    index_entry_building character varying(256) NOT NULL
);


--
-- Name: oocke1_join_finderhasidxcontr; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_join_finderhasidxcontr (
    object_finder character varying(256) NOT NULL,
    index_entry_contract character varying(256) NOT NULL
);


--
-- Name: oocke1_join_finderhasidxdep; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_join_finderhasidxdep (
    object_finder character varying(256) NOT NULL,
    index_entry_depot character varying(256) NOT NULL
);


--
-- Name: oocke1_join_finderhasidxdoc; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_join_finderhasidxdoc (
    object_finder character varying(256) NOT NULL,
    index_entry_document character varying(256) NOT NULL
);


--
-- Name: oocke1_join_finderhasidxprod; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_join_finderhasidxprod (
    object_finder character varying(256) NOT NULL,
    index_entry_product character varying(256) NOT NULL
);


--
-- Name: oocke1_join_fldcontainsdoc; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW oocke1_join_fldcontainsdoc AS
    SELECT d_.object_id AS document, f.object_id AS folder FROM (oocke1_document_ d_ JOIN oocke1_documentfolder f ON (((d_.folder)::text = (f.object_id)::text)));


--
-- Name: oocke1_join_fldcontainsfld; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW oocke1_join_fldcontainsfld AS
    SELECT f.object_id AS folder, fp.object_id AS parent FROM (oocke1_documentfolder f JOIN oocke1_documentfolder fp ON (((f.parent)::text = (fp.object_id)::text)));


--
-- Name: oocke1_userhome; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_userhome (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    chart0 character varying(256),
    chart1 character varying(256),
    chart2 character varying(256),
    chart3 character varying(256),
    contact character varying(256),
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    modified_by_ integer DEFAULT -1 NOT NULL,
    owner_ integer DEFAULT -1 NOT NULL,
    primary_group character varying(256),
    "p$$parent" character varying(256),
    send_mail_subject_prefix character varying(256),
    settings text,
    store_settings_on_logoff boolean,
    web_access_url character varying(256),
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_join_homehasassact; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW oocke1_join_homehasassact AS
    ((SELECT a.object_id AS assigned_activity, h0.object_id AS user_home FROM (oocke1_activity a JOIN oocke1_userhome h0 ON (((a.assigned_to)::text = (h0.contact)::text))) UNION SELECT a.object_id AS assigned_activity, h0.object_id AS user_home FROM ((oocke1_activity a JOIN oocke1_address adr ON (((adr.object_id)::text = (a.sender)::text))) JOIN oocke1_userhome h0 ON (((adr."p$$parent")::text = (h0.contact)::text)))) UNION SELECT a.object_id AS assigned_activity, h0.object_id AS user_home FROM ((oocke1_activityparty p0 JOIN oocke1_userhome h0 ON (((p0.party)::text = (h0.contact)::text))) JOIN oocke1_activity a ON (((p0."p$$parent")::text = (a.object_id)::text)))) UNION SELECT a.object_id AS assigned_activity, h0.object_id AS user_home FROM (((oocke1_activityparty p0 JOIN oocke1_activity a ON (((p0."p$$parent")::text = (a.object_id)::text))) JOIN oocke1_address adr ON (((adr.object_id)::text = (p0.party)::text))) JOIN oocke1_userhome h0 ON (((adr."p$$parent")::text = (h0.contact)::text)));


--
-- Name: oocke1_join_homehasasscontr; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW oocke1_join_homehasasscontr AS
    SELECT c.object_id AS assigned_contract, h.object_id AS user_home FROM (oocke1_contract c JOIN oocke1_userhome h ON (((c.sales_rep)::text = (h.contact)::text))) UNION ALL SELECT c.object_id AS assigned_contract, h.object_id AS user_home FROM (oocke1_contract c JOIN oocke1_userhome h ON (((c.customer)::text = (h.contact)::text)));


--
-- Name: oocke1_join_iitemhasbooking; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW oocke1_join_iitemhasbooking AS
    SELECT b.object_id AS booking, i.object_id AS inventory_item FROM (oocke1_inventoryitem i JOIN oocke1_booking b ON (((b.origin)::text = (i.object_id)::text)));


--
-- Name: oocke1_join_nscontainselt; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW oocke1_join_nscontainselt AS
    SELECT n.object_id AS namespace, e.object_id AS content FROM (oocke1_modelelement n JOIN oocke1_modelelement e ON (((e.container)::text = (n.object_id)::text)));


--
-- Name: oocke1_join_plhasassple; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_join_plhasassple (
    price_level character varying(256) NOT NULL,
    price_list_entry character varying(256) NOT NULL
);


--
-- Name: oocke1_join_rescontainswre; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_join_rescontainswre (
    resource character varying(256) NOT NULL,
    work_report_entry character varying(256) NOT NULL
);


--
-- Name: oocke1_resource; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_resource (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    calendar character varying(256),
    category_ integer DEFAULT -1 NOT NULL,
    contact character varying(256),
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    disabled boolean,
    disabled_reason character varying(256),
    external_link_ integer DEFAULT -1 NOT NULL,
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    overtime_rate numeric,
    owner_ integer DEFAULT -1 NOT NULL,
    rate_currency smallint,
    "p$$parent" character varying(256),
    standard_rate numeric,
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_resourceassignment; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_resourceassignment (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    "p$$parent" character varying(256),
    calendar character varying(256),
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    resrc character varying(256),
    resource_order smallint,
    resource_role smallint,
    work_duration_percentage smallint,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_join_reshasassignedact; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW oocke1_join_reshasassignedact AS
    SELECT a.object_id AS assigned_activity, r.object_id AS resource FROM ((oocke1_activity a JOIN oocke1_resourceassignment ra ON (((ra."p$$parent")::text = (a.object_id)::text))) JOIN oocke1_resource r ON (((ra.resrc)::text = (r.object_id)::text)));


--
-- Name: oocke1_join_segcontainsadr; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW oocke1_join_segcontainsadr AS
    SELECT adr.object_id AS address, act."p$$parent" AS segment FROM (oocke1_address adr JOIN oocke1_account act ON (((adr."p$$parent")::text = (act.object_id)::text)));


--
-- Name: oocke1_segment; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_segment (
    object_id character varying(256) NOT NULL,
    description character varying(256),
    "p$$parent" character varying(256),
    dtype character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    owner_ integer DEFAULT -1 NOT NULL
);


--
-- Name: oocke1_join_segcontainsbu; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW oocke1_join_segcontainsbu AS
    SELECT b.object_id AS building_unit, s.object_id AS segment FROM (oocke1_buildingunit b JOIN oocke1_segment s ON (((b."p$$parent")::text = (s.object_id)::text))) UNION ALL SELECT b.object_id AS building_unit, bp."p$$parent" AS segment FROM (oocke1_buildingunit b JOIN oocke1_buildingunit bp ON (((b."p$$parent")::text = (bp.object_id)::text)));


--
-- Name: oocke1_join_segcontainsfac; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW oocke1_join_segcontainsfac AS
    SELECT f.object_id AS facility, b."p$$parent" AS segment FROM (oocke1_facility f JOIN oocke1_buildingunit b ON (((f."p$$parent")::text = (b.object_id)::text))) UNION ALL SELECT f.object_id AS facility, bu1."p$$parent" AS segment FROM ((oocke1_facility f JOIN oocke1_buildingunit bu2 ON (((f."p$$parent")::text = (bu2.object_id)::text))) JOIN oocke1_buildingunit bu1 ON (((bu2."p$$parent")::text = (bu1.object_id)::text)));


--
-- Name: oocke1_linkableitemlink; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_linkableitemlink (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    category_ integer DEFAULT -1 NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    disabled boolean,
    disabled_reason character varying(256),
    external_link_ integer DEFAULT -1 NOT NULL,
    link_to character varying(256),
    link_type smallint,
    "p$$parent" character varying(256),
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    valid_from timestamp with time zone,
    valid_to timestamp with time zone,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_linkableitemlink_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_linkableitemlink_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_media; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_media (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    "p$$parent" character varying(256),
    modified_by_ integer DEFAULT -1 NOT NULL,
    owner_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL,
    media character varying(256),
    uri character varying(256),
    content bytea,
    content_mime_type character varying(256),
    content_name character varying(256),
    description character varying(256),
    version character varying(256),
    author character varying(256),
    name character varying(256)
);


--
-- Name: oocke1_media_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_media_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    created_by character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_mmsslide; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_mmsslide (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    category_ integer DEFAULT -1 NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    disabled boolean,
    disabled_reason character varying(256),
    external_link_ integer DEFAULT -1 NOT NULL,
    media_object character varying(256),
    "p$$parent" character varying(256),
    modified_by_ integer DEFAULT -1 NOT NULL,
    owner_ integer DEFAULT -1 NOT NULL,
    slide_order integer,
    text character varying(256),
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_mmsslide_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_mmsslide_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_modelelement_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_modelelement_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    stereotype character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL,
    exception character varying(256),
    supertype character varying(256),
    qualifier_name character varying(256),
    qualifier_type character varying(256),
    label character varying(256)
);


--
-- Name: oocke1_note_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_note_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    created_by character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_objectfinder; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_objectfinder (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    all_words character varying(256),
    at_least_one_of_the_words character varying(256),
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    search_domain smallint,
    search_expression character varying(256),
    "p$$parent" character varying(256),
    without_words character varying(256),
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_objectfinder_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_objectfinder_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    created_by character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_organization; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_organization (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    category_ integer DEFAULT -1 NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    disabled boolean,
    disabled_reason character varying(256),
    external_link_ integer DEFAULT -1 NOT NULL,
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    organization_state smallint,
    owner_ integer DEFAULT -1 NOT NULL,
    "p$$parent" character varying(256),
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_organization_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_organization_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_organizationalunit; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_organizationalunit (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    category_ integer DEFAULT -1 NOT NULL,
    cost_center character varying(256),
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    disabled boolean,
    disabled_reason character varying(256),
    external_link_ integer DEFAULT -1 NOT NULL,
    "p$$parent" character varying(256),
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    organizational_unit_state smallint,
    owner_ integer DEFAULT -1 NOT NULL,
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_organizationalunit_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_organizationalunit_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_orgunitrelship; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_orgunitrelship (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    category_ integer DEFAULT -1 NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    disabled boolean,
    disabled_reason character varying(256),
    external_link_ integer DEFAULT -1 NOT NULL,
    from_unit character varying(256),
    "p$$parent" character varying(256),
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    org_unit_relationship_state smallint,
    owner_ integer DEFAULT -1 NOT NULL,
    relationship_type smallint,
    to_unit character varying(256),
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    weight numeric,
    weight_is_percentage boolean,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_orgunitrelship_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_orgunitrelship_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_pricelevel; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_pricelevel (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    based_on character varying(256),
    category_ integer DEFAULT -1 NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    disabled boolean,
    disabled_reason character varying(256),
    external_link_ integer DEFAULT -1 NOT NULL,
    is_final boolean,
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    payment_method_ integer DEFAULT -1 NOT NULL,
    price_currency smallint,
    price_usage_ integer DEFAULT -1 NOT NULL,
    "p$$parent" character varying(256),
    shipping_method_ integer DEFAULT -1 NOT NULL,
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    valid_from timestamp with time zone,
    valid_to timestamp with time zone,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL,
    product_phase_key character varying(256),
    default_offset_valid_to_hh integer,
    default_offset_valid_from_hh integer
);


--
-- Name: oocke1_pricelevel_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_pricelevel_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    payment_method smallint,
    price_usage smallint,
    shipping_method smallint,
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_pricemodifier; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_pricemodifier (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    modified_by_ integer DEFAULT -1 NOT NULL,
    owner_ integer DEFAULT -1 NOT NULL,
    "p$$parent" character varying(256),
    quantity_from numeric,
    quantity_to numeric,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL,
    discount numeric,
    discount_is_percentage boolean,
    price_multiplier numeric,
    price_offset numeric,
    rounding_factor numeric
);


--
-- Name: oocke1_pricemodifier_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_pricemodifier_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    created_by character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_pricingrule; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_pricingrule (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    category_ integer DEFAULT -1 NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    disabled boolean,
    disabled_reason character varying(256),
    external_link_ integer DEFAULT -1 NOT NULL,
    get_price_level_script text,
    is_default boolean,
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    "p$$parent" character varying(256),
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_pricingrule_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_pricingrule_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_product_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_product_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    alternate_product_number character varying(256),
    category character varying(256),
    classification character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    price_uom character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL,
    product_serial_number character varying(256)
);


--
-- Name: oocke1_productapplication; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_productapplication (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    category_ integer DEFAULT -1 NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    disabled boolean,
    disabled_reason character varying(256),
    external_link_ integer DEFAULT -1 NOT NULL,
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    "p$$parent" character varying(256),
    underlying character varying(256),
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_productapplication_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_productapplication_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_productbaseprice; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_productbaseprice (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    category_ integer DEFAULT -1 NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    disabled boolean,
    disabled_reason character varying(256),
    discount numeric,
    discount_is_percentage boolean,
    external_link_ integer DEFAULT -1 NOT NULL,
    modified_by_ integer DEFAULT -1 NOT NULL,
    owner_ integer DEFAULT -1 NOT NULL,
    price numeric,
    price_currency smallint,
    price_level_ integer DEFAULT -1 NOT NULL,
    "p$$parent" character varying(256),
    quantity_from numeric,
    quantity_to numeric,
    uom character varying(256),
    objusage_ integer DEFAULT -1 NOT NULL,
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_productbaseprice_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_productbaseprice_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    price_level character varying(256),
    objusage smallint,
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_productclass; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_productclass (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    category_ integer DEFAULT -1 NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    disabled boolean,
    disabled_reason character varying(256),
    external_link_ integer DEFAULT -1 NOT NULL,
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    "p$$parent" character varying(256),
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_productclass_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_productclass_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_productclassrel; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_productclassrel (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    category_ integer DEFAULT -1 NOT NULL,
    "p$$parent" character varying(256),
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    disabled boolean,
    disabled_reason character varying(256),
    external_link_ integer DEFAULT -1 NOT NULL,
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    relationship_to character varying(256),
    relationship_type smallint,
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_productclassrel_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_productclassrel_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_productconfig; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_productconfig (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    "p$$parent" character varying(256),
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    is_default boolean,
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    valid_from timestamp with time zone,
    valid_to timestamp with time zone,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL,
    config_type character varying(256)
);


--
-- Name: oocke1_productconfig_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_productconfig_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    created_by character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_productconftypeset; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_productconftypeset (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    "p$$parent" character varying(256),
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_productconftypeset_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_productconftypeset_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    created_by character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_productphase; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_productphase (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    category_ integer DEFAULT -1 NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    disabled boolean,
    disabled_reason character varying(256),
    external_link_ integer DEFAULT -1 NOT NULL,
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    "p$$parent" character varying(256),
    product_phase_key character varying(256),
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL,
    valid_from timestamp with time zone,
    valid_to timestamp with time zone
);


--
-- Name: oocke1_productphase_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_productphase_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_productreference; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_productreference (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    "p$$parent" character varying(256),
    config_type character varying(256),
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    current_config character varying(256),
    modified_by_ integer DEFAULT -1 NOT NULL,
    owner_ integer DEFAULT -1 NOT NULL,
    product character varying(256),
    product_serial_number_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_productreference_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_productreference_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    created_by character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    product_serial_number character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_property; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_property (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    "domain" character varying(256),
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    "p$$parent" character varying(256),
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL,
    string_value character varying(256),
    decimal_value numeric,
    date_value date,
    boolean_value boolean,
    date_time_value timestamp with time zone,
    uri_value character varying(256),
    reference_value character varying(256),
    integer_value integer
);


--
-- Name: oocke1_property_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_property_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    created_by character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_propertyset; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_propertyset (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    "p$$parent" character varying(256),
    description character varying(256),
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_propertyset_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_propertyset_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    created_by character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_quickaccess; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_quickaccess (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    action_name character varying(400),
    action_param_ integer DEFAULT -1 NOT NULL,
    action_type smallint,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    icon_key character varying(256),
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    reference character varying(256),
    "p$$parent" character varying(256),
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_quickaccess_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_quickaccess_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    action_param character varying(256),
    created_by character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_rasartifactcontext; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_rasartifactcontext (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    "p$$parent" character varying(256),
    asset_context character varying(256),
    category_ integer DEFAULT -1 NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    disabled boolean,
    disabled_reason character varying(256),
    external_link_ integer DEFAULT -1 NOT NULL,
    modified_by_ integer DEFAULT -1 NOT NULL,
    owner_ integer DEFAULT -1 NOT NULL,
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    vp character varying(256),
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_rasartifactcontext_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_rasartifactcontext_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_rasartifactdep; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_rasartifactdep (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    artifact character varying(256),
    category_ integer DEFAULT -1 NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    dependency_type smallint,
    "p$$parent" character varying(256),
    disabled boolean,
    disabled_reason character varying(256),
    external_link_ integer DEFAULT -1 NOT NULL,
    modified_by_ integer DEFAULT -1 NOT NULL,
    owner_ integer DEFAULT -1 NOT NULL,
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_rasartifactdep_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_rasartifactdep_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_rasclassificatielt; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_rasclassificatielt (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    "p$$parent" character varying(256),
    category_ integer DEFAULT -1 NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    disabled boolean,
    disabled_reason character varying(256),
    external_link_ integer DEFAULT -1 NOT NULL,
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL,
    desc_group_ integer DEFAULT -1 NOT NULL
);


--
-- Name: oocke1_rasclassificatielt_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_rasclassificatielt_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL,
    desc_group character varying(256)
);


--
-- Name: oocke1_rasdescriptor; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_rasdescriptor (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    asset_context character varying(256),
    category_ integer DEFAULT -1 NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    "p$$parent" character varying(256),
    disabled boolean,
    disabled_reason character varying(256),
    external_link_ integer DEFAULT -1 NOT NULL,
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_rasdescriptor_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_rasdescriptor_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_rasprofile; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_rasprofile (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    "p$$parent" character varying(256),
    category_ integer DEFAULT -1 NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    disabled boolean,
    disabled_reason character varying(256),
    external_link_ integer DEFAULT -1 NOT NULL,
    id_history character varying(256),
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    parent_profile character varying(256),
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    version_major character varying(256),
    version_minor character varying(256),
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_rasprofile_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_rasprofile_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_rassolutionpart; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_rassolutionpart (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    "p$$parent" character varying(256),
    category_ integer DEFAULT -1 NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    disabled boolean,
    disabled_reason character varying(256),
    external_link_ integer DEFAULT -1 NOT NULL,
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL,
    artifact_type character varying(256),
    digest_name character varying(256),
    digest_value character varying(256),
    version character varying(256),
    related_diagram character varying(256),
    related_model character varying(256),
    operation_def_ integer DEFAULT -1 NOT NULL
);


--
-- Name: oocke1_rassolutionpart_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_rassolutionpart_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL,
    operation_def character varying(256)
);


--
-- Name: oocke1_rasvarpoint; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_rasvarpoint (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    "p$$parent" character varying(256),
    asset_context character varying(256),
    category_ integer DEFAULT -1 NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    disabled boolean,
    disabled_reason character varying(256),
    external_link_ integer DEFAULT -1 NOT NULL,
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_rasvarpoint_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_rasvarpoint_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_rating; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_rating (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    "p$$parent" character varying(256),
    description character varying(256),
    modified_by_ integer DEFAULT -1 NOT NULL,
    owner_ integer DEFAULT -1 NOT NULL,
    rated_by character varying(256),
    rating_level smallint,
    rating_type smallint,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_rating_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_rating_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    created_by character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_relatedproduct; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_relatedproduct (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    category_ integer DEFAULT -1 NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    disabled boolean,
    disabled_reason character varying(256),
    external_link_ integer DEFAULT -1 NOT NULL,
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    "p$$parent" character varying(256),
    product character varying(256),
    relationship_type smallint,
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_relatedproduct_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_relatedproduct_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_resource_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_resource_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_resourceassignment_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_resourceassignment_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    created_by character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_revenuereport; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_revenuereport (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    category_ integer DEFAULT -1 NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    disabled boolean,
    disabled_reason character varying(256),
    external_link_ integer DEFAULT -1 NOT NULL,
    "p$$parent" character varying(256),
    modified_by_ integer DEFAULT -1 NOT NULL,
    owner_ integer DEFAULT -1 NOT NULL,
    report_number smallint,
    reported_revenue numeric,
    reporting_currency smallint,
    reporting_quarter smallint,
    reporting_year smallint,
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_revenuereport_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_revenuereport_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_salestaxtype; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_salestaxtype (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    category_ integer DEFAULT -1 NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    detailed_description character varying(256),
    disabled boolean,
    disabled_reason character varying(256),
    external_link_ integer DEFAULT -1 NOT NULL,
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    rate numeric,
    "p$$parent" character varying(256),
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_salestaxtype_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_salestaxtype_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_segment_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_segment_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    dtype character varying(256) NOT NULL,
    "owner" character varying(256)
);


--
-- Name: oocke1_simplebooking_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_simplebooking_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    created_by character varying(256),
    modified_by character varying(256),
    origin_context character varying(256),
    origin_context_params character varying(256),
    "owner" character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_subscription; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_subscription (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    event_type_ integer DEFAULT -1 NOT NULL,
    filter_name0 character varying(256),
    filter_name1 character varying(256),
    filter_name2 character varying(256),
    filter_name3 character varying(256),
    filter_name4 character varying(256),
    filter_value0_ integer DEFAULT -1 NOT NULL,
    filter_value1_ integer DEFAULT -1 NOT NULL,
    filter_value2_ integer DEFAULT -1 NOT NULL,
    filter_value3_ integer DEFAULT -1 NOT NULL,
    filter_value4_ integer DEFAULT -1 NOT NULL,
    is_active boolean,
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    topic character varying(256),
    "p$$parent" character varying(256),
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_subscription_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_subscription_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    created_by character varying(256),
    event_type smallint,
    filter_value0 character varying(256),
    filter_value1 character varying(256),
    filter_value2 character varying(256),
    filter_value3 character varying(256),
    filter_value4 character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_tobj_acctmembership; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW oocke1_tobj_acctmembership AS
    SELECT (("replace"((a.object_id)::text, 'account/'::text, 'accountMembership/'::text) || '/'::text) || "replace"((ass.object_id)::text, '/'::text, ':'::text)) AS object_id, ass.account AS "p$$parent", ass.created_at, ass.created_by_, ass.modified_at, ass.modified_by_, 'org:opencrx:kernel:account1:AccountMembership' AS dtype, ass.access_level_browse, ass.access_level_update, ass.access_level_delete, ass.owner_, ass.name, ass.description, ass.valid_from, ass.valid_to, ass.object_id AS member, ass."p$$parent" AS member_of_account, ass.member_role_ FROM (oocke1_accountassignment ass JOIN oocke1_account a ON ((((ass.account)::text = (a.object_id)::text) AND ((ass.dtype)::text = 'org:opencrx:kernel:account1:Member'::text))));


--
-- Name: oocke1_tobj_acctmembership_; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW oocke1_tobj_acctmembership_ AS
    SELECT oocke1_accountassignment_.object_id, oocke1_accountassignment_.idx, oocke1_accountassignment_.created_by, oocke1_accountassignment_.member_role, oocke1_accountassignment_.modified_by, oocke1_accountassignment_."owner", oocke1_accountassignment_.dtype FROM oocke1_accountassignment_;


--
-- Name: oocke1_tobj_activitylinkfrom; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW oocke1_tobj_activitylinkfrom AS
    SELECT (("replace"((a.object_id)::text, 'activity/'::text, 'activityLinkFrom/'::text) || '/'::text) || "replace"((l.object_id)::text, '/'::text, ':'::text)) AS object_id, a.object_id AS "p$$parent", 'org:opencrx:kernel:activity1:ActivityLinkFrom' AS dtype, l.modified_at, l.created_at, l.created_by_, l.modified_by_, l.access_level_browse, l.access_level_update, l.access_level_delete, l.owner_, (100 - l.activity_link_type) AS activity_link_type, l.name, l.description, l."p$$parent" AS link_from, l.object_id AS link_to FROM oocke1_activity a, oocke1_activitylink l WHERE (((l.link_to)::text = (a.object_id)::text) AND ((l.object_id)::text = (l.object_id)::text));


--
-- Name: oocke1_tobj_activitylinkfrom_; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW oocke1_tobj_activitylinkfrom_ AS
    SELECT oocke1_activitylink_.object_id, oocke1_activitylink_.idx, oocke1_activitylink_.created_by, oocke1_activitylink_.modified_by, oocke1_activitylink_."owner", oocke1_activitylink_.dtype FROM oocke1_activitylink_;


--
-- Name: oocke1_tobj_contractrole; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW oocke1_tobj_contractrole AS
    (SELECT (((("replace"((c."p$$parent")::text, 'contracts/'::text, 'contractRole/'::text) || '/'::text) || "replace"((dhn.object_id)::text, '/'::text, ':'::text)) || ':'::text) || "replace"((c.object_id)::text, '/'::text, ':'::text)) AS object_id, c."p$$parent", 'org:opencrx:kernel:contract1:CustomerContractRole' AS dtype, c.modified_at, c.modified_by_, c.created_at, c.created_by_, c.access_level_browse, c.access_level_update, c.access_level_delete, c.owner_, c.customer AS account, dhn.object_id AS contract_reference_holder, dhn.contract FROM ((oocke1_depotholder_ dhn JOIN oocke1_contract c ON (((c.object_id)::text = (dhn.contract)::text))) JOIN oocke1_account a ON (((c.customer)::text = (a.object_id)::text))) UNION ALL SELECT (((("replace"((c."p$$parent")::text, 'contracts/'::text, 'contractRole/'::text) || '/'::text) || "replace"((dgn.object_id)::text, '/'::text, ':'::text)) || ':'::text) || "replace"((c.object_id)::text, '/'::text, ':'::text)) AS object_id, c."p$$parent", 'org:opencrx:kernel:contract1:CustomerContractRole' AS dtype, c.modified_at, c.modified_by_, c.created_at, c.created_by_, c.access_level_browse, c.access_level_update, c.access_level_delete, c.owner_, c.customer AS account, dgn.object_id AS contract_reference_holder, dgn.contract FROM ((oocke1_depotgroup_ dgn JOIN oocke1_contract c ON (((c.object_id)::text = (dgn.contract)::text))) JOIN oocke1_account a ON (((c.customer)::text = (a.object_id)::text)))) UNION ALL SELECT (((("replace"((c."p$$parent")::text, 'contracts/'::text, 'contractRole/'::text) || '/'::text) || "replace"((dn.object_id)::text, '/'::text, ':'::text)) || ':'::text) || "replace"((c.object_id)::text, '/'::text, ':'::text)) AS object_id, c."p$$parent", 'org:opencrx:kernel:contract1:CustomerContractRole' AS dtype, c.modified_at, c.modified_by_, c.created_at, c.created_by_, c.access_level_browse, c.access_level_update, c.access_level_delete, c.owner_, c.customer AS account, dn.object_id AS contract_reference_holder, dn.contract FROM ((oocke1_depot_ dn JOIN oocke1_contract c ON (((c.object_id)::text = (dn.contract)::text))) JOIN oocke1_account a ON (((c.customer)::text = (a.object_id)::text)));


--
-- Name: oocke1_tobj_contractrole_; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW oocke1_tobj_contractrole_ AS
    SELECT oocke1_contract_.object_id, oocke1_contract_.idx, oocke1_contract_.created_by, oocke1_contract_.modified_by, oocke1_contract_."owner", 'org:opencrx:kernel:contract1:CustomerContractRole' AS dtype FROM oocke1_contract_;


--
-- Name: oocke1_tobj_lnkitemlnkfrom; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW oocke1_tobj_lnkitemlnkfrom AS
    ((SELECT (("replace"((l.link_to)::text, 'facility/'::text, 'itemLinkFrom/'::text) || '/'::text) || "replace"((l.object_id)::text, '/'::text, ':'::text)) AS object_id, l.link_to AS "p$$parent", l.created_at, l.created_by_, l.modified_at, l.modified_by_, 'org:opencrx:kernel:building1:LinkableItemLinkFrom' AS dtype, l.access_level_browse, l.access_level_update, l.access_level_delete, l.owner_, l.disabled, l.disabled_reason, l.name, l.description, (100 - l.link_type) AS link_type, l.valid_from, l.valid_to, l.object_id AS link_to, l."p$$parent" AS link_from FROM oocke1_linkableitemlink l WHERE ((l.link_to)::text ~~ 'facility/%'::text) UNION ALL SELECT (("replace"((l.link_to)::text, 'facility1/'::text, 'itemLinkFrom1/'::text) || '/'::text) || "replace"((l.object_id)::text, '/'::text, ':'::text)) AS object_id, l.link_to AS "p$$parent", l.created_at, l.created_by_, l.modified_at, l.modified_by_, 'org:opencrx:kernel:building1:LinkableItemLinkFrom' AS dtype, l.access_level_browse, l.access_level_update, l.access_level_delete, l.owner_, l.disabled, l.disabled_reason, l.name, l.description, (100 - l.link_type) AS link_type, l.valid_from, l.valid_to, l.object_id AS link_to, l."p$$parent" AS link_from FROM oocke1_linkableitemlink l WHERE ((l.link_to)::text ~~ 'facility1/%'::text)) UNION ALL SELECT (("replace"((l.link_to)::text, 'facility2/'::text, 'itemLinkFrom2/'::text) || '/'::text) || "replace"((l.object_id)::text, '/'::text, ':'::text)) AS object_id, l.link_to AS "p$$parent", l.created_at, l.created_by_, l.modified_at, l.modified_by_, 'org:opencrx:kernel:building1:LinkableItemLinkFrom' AS dtype, l.access_level_browse, l.access_level_update, l.access_level_delete, l.owner_, l.disabled, l.disabled_reason, l.name, l.description, (100 - l.link_type) AS link_type, l.valid_from, l.valid_to, l.object_id AS link_to, l."p$$parent" AS link_from FROM oocke1_linkableitemlink l WHERE ((l.link_to)::text ~~ 'facility2/%'::text)) UNION ALL SELECT (("replace"((l.link_to)::text, 'inventoryItem/'::text, 'itemLinkFrom3/'::text) || '/'::text) || "replace"((l.object_id)::text, '/'::text, ':'::text)) AS object_id, l.link_to AS "p$$parent", l.created_at, l.created_by_, l.modified_at, l.modified_by_, 'org:opencrx:kernel:building1:LinkableItemLinkFrom' AS dtype, l.access_level_browse, l.access_level_update, l.access_level_delete, l.owner_, l.disabled, l.disabled_reason, l.name, l.description, (100 - l.link_type) AS link_type, l.valid_from, l.valid_to, l.object_id AS link_to, l."p$$parent" AS link_from FROM oocke1_linkableitemlink l WHERE ((l.link_to)::text ~~ 'inventoryItem/%'::text);


--
-- Name: oocke1_tobj_lnkitemlnkfrom_; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW oocke1_tobj_lnkitemlnkfrom_ AS
    SELECT oocke1_linkableitemlink_.object_id, oocke1_linkableitemlink_.idx, oocke1_linkableitemlink_.created_by, oocke1_linkableitemlink_.modified_by, oocke1_linkableitemlink_."owner", oocke1_linkableitemlink_.dtype FROM oocke1_linkableitemlink_;


--
-- Name: oocke1_tobj_pricelistentry; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW oocke1_tobj_pricelistentry AS
    SELECT (("replace"((p."p$$parent")::text, 'products/'::text, 'priceListEntry/'::text) || '/'::text) || "replace"((bp.object_id)::text, '/'::text, ':'::text)) AS object_id, p."p$$parent", 'org:opencrx:kernel:product1:PriceListEntry' AS dtype, bp.modified_at, bp.modified_by_, bp.created_at, bp.created_by_, bp.access_level_browse, bp.access_level_update, bp.access_level_delete, bp.owner_, bp.objusage_, bp.price, bp.price_currency, bp.price_level_, bp.description, bp.quantity_from, bp.quantity_to, bp.discount, bp.discount_is_percentage, bp.uom, p.name AS product_name, p.description AS product_description, p.object_id AS product, p.sales_tax_type, bp.object_id AS base_price FROM (oocke1_productbaseprice bp JOIN oocke1_product p ON (((bp."p$$parent")::text = (p.object_id)::text))) UNION ALL SELECT (("replace"((pp."p$$parent")::text, 'products/'::text, 'priceListEntry/'::text) || '/'::text) || "replace"((bp.object_id)::text, '/'::text, ':'::text)) AS object_id, pp."p$$parent", 'org:opencrx:kernel:product1:PriceListEntry' AS dtype, bp.modified_at, bp.modified_by_, bp.created_at, bp.created_by_, bp.access_level_browse, bp.access_level_update, bp.access_level_delete, bp.owner_, bp.objusage_, bp.price, bp.price_currency, bp.price_level_, bp.description, bp.quantity_from, bp.quantity_to, bp.discount, bp.discount_is_percentage, bp.uom, p.name AS product_name, p.description AS product_description, p.object_id AS product, p.sales_tax_type, bp.object_id AS base_price FROM ((oocke1_productbaseprice bp JOIN oocke1_product p ON (((bp."p$$parent")::text = (p.object_id)::text))) JOIN oocke1_product pp ON (((p."p$$parent")::text = (pp.object_id)::text)));


--
-- Name: oocke1_tobj_pricelistentry_; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW oocke1_tobj_pricelistentry_ AS
    SELECT oocke1_productbaseprice_.object_id, oocke1_productbaseprice_.idx, oocke1_productbaseprice_.created_by, oocke1_productbaseprice_.modified_by, oocke1_productbaseprice_."owner", oocke1_productbaseprice_.price_level, oocke1_productbaseprice_.objusage, oocke1_productbaseprice_.dtype FROM oocke1_productbaseprice_;


--
-- Name: oocke1_tobj_propertysetentry; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW oocke1_tobj_propertysetentry AS
    SELECT "replace"((p.object_id)::text, 'p2/'::text, 'propertySetEntry1/'::text) AS object_id, ps."p$$parent", p.created_at, p.created_by_, p.modified_at, p.modified_by_, "replace"("replace"((p.dtype)::text, 'org:opencrx:kernel:base:'::text, 'org:opencrx:kernel:generic:'::text), 'Property'::text, 'PropertySetEntry'::text) AS dtype, p.access_level_browse, p.access_level_update, p.access_level_delete, p.owner_, p.name AS property_name, p.description AS property_description, ps.name AS property_set_name, ps.description AS property_set_description, p.string_value, p.integer_value, p.boolean_value, p.uri_value, p.decimal_value, p.reference_value, p.date_value, p.date_time_value, p.object_id AS property FROM (oocke1_property p JOIN oocke1_propertyset ps ON (((p."p$$parent")::text = (ps.object_id)::text))) UNION ALL SELECT "replace"((p.object_id)::text, 'p3/'::text, 'propertySetEntry2/'::text) AS object_id, ps."p$$parent", p.created_at, p.created_by_, p.modified_at, p.modified_by_, "replace"("replace"((p.dtype)::text, 'org:opencrx:kernel:base:'::text, 'org:opencrx:kernel:generic:'::text), 'Property'::text, 'PropertySetEntry'::text) AS dtype, p.access_level_browse, p.access_level_update, p.access_level_delete, p.owner_, p.name AS property_name, p.description AS property_description, ps.name AS property_set_name, ps.description AS property_set_description, p.string_value, p.integer_value, p.boolean_value, p.uri_value, p.decimal_value, p.reference_value, p.date_value, p.date_time_value, p.object_id AS property FROM (oocke1_property p JOIN oocke1_propertyset ps ON (((p."p$$parent")::text = (ps.object_id)::text)));


--
-- Name: oocke1_tobj_propertysetentry_; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW oocke1_tobj_propertysetentry_ AS
    SELECT oocke1_property_.object_id, oocke1_property_.idx, oocke1_property_.created_by, oocke1_property_.modified_by, oocke1_property_."owner", oocke1_property_.dtype FROM oocke1_property_;


--
-- Name: oocke1_tobj_searchindexentry; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW oocke1_tobj_searchindexentry AS
    SELECT (("replace"((act."p$$parent")::text, 'accounts/'::text, 'searchIndexEntry/'::text) || '/'::text) || "replace"((act.object_id)::text, '/'::text, ':'::text)) AS object_id, act."p$$parent", 'org:opencrx:kernel:account1:SearchIndexEntry' AS dtype, act.modified_at, act.modified_by_, act.created_at, act.created_by_, act.access_level_browse, act.access_level_update, act.access_level_delete, act.owner_, (((CASE WHEN (act.last_name IS NULL) THEN '-'::character varying ELSE act.last_name END)::text || ', '::text) || (CASE WHEN (act.first_name IS NULL) THEN '-'::character varying ELSE act.first_name END)::text) AS account_address_index, act.object_id AS account FROM oocke1_account act UNION ALL SELECT (("replace"((act."p$$parent")::text, 'accounts/'::text, 'searchIndexEntry/'::text) || '/'::text) || "replace"((adr.object_id)::text, '/'::text, ':'::text)) AS object_id, act."p$$parent", 'org:opencrx:kernel:account1:SearchIndexEntry' AS dtype, act.modified_at, act.modified_by_, act.created_at, act.created_by_, act.access_level_browse, act.access_level_update, act.access_level_delete, act.owner_, (((((((((((((CASE WHEN (act.last_name IS NULL) THEN '-'::character varying ELSE act.last_name END)::text || ', '::text) || (CASE WHEN (act.first_name IS NULL) THEN '-'::character varying ELSE act.first_name END)::text) || ', '::text) || (CASE WHEN (adr.email_address IS NULL) THEN '-'::character varying ELSE adr.email_address END)::text) || ', '::text) || (CASE WHEN (adr.phone_number_full IS NULL) THEN '-'::character varying ELSE adr.phone_number_full END)::text) || ', '::text) || (CASE WHEN (adr.room_number IS NULL) THEN '-'::character varying ELSE adr.room_number END)::text) || ', '::text) || (CASE WHEN (adr.postal_street_0 IS NULL) THEN '-'::character varying ELSE adr.postal_street_0 END)::text) || ', '::text) || (CASE WHEN (adr.postal_city IS NULL) THEN '-'::character varying ELSE adr.postal_city END)::text) AS account_address_index, act.object_id AS account FROM (oocke1_account act JOIN oocke1_address adr ON (((adr."p$$parent")::text = (act.object_id)::text)));


--
-- Name: oocke1_tobj_searchindexentry_; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW oocke1_tobj_searchindexentry_ AS
    SELECT oocke1_account_.object_id, oocke1_account_.idx, oocke1_account_.created_by, oocke1_account_.modified_by, oocke1_account_."owner", oocke1_account_.dtype FROM oocke1_account_;


--
-- Name: oocke1_workrecord; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_workrecord (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    "p$$parent" character varying(256),
    billable_amount numeric,
    billing_currency smallint,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    depot_selector smallint,
    description character varying(256),
    duration_calculation_mode smallint,
    duration_hours integer,
    duration_minutes integer,
    ended_at timestamp with time zone,
    is_billable boolean,
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    pause_duration_hours integer,
    pause_duration_minutes integer,
    rate numeric,
    rate_type smallint,
    started_at timestamp with time zone,
    work_cb character varying(256),
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_tobj_workreportentry; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW oocke1_tobj_workreportentry AS
    ((SELECT (("replace"((a."p$$parent")::text, 'activities/'::text, 'workReportEntry1/'::text) || '/'::text) || "replace"((w.object_id)::text, '/'::text, ':'::text)) AS object_id, a."p$$parent", 'org:opencrx:kernel:activity1:WorkReportEntry' AS dtype, w.modified_at, w.modified_by_, w.created_at, w.created_by_, w.access_level_browse, w.access_level_update, w.access_level_delete, w.owner_, w.name, w.description, w.started_at, w.ended_at, w.duration_hours, w.duration_minutes, ((w.duration_hours)::numeric + ((w.duration_minutes)::numeric / 60.0)) AS duration_decimal, (((((w.duration_hours)::character(1))::text || ':'::text) || ((w.duration_minutes)::character(1))::text) || chr(39)) AS duration_hh_mm, w.pause_duration_hours, w.pause_duration_minutes, ((w.pause_duration_hours)::numeric + ((w.pause_duration_minutes)::numeric / 60.0)) AS pause_duration_decimal, (((((w.pause_duration_hours)::character(1))::text || ':'::text) || ((w.pause_duration_minutes)::character(1))::text) || chr(39)) AS pause_duration_hh_mm, w.billable_amount, w.billing_currency, a.activity_number, a.object_id AS activity, ra.object_id AS "assignment", w.object_id AS work_record, ra.resrc FROM ((oocke1_workrecord w JOIN oocke1_resourceassignment ra ON (((w."p$$parent")::text = (ra.object_id)::text))) JOIN oocke1_activity a ON (((a.object_id)::text = (ra."p$$parent")::text))) UNION ALL SELECT (("replace"("replace"("replace"((ga.activity_group)::text, 'activityTracker/'::text, 'workReportEntry2/'::text), 'activityCategory/'::text, 'workReportEntry2/'::text), 'activityMilestone/'::text, 'workReportEntry2/'::text) || '/'::text) || "replace"((w.object_id)::text, '/'::text, ':'::text)) AS object_id, ga.activity_group AS "p$$parent", 'org:opencrx:kernel:activity1:WorkReportEntry' AS dtype, w.modified_at, w.modified_by_, w.created_at, w.created_by_, w.access_level_browse, w.access_level_update, w.access_level_delete, w.owner_, w.name, w.description, w.started_at, w.ended_at, w.duration_hours, w.duration_minutes, ((w.duration_hours)::numeric + ((w.duration_minutes)::numeric / 60.0)) AS duration_decimal, (((((w.duration_hours)::character(1))::text || ':'::text) || ((w.duration_minutes)::character(1))::text) || chr(39)) AS duration_hh_mm, w.pause_duration_hours, w.pause_duration_minutes, ((w.pause_duration_hours)::numeric + ((w.pause_duration_minutes)::numeric / 60.0)) AS pause_duration_decimal, (((((w.pause_duration_hours)::character(1))::text || ':'::text) || ((w.pause_duration_minutes)::character(1))::text) || chr(39)) AS pause_duration_hh_mm, w.billable_amount, w.billing_currency, a.activity_number, a.object_id AS activity, ra.object_id AS "assignment", w.object_id AS work_record, ra.resrc FROM (((oocke1_workrecord w JOIN oocke1_resourceassignment ra ON (((w."p$$parent")::text = (ra.object_id)::text))) JOIN oocke1_activity a ON (((a.object_id)::text = (ra."p$$parent")::text))) JOIN oocke1_activitygroupass ga ON ((((a.object_id)::text = (ga."p$$parent")::text) AND (ga.activity_group IS NOT NULL))))) UNION ALL SELECT (("replace"("replace"("replace"((ga.activity_group)::text, 'activityTracker/'::text, 'workReportEntry3/'::text), 'activityCategory/'::text, 'workReportEntry3/'::text), 'activityMilestone/'::text, 'workReportEntry3/'::text) || '/'::text) || "replace"((w.object_id)::text, '/'::text, ':'::text)) AS object_id, ga.activity_group AS "p$$parent", 'org:opencrx:kernel:activity1:WorkReportEntry' AS dtype, w.modified_at, w.modified_by_, w.created_at, w.created_by_, w.access_level_browse, w.access_level_update, w.access_level_delete, w.owner_, w.name, w.description, w.started_at, w.ended_at, w.duration_hours, w.duration_minutes, ((w.duration_hours)::numeric + ((w.duration_minutes)::numeric / 60.0)) AS duration_decimal, (((((w.duration_hours)::character(1))::text || ':'::text) || ((w.duration_minutes)::character(1))::text) || chr(39)) AS duration_hh_mm, w.pause_duration_hours, w.pause_duration_minutes, ((w.pause_duration_hours)::numeric + ((w.pause_duration_minutes)::numeric / 60.0)) AS pause_duration_decimal, (((((w.pause_duration_hours)::character(1))::text || ':'::text) || ((w.pause_duration_minutes)::character(1))::text) || chr(39)) AS pause_duration_hh_mm, w.billable_amount, w.billing_currency, a.activity_number, a.object_id AS activity, ra.object_id AS "assignment", w.object_id AS work_record, ra.resrc FROM (((oocke1_workrecord w JOIN oocke1_resourceassignment ra ON (((w."p$$parent")::text = (ra.object_id)::text))) JOIN oocke1_activity a ON (((a.object_id)::text = (ra."p$$parent")::text))) JOIN oocke1_activitygroupass ga ON ((((a.object_id)::text = (ga."p$$parent")::text) AND (ga.activity_group IS NOT NULL))))) UNION ALL SELECT (("replace"("replace"("replace"((ga.activity_group)::text, 'activityTracker/'::text, 'workReportEntry4/'::text), 'activityCategory/'::text, 'workReportEntry4/'::text), 'activityMilestone/'::text, 'workReportEntry4/'::text) || '/'::text) || "replace"((w.object_id)::text, '/'::text, ':'::text)) AS object_id, ga.activity_group AS "p$$parent", 'org:opencrx:kernel:activity1:WorkReportEntry' AS dtype, w.modified_at, w.modified_by_, w.created_at, w.created_by_, w.access_level_browse, w.access_level_update, w.access_level_delete, w.owner_, w.name, w.description, w.started_at, w.ended_at, w.duration_hours, w.duration_minutes, ((w.duration_hours)::numeric + ((w.duration_minutes)::numeric / 60.0)) AS duration_decimal, (((((w.duration_hours)::character(1))::text || ':'::text) || ((w.duration_minutes)::character(1))::text) || chr(39)) AS duration_hh_mm, w.pause_duration_hours, w.pause_duration_minutes, ((w.pause_duration_hours)::numeric + ((w.pause_duration_minutes)::numeric / 60.0)) AS pause_duration_decimal, (((((w.pause_duration_hours)::character(1))::text || ':'::text) || ((w.pause_duration_minutes)::character(1))::text) || chr(39)) AS pause_duration_hh_mm, w.billable_amount, w.billing_currency, a.activity_number, a.object_id AS activity, ra.object_id AS "assignment", w.object_id AS work_record, ra.resrc FROM (((oocke1_workrecord w JOIN oocke1_resourceassignment ra ON (((w."p$$parent")::text = (ra.object_id)::text))) JOIN oocke1_activity a ON (((a.object_id)::text = (ra."p$$parent")::text))) JOIN oocke1_activitygroupass ga ON ((((a.object_id)::text = (ga."p$$parent")::text) AND (ga.activity_group IS NOT NULL))));


--
-- Name: oocke1_workrecord_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_workrecord_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    created_by character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_tobj_workreportentry_; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW oocke1_tobj_workreportentry_ AS
    SELECT oocke1_workrecord_.object_id, oocke1_workrecord_.idx, oocke1_workrecord_.created_by, oocke1_workrecord_.modified_by, oocke1_workrecord_."owner", oocke1_workrecord_.dtype FROM oocke1_workrecord_;


--
-- Name: oocke1_topic; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_topic (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    category_ integer DEFAULT -1 NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    disabled boolean,
    disabled_reason character varying(256),
    external_link_ integer DEFAULT -1 NOT NULL,
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    perform_action_ integer DEFAULT -1 NOT NULL,
    "p$$parent" character varying(256),
    topic_path_pattern character varying(256),
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_topic_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_topic_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    perform_action character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_uom; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_uom (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    base_uom character varying(256),
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    detailed_description character varying(256),
    is_schedule_base_uom boolean,
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    quantity numeric,
    "p$$parent" character varying(256),
    uom_schedule character varying(256),
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_uom_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_uom_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    created_by character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_uomschedule; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_uomschedule (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    detailed_description character varying(256),
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    "p$$parent" character varying(256),
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_uomschedule_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_uomschedule_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    created_by character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_userhome_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_userhome_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    created_by character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_vote; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_vote (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    "p$$parent" character varying(256),
    assigned_to character varying(256),
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_vote_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_vote_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    created_by character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_wfactionlogentry; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_wfactionlogentry (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    correlation character varying(256),
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description text,
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    "p$$parent" character varying(256),
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_wfactionlogentry_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_wfactionlogentry_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    created_by character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_wfprocess; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_wfprocess (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    category_ integer DEFAULT -1 NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    disabled boolean,
    disabled_reason character varying(256),
    external_link_ integer DEFAULT -1 NOT NULL,
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    owner_ integer DEFAULT -1 NOT NULL,
    priority integer,
    "p$$parent" character varying(256),
    user_boolean0 boolean,
    user_boolean1 boolean,
    user_boolean2 boolean,
    user_boolean3 boolean,
    user_boolean4_ integer DEFAULT -1 NOT NULL,
    user_code0 smallint,
    user_code1 smallint,
    user_code2 smallint,
    user_code3 smallint,
    user_code4_ integer DEFAULT -1 NOT NULL,
    user_date0 date,
    user_date1 date,
    user_date2 date,
    user_date3 date,
    user_date4_ integer DEFAULT -1 NOT NULL,
    user_date_time0 timestamp with time zone,
    user_date_time1 timestamp with time zone,
    user_date_time2 timestamp with time zone,
    user_date_time3 timestamp with time zone,
    user_date_time4_ integer DEFAULT -1 NOT NULL,
    user_number0 numeric,
    user_number1 numeric,
    user_number2 numeric,
    user_number3 numeric,
    user_number4_ integer DEFAULT -1 NOT NULL,
    user_string0 character varying(256),
    user_string1 character varying(256),
    user_string2 character varying(256),
    user_string3 character varying(256),
    user_string4_ integer DEFAULT -1 NOT NULL,
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL,
    is_synchronous boolean,
    max_retries integer
);


--
-- Name: oocke1_wfprocess_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_wfprocess_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    category character varying(256),
    created_by character varying(256),
    external_link character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    user_boolean4 boolean,
    user_code4 smallint,
    user_date4 date,
    user_date_time4 timestamp with time zone,
    user_number4 numeric,
    user_string4 character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_wfprocessinstance; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_wfprocessinstance (
    object_id character varying(256) NOT NULL,
    access_level_browse smallint,
    access_level_delete smallint,
    access_level_update smallint,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    failed boolean,
    last_activity_on timestamp with time zone,
    modified_by_ integer DEFAULT -1 NOT NULL,
    owner_ integer DEFAULT -1 NOT NULL,
    parent character varying(256),
    process character varying(256),
    started_on timestamp with time zone,
    step_counter integer,
    target_object character varying(256),
    "p$$parent" character varying(256),
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocke1_wfprocessinstance_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocke1_wfprocessinstance_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    created_by character varying(256),
    modified_by character varying(256),
    "owner" character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocse1_authenticationcontext; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocse1_authenticationcontext (
    object_id character varying(256) NOT NULL,
    completion smallint,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    modified_by_ integer DEFAULT -1 NOT NULL,
    realm character varying(256),
    "p$$parent" character varying(256),
    subject character varying(256),
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocse1_authenticationcontext_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocse1_authenticationcontext_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    created_by character varying(256),
    modified_by character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocse1_credential; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocse1_credential (
    object_id character varying(256) NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    id character varying(256),
    locked boolean,
    modified_by_ integer DEFAULT -1 NOT NULL,
    reset_credential_ integer DEFAULT -1 NOT NULL,
    "p$$parent" character varying(256),
    subject character varying(256),
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL,
    passwd character varying(256),
    address character varying(256),
    "template" smallint
);


--
-- Name: oocse1_credential_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocse1_credential_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    created_by character varying(256),
    modified_by character varying(256),
    reset_credential character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocse1_permission; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocse1_permission (
    object_id character varying(256) NOT NULL,
    action_ integer DEFAULT -1 NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    privilege character varying(256),
    "p$$parent" character varying(256),
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocse1_permission_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocse1_permission_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    "action" character varying(256),
    created_by character varying(256),
    modified_by character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocse1_policy; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocse1_policy (
    object_id character varying(256) NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    parent character varying(256),
    "p$$parent" character varying(256),
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocse1_policy_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocse1_policy_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    created_by character varying(256),
    modified_by character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocse1_principal; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocse1_principal (
    object_id character varying(256) NOT NULL,
    auth_credential_ integer DEFAULT -1 NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    credential character varying(256),
    description character varying(256),
    disabled boolean,
    is_member_of_ integer DEFAULT -1 NOT NULL,
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    "p$$parent" character varying(256),
    subject character varying(256),
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL,
    last_login_at timestamp with time zone,
    granted_role_ integer DEFAULT -1 NOT NULL
);


--
-- Name: oocse1_principal_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocse1_principal_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    auth_credential character varying(256),
    created_by character varying(256),
    is_member_of character varying(256),
    modified_by character varying(256),
    dtype character varying(256) NOT NULL,
    granted_role character varying(256)
);


--
-- Name: oocse1_privilege; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocse1_privilege (
    object_id character varying(256) NOT NULL,
    action_ integer DEFAULT -1 NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    "p$$parent" character varying(256),
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocse1_privilege_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocse1_privilege_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    "action" character varying(256),
    created_by character varying(256),
    modified_by character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocse1_realm; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocse1_realm (
    object_id character varying(256) NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    "p$$parent" character varying(256),
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocse1_realm_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocse1_realm_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    created_by character varying(256),
    modified_by character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocse1_role; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocse1_role (
    object_id character varying(256) NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    disabled boolean,
    modified_by_ integer DEFAULT -1 NOT NULL,
    name character varying(256),
    "p$$parent" character varying(256),
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocse1_role_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocse1_role_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    created_by character varying(256),
    modified_by character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocse1_segment; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocse1_segment (
    object_id character varying(256) NOT NULL,
    description character varying(256),
    "p$$parent" character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocse1_segment_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocse1_segment_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocse1_subject; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocse1_subject (
    object_id character varying(256) NOT NULL,
    created_at timestamp with time zone,
    created_by_ integer DEFAULT -1 NOT NULL,
    description character varying(256),
    modified_by_ integer DEFAULT -1 NOT NULL,
    "p$$parent" character varying(256),
    modified_at timestamp with time zone NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oocse1_subject_; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oocse1_subject_ (
    object_id character varying(256) NOT NULL,
    idx integer NOT NULL,
    created_by character varying(256),
    modified_by character varying(256),
    dtype character varying(256) NOT NULL
);


--
-- Name: oocse1_tobj_roles; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW oocse1_tobj_roles AS
    SELECT p.name AS principal_name, r.name AS role_name FROM oocse1_principal_ pg, oocse1_principal p, oocse1_principal_ pn, oocse1_role r WHERE (((((p.object_id)::text = (pn.object_id)::text) AND ((pn.is_member_of)::text = (pg.object_id)::text)) AND ((pg.granted_role)::text = (r.object_id)::text)) AND ((p.object_id)::text ~~ 'principal/%/Root/Default/%'::text));


--
-- Name: oocse1_tobj_users; Type: VIEW; Schema: public; Owner: postgres
--

CREATE VIEW oocse1_tobj_users AS
    SELECT p.name AS principal_name, c.passwd FROM (oocse1_principal p JOIN oocse1_credential c ON (((p.credential)::text = (c.object_id)::text)));


--
-- Name: oom0base_authority; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oom0base_authority (
    authority_id character varying(256) NOT NULL,
    dtype character varying(256) NOT NULL
);


--
-- Name: oom0base_provider; Type: TABLE; Schema: public; Owner: postgres; Tablespace: 
--

CREATE TABLE oom0base_provider (
    provider_id character varying(256) NOT NULL,
    authority character varying(256),
    dtype character varying(256) NOT NULL
);


SET default_with_oids = false;

--
-- Name: oocke1_accesshistory__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_accesshistory_
    ADD CONSTRAINT oocke1_accesshistory__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_accesshistory_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_accesshistory
    ADD CONSTRAINT oocke1_accesshistory_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_account__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_account_
    ADD CONSTRAINT oocke1_account__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_account_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_account
    ADD CONSTRAINT oocke1_account_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_accountassignment__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_accountassignment_
    ADD CONSTRAINT oocke1_accountassignment__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_accountassignment_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_accountassignment
    ADD CONSTRAINT oocke1_accountassignment_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_activity__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_activity_
    ADD CONSTRAINT oocke1_activity__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_activity_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_activity
    ADD CONSTRAINT oocke1_activity_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_activitycreator__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_activitycreator_
    ADD CONSTRAINT oocke1_activitycreator__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_activitycreator_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_activitycreator
    ADD CONSTRAINT oocke1_activitycreator_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_activityeffortesti__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_activityeffortesti_
    ADD CONSTRAINT oocke1_activityeffortesti__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_activityeffortesti_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_activityeffortesti
    ADD CONSTRAINT oocke1_activityeffortesti_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_activityfollowup__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_activityfollowup_
    ADD CONSTRAINT oocke1_activityfollowup__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_activityfollowup_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_activityfollowup
    ADD CONSTRAINT oocke1_activityfollowup_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_activitygroup__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_activitygroup_
    ADD CONSTRAINT oocke1_activitygroup__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_activitygroup_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_activitygroup
    ADD CONSTRAINT oocke1_activitygroup_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_activitygroupass__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_activitygroupass_
    ADD CONSTRAINT oocke1_activitygroupass__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_activitygroupass_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_activitygroupass
    ADD CONSTRAINT oocke1_activitygroupass_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_activitylink__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_activitylink_
    ADD CONSTRAINT oocke1_activitylink__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_activitylink_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_activitylink
    ADD CONSTRAINT oocke1_activitylink_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_activityparty__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_activityparty_
    ADD CONSTRAINT oocke1_activityparty__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_activityparty_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_activityparty
    ADD CONSTRAINT oocke1_activityparty_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_activityprocaction__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_activityprocaction_
    ADD CONSTRAINT oocke1_activityprocaction__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_activityprocaction_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_activityprocaction
    ADD CONSTRAINT oocke1_activityprocaction_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_activityprocess__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_activityprocess_
    ADD CONSTRAINT oocke1_activityprocess__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_activityprocess_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_activityprocess
    ADD CONSTRAINT oocke1_activityprocess_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_activityprocstate__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_activityprocstate_
    ADD CONSTRAINT oocke1_activityprocstate__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_activityprocstate_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_activityprocstate
    ADD CONSTRAINT oocke1_activityprocstate_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_activityproctrans__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_activityproctrans_
    ADD CONSTRAINT oocke1_activityproctrans__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_activityproctrans_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_activityproctrans
    ADD CONSTRAINT oocke1_activityproctrans_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_activitytype__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_activitytype_
    ADD CONSTRAINT oocke1_activitytype__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_activitytype_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_activitytype
    ADD CONSTRAINT oocke1_activitytype_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_additionalextlink__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_additionalextlink_
    ADD CONSTRAINT oocke1_additionalextlink__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_additionalextlink_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_additionalextlink
    ADD CONSTRAINT oocke1_additionalextlink_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_address__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_address_
    ADD CONSTRAINT oocke1_address__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_address_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_address
    ADD CONSTRAINT oocke1_address_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_addressgroup__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_addressgroup_
    ADD CONSTRAINT oocke1_addressgroup__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_addressgroup_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_addressgroup
    ADD CONSTRAINT oocke1_addressgroup_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_addressgroupmember__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_addressgroupmember_
    ADD CONSTRAINT oocke1_addressgroupmember__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_addressgroupmember_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_addressgroupmember
    ADD CONSTRAINT oocke1_addressgroupmember_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_alert__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_alert_
    ADD CONSTRAINT oocke1_alert__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_alert_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_alert
    ADD CONSTRAINT oocke1_alert_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_auditentry__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_auditentry_
    ADD CONSTRAINT oocke1_auditentry__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_auditentry_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_auditentry
    ADD CONSTRAINT oocke1_auditentry_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_booking__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_booking_
    ADD CONSTRAINT oocke1_booking__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_booking_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_booking
    ADD CONSTRAINT oocke1_booking_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_bookingperiod__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_bookingperiod_
    ADD CONSTRAINT oocke1_bookingperiod__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_bookingperiod_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_bookingperiod
    ADD CONSTRAINT oocke1_bookingperiod_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_bookingtext__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_bookingtext_
    ADD CONSTRAINT oocke1_bookingtext__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_bookingtext_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_bookingtext
    ADD CONSTRAINT oocke1_bookingtext_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_budget__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_budget_
    ADD CONSTRAINT oocke1_budget__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_budget_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_budget
    ADD CONSTRAINT oocke1_budget_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_budgetmilestone__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_budgetmilestone_
    ADD CONSTRAINT oocke1_budgetmilestone__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_budgetmilestone_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_budgetmilestone
    ADD CONSTRAINT oocke1_budgetmilestone_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_buildingunit__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_buildingunit_
    ADD CONSTRAINT oocke1_buildingunit__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_buildingunit_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_buildingunit
    ADD CONSTRAINT oocke1_buildingunit_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_calculationrule__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_calculationrule_
    ADD CONSTRAINT oocke1_calculationrule__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_calculationrule_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_calculationrule
    ADD CONSTRAINT oocke1_calculationrule_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_calendar__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_calendar_
    ADD CONSTRAINT oocke1_calendar__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_calendar_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_calendar
    ADD CONSTRAINT oocke1_calendar_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_calendarday__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_calendarday_
    ADD CONSTRAINT oocke1_calendarday__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_calendarday_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_calendarday
    ADD CONSTRAINT oocke1_calendarday_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_chart__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_chart_
    ADD CONSTRAINT oocke1_chart__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_chart_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_chart
    ADD CONSTRAINT oocke1_chart_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_codevaluecontainer__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_codevaluecontainer_
    ADD CONSTRAINT oocke1_codevaluecontainer__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_codevaluecontainer_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_codevaluecontainer
    ADD CONSTRAINT oocke1_codevaluecontainer_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_codevalueentry__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_codevalueentry_
    ADD CONSTRAINT oocke1_codevalueentry__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_codevalueentry_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_codevalueentry
    ADD CONSTRAINT oocke1_codevalueentry_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_competitor__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_competitor_
    ADD CONSTRAINT oocke1_competitor__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_competitor_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_competitor
    ADD CONSTRAINT oocke1_competitor_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_componentconfig__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_componentconfig_
    ADD CONSTRAINT oocke1_componentconfig__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_componentconfig_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_componentconfig
    ADD CONSTRAINT oocke1_componentconfig_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_compoundbooking__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_compoundbooking_
    ADD CONSTRAINT oocke1_compoundbooking__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_compoundbooking_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_compoundbooking
    ADD CONSTRAINT oocke1_compoundbooking_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_contactmembership__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_contactmembership_
    ADD CONSTRAINT oocke1_contactmembership__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_contactmembership_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_contactmembership
    ADD CONSTRAINT oocke1_contactmembership_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_contactrel__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_contactrel_
    ADD CONSTRAINT oocke1_contactrel__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_contactrel_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_contactrel
    ADD CONSTRAINT oocke1_contactrel_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_contactrole__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_contactrole_
    ADD CONSTRAINT oocke1_contactrole__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_contactrole_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_contactrole
    ADD CONSTRAINT oocke1_contactrole_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_contract__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_contract_
    ADD CONSTRAINT oocke1_contract__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_contract_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_contract
    ADD CONSTRAINT oocke1_contract_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_contractposition__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_contractposition_
    ADD CONSTRAINT oocke1_contractposition__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_contractposition_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_contractposition
    ADD CONSTRAINT oocke1_contractposition_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_contractposmod__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_contractposmod_
    ADD CONSTRAINT oocke1_contractposmod__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_contractposmod_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_contractposmod
    ADD CONSTRAINT oocke1_contractposmod_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_creditlimit__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_creditlimit_
    ADD CONSTRAINT oocke1_creditlimit__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_creditlimit_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_creditlimit
    ADD CONSTRAINT oocke1_creditlimit_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_deliveryinfo__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_deliveryinfo_
    ADD CONSTRAINT oocke1_deliveryinfo__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_deliveryinfo_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_deliveryinfo
    ADD CONSTRAINT oocke1_deliveryinfo_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_deliveryrequest__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_deliveryrequest_
    ADD CONSTRAINT oocke1_deliveryrequest__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_deliveryrequest_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_deliveryrequest
    ADD CONSTRAINT oocke1_deliveryrequest_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_depot__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_depot_
    ADD CONSTRAINT oocke1_depot__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_depot_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_depot
    ADD CONSTRAINT oocke1_depot_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_depotentity__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_depotentity_
    ADD CONSTRAINT oocke1_depotentity__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_depotentity_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_depotentity
    ADD CONSTRAINT oocke1_depotentity_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_depotentityrel__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_depotentityrel_
    ADD CONSTRAINT oocke1_depotentityrel__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_depotentityrel_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_depotentityrel
    ADD CONSTRAINT oocke1_depotentityrel_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_depotgroup__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_depotgroup_
    ADD CONSTRAINT oocke1_depotgroup__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_depotgroup_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_depotgroup
    ADD CONSTRAINT oocke1_depotgroup_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_depotholder__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_depotholder_
    ADD CONSTRAINT oocke1_depotholder__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_depotholder_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_depotholder
    ADD CONSTRAINT oocke1_depotholder_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_depotposition__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_depotposition_
    ADD CONSTRAINT oocke1_depotposition__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_depotposition_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_depotposition
    ADD CONSTRAINT oocke1_depotposition_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_depotreference__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_depotreference_
    ADD CONSTRAINT oocke1_depotreference__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_depotreference_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_depotreference
    ADD CONSTRAINT oocke1_depotreference_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_depotreport__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_depotreport_
    ADD CONSTRAINT oocke1_depotreport__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_depotreport_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_depotreport
    ADD CONSTRAINT oocke1_depotreport_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_depotreportitem__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_depotreportitem_
    ADD CONSTRAINT oocke1_depotreportitem__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_depotreportitem_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_depotreportitem
    ADD CONSTRAINT oocke1_depotreportitem_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_depottype__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_depottype_
    ADD CONSTRAINT oocke1_depottype__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_depottype_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_depottype
    ADD CONSTRAINT oocke1_depottype_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_description__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_description_
    ADD CONSTRAINT oocke1_description__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_description_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_description
    ADD CONSTRAINT oocke1_description_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_document__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_document_
    ADD CONSTRAINT oocke1_document__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_document_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_document
    ADD CONSTRAINT oocke1_document_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_documentattachment__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_documentattachment_
    ADD CONSTRAINT oocke1_documentattachment__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_documentattachment_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_documentattachment
    ADD CONSTRAINT oocke1_documentattachment_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_documentfolder__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_documentfolder_
    ADD CONSTRAINT oocke1_documentfolder__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_documentfolder_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_documentfolder
    ADD CONSTRAINT oocke1_documentfolder_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_documentlink__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_documentlink_
    ADD CONSTRAINT oocke1_documentlink__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_documentlink_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_documentlink
    ADD CONSTRAINT oocke1_documentlink_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_documentlock__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_documentlock_
    ADD CONSTRAINT oocke1_documentlock__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_documentlock_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_documentlock
    ADD CONSTRAINT oocke1_documentlock_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_emailaccount__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_emailaccount_
    ADD CONSTRAINT oocke1_emailaccount__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_emailaccount_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_emailaccount
    ADD CONSTRAINT oocke1_emailaccount_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_event__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_event_
    ADD CONSTRAINT oocke1_event__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_event_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_event
    ADD CONSTRAINT oocke1_event_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_eventpart__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_eventpart_
    ADD CONSTRAINT oocke1_eventpart__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_eventpart_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_eventpart
    ADD CONSTRAINT oocke1_eventpart_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_eventslot__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_eventslot_
    ADD CONSTRAINT oocke1_eventslot__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_eventslot_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_eventslot
    ADD CONSTRAINT oocke1_eventslot_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_facility__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_facility_
    ADD CONSTRAINT oocke1_facility__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_facility_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_facility
    ADD CONSTRAINT oocke1_facility_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_filter__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_filter_
    ADD CONSTRAINT oocke1_filter__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_filter_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_filter
    ADD CONSTRAINT oocke1_filter_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_filterproperty__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_filterproperty_
    ADD CONSTRAINT oocke1_filterproperty__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_filterproperty_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_filterproperty
    ADD CONSTRAINT oocke1_filterproperty_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_forecast__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_forecast_
    ADD CONSTRAINT oocke1_forecast__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_forecast_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_forecast
    ADD CONSTRAINT oocke1_forecast_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_forecastperiod__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_forecastperiod_
    ADD CONSTRAINT oocke1_forecastperiod__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_forecastperiod_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_forecastperiod
    ADD CONSTRAINT oocke1_forecastperiod_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_indexentry__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_indexentry_
    ADD CONSTRAINT oocke1_indexentry__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_indexentry_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_indexentry
    ADD CONSTRAINT oocke1_indexentry_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_inventoryitem__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_inventoryitem_
    ADD CONSTRAINT oocke1_inventoryitem__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_inventoryitem_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_inventoryitem
    ADD CONSTRAINT oocke1_inventoryitem_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_involvedobject__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_involvedobject_
    ADD CONSTRAINT oocke1_involvedobject__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_involvedobject_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_involvedobject
    ADD CONSTRAINT oocke1_involvedobject_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_join_actcontainswre_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_join_actcontainswre
    ADD CONSTRAINT oocke1_join_actcontainswre_pkey PRIMARY KEY (activity, work_report_entry);


--
-- Name: oocke1_join_actgcontainswre_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_join_actgcontainswre
    ADD CONSTRAINT oocke1_join_actgcontainswre_pkey PRIMARY KEY (activity_group, work_report_entry);


--
-- Name: oocke1_join_fcperiodhaslead_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_join_fcperiodhaslead
    ADD CONSTRAINT oocke1_join_fcperiodhaslead_pkey PRIMARY KEY (forecast_period, lead);


--
-- Name: oocke1_join_fcperiodhasopty_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_join_fcperiodhasopty
    ADD CONSTRAINT oocke1_join_fcperiodhasopty_pkey PRIMARY KEY (forecast_period, opportunity);


--
-- Name: oocke1_join_fcperiodhasquote_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_join_fcperiodhasquote
    ADD CONSTRAINT oocke1_join_fcperiodhasquote_pkey PRIMARY KEY (forecast_period, "quote");


--
-- Name: oocke1_join_filterincludesacct_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_join_filterincludesacct
    ADD CONSTRAINT oocke1_join_filterincludesacct_pkey PRIMARY KEY (account_filter, filtered_account);


--
-- Name: oocke1_join_filterincludesact_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_join_filterincludesact
    ADD CONSTRAINT oocke1_join_filterincludesact_pkey PRIMARY KEY (activity_filter, filtered_activity);


--
-- Name: oocke1_join_filterincludesaddr_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_join_filterincludesaddr
    ADD CONSTRAINT oocke1_join_filterincludesaddr_pkey PRIMARY KEY (address_filter, filtered_address);


--
-- Name: oocke1_join_filterincludesprod_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_join_filterincludesprod
    ADD CONSTRAINT oocke1_join_filterincludesprod_pkey PRIMARY KEY (product_filter, filtered_product);


--
-- Name: oocke1_join_finderhasidxacct_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_join_finderhasidxacct
    ADD CONSTRAINT oocke1_join_finderhasidxacct_pkey PRIMARY KEY (object_finder, index_entry_account);


--
-- Name: oocke1_join_finderhasidxact_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_join_finderhasidxact
    ADD CONSTRAINT oocke1_join_finderhasidxact_pkey PRIMARY KEY (object_finder, index_entry_activity);


--
-- Name: oocke1_join_finderhasidxbldg_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_join_finderhasidxbldg
    ADD CONSTRAINT oocke1_join_finderhasidxbldg_pkey PRIMARY KEY (object_finder, index_entry_building);


--
-- Name: oocke1_join_finderhasidxcontr_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_join_finderhasidxcontr
    ADD CONSTRAINT oocke1_join_finderhasidxcontr_pkey PRIMARY KEY (object_finder, index_entry_contract);


--
-- Name: oocke1_join_finderhasidxdep_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_join_finderhasidxdep
    ADD CONSTRAINT oocke1_join_finderhasidxdep_pkey PRIMARY KEY (object_finder, index_entry_depot);


--
-- Name: oocke1_join_finderhasidxdoc_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_join_finderhasidxdoc
    ADD CONSTRAINT oocke1_join_finderhasidxdoc_pkey PRIMARY KEY (object_finder, index_entry_document);


--
-- Name: oocke1_join_finderhasidxprod_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_join_finderhasidxprod
    ADD CONSTRAINT oocke1_join_finderhasidxprod_pkey PRIMARY KEY (object_finder, index_entry_product);


--
-- Name: oocke1_join_plhasassple_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_join_plhasassple
    ADD CONSTRAINT oocke1_join_plhasassple_pkey PRIMARY KEY (price_level, price_list_entry);


--
-- Name: oocke1_join_rescontainswre_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_join_rescontainswre
    ADD CONSTRAINT oocke1_join_rescontainswre_pkey PRIMARY KEY (resource, work_report_entry);


--
-- Name: oocke1_linkableitemlink__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_linkableitemlink_
    ADD CONSTRAINT oocke1_linkableitemlink__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_linkableitemlink_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_linkableitemlink
    ADD CONSTRAINT oocke1_linkableitemlink_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_media__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_media_
    ADD CONSTRAINT oocke1_media__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_media_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_media
    ADD CONSTRAINT oocke1_media_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_mmsslide__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_mmsslide_
    ADD CONSTRAINT oocke1_mmsslide__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_mmsslide_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_mmsslide
    ADD CONSTRAINT oocke1_mmsslide_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_modelelement__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_modelelement_
    ADD CONSTRAINT oocke1_modelelement__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_modelelement_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_modelelement
    ADD CONSTRAINT oocke1_modelelement_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_note__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_note_
    ADD CONSTRAINT oocke1_note__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_note_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_note
    ADD CONSTRAINT oocke1_note_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_objectfinder__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_objectfinder_
    ADD CONSTRAINT oocke1_objectfinder__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_objectfinder_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_objectfinder
    ADD CONSTRAINT oocke1_objectfinder_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_organization__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_organization_
    ADD CONSTRAINT oocke1_organization__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_organization_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_organization
    ADD CONSTRAINT oocke1_organization_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_organizationalunit__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_organizationalunit_
    ADD CONSTRAINT oocke1_organizationalunit__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_organizationalunit_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_organizationalunit
    ADD CONSTRAINT oocke1_organizationalunit_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_orgunitrelship__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_orgunitrelship_
    ADD CONSTRAINT oocke1_orgunitrelship__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_orgunitrelship_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_orgunitrelship
    ADD CONSTRAINT oocke1_orgunitrelship_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_pricelevel__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_pricelevel_
    ADD CONSTRAINT oocke1_pricelevel__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_pricelevel_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_pricelevel
    ADD CONSTRAINT oocke1_pricelevel_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_pricemodifier__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_pricemodifier_
    ADD CONSTRAINT oocke1_pricemodifier__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_pricemodifier_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_pricemodifier
    ADD CONSTRAINT oocke1_pricemodifier_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_pricingrule__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_pricingrule_
    ADD CONSTRAINT oocke1_pricingrule__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_pricingrule_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_pricingrule
    ADD CONSTRAINT oocke1_pricingrule_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_product__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_product_
    ADD CONSTRAINT oocke1_product__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_product_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_product
    ADD CONSTRAINT oocke1_product_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_productapplication__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_productapplication_
    ADD CONSTRAINT oocke1_productapplication__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_productapplication_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_productapplication
    ADD CONSTRAINT oocke1_productapplication_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_productbaseprice__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_productbaseprice_
    ADD CONSTRAINT oocke1_productbaseprice__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_productbaseprice_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_productbaseprice
    ADD CONSTRAINT oocke1_productbaseprice_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_productclass__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_productclass_
    ADD CONSTRAINT oocke1_productclass__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_productclass_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_productclass
    ADD CONSTRAINT oocke1_productclass_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_productclassrel__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_productclassrel_
    ADD CONSTRAINT oocke1_productclassrel__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_productclassrel_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_productclassrel
    ADD CONSTRAINT oocke1_productclassrel_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_productconfig__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_productconfig_
    ADD CONSTRAINT oocke1_productconfig__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_productconfig_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_productconfig
    ADD CONSTRAINT oocke1_productconfig_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_productconftypeset__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_productconftypeset_
    ADD CONSTRAINT oocke1_productconftypeset__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_productconftypeset_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_productconftypeset
    ADD CONSTRAINT oocke1_productconftypeset_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_productphase__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_productphase_
    ADD CONSTRAINT oocke1_productphase__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_productphase_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_productphase
    ADD CONSTRAINT oocke1_productphase_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_productreference__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_productreference_
    ADD CONSTRAINT oocke1_productreference__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_productreference_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_productreference
    ADD CONSTRAINT oocke1_productreference_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_property__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_property_
    ADD CONSTRAINT oocke1_property__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_property_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_property
    ADD CONSTRAINT oocke1_property_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_propertyset__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_propertyset_
    ADD CONSTRAINT oocke1_propertyset__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_propertyset_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_propertyset
    ADD CONSTRAINT oocke1_propertyset_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_quickaccess__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_quickaccess_
    ADD CONSTRAINT oocke1_quickaccess__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_quickaccess_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_quickaccess
    ADD CONSTRAINT oocke1_quickaccess_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_rasartifactcontext__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_rasartifactcontext_
    ADD CONSTRAINT oocke1_rasartifactcontext__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_rasartifactcontext_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_rasartifactcontext
    ADD CONSTRAINT oocke1_rasartifactcontext_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_rasartifactdep__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_rasartifactdep_
    ADD CONSTRAINT oocke1_rasartifactdep__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_rasartifactdep_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_rasartifactdep
    ADD CONSTRAINT oocke1_rasartifactdep_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_rasclassificatielt__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_rasclassificatielt_
    ADD CONSTRAINT oocke1_rasclassificatielt__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_rasclassificatielt_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_rasclassificatielt
    ADD CONSTRAINT oocke1_rasclassificatielt_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_rasdescriptor__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_rasdescriptor_
    ADD CONSTRAINT oocke1_rasdescriptor__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_rasdescriptor_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_rasdescriptor
    ADD CONSTRAINT oocke1_rasdescriptor_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_rasprofile__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_rasprofile_
    ADD CONSTRAINT oocke1_rasprofile__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_rasprofile_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_rasprofile
    ADD CONSTRAINT oocke1_rasprofile_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_rassolutionpart__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_rassolutionpart_
    ADD CONSTRAINT oocke1_rassolutionpart__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_rassolutionpart_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_rassolutionpart
    ADD CONSTRAINT oocke1_rassolutionpart_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_rasvarpoint__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_rasvarpoint_
    ADD CONSTRAINT oocke1_rasvarpoint__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_rasvarpoint_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_rasvarpoint
    ADD CONSTRAINT oocke1_rasvarpoint_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_rating__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_rating_
    ADD CONSTRAINT oocke1_rating__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_rating_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_rating
    ADD CONSTRAINT oocke1_rating_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_relatedproduct__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_relatedproduct_
    ADD CONSTRAINT oocke1_relatedproduct__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_relatedproduct_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_relatedproduct
    ADD CONSTRAINT oocke1_relatedproduct_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_resource__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_resource_
    ADD CONSTRAINT oocke1_resource__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_resource_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_resource
    ADD CONSTRAINT oocke1_resource_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_resourceassignment__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_resourceassignment_
    ADD CONSTRAINT oocke1_resourceassignment__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_resourceassignment_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_resourceassignment
    ADD CONSTRAINT oocke1_resourceassignment_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_revenuereport__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_revenuereport_
    ADD CONSTRAINT oocke1_revenuereport__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_revenuereport_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_revenuereport
    ADD CONSTRAINT oocke1_revenuereport_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_salestaxtype__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_salestaxtype_
    ADD CONSTRAINT oocke1_salestaxtype__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_salestaxtype_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_salestaxtype
    ADD CONSTRAINT oocke1_salestaxtype_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_segment__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_segment_
    ADD CONSTRAINT oocke1_segment__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_segment_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_segment
    ADD CONSTRAINT oocke1_segment_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_simplebooking__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_simplebooking_
    ADD CONSTRAINT oocke1_simplebooking__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_simplebooking_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_simplebooking
    ADD CONSTRAINT oocke1_simplebooking_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_subscription__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_subscription_
    ADD CONSTRAINT oocke1_subscription__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_subscription_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_subscription
    ADD CONSTRAINT oocke1_subscription_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_topic__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_topic_
    ADD CONSTRAINT oocke1_topic__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_topic_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_topic
    ADD CONSTRAINT oocke1_topic_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_uom__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_uom_
    ADD CONSTRAINT oocke1_uom__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_uom_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_uom
    ADD CONSTRAINT oocke1_uom_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_uomschedule__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_uomschedule_
    ADD CONSTRAINT oocke1_uomschedule__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_uomschedule_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_uomschedule
    ADD CONSTRAINT oocke1_uomschedule_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_userhome__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_userhome_
    ADD CONSTRAINT oocke1_userhome__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_userhome_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_userhome
    ADD CONSTRAINT oocke1_userhome_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_vote__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_vote_
    ADD CONSTRAINT oocke1_vote__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_vote_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_vote
    ADD CONSTRAINT oocke1_vote_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_wfactionlogentry__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_wfactionlogentry_
    ADD CONSTRAINT oocke1_wfactionlogentry__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_wfactionlogentry_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_wfactionlogentry
    ADD CONSTRAINT oocke1_wfactionlogentry_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_wfprocess__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_wfprocess_
    ADD CONSTRAINT oocke1_wfprocess__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_wfprocess_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_wfprocess
    ADD CONSTRAINT oocke1_wfprocess_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_wfprocessinstance__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_wfprocessinstance_
    ADD CONSTRAINT oocke1_wfprocessinstance__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_wfprocessinstance_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_wfprocessinstance
    ADD CONSTRAINT oocke1_wfprocessinstance_pkey PRIMARY KEY (object_id);


--
-- Name: oocke1_workrecord__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_workrecord_
    ADD CONSTRAINT oocke1_workrecord__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocke1_workrecord_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocke1_workrecord
    ADD CONSTRAINT oocke1_workrecord_pkey PRIMARY KEY (object_id);


--
-- Name: oocse1_authenticationcontext__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocse1_authenticationcontext_
    ADD CONSTRAINT oocse1_authenticationcontext__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocse1_authenticationcontext_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocse1_authenticationcontext
    ADD CONSTRAINT oocse1_authenticationcontext_pkey PRIMARY KEY (object_id);


--
-- Name: oocse1_credential__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocse1_credential_
    ADD CONSTRAINT oocse1_credential__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocse1_credential_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocse1_credential
    ADD CONSTRAINT oocse1_credential_pkey PRIMARY KEY (object_id);


--
-- Name: oocse1_permission__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocse1_permission_
    ADD CONSTRAINT oocse1_permission__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocse1_permission_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocse1_permission
    ADD CONSTRAINT oocse1_permission_pkey PRIMARY KEY (object_id);


--
-- Name: oocse1_policy__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocse1_policy_
    ADD CONSTRAINT oocse1_policy__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocse1_policy_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocse1_policy
    ADD CONSTRAINT oocse1_policy_pkey PRIMARY KEY (object_id);


--
-- Name: oocse1_principal__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocse1_principal_
    ADD CONSTRAINT oocse1_principal__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocse1_principal_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocse1_principal
    ADD CONSTRAINT oocse1_principal_pkey PRIMARY KEY (object_id);


--
-- Name: oocse1_privilege__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocse1_privilege_
    ADD CONSTRAINT oocse1_privilege__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocse1_privilege_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocse1_privilege
    ADD CONSTRAINT oocse1_privilege_pkey PRIMARY KEY (object_id);


--
-- Name: oocse1_realm__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocse1_realm_
    ADD CONSTRAINT oocse1_realm__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocse1_realm_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocse1_realm
    ADD CONSTRAINT oocse1_realm_pkey PRIMARY KEY (object_id);


--
-- Name: oocse1_role__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocse1_role_
    ADD CONSTRAINT oocse1_role__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocse1_role_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocse1_role
    ADD CONSTRAINT oocse1_role_pkey PRIMARY KEY (object_id);


--
-- Name: oocse1_segment__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocse1_segment_
    ADD CONSTRAINT oocse1_segment__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocse1_segment_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocse1_segment
    ADD CONSTRAINT oocse1_segment_pkey PRIMARY KEY (object_id);


--
-- Name: oocse1_subject__pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocse1_subject_
    ADD CONSTRAINT oocse1_subject__pkey PRIMARY KEY (object_id, idx);


--
-- Name: oocse1_subject_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oocse1_subject
    ADD CONSTRAINT oocse1_subject_pkey PRIMARY KEY (object_id);


--
-- Name: oom0base_authority_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oom0base_authority
    ADD CONSTRAINT oom0base_authority_pkey PRIMARY KEY (authority_id);


--
-- Name: oom0base_provider_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres; Tablespace: 
--

ALTER TABLE ONLY oom0base_provider
    ADD CONSTRAINT oom0base_provider_pkey PRIMARY KEY (provider_id);

--
-- PostgreSQL database dump complete
--
