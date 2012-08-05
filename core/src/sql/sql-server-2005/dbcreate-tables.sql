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
CREATE TABLE prefs_Preference(
    object_rid nvarchar(100) NOT NULL,
    object_oid nvarchar(200) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
    object_idx int NOT NULL,
    object__class nvarchar(100) NULL,
    name nvarchar(100) NULL,
    description nvarchar(100) NULL,
    absolute_path nvarchar(200) NULL,
    parent nvarchar(100) NULL,
    string_value ntext NULL,
    integer_value int NULL,
    boolean_value nvarchar(10) NULL,
    uri_value nvarchar(200) NULL,
    decimal_value numeric(18,9) NULL,
    CONSTRAINT PK_PREFS_Preferences PRIMARY KEY (object_rid, object_oid, object_idx)
)
GO

CREATE TABLE OOCKE1_activity_number_SEQ (
    nextval int
)
GO

INSERT INTO OOCKE1_activity_number_SEQ (nextval) VALUES (1000000)
GO

CREATE TABLE OOCKE1_position_number_SEQ (
    nextval int
)
GO

INSERT INTO OOCKE1_position_number_SEQ (nextval) VALUES (1000000)
GO


/****** Object:  Table [dbo].[JPOX_TABLES]    Script Date: 07/08/2008 12:06:22 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[JPOX_TABLES](
	[CLASS_NAME] [nvarchar](128) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[VERSION] [nvarchar](20) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[TYPE] [nvarchar](4) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[INTERFACE_NAME] [nvarchar](255) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[TABLE_NAME] [nvarchar](128) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](2) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
 CONSTRAINT [PK_JPOX_TABLES] PRIMARY KEY CLUSTERED 
(
	[CLASS_NAME] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_ACCESSHISTORY]    Script Date: 07/08/2008 12:06:23 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_ACCESSHISTORY](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL DEFAULT ((-1)),
	[OWNER_] [int] NOT NULL DEFAULT ((-1)),
	[REFERENCE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_ACCESSHISTORY_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_ACCESSHISTORY_]    Script Date: 07/08/2008 12:06:23 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_ACCESSHISTORY_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_ACCESSHISTORY__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_ACCOUNT]    Script Date: 07/08/2008 12:06:23 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_ACCOUNT](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[ACCOUNT_CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__ACCOU__0AFFEA93]  DEFAULT ((-1)),
	[ACCOUNT_RATING] [smallint] NULL,
	[ACCOUNT_STATE] [smallint] NULL,
	[ACCOUNT_TYPE_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__ACCOU__0BF40ECC]  DEFAULT ((-1)),
	[ALIAS_NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[BUSINESS_TYPE_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__BUSIN__0CE83305]  DEFAULT ((-1)),
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__CATEG__0DDC573E]  DEFAULT ((-1)),
	[CONTACT_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__CONTA__0ED07B77]  DEFAULT ((-1)),
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__CREAT__0FC49FB0]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__EXTER__10B8C3E9]  DEFAULT ((-1)),
	[FULL_NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__MODIF__11ACE822]  DEFAULT ((-1)),
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__OWNER__12A10C5B]  DEFAULT ((-1)),
	[PARTNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__PARTN__13953094]  DEFAULT ((-1)),
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___148954CD]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___157D7906]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___16719D3F]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___1765C178]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___1859E5B1]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___194E09EA]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[INDUSTRY] [smallint] NULL,
	[NUMBER_OF_EMPLOYEES] [int] NULL,
	[NUMBER_OF_EMPLOYEES_CATEGORY] [smallint] NULL,
	[ORG_UNIT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[STOCK_EXCHANGE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[TICKER_SYMBOL] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[ANNIVERSARY] [datetime] NULL,
	[ANNUAL_INCOME_AMOUNT] [decimal](19, 9) NULL,
	[ANNUAL_INCOME_CURRENCY] [smallint] NULL,
	[ASSISTANT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[BIRTHDATE] [datetime] NULL,
	[CHILDREN_NAMES_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__CHILD__1A422E23]  DEFAULT ((-1)),
	[DEPARTMENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DEPUTY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DO_NOT_BULK_POSTAL_MAIL] [bit] NULL,
	[DO_NOT_E_MAIL] [bit] NULL,
	[DO_NOT_FAX] [bit] NULL,
	[DO_NOT_PHONE] [bit] NULL,
	[DO_NOT_POSTAL_MAIL] [bit] NULL,
	[EDUCATION] [smallint] NULL,
	[FAMILY_STATUS] [smallint] NULL,
	[FIRST_NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[GENDER] [smallint] NULL,
	[GOVERNMENT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[JOB_ROLE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[JOB_TITLE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[LAST_NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MIDDLE_NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[NICK_NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[NUMBER_OF_CHILDREN] [smallint] NULL,
	[ORGANIZATION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OU_MEMBERSHIP_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__OU_ME__1B36525C]  DEFAULT ((-1)),
	[PICTURE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[PREFERRED_CONTACT_METHOD] [smallint] NULL,
	[PREFERRED_SPOKEN_LANGUAGE] [smallint] NULL,
	[PREFERRED_WRITTEN_LANGUAGE] [smallint] NULL,
	[REPORTS_TO] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[SALUTATION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[SALUTATION_CODE] [smallint] NULL,
	[SUFFIX] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXT_BOOLEAN0] [bit] NULL,
	[EXT_BOOLEAN1] [bit] NULL,
	[EXT_BOOLEAN2] [bit] NULL,
	[EXT_BOOLEAN3] [bit] NULL,
	[EXT_BOOLEAN4] [bit] NULL,
	[EXT_BOOLEAN5] [bit] NULL,
	[EXT_BOOLEAN6] [bit] NULL,
	[EXT_BOOLEAN7] [bit] NULL,
	[EXT_BOOLEAN8] [bit] NULL,
	[EXT_BOOLEAN9] [bit] NULL,
	[EXT_CODE0] [smallint] NULL,
	[EXT_CODE1] [smallint] NULL,
	[EXT_CODE2] [smallint] NULL,
	[EXT_CODE3] [smallint] NULL,
	[EXT_CODE4] [smallint] NULL,
	[EXT_CODE5] [smallint] NULL,
	[EXT_CODE6] [smallint] NULL,
	[EXT_CODE7] [smallint] NULL,
	[EXT_CODE8] [smallint] NULL,
	[EXT_CODE9] [smallint] NULL,
	[EXT_DATE0] [datetime] NULL,
	[EXT_DATE1] [datetime] NULL,
	[EXT_DATE2] [datetime] NULL,
	[EXT_DATE3] [datetime] NULL,
	[EXT_DATE4] [datetime] NULL,
	[EXT_DATE5] [datetime] NULL,
	[EXT_DATE6] [datetime] NULL,
	[EXT_DATE7] [datetime] NULL,
	[EXT_DATE8] [datetime] NULL,
	[EXT_DATE9] [datetime] NULL,
	[EXT_DATE_TIME0] [datetime] NULL,
	[EXT_DATE_TIME1] [datetime] NULL,
	[EXT_DATE_TIME2] [datetime] NULL,
	[EXT_DATE_TIME3] [datetime] NULL,
	[EXT_DATE_TIME4] [datetime] NULL,
	[EXT_DATE_TIME5] [datetime] NULL,
	[EXT_DATE_TIME6] [datetime] NULL,
	[EXT_DATE_TIME7] [datetime] NULL,
	[EXT_DATE_TIME8] [datetime] NULL,
	[EXT_DATE_TIME9] [datetime] NULL,
	[EXT_NUMBER0] [decimal](19, 9) NULL,
	[EXT_NUMBER1] [decimal](19, 9) NULL,
	[EXT_NUMBER2] [decimal](19, 9) NULL,
	[EXT_NUMBER3] [decimal](19, 9) NULL,
	[EXT_NUMBER4] [decimal](19, 9) NULL,
	[EXT_NUMBER5] [decimal](19, 9) NULL,
	[EXT_NUMBER6] [decimal](19, 9) NULL,
	[EXT_NUMBER7] [decimal](19, 9) NULL,
	[EXT_NUMBER8] [decimal](19, 9) NULL,
	[EXT_NUMBER9] [decimal](19, 9) NULL,
	[EXT_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXT_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXT_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXT_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXT_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXT_STRING5] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXT_STRING6] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXT_STRING7] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXT_STRING8] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXT_STRING9] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MASTER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CITIZENSHIP_] [int] NOT NULL DEFAULT ((-1)),
	[RELIGION_] [int] NOT NULL DEFAULT ((-1)),
	[EXT_CODE10] [smallint] NULL,
	[EXT_CODE11] [smallint] NULL,
	[EXT_CODE12] [smallint] NULL,
	[EXT_CODE13] [smallint] NULL,
	[EXT_CODE14] [smallint] NULL,
	[EXT_CODE15] [smallint] NULL,
	[EXT_CODE17] [smallint] NULL,
	[EXT_CODE16] [smallint] NULL,
	[EXT_CODE19] [smallint] NULL,
	[EXT_CODE18] [smallint] NULL,
	[EXT_CODE26_] [int] NOT NULL DEFAULT ((-1)),
	[EXT_CODE27_] [int] NOT NULL DEFAULT ((-1)),
	[EXT_CODE24_] [int] NOT NULL DEFAULT ((-1)),
	[EXT_CODE25_] [int] NOT NULL DEFAULT ((-1)),
	[EXT_CODE22_] [int] NOT NULL DEFAULT ((-1)),
	[EXT_CODE23_] [int] NOT NULL DEFAULT ((-1)),
	[EXT_CODE28_] [int] NOT NULL DEFAULT ((-1)),
	[EXT_CODE20_] [int] NOT NULL DEFAULT ((-1)),
	[EXT_CODE29_] [int] NOT NULL DEFAULT ((-1)),
	[EXT_CODE21_] [int] NOT NULL DEFAULT ((-1)),
	[VCARD] [text] COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
 CONSTRAINT [OOCKE1_ACCOUNT_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_ACCOUNT_]    Script Date: 07/08/2008 12:06:23 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_ACCOUNT_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[ACCOUNT_CATEGORY] [smallint] NULL,
	[ACCOUNT_TYPE] [smallint] NULL,
	[BUSINESS_TYPE] [smallint] NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CONTACT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[PARTNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[CHILDREN_NAMES] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OU_MEMBERSHIP] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[RELIGION] [smallint] NULL,
	[CITIZENSHIP] [smallint] NULL,
	[EXT_CODE21] [smallint] NULL,
	[EXT_CODE22] [smallint] NULL,
	[EXT_CODE20] [smallint] NULL,
	[EXT_CODE25] [smallint] NULL,
	[EXT_CODE26] [smallint] NULL,
	[EXT_CODE23] [smallint] NULL,
	[EXT_CODE24] [smallint] NULL,
	[EXT_CODE29] [smallint] NULL,
	[EXT_CODE28] [smallint] NULL,
	[EXT_CODE27] [smallint] NULL,
 CONSTRAINT [OOCKE1_ACCOUNT__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_ACCOUNTASSIGNMENT]    Script Date: 07/08/2008 12:06:23 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_ACCOUNTASSIGNMENT](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[ACCOUNT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__CREAT__287051E9]  DEFAULT ((-1)),
	[DISCOUNT] [decimal](19, 9) NULL,
	[DISCOUNT_IS_PERCENTAGE] [bit] NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__MODIF__29647622]  DEFAULT ((-1)),
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__OWNER__2A589A5B]  DEFAULT ((-1)),
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[VALID_FROM] [datetime] NULL,
	[VALID_TO] [datetime] NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[MEMBER_ROLE_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__MEMBE__5DC32FC8]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[DISABLED] [bit] NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__CATEG__5EB75401]  DEFAULT ((-1)),
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___5FAB783A]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___609F9C73]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___6193C0AC]  DEFAULT ((-1)),
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__EXTER__6287E4E5]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___637C091E]  DEFAULT ((-1)),
	[USER_CODE3] [smallint] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___64702D57]  DEFAULT ((-1)),
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___65645190]  DEFAULT ((-1)),
	[USER_BOOLEAN1] [bit] NULL,
	[USER_CODE0] [smallint] NULL,
	[USER_BOOLEAN0] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[ACCOUNT_ROLE] [smallint] NULL,
	[QUALITY] [smallint] NULL,
	[FOR_USE_BY_] [int] NOT NULL DEFAULT ((-1)),
 CONSTRAINT [OOCKE1_ACCOUNTASSIGNMENT_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_ACCOUNTASSIGNMENT_]    Script Date: 07/08/2008 12:06:23 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_ACCOUNTASSIGNMENT_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MEMBER_ROLE] [smallint] NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[FOR_USE_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
 CONSTRAINT [OOCKE1_ACCOUNTASSIGNMENT__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_ACTIVITY]    Script Date: 07/08/2008 12:06:23 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_ACTIVITY](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[ACTIVITY_NUMBER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[ACTIVITY_STATE] [smallint] NULL,
	[ACTIVITY_TYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[ACTUAL_END] [datetime] NULL,
	[ACTUAL_START] [datetime] NULL,
	[ASSIGNED_TO] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__CATEG__0DBC5BAD]  DEFAULT ((-1)),
	[CONTRACT_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__CONTR__0EB07FE6]  DEFAULT ((-1)),
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__CREAT__0FA4A41F]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DETAILED_DESCRIPTION] [text] COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DUE_BY] [datetime] NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__EXTER__1098C858]  DEFAULT ((-1)),
	[LAST_TRANSITION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MISC1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MISC2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MISC3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__MODIF__118CEC91]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[ORIGINAL_SCHEDULED_END] [datetime] NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__OWNER__128110CA]  DEFAULT ((-1)),
	[PERCENT_COMPLETE] [smallint] NULL,
	[PRIORITY] [smallint] NULL,
	[PROCESS_STATE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[REP_ACCT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[REP_CONTACT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[SCHEDULED_END] [datetime] NULL,
	[SCHEDULED_START] [datetime] NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[TOTAL_VOTES] [int] NULL,
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___13753503]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___1469593C]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___155D7D75]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___1651A1AE]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___1745C5E7]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___1839EA20]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[CASE_ORIGIN] [smallint] NULL,
	[CASE_TYPE] [smallint] NULL,
	[CUSTOMER_SATISFACTION] [smallint] NULL,
	[REPRODUCIBILITY] [smallint] NULL,
	[SEVERITY] [smallint] NULL,
	[DELIVERY_RECEIPT_REQUESTED] [bit] NULL,
	[MESSAGE_BODY] [ntext] COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[MESSAGE_SUBJECT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[READ_RECEIPT_REQUESTED] [bit] NULL,
	[SEND_DATE] [datetime] NULL,
	[SENDER_EMAIL] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[REFERENCE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[SENDER_MMS] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[SENDER_SMS] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DOCUMENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[SENDER_MAILING] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[TEMPLATE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[SENDER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[COMPETITOR_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__COMPE__192E0E59]  DEFAULT ((-1)),
	[IS_ALL_DAY_EVENT] [bit] NULL,
	[LOCATION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[ICAL] [ntext] COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[LAST_APPLIED_CREATOR] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
 CONSTRAINT [OOCKE1_ACTIVITY_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_ACTIVITY_]    Script Date: 07/08/2008 12:06:23 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_ACTIVITY_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CONTRACT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[COMPETITOR] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
 CONSTRAINT [OOCKE1_ACTIVITY__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_ACTIVITYCREATOR]    Script Date: 07/08/2008 12:06:23 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_ACTIVITYCREATOR](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[ACTIVITY_GROUP_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__ACTIV__28112A8D]  DEFAULT ((-1)),
	[ACTIVITY_TYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[BASE_DATE] [datetime] NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__CATEG__29054EC6]  DEFAULT ((-1)),
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__CREAT__29F972FF]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DUE_BY] [datetime] NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__EXTER__2AED9738]  DEFAULT ((-1)),
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__MODIF__2BE1BB71]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__OWNER__2CD5DFAA]  DEFAULT ((-1)),
	[PRIORITY] [smallint] NULL,
	[RESRC_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__RESRC__2DCA03E3]  DEFAULT ((-1)),
	[SCHEDULED_END] [datetime] NULL,
	[SCHEDULED_START] [datetime] NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___2EBE281C]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___2FB24C55]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___30A6708E]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___319A94C7]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___328EB900]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___3382DD39]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_ACTIVITYCREATOR_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_ACTIVITYCREATOR_]    Script Date: 07/08/2008 12:06:23 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_ACTIVITYCREATOR_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[ACTIVITY_GROUP] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[RESRC] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_ACTIVITYCREATOR__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_ACTIVITYEFFORTESTI]    Script Date: 07/08/2008 12:06:23 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_ACTIVITYEFFORTESTI](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__CREAT__48C812E2]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[ESTIMATE_EFFORT_HOURS] [int] NULL,
	[ESTIMATE_EFFORT_MINUTES] [int] NULL,
	[ESTIMATE_MAX_DEVIATION] [int] NULL,
	[IS_MAIN] [bit] NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__MODIF__49BC371B]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__OWNER__4AB05B54]  DEFAULT ((-1)),
	[SELECTOR_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__SELEC__4BA47F8D]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_ACTIVITYEFFORTESTI_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_ACTIVITYEFFORTESTI_]    Script Date: 07/08/2008 12:06:23 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_ACTIVITYEFFORTESTI_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[SELECTOR] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_ACTIVITYEFFORTESTI__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_ACTIVITYFOLLOWUP]    Script Date: 07/08/2008 12:06:23 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_ACTIVITYFOLLOWUP](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[ACTIVITY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[ASSIGNED_TO] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__CREAT__74BBA3B9]  DEFAULT ((-1)),
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__MODIF__75AFC7F2]  DEFAULT ((-1)),
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__OWNER__76A3EC2B]  DEFAULT ((-1)),
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[TEXT] [text] COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[TITLE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[TRANSITION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[DISABLED] [bit] NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__CATEG__078F4C62]  DEFAULT ((-1)),
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___0883709B]  DEFAULT ((-1)),
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___097794D4]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___0A6BB90D]  DEFAULT ((-1)),
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__EXTER__0B5FDD46]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___0C54017F]  DEFAULT ((-1)),
	[USER_CODE3] [smallint] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___0D4825B8]  DEFAULT ((-1)),
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___0E3C49F1]  DEFAULT ((-1)),
	[USER_BOOLEAN1] [bit] NULL,
	[USER_CODE0] [smallint] NULL,
	[USER_BOOLEAN0] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
 CONSTRAINT [OOCKE1_ACTIVITYFOLLOWUP_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_ACTIVITYFOLLOWUP_]    Script Date: 07/08/2008 12:06:23 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_ACTIVITYFOLLOWUP_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_DATE4] [datetime] NULL,
 CONSTRAINT [OOCKE1_ACTIVITYFOLLOWUP__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_ACTIVITYGROUP]    Script Date: 07/08/2008 12:06:23 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_ACTIVITYGROUP](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__CATEG__74FB9ADB]  DEFAULT ((-1)),
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__CREAT__75EFBF14]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[ESTIMATE_EFFORT_HOURS] [int] NULL,
	[ESTIMATE_EFFORT_MINUTES] [int] NULL,
	[ESTIMATE_MAX_DEVIATION] [int] NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__EXTER__76E3E34D]  DEFAULT ((-1)),
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__MODIF__77D80786]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__OWNER__78CC2BBF]  DEFAULT ((-1)),
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[SUM_ESTIMATE_EFFORT_HOURS] [int] NULL,
	[SUM_ESTIMATE_EFFORT_MINUTES] [int] NULL,
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___79C04FF8]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___7AB47431]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___7BA8986A]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___7C9CBCA3]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___7D90E0DC]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___7E850515]  DEFAULT ((-1)),
	[WELCOME] [text] COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[SCHEDULED_DATE] [datetime] NULL,
	[DEFAULT_CREATOR] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
 CONSTRAINT [OOCKE1_ACTIVITYGROUP_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_ACTIVITYGROUP_]    Script Date: 07/08/2008 12:06:23 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_ACTIVITYGROUP_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_ACTIVITYGROUP__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_ACTIVITYGROUPASS]    Script Date: 07/08/2008 12:06:23 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_ACTIVITYGROUPASS](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[ACTIVITY_GROUP] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__CREAT__4191056E]  DEFAULT ((-1)),
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__MODIF__428529A7]  DEFAULT ((-1)),
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__OWNER__43794DE0]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_ACTIVITYGROUPASS_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_ACTIVITYGROUPASS_]    Script Date: 07/08/2008 12:06:23 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_ACTIVITYGROUPASS_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_ACTIVITYGROUPASS__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_ACTIVITYLINK]    Script Date: 07/08/2008 12:06:23 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_ACTIVITYLINK](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[ACTIVITY_LINK_TYPE] [smallint] NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__CREAT__741C853B]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[LINK_TO] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__MODIF__7510A974]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__OWNER__7604CDAD]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_ACTIVITYLINK_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_ACTIVITYLINK_]    Script Date: 07/08/2008 12:06:23 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_ACTIVITYLINK_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_ACTIVITYLINK__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_ACTIVITYPARTY]    Script Date: 07/08/2008 12:06:23 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_ACTIVITYPARTY](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__CREAT__7AA98739]  DEFAULT ((-1)),
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__MODIF__7B9DAB72]  DEFAULT ((-1)),
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__OWNER__7C91CFAB]  DEFAULT ((-1)),
	[PARTY_TYPE] [smallint] NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[PARTY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
 CONSTRAINT [OOCKE1_ACTIVITYPARTY_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_ACTIVITYPARTY_]    Script Date: 07/08/2008 12:06:23 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_ACTIVITYPARTY_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_ACTIVITYPARTY__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_ACTIVITYPROCACTION]    Script Date: 07/08/2008 12:06:23 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_ACTIVITYPROCACTION](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__CATEG__50142EEF]  DEFAULT ((-1)),
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__CREAT__51085328]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__EXTER__51FC7761]  DEFAULT ((-1)),
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__MODIF__52F09B9A]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__OWNER__53E4BFD3]  DEFAULT ((-1)),
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___54D8E40C]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___55CD0845]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___56C12C7E]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___57B550B7]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___58A974F0]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___599D9929]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACTIVITY_LINK_TYPE] [smallint] NULL,
	[TRANSITION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[TRANSITION_TEXT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[TRANSITION_TITLE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[WF_PROCESS] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[ACTIVITY_CREATOR] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[ACTIVITY_DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[ACTIVITY_NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[RESET_TO_NULL] [bit] NULL,
	[CONTACT_FEATURE_NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[RESOURCE_ORDER] [smallint] NULL,
	[RESOURCE_ROLE] [smallint] NULL,
 CONSTRAINT [OOCKE1_ACTIVITYPROCACTION_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_ACTIVITYPROCACTION_]    Script Date: 07/08/2008 12:06:23 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_ACTIVITYPROCACTION_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_ACTIVITYPROCACTION__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_ACTIVITYPROCESS]    Script Date: 07/08/2008 12:06:23 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_ACTIVITYPROCESS](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__CATEG__223855A6]  DEFAULT ((-1)),
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__CREAT__232C79DF]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__EXTER__24209E18]  DEFAULT ((-1)),
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__MODIF__2514C251]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__OWNER__2608E68A]  DEFAULT ((-1)),
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[START_STATE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___26FD0AC3]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___27F12EFC]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___28E55335]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___29D9776E]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___2ACD9BA7]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___2BC1BFE0]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_ACTIVITYPROCESS_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_ACTIVITYPROCESS_]    Script Date: 07/08/2008 12:06:23 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_ACTIVITYPROCESS_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_ACTIVITYPROCESS__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_ACTIVITYPROCSTATE]    Script Date: 07/08/2008 12:06:23 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_ACTIVITYPROCSTATE](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__CATEG__29EE8607]  DEFAULT ((-1)),
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__CREAT__2AE2AA40]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__EXTER__2BD6CE79]  DEFAULT ((-1)),
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__MODIF__2CCAF2B2]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__OWNER__2DBF16EB]  DEFAULT ((-1)),
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___2EB33B24]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___2FA75F5D]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___309B8396]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___318FA7CF]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___3283CC08]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___3377F041]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_ACTIVITYPROCSTATE_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_ACTIVITYPROCSTATE_]    Script Date: 07/08/2008 12:06:23 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_ACTIVITYPROCSTATE_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_ACTIVITYPROCSTATE__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_ACTIVITYPROCTRANS]    Script Date: 07/08/2008 12:06:23 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_ACTIVITYPROCTRANS](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__CATEG__3A79F38B]  DEFAULT ((-1)),
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__CREAT__3B6E17C4]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__EXTER__3C623BFD]  DEFAULT ((-1)),
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__MODIF__3D566036]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[NEW_ACTIVITY_STATE] [smallint] NULL,
	[NEW_PERCENT_COMPLETE] [smallint] NULL,
	[NEXT_STATE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__OWNER__3E4A846F]  DEFAULT ((-1)),
	[PREV_STATE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___3F3EA8A8]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___4032CCE1]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___4126F11A]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___421B1553]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___430F398C]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___44035DC5]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ERR_STATE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
 CONSTRAINT [OOCKE1_ACTIVITYPROCTRANS_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_ACTIVITYPROCTRANS_]    Script Date: 07/08/2008 12:06:23 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_ACTIVITYPROCTRANS_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_ACTIVITYPROCTRANS__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_ACTIVITYTYPE]    Script Date: 07/08/2008 12:06:23 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_ACTIVITYTYPE](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[ACTIVITY_CLASS] [smallint] NULL,
	[ACTIVITY_CLASS_NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__CATEG__5C2FED11]  DEFAULT ((-1)),
	[CONTROLLED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__CREAT__5D24114A]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__EXTER__5E183583]  DEFAULT ((-1)),
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__MODIF__5F0C59BC]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__OWNER__60007DF5]  DEFAULT ((-1)),
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___60F4A22E]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___61E8C667]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___62DCEAA0]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___63D10ED9]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___64C53312]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___65B9574B]  DEFAULT ((-1)),
	[WORK_BT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_ACTIVITYTYPE_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_ACTIVITYTYPE_]    Script Date: 07/08/2008 12:06:23 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_ACTIVITYTYPE_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_ACTIVITYTYPE__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_ADDITIONALEXTLINK]    Script Date: 07/08/2008 12:06:23 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_ADDITIONALEXTLINK](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AD__CREAT__67378B69]  DEFAULT ((-1)),
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AD__MODIF__682BAFA2]  DEFAULT ((-1)),
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AD__OWNER__691FD3DB]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_ADDITIONALEXTLINK_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_ADDITIONALEXTLINK_]    Script Date: 07/08/2008 12:06:23 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_ADDITIONALEXTLINK_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_ADDITIONALEXTLINK__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_ADDRESS]    Script Date: 07/08/2008 12:06:23 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_ADDRESS](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[BUILDING] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AD__CATEG__6B6743A9]  DEFAULT ((-1)),
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AD__CREAT__6C5B67E2]  DEFAULT ((-1)),
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AD__EXTER__6D4F8C1B]  DEFAULT ((-1)),
	[IS_MAIN] [bit] NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AD__MODIF__6E43B054]  DEFAULT ((-1)),
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AD__OWNER__6F37D48D]  DEFAULT ((-1)),
	[OBJUSAGE_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AD__OBJUS__702BF8C6]  DEFAULT ((-1)),
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AD__USER___71201CFF]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AD__USER___72144138]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AD__USER___73086571]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AD__USER___73FC89AA]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AD__USER___74F0ADE3]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AD__USER___75E4D21C]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[WEB_URL] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[PICTURE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[POSTAL_CITY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[POSTAL_CODE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[POSTAL_COUNTRY] [smallint] NULL,
	[POSTAL_COUNTY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[POSTAL_FREIGHT_TERMS] [smallint] NULL,
	[POSTAL_LATITUDE] [decimal](19, 9) NULL,
	[POSTAL_LONGITUDE] [decimal](19, 9) NULL,
	[POSTAL_STATE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[POSTAL_UTC_OFFSET] [smallint] NULL,
	[AUTOMATIC_PARSING] [bit] NULL,
	[PHONE_CITY_AREA] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[PHONE_COUNTRY_PREFIX] [smallint] NULL,
	[PHONE_EXTENSION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[PHONE_LOCAL_NUMBER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[PHONE_NUMBER_FULL] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[ADDRESS] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EMAIL_ADDRESS] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EMAIL_FORMAT] [smallint] NULL,
	[EMAIL_TYPE] [smallint] NULL,
	[ROOM_NUMBER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[VALID_TO] [datetime] NULL,
	[VALID_FROM] [datetime] NULL,
	[POSTAL_STREET_NUMBER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[POSTAL_ADDRESS_LINE_2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[POSTAL_ADDRESS_LINE_3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[POSTAL_ADDRESS_LINE_0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[POSTAL_ADDRESS_LINE_1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[POSTAL_ADDRESS_LINE_4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[POSTAL_STREET_0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[POSTAL_STREET_1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[POSTAL_STREET_2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[POSTAL_STREET_3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[POSTAL_STREET_4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
 CONSTRAINT [OOCKE1_ADDRESS_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_ADDRESS_]    Script Date: 07/08/2008 12:06:23 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_ADDRESS_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OBJUSAGE] [smallint] NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_ADDRESS__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_ADDRESSGROUP]    Script Date: 07/08/2008 12:06:23 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_ADDRESSGROUP](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AD__CATEG__0D524759]  DEFAULT ((-1)),
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AD__CREAT__0E466B92]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AD__EXTER__0F3A8FCB]  DEFAULT ((-1)),
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AD__MODIF__102EB404]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AD__OWNER__1122D83D]  DEFAULT ((-1)),
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AD__USER___1216FC76]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AD__USER___130B20AF]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AD__USER___13FF44E8]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AD__USER___14F36921]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AD__USER___15E78D5A]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AD__USER___16DBB193]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_ADDRESSGROUP_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_ADDRESSGROUP_]    Script Date: 07/08/2008 12:06:23 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_ADDRESSGROUP_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_ADDRESSGROUP__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_ADDRESSGROUPMEMBER]    Script Date: 07/08/2008 12:06:23 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_ADDRESSGROUPMEMBER](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[ADDRESS] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AD__CATEG__311084E2]  DEFAULT ((-1)),
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AD__CREAT__3204A91B]  DEFAULT ((-1)),
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AD__EXTER__32F8CD54]  DEFAULT ((-1)),
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AD__MODIF__33ECF18D]  DEFAULT ((-1)),
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AD__OWNER__34E115C6]  DEFAULT ((-1)),
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AD__USER___35D539FF]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AD__USER___36C95E38]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AD__USER___37BD8271]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AD__USER___38B1A6AA]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AD__USER___39A5CAE3]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AD__USER___3A99EF1C]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_ADDRESSGROUPMEMBER_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_ADDRESSGROUPMEMBER_]    Script Date: 07/08/2008 12:06:23 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_ADDRESSGROUPMEMBER_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_ADDRESSGROUPMEMBER__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_ALERT]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_ALERT](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[ALERT_STATE] [smallint] NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AL__CREAT__140A31E0]  DEFAULT ((-1)),
	[DESCRIPTION] [ntext] COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[IMPORTANCE] [smallint] NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AL__MODIF__14FE5619]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AL__OWNER__15F27A52]  DEFAULT ((-1)),
	[REFERENCE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_ALERT_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_ALERT_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_ALERT_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_ALERT__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_AUDITENTRY]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_AUDITENTRY](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[AUDITEE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AU__CREAT__0487F72E]  DEFAULT ((-1)),
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AU__MODIF__057C1B67]  DEFAULT ((-1)),
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AU__OWNER__06703FA0]  DEFAULT ((-1)),
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[UNIT_OF_WORK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[VISITED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AU__VISIT__076463D9]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[BEFORE_IMAGE] [text] COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_FEATURES] [nvarchar](300) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
 CONSTRAINT [OOCKE1_AUDITENTRY_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_AUDITENTRY_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_AUDITENTRY_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[VISITED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_AUDITENTRY__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_BOOKING]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_BOOKING](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[BOOKING_DATE] [datetime] NULL,
	[BOOKING_STATUS] [smallint] NULL,
	[BOOKING_TYPE] [smallint] NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BO__CATEG__33E20495]  DEFAULT ((-1)),
	[CB] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CONFIG_TYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BO__CREAT__34D628CE]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BO__EXTER__35CA4D07]  DEFAULT ((-1)),
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BO__MODIF__36BE7140]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[ORIGIN] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BO__OWNER__37B29579]  DEFAULT ((-1)),
	[POSITION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BO__USER___38A6B9B2]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BO__USER___399ADDEB]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BO__USER___3A8F0224]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BO__USER___3B83265D]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BO__USER___3C774A96]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BO__USER___3D6B6ECF]  DEFAULT ((-1)),
	[VALUE_DATE] [datetime] NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[QUANTITY_CREDIT] [decimal](19, 9) NULL,
	[QUANTITY_DEBIT] [decimal](19, 9) NULL,
 CONSTRAINT [OOCKE1_BOOKING_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_BOOKING_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_BOOKING_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_BOOKING__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_BOOKINGPERIOD]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_BOOKINGPERIOD](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BO__CATEG__2455A842]  DEFAULT ((-1)),
	[CLOSING_BOOKING_TYPE_THRESHOLD] [smallint] NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BO__CREAT__2549CC7B]  DEFAULT ((-1)),
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BO__EXTER__263DF0B4]  DEFAULT ((-1)),
	[IS_CLOSED] [bit] NULL,
	[IS_FINAL] [bit] NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BO__MODIF__273214ED]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BO__OWNER__28263926]  DEFAULT ((-1)),
	[PERIOD_ENDS_AT_EXCLUSIVE] [datetime] NULL,
	[PERIOD_STARTS_AT] [datetime] NULL,
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BO__USER___291A5D5F]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BO__USER___2A0E8198]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BO__USER___2B02A5D1]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BO__USER___2BF6CA0A]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BO__USER___2CEAEE43]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BO__USER___2DDF127C]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_BOOKINGPERIOD_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_BOOKINGPERIOD_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_BOOKINGPERIOD_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_BOOKINGPERIOD__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_BOOKINGTEXT]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_BOOKINGTEXT](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BO__CATEG__05670CCE]  DEFAULT ((-1)),
	[CB_NAME_INFIX1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CB_NAME_INFIX2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BO__CREAT__065B3107]  DEFAULT ((-1)),
	[CREDIT_BOOKING_NAME_INFIX] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREDIT_FIRST] [bit] NULL,
	[DEBIT_BOOKING_NAME_INFIX] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BO__EXTER__074F5540]  DEFAULT ((-1)),
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BO__MODIF__08437979]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BO__OWNER__09379DB2]  DEFAULT ((-1)),
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BO__USER___0A2BC1EB]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BO__USER___0B1FE624]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BO__USER___0C140A5D]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BO__USER___0D082E96]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BO__USER___0DFC52CF]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BO__USER___0EF07708]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_BOOKINGTEXT_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_BOOKINGTEXT_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_BOOKINGTEXT_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_BOOKINGTEXT__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_BUDGET]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_BUDGET](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[ACCOUNT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[ACTUAL_VALUE] [decimal](19, 9) NULL,
	[BUDGET_TYPE] [smallint] NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BU__CATEG__75BAB4EA]  DEFAULT ((-1)),
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BU__CREAT__76AED923]  DEFAULT ((-1)),
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[ENDING_AT] [datetime] NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BU__EXTER__77A2FD5C]  DEFAULT ((-1)),
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BU__MODIF__78972195]  DEFAULT ((-1)),
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BU__OWNER__798B45CE]  DEFAULT ((-1)),
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[STARTING_FROM] [datetime] NULL,
	[TARGET_VALUE] [decimal](19, 9) NULL,
	[FULFIL_ORG_UNIT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[UNDERLYING] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BU__USER___7A7F6A07]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BU__USER___7B738E40]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BU__USER___7C67B279]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BU__USER___7D5BD6B2]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BU__USER___7E4FFAEB]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BU__USER___7F441F24]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_BUDGET_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_BUDGET_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_BUDGET_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_BUDGET__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_BUDGETMILESTONE]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_BUDGETMILESTONE](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[ACTUAL_VALUE] [decimal](19, 9) NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BU__CATEG__39BAD97C]  DEFAULT ((-1)),
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BU__CREAT__3AAEFDB5]  DEFAULT ((-1)),
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BU__EXTER__3BA321EE]  DEFAULT ((-1)),
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BU__MODIF__3C974627]  DEFAULT ((-1)),
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BU__OWNER__3D8B6A60]  DEFAULT ((-1)),
	[TARGET_VALUE] [decimal](19, 9) NULL,
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BU__USER___3E7F8E99]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BU__USER___3F73B2D2]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BU__USER___4067D70B]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BU__USER___415BFB44]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BU__USER___42501F7D]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BU__USER___434443B6]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_BUDGETMILESTONE_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_BUDGETMILESTONE_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_BUDGETMILESTONE_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_BUDGETMILESTONE__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_BUILDINGUNIT]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_BUILDINGUNIT](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[BUILDING_UNIT_TYPE] [smallint] NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BU__CATEG__5231818B]  DEFAULT ((-1)),
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BU__CREAT__5325A5C4]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BU__EXTER__5419C9FD]  DEFAULT ((-1)),
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BU__MODIF__550DEE36]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BU__OWNER__5602126F]  DEFAULT ((-1)),
	[PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[PICTURE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[SIZE_IN_CUBIC_METER] [decimal](19, 9) NULL,
	[SIZE_IN_SQUARE_METER] [decimal](19, 9) NULL,
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BU__USER___56F636A8]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BU__USER___57EA5AE1]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BU__USER___58DE7F1A]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BU__USER___59D2A353]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BU__USER___5AC6C78C]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_BU__USER___5BBAEBC5]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[DEPOT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[BUILDING_CMPLX] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
 CONSTRAINT [OOCKE1_BUILDINGUNIT_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_BUILDINGUNIT_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_BUILDINGUNIT_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_BUILDINGUNIT__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_CALCULATIONRULE]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_CALCULATIONRULE](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CA__CATEG__5F56727F]  DEFAULT ((-1)),
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CA__CREAT__604A96B8]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CA__EXTER__613EBAF1]  DEFAULT ((-1)),
	[GET_CONTRACT_AMOUNTS_SCRIPT] [text] COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[GET_POSITION_AMOUNTS_SCRIPT] [text] COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[IS_DEFAULT] [bit] NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CA__MODIF__6232DF2A]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CA__OWNER__63270363]  DEFAULT ((-1)),
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CA__USER___641B279C]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CA__USER___650F4BD5]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CA__USER___6603700E]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CA__USER___66F79447]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CA__USER___67EBB880]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CA__USER___68DFDCB9]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_CALCULATIONRULE_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_CALCULATIONRULE_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_CALCULATIONRULE_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_CALCULATIONRULE__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_CALENDAR]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_CALENDAR](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[BASE_CALENDAR] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CA__CATEG__6EB8B1A0]  DEFAULT ((-1)),
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CA__CREAT__6FACD5D9]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CA__EXTER__70A0FA12]  DEFAULT ((-1)),
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CA__MODIF__71951E4B]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CA__OWNER__72894284]  DEFAULT ((-1)),
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CA__USER___737D66BD]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CA__USER___74718AF6]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CA__USER___7565AF2F]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CA__USER___7659D368]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CA__USER___774DF7A1]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CA__USER___78421BDA]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_CALENDAR_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_CALENDAR_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_CALENDAR_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_CALENDAR__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_CALENDARDAY]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_CALENDARDAY](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CA__CREAT__11E1F24C]  DEFAULT ((-1)),
	[DAY_OF_WEEK] [smallint] NULL,
	[IS_WORKING_DAY] [bit] NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CA__MODIF__12D61685]  DEFAULT ((-1)),
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CA__OWNER__13CA3ABE]  DEFAULT ((-1)),
	[WORK_DURATION_HOURS] [smallint] NULL,
	[WORK_DURATION_MINUTES] [smallint] NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DATE_OF_DAY] [datetime] NULL,
 CONSTRAINT [OOCKE1_CALENDARDAY_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_CALENDARDAY_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_CALENDARDAY_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_CALENDARDAY__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_CHART]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_CHART](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CHART] [image] NULL,
	[CHART_MIME_TYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CHART_NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CH__CREAT__6346FEF4]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CH__MODIF__643B232D]  DEFAULT ((-1)),
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CH__OWNER__652F4766]  DEFAULT ((-1)),
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_CHART_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_CHART_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_CHART_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_CHART__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_CODEVALUECONTAINER]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_CODEVALUECONTAINER](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__CREAT__37687CB6]  DEFAULT ((-1)),
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__MODIF__385CA0EF]  DEFAULT ((-1)),
	[NAME_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__NAME___3950C528]  DEFAULT ((-1)),
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__OWNER__3A44E961]  DEFAULT ((-1)),
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_CODEVALUECONTAINER_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_CODEVALUECONTAINER_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_CODEVALUECONTAINER_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_CODEVALUECONTAINER__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_CODEVALUEENTRY]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_CODEVALUEENTRY](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[ENTRY_VALUE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__OWNER__24F4C6C0]  DEFAULT ((-1)),
	[VALID_FROM] [datetime] NULL,
	[VALID_TO] [datetime] NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[BACK_COLOR] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[COLOR] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__CREAT__25E8EAF9]  DEFAULT ((-1)),
	[FONT_WEIGHT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[ICON_KEY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[LONG_TEXT_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__LONG___26DD0F32]  DEFAULT ((-1)),
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__MODIF__27D1336B]  DEFAULT ((-1)),
	[SHORT_TEXT_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__SHORT__28C557A4]  DEFAULT ((-1)),
 CONSTRAINT [OOCKE1_CODEVALUEENTRY_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_CODEVALUEENTRY_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_CODEVALUEENTRY_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[LONG_TEXT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[SHORT_TEXT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
 CONSTRAINT [OOCKE1_CODEVALUEENTRY__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_COMPETITOR]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_COMPETITOR](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[ACCOUNT_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__ACCOU__1236F807]  DEFAULT ((-1)),
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__CATEG__132B1C40]  DEFAULT ((-1)),
	[COMPETITOR_STATE] [smallint] NULL,
	[CONTACT_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__CONTA__141F4079]  DEFAULT ((-1)),
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__CREAT__151364B2]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__EXTER__160788EB]  DEFAULT ((-1)),
	[KEY_PRODUCT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__MODIF__16FBAD24]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OPPORTUNITIES] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__OWNER__17EFD15D]  DEFAULT ((-1)),
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[STRENGTHS] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[THREATS] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__USER___18E3F596]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__USER___19D819CF]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__USER___1ACC3E08]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__USER___1BC06241]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__USER___1CB4867A]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__USER___1DA8AAB3]  DEFAULT ((-1)),
	[WEAKNESSES] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[WIN_PERCENTAGE] [decimal](19, 9) NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_COMPETITOR_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_COMPETITOR_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_COMPETITOR_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[ACCOUNT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CONTACT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_COMPETITOR__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_COMPONENTCONFIG]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_COMPONENTCONFIG](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__CREAT__326EBD6F]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__MODIF__3362E1A8]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__OWNER__345705E1]  DEFAULT ((-1)),
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_COMPONENTCONFIG_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_COMPONENTCONFIG_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_COMPONENTCONFIG_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_COMPONENTCONFIG__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_COMPOUNDBOOKING]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_COMPOUNDBOOKING](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCEPTED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__ACCEP__12F61216]  DEFAULT ((-1)),
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[BOOKING_DATE] [datetime] NULL,
	[BOOKING_STATUS] [smallint] NULL,
	[BOOKING_TYPE] [smallint] NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__CATEG__13EA364F]  DEFAULT ((-1)),
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__CREAT__14DE5A88]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__EXTER__15D27EC1]  DEFAULT ((-1)),
	[IS_LOCKED] [bit] NULL,
	[LOCK_MODIFIED_AT] [datetime] NULL,
	[LOCKING_REASON] [smallint] NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__MODIF__16C6A2FA]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__OWNER__17BAC733]  DEFAULT ((-1)),
	[REVERSAL_OF] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__USER___18AEEB6C]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__USER___19A30FA5]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__USER___1A9733DE]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__USER___1B8B5817]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__USER___1C7F7C50]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__USER___1D73A089]  DEFAULT ((-1)),
	[WF_PROCESS] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_COMPOUNDBOOKING_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_COMPOUNDBOOKING_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_COMPOUNDBOOKING_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[ACCEPTED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_COMPOUNDBOOKING__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_CONTACTMEMBERSHIP]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_CONTACTMEMBERSHIP](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__CATEG__6934E274]  DEFAULT ((-1)),
	[CONTACT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__CREAT__6A2906AD]  DEFAULT ((-1)),
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EFFECTIVE_ON] [datetime] NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__EXTER__6B1D2AE6]  DEFAULT ((-1)),
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__MODIF__6C114F1F]  DEFAULT ((-1)),
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__OWNER__6D057358]  DEFAULT ((-1)),
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__USER___6DF99791]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__USER___6EEDBBCA]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__USER___6FE1E003]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__USER___70D6043C]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__USER___71CA2875]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__USER___72BE4CAE]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[EMPLOYMENT_CONTRACT_STATE] [smallint] NULL,
	[EMPLOYMENT_POSITION] [smallint] NULL,
 CONSTRAINT [OOCKE1_CONTACTMEMBERSHIP_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_CONTACTMEMBERSHIP_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_CONTACTMEMBERSHIP_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_CONTACTMEMBERSHIP__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_CONTACTREL]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_CONTACTREL](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__CATEG__6149A7E9]  DEFAULT ((-1)),
	[COMPLIMENTARY_CLOSE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__CREAT__623DCC22]  DEFAULT ((-1)),
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__EXTER__6331F05B]  DEFAULT ((-1)),
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__MODIF__64261494]  DEFAULT ((-1)),
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__OWNER__651A38CD]  DEFAULT ((-1)),
	[SALUTATION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[TO_CONTACT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__USER___660E5D06]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__USER___6702813F]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__USER___67F6A578]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__USER___68EAC9B1]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__USER___69DEEDEA]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__USER___6AD31223]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_CONTACTREL_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_CONTACTREL_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_CONTACTREL_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_CONTACTREL__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_CONTACTROLE]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_CONTACTROLE](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[ACCOUNT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__CATEG__6BBC4964]  DEFAULT ((-1)),
	[CONTACT_ROLE] [smallint] NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__CREAT__6CB06D9D]  DEFAULT ((-1)),
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__EXTER__6DA491D6]  DEFAULT ((-1)),
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__MODIF__6E98B60F]  DEFAULT ((-1)),
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__OWNER__6F8CDA48]  DEFAULT ((-1)),
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__USER___7080FE81]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__USER___717522BA]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__USER___726946F3]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__USER___735D6B2C]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__USER___74518F65]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__USER___7545B39E]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_CONTACTROLE_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_CONTACTROLE_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_CONTACTROLE_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_CONTACTROLE__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_CONTRACT]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_CONTRACT](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[ACTIVE_ON] [datetime] NULL,
	[ACTIVITY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__ACTIV__4655BA8B]  DEFAULT ((-1)),
	[BROKER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CALC_RULE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CANCEL_ON] [datetime] NULL,
	[CARRIER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__CATEG__4749DEC4]  DEFAULT ((-1)),
	[CLOSE_PROBABILITY] [smallint] NULL,
	[CLOSED_ON] [datetime] NULL,
	[COMPETITOR_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__COMPE__483E02FD]  DEFAULT ((-1)),
	[CONTACT_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__CONTA__49322736]  DEFAULT ((-1)),
	[CONTRACT_CURRENCY] [smallint] NULL,
	[CONTRACT_LANGUAGE] [smallint] NULL,
	[CONTRACT_NUMBER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CONTRACT_STATE] [smallint] NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__CREAT__4A264B6F]  DEFAULT ((-1)),
	[CUSTOMER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[ESTIMATED_CLOSE_DATE] [datetime] NULL,
	[ESTIMATED_VALUE] [decimal](19, 9) NULL,
	[EXPIRES_ON] [datetime] NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__EXTER__4B1A6FA8]  DEFAULT ((-1)),
	[GIFT_MESSAGE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[INVENTORY_CB_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__INVEN__4C0E93E1]  DEFAULT ((-1)),
	[IS_GIFT] [bit] NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__MODIF__4D02B81A]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[NEXT_STEP] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OPPORTUNITY_RATING] [smallint] NULL,
	[OPPORTUNITY_SOURCE] [smallint] NULL,
	[ORIGIN] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__OWNER__4DF6DC53]  DEFAULT ((-1)),
	[PAYMENT_TERMS] [smallint] NULL,
	[PRICING_DATE] [datetime] NULL,
	[PRICING_RULE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[PRICING_STATE] [smallint] NULL,
	[PRIORITY] [smallint] NULL,
	[QUOTE_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__QUOTE__4EEB008C]  DEFAULT ((-1)),
	[SALES_REP] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[SHIPPING_INSTRUCTIONS] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[SHIPPING_METHOD] [smallint] NULL,
	[SHIPPING_TRACKING_NUMBER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[SUPPLIER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[TOTAL_AMOUNT] [decimal](19, 9) NULL,
	[TOTAL_AMOUNT_INCLUDING_TAX] [decimal](19, 9) NULL,
	[TOTAL_BASE_AMOUNT] [decimal](19, 9) NULL,
	[TOTAL_DISCOUNT_AMOUNT] [decimal](19, 9) NULL,
	[TOTAL_SALES_COMMISSION] [decimal](19, 9) NULL,
	[TOTAL_TAX_AMOUNT] [decimal](19, 9) NULL,
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__USER___4FDF24C5]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__USER___50D348FE]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__USER___51C76D37]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__USER___52BB9170]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__USER___53AFB5A9]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__USER___54A3D9E2]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ESTIMATED_SALES_COMMISSION] [decimal](19, 9) NULL,
	[OPPORTUNITY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__OPPOR__23817F9A]  DEFAULT ((-1)),
	[LEAD_SOURCE] [smallint] NULL,
	[LEAD_RATING] [smallint] NULL,
	[SALES_ORDER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__SALES__6B081C4D]  DEFAULT ((-1)),
	[FREIGHT_TERMS] [smallint] NULL,
	[SUBMIT_STATUS_DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[SUBMIT_DATE] [datetime] NULL,
	[SUBMIT_STATUS] [smallint] NULL,
	[INVOICE_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__INVOI__1039A0FC]  DEFAULT ((-1)),
 CONSTRAINT [OOCKE1_CONTRACT_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_CONTRACT_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_CONTRACT_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[ACTIVITY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[COMPETITOR] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CONTACT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[INVENTORY_CB] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[SALES_ORDER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[QUOTE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OPPORTUNITY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[INVOICE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
 CONSTRAINT [OOCKE1_CONTRACT__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_CONTRACTPOSITION]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_CONTRACTPOSITION](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CALC_RULE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CARRIER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__CATEG__7F6E3C56]  DEFAULT ((-1)),
	[CONTACT_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__CONTA__0062608F]  DEFAULT ((-1)),
	[CONTRACT_POSITION_STATE] [smallint] NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__CREAT__015684C8]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DISCOUNT] [decimal](19, 9) NULL,
	[DISCOUNT_DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DISCOUNT_IS_PERCENTAGE] [bit] NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__EXTER__024AA901]  DEFAULT ((-1)),
	[GIFT_MESSAGE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[IS_GIFT] [bit] NULL,
	[LINE_ITEM_NUMBER] [bigint] NULL,
	[LIST_PRICE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MAX_QUANTITY] [decimal](19, 9) NULL,
	[MIN_MAX_QUANTITY_HANDLING] [smallint] NULL,
	[MIN_QUANTITY] [decimal](19, 9) NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__MODIF__033ECD3A]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OFFSET_QUANTITY] [decimal](19, 9) NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__OWNER__0432F173]  DEFAULT ((-1)),
	[POSITION_NUMBER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[PRICE_LEVEL] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[PRICE_PER_UNIT] [decimal](19, 9) NULL,
	[PRICE_UOM] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[PRICING_DATE] [datetime] NULL,
	[PRICING_RULE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[PRICING_STATE] [smallint] NULL,
	[QUANTITY] [decimal](19, 9) NULL,
	[SALES_COMMISSION] [decimal](19, 9) NULL,
	[SALES_COMMISSION_IS_PERCENTAGE] [bit] NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[SALES_TAX_TYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[SHIPPING_INSTRUCTIONS] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[SHIPPING_METHOD] [smallint] NULL,
	[SHIPPING_TRACKING_NUMBER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[UOM] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__USER___052715AC]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__USER___061B39E5]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__USER___070F5E1E]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__USER___08038257]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__USER___08F7A690]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__USER___09EBCAC9]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[BASED_ON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[PARENT_POSITION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CONFIG_TYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CURRENT_CONFIG] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[PRODUCT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[PRODUCT_SERIAL_NUMBER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__PRODU__0ADFEF02]  DEFAULT ((-1)),
	[CLOSE_PROBABILITY] [smallint] NULL,
	[ESTIMATED_CLOSE_DATE] [datetime] NULL,
 CONSTRAINT [OOCKE1_CONTRACTPOSITION_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_CONTRACTPOSITION_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_CONTRACTPOSITION_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CONTACT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[PRODUCT_SERIAL_NUMBER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
 CONSTRAINT [OOCKE1_CONTRACTPOSITION__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_CONTRACTPOSMOD]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_CONTRACTPOSMOD](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__CREAT__19B81E3E]  DEFAULT ((-1)),
	[INVOLVED] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__MODIF__1AAC4277]  DEFAULT ((-1)),
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CO__OWNER__1BA066B0]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[INVOLVED_PTY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[PROPERTY_VALUE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[QUANTITY] [decimal](19, 9) NULL,
 CONSTRAINT [OOCKE1_CONTRACTPOSMOD_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_CONTRACTPOSMOD_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_CONTRACTPOSMOD_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_CONTRACTPOSMOD__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_CREDITLIMIT]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_CREDITLIMIT](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CR__CATEG__1DFDB06E]  DEFAULT ((-1)),
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CR__CREAT__1EF1D4A7]  DEFAULT ((-1)),
	[CREDIT_CURRENCY] [smallint] NULL,
	[CREDIT_LIMIT_AMOUNT] [decimal](19, 9) NULL,
	[CURRENT_CREDIT_USAGE_AMOUNT] [decimal](19, 9) NULL,
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXPECTED_CREDIT_USAGE_AMOUNT1] [decimal](19, 9) NULL,
	[EXPECTED_CREDIT_USAGE_AMOUNT2] [decimal](19, 9) NULL,
	[EXPECTED_CREDIT_USAGE_AMOUNT3] [decimal](19, 9) NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CR__EXTER__1FE5F8E0]  DEFAULT ((-1)),
	[IS_CREDIT_ON_HOLD] [bit] NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CR__MODIF__20DA1D19]  DEFAULT ((-1)),
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CR__OWNER__21CE4152]  DEFAULT ((-1)),
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CR__USER___22C2658B]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CR__USER___23B689C4]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CR__USER___24AAADFD]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CR__USER___259ED236]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CR__USER___2692F66F]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_CR__USER___27871AA8]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_CREDITLIMIT_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_CREDITLIMIT_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_CREDITLIMIT_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_CREDITLIMIT__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_DELIVERYINFO]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_DELIVERYINFO](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[ACTUAL_DELIVERY_ON] [datetime] NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__CATEG__02208BCF]  DEFAULT ((-1)),
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__CREAT__0314B008]  DEFAULT ((-1)),
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__EXTER__0408D441]  DEFAULT ((-1)),
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__MODIF__04FCF87A]  DEFAULT ((-1)),
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__OWNER__05F11CB3]  DEFAULT ((-1)),
	[QUANTITY_SHIPPED] [decimal](19, 9) NULL,
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__USER___06E540EC]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__USER___07D96525]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__USER___08CD895E]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__USER___09C1AD97]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__USER___0AB5D1D0]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__USER___0BA9F609]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_DELIVERYINFO_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_DELIVERYINFO_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_DELIVERYINFO_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_DELIVERYINFO__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_DELIVERYREQUEST]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_DELIVERYREQUEST](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__CATEG__3C4D2D64]  DEFAULT ((-1)),
	[COMMENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__CREAT__3D41519D]  DEFAULT ((-1)),
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EARLIEST_DELIVERY_AT] [datetime] NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__EXTER__3E3575D6]  DEFAULT ((-1)),
	[LATEST_DELIVERY_AT] [datetime] NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__MODIF__3F299A0F]  DEFAULT ((-1)),
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__OWNER__401DBE48]  DEFAULT ((-1)),
	[STATE] [smallint] NULL,
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__USER___4111E281]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__USER___420606BA]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__USER___42FA2AF3]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__USER___43EE4F2C]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__USER___44E27365]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__USER___45D6979E]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_DELIVERYREQUEST_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_DELIVERYREQUEST_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_DELIVERYREQUEST_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_DELIVERYREQUEST__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_DEPOT]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_DEPOT](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[ALLOW_POSITION_AUTO_CREATE] [bit] NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__CATEG__51284EB9]  DEFAULT ((-1)),
	[CLOSING_DATE] [datetime] NULL,
	[CONTRACT_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__CONTR__521C72F2]  DEFAULT ((-1)),
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__CREAT__5310972B]  DEFAULT ((-1)),
	[DEPOT_GROUP] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DEPOT_NUMBER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DEPOT_TYPE_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__DEPOT__5404BB64]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__EXTER__54F8DF9D]  DEFAULT ((-1)),
	[IS_DEFAULT] [bit] NULL,
	[IS_LOCKED] [bit] NULL,
	[LATEST_REPORT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__MODIF__55ED03D6]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OPENING_DATE] [datetime] NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__OWNER__56E1280F]  DEFAULT ((-1)),
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__USER___57D54C48]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__USER___58C97081]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__USER___59BD94BA]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__USER___5AB1B8F3]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__USER___5BA5DD2C]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__USER___5C9A0165]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_DEPOT_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_DEPOT_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_DEPOT_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CONTRACT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DEPOT_TYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_DEPOT__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_DEPOTENTITY]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_DEPOTENTITY](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__CATEG__44C277D4]  DEFAULT ((-1)),
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__CREAT__45B69C0D]  DEFAULT ((-1)),
	[DEPOT_ENTITY_NUMBER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__EXTER__46AAC046]  DEFAULT ((-1)),
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__MODIF__479EE47F]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__OWNER__489308B8]  DEFAULT ((-1)),
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__USER___49872CF1]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__USER___4A7B512A]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__USER___4B6F7563]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__USER___4C63999C]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__USER___4D57BDD5]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__USER___4E4BE20E]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_DEPOTENTITY_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_DEPOTENTITY_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_DEPOTENTITY_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_DEPOTENTITY__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_DEPOTENTITYREL]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_DEPOTENTITYREL](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__CATEG__7679CEF9]  DEFAULT ((-1)),
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__CREAT__776DF332]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[ENTITY1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[ENTITY2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__EXTER__7862176B]  DEFAULT ((-1)),
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__MODIF__79563BA4]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__OWNER__7A4A5FDD]  DEFAULT ((-1)),
	[RELATIONSHIP_TYPE] [smallint] NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__USER___7B3E8416]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__USER___7C32A84F]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__USER___7D26CC88]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__USER___7E1AF0C1]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__USER___7F0F14FA]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__USER___00033933]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_DEPOTENTITYREL_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_DEPOTENTITYREL_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_DEPOTENTITYREL_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_DEPOTENTITYREL__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_DEPOTGROUP]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_DEPOTGROUP](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__CATEG__6129AC58]  DEFAULT ((-1)),
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__CREAT__621DD091]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__EXTER__6311F4CA]  DEFAULT ((-1)),
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__MODIF__64061903]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__OWNER__64FA3D3C]  DEFAULT ((-1)),
	[PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__USER___65EE6175]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__USER___66E285AE]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__USER___67D6A9E7]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__USER___68CACE20]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__USER___69BEF259]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__USER___6AB31692]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[CONTRACT_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__CONTR__28F1D6DB]  DEFAULT ((-1)),
 CONSTRAINT [OOCKE1_DEPOTGROUP_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_DEPOTGROUP_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_DEPOTGROUP_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[CONTRACT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
 CONSTRAINT [OOCKE1_DEPOTGROUP__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_DEPOTHOLDER]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_DEPOTHOLDER](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__CATEG__2F725533]  DEFAULT ((-1)),
	[CONTRACT_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__CONTR__3066796C]  DEFAULT ((-1)),
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__CREAT__315A9DA5]  DEFAULT ((-1)),
	[DEPOT_HOLDER_NUMBER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__EXTER__324EC1DE]  DEFAULT ((-1)),
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__MODIF__3342E617]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__OWNER__34370A50]  DEFAULT ((-1)),
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__USER___352B2E89]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__USER___361F52C2]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__USER___371376FB]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__USER___38079B34]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__USER___38FBBF6D]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__USER___39EFE3A6]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_DEPOTHOLDER_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_DEPOTHOLDER_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_DEPOTHOLDER_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CONTRACT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_DEPOTHOLDER__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_DEPOTPOSITION]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_DEPOTPOSITION](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__CATEG__1C0A7B04]  DEFAULT ((-1)),
	[CLOSING_DATE] [datetime] NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__CREAT__1CFE9F3D]  DEFAULT ((-1)),
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__EXTER__1DF2C376]  DEFAULT ((-1)),
	[IS_LOCKED] [bit] NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__MODIF__1EE6E7AF]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OPENING_DATE] [datetime] NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__OWNER__1FDB0BE8]  DEFAULT ((-1)),
	[QUALIFIER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__USER___20CF3021]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__USER___21C3545A]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__USER___22B77893]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__USER___23AB9CCC]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__USER___249FC105]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__USER___2593E53E]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[PRODUCT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[BASED_ON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[PARENT_POSITION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
 CONSTRAINT [OOCKE1_DEPOTPOSITION_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_DEPOTPOSITION_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_DEPOTPOSITION_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_DEPOTPOSITION__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_DEPOTREFERENCE]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_DEPOTREFERENCE](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__CREAT__3FC8B88D]  DEFAULT ((-1)),
	[DEPOT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DEPOT_USAGE] [smallint] NULL,
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[HOLDER_QUALIFIES_POSITION] [bit] NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__MODIF__40BCDCC6]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__OWNER__41B100FF]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_DEPOTREFERENCE_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_DEPOTREFERENCE_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_DEPOTREFERENCE_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_DEPOTREFERENCE__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_DEPOTREPORT]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_DEPOTREPORT](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[BOOKING_PERIOD] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__CATEG__360A4429]  DEFAULT ((-1)),
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__CREAT__36FE6862]  DEFAULT ((-1)),
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__EXTER__37F28C9B]  DEFAULT ((-1)),
	[IS_DRAFT] [bit] NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__MODIF__38E6B0D4]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__OWNER__39DAD50D]  DEFAULT ((-1)),
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__USER___3ACEF946]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__USER___3BC31D7F]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__USER___3CB741B8]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__USER___3DAB65F1]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__USER___3E9F8A2A]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__USER___3F93AE63]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[BOOKING_STATUS_THRESHOLD] [smallint] NULL,
 CONSTRAINT [OOCKE1_DEPOTREPORT_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_DEPOTREPORT_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_DEPOTREPORT_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_DEPOTREPORT__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_DEPOTREPORTITEM]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_DEPOTREPORTITEM](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[BALANCE] [decimal](19, 9) NULL,
	[BALANCE_CREDIT] [decimal](19, 9) NULL,
	[BALANCE_DEBIT] [decimal](19, 9) NULL,
	[BOOKING] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__CREAT__2D350706]  DEFAULT ((-1)),
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__MODIF__2E292B3F]  DEFAULT ((-1)),
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__OWNER__2F1D4F78]  DEFAULT ((-1)),
	[POSITION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[POSITION_NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[VALUE_DATE] [datetime] NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[BALANCE_DEBIT_BOP] [decimal](19, 9) NULL,
	[BALANCE_CREDIT_BOP] [decimal](19, 9) NULL,
	[BALANCE_BOP] [decimal](19, 9) NULL,
	[BALANCE_SIMPLE_BOP] [decimal](19, 9) NULL,
	[BALANCE_SIMPLE] [decimal](19, 9) NULL,
 CONSTRAINT [OOCKE1_DEPOTREPORTITEM_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_DEPOTREPORTITEM_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_DEPOTREPORTITEM_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_DEPOTREPORTITEM__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_DEPOTTYPE]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_DEPOTTYPE](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[ALLOW_CREDIT_BOOKINGS] [bit] NULL,
	[ALLOW_DEBIT_BOOKINGS] [bit] NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__CATEG__33ACFA6B]  DEFAULT ((-1)),
	[COMPATIBLE_TO_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__COMPA__34A11EA4]  DEFAULT ((-1)),
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__CREAT__359542DD]  DEFAULT ((-1)),
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__EXTER__36896716]  DEFAULT ((-1)),
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__MODIF__377D8B4F]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__OWNER__3871AF88]  DEFAULT ((-1)),
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__USER___3965D3C1]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__USER___3A59F7FA]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__USER___3B4E1C33]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__USER___3C42406C]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__USER___3D3664A5]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__USER___3E2A88DE]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_DEPOTTYPE_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_DEPOTTYPE_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_DEPOTTYPE_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[COMPATIBLE_TO] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_DEPOTTYPE__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_DESCRIPTION]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_DESCRIPTION](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__CREAT__3FE8B41E]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DETAILED_DESCRIPTION] [text] COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[LANGUAGE] [smallint] NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__MODIF__40DCD857]  DEFAULT ((-1)),
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DE__OWNER__41D0FC90]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_DESCRIPTION_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_DESCRIPTION_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_DESCRIPTION_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_DESCRIPTION__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_DOCUMENT]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_DOCUMENT](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[ACTIVE_ON] [datetime] NULL,
	[ACTIVE_UNTIL] [datetime] NULL,
	[AUTHOR] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CONTENT_LANGUAGE] [smallint] NULL,
	[CONTENT_LENGTH] [int] NULL,
	[CONTENT_TYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DO__CREAT__1360266A]  DEFAULT ((-1)),
	[DOCUMENT_ABSTRACT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DOCUMENT_NUMBER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DOCUMENT_STATE] [smallint] NULL,
	[DOCUMENT_TYPE] [smallint] NULL,
	[FOLDER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DO__FOLDE__14544AA3]  DEFAULT ((-1)),
	[HEAD_REVISION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[KEYWORDS] [nvarchar](4000) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[LITERATURE_TYPE] [smallint] NULL,
	[LOCATION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DO__MODIF__15486EDC]  DEFAULT ((-1)),
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DO__OWNER__163C9315]  DEFAULT ((-1)),
	[SEARCH_TEXT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[TITLE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[CMS_CLASS] [text] COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[CMS_TRANSLATION] [int] NULL,
	[CMS_LANGUAGE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[QUALIFIED_NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[CMS_META] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[CMS_DEFAULT_LANGUAGE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[CMS_TEMPLATE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
 CONSTRAINT [OOCKE1_DOCUMENT_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_DOCUMENT_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_DOCUMENT_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[FOLDER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_DOCUMENT__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_DOCUMENTATTACHMENT]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_DOCUMENTATTACHMENT](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DO__CREAT__62BCEF0F]  DEFAULT ((-1)),
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DOCUMENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DO__MODIF__63B11348]  DEFAULT ((-1)),
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DO__OWNER__64A53781]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_DOCUMENTATTACHMENT_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_DOCUMENTATTACHMENT_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_DOCUMENTATTACHMENT_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_DOCUMENTATTACHMENT__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_DOCUMENTFOLDER]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_DOCUMENTFOLDER](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DO__CREAT__294F6789]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DO__MODIF__2A438BC2]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DO__OWNER__2B37AFFB]  DEFAULT ((-1)),
	[PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_DOCUMENTFOLDER_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_DOCUMENTFOLDER_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_DOCUMENTFOLDER_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_DOCUMENTFOLDER__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_DOCUMENTLINK]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_DOCUMENTLINK](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DO__CREAT__24023935]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[LINK_URI] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DO__MODIF__24F65D6E]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DO__OWNER__25EA81A7]  DEFAULT ((-1)),
	[MODIFIED_AT] [datetime] NOT NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
 CONSTRAINT [OOCKE1_DOCUMENTLINK_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_DOCUMENTLINK_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_DOCUMENTLINK_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
 CONSTRAINT [OOCKE1_DOCUMENTLINK__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_DOCUMENTLOCK]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_DOCUMENTLOCK](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DO__CREAT__2AAF36C4]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[LOCKED_AT] [datetime] NULL,
	[LOCKED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DO__LOCKE__2BA35AFD]  DEFAULT ((-1)),
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DO__MODIF__2C977F36]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_DO__OWNER__2D8BA36F]  DEFAULT ((-1)),
	[MODIFIED_AT] [datetime] NOT NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
 CONSTRAINT [OOCKE1_DOCUMENTLOCK_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_DOCUMENTLOCK_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_DOCUMENTLOCK_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[LOCKED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
 CONSTRAINT [OOCKE1_DOCUMENTLOCK__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_EMAILACCOUNT]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_EMAILACCOUNT](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_EM__CREAT__330DDBED]  DEFAULT ((-1)),
	[E_MAIL_ADDRESS] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[INCOMING_MAIL_SERVICE_NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[IS_DEFAULT] [bit] NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_EM__MODIF__34020026]  DEFAULT ((-1)),
	[OUTGOING_MAIL_SERVICE_NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_EM__OWNER__34F6245F]  DEFAULT ((-1)),
	[REPLY_E_MAIL_ADDRESS] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_EMAILACCOUNT_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_EMAILACCOUNT_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_EMAILACCOUNT_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_EMAILACCOUNT__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_EVENT]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_EVENT](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_EV__CATEG__45CBAAA6]  DEFAULT ((-1)),
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_EV__CREAT__46BFCEDF]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_EV__EXTER__47B3F318]  DEFAULT ((-1)),
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_EV__MODIF__48A81751]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_EV__OWNER__499C3B8A]  DEFAULT ((-1)),
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[SLICE_UOM] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[SLOT_UOM] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_EV__USER___4A905FC3]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_EV__USER___4B8483FC]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_EV__USER___4C78A835]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_EV__USER___4D6CCC6E]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_EV__USER___4E60F0A7]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_EV__USER___4F5514E0]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_EVENT_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_EVENT_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_EVENT_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_EVENT__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_EVENTPART]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_EVENTPART](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_EV__CATEG__523C6E83]  DEFAULT ((-1)),
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_EV__CREAT__533092BC]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_EV__EXTER__5424B6F5]  DEFAULT ((-1)),
	[FILLING] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_EV__MODIF__5518DB2E]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_EV__OWNER__560CFF67]  DEFAULT ((-1)),
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_EV__USER___570123A0]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_EV__USER___57F547D9]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_EV__USER___58E96C12]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_EV__USER___59DD904B]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_EV__USER___5AD1B484]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_EV__USER___5BC5D8BD]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_EVENTPART_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_EVENTPART_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_EVENTPART_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_EVENTPART__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_EVENTSLOT]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_EVENTSLOT](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[BOOKED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_EV__CATEG__4BF98548]  DEFAULT ((-1)),
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_EV__CREAT__4CEDA981]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_EV__EXTER__4DE1CDBA]  DEFAULT ((-1)),
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_EV__MODIF__4ED5F1F3]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[NUMBER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_EV__OWNER__4FCA162C]  DEFAULT ((-1)),
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[SOLD] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_EV__USER___50BE3A65]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_EV__USER___51B25E9E]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_EV__USER___52A682D7]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_EV__USER___539AA710]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_EV__USER___548ECB49]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_EV__USER___5582EF82]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_EVENTSLOT_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_EVENTSLOT_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_EVENTSLOT_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_EVENTSLOT__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_EXPORTPROFILE]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_EXPORTPROFILE](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_EX__CREAT__351881F5]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[MIME_TYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_EX__MODIF__360CA62E]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_EX__OWNER__3700CA67]  DEFAULT ((-1)),
	[REFERENCE_FILTER] [nvarchar](1024) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[MODIFIED_AT] [datetime] NOT NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[TEMPLATE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[FOR_CLASS_] [int] NOT NULL CONSTRAINT [DF_OOCKE1_EXPORTPROFILE_FOR_CLASS_]  DEFAULT ((-1)),
 CONSTRAINT [OOCKE1_EXPORTPROFILE_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_EXPORTPROFILE_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_EXPORTPROFILE_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[FOR_CLASS] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
 CONSTRAINT [OOCKE1_EXPORTPROFILE__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_FACILITY]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_FACILITY](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_FA__CATEG__16A6A769]  DEFAULT ((-1)),
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_FA__CREAT__179ACBA2]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_FA__EXTER__188EEFDB]  DEFAULT ((-1)),
	[FACILITY_TYPE] [smallint] NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_FA__MODIF__19831414]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_FA__OWNER__1A77384D]  DEFAULT ((-1)),
	[PICTURE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_FA__USER___1B6B5C86]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_FA__USER___1C5F80BF]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_FA__USER___1D53A4F8]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_FA__USER___1E47C931]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_FA__USER___1F3BED6A]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_FA__USER___203011A3]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_FACILITY_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_FACILITY_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_FACILITY_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_FACILITY__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_FILTER]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_FILTER](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__CREAT__3B040370]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__MODIF__3BF827A9]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__OWNER__3CEC4BE2]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[DISABLED] [bit] NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__CATEG__6E83A776]  DEFAULT ((-1)),
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___6F77CBAF]  DEFAULT ((-1)),
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__EXTER__706BEFE8]  DEFAULT ((-1)),
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___71601421]  DEFAULT ((-1)),
	[USER_DATE1] [datetime] NULL,
	[USER_DATE0] [datetime] NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___7254385A]  DEFAULT ((-1)),
	[USER_CODE3] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___73485C93]  DEFAULT ((-1)),
	[USER_CODE2] [smallint] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___743C80CC]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_AC__USER___7530A505]  DEFAULT ((-1)),
	[USER_BOOLEAN2] [bit] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
 CONSTRAINT [OOCKE1_ACTIVITYFILTER_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_FILTER_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_FILTER_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_DATE4] [datetime] NULL,
 CONSTRAINT [OOCKE1_ACTIVITYFILTER__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_FILTERPROPERTY]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_FILTERPROPERTY](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF_OOCKE1_FILTERPROPERTY_CREATED_BY_]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[FILTER_OPERATOR] [smallint] NULL,
	[FILTER_QUANTOR] [smallint] NULL,
	[IS_ACTIVE] [bit] NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF_OOCKE1_FILTERPROPERTY_MODIFIED_BY_]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF_OOCKE1_FILTERPROPERTY_OWNER_]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACTIVITY_TYPE_] [int] NOT NULL CONSTRAINT [DF_OOCKE1_FILTERPROPERTY_ACTIVITY_TYPE_]  DEFAULT ((-1)),
	[ACTIVITY_NUMBER_] [int] NOT NULL CONSTRAINT [DF_OOCKE1_FILTERPROPERTY_ACTIVITY_NUMBER_]  DEFAULT ((-1)),
	[PROCESS_STATE_] [int] NOT NULL CONSTRAINT [DF_OOCKE1_FILTERPROPERTY_PROCESS_STATE_]  DEFAULT ((-1)),
	[ACTIVITY_STATE_] [int] NOT NULL CONSTRAINT [DF_OOCKE1_FILTERPROPERTY_ACTIVITY_STATE_]  DEFAULT ((-1)),
	[DISABLED] [bit] NULL,
	[SCHEDULED_END_] [int] NOT NULL CONSTRAINT [DF_OOCKE1_FILTERPROPERTY_SCHEDULED_END_]  DEFAULT ((-1)),
	[SCHEDULED_START_] [int] NOT NULL CONSTRAINT [DF_OOCKE1_FILTERPROPERTY_SCHEDULED_START_]  DEFAULT ((-1)),
	[ACCOUNT_CATEGORY_] [int] NOT NULL CONSTRAINT [DF_OOCKE1_FILTERPROPERTY_ACCOUNT_CATEGORY_]  DEFAULT ((-1)),
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF_OOCKE1_FILTERPROPERTY_CATEGORY_]  DEFAULT ((-1)),
	[ACCOUNT_TYPE_] [int] NOT NULL CONSTRAINT [DF_OOCKE1_FILTERPROPERTY_ACCOUNT_TYPE_]  DEFAULT ((-1)),
	[SALES_TAX_TYPE_] [int] NOT NULL CONSTRAINT [DF_OOCKE1_FILTERPROPERTY_SALES_TAX_TYPE_]  DEFAULT ((-1)),
	[CLASSIFICATION_] [int] NOT NULL CONSTRAINT [DF_OOCKE1_FILTERPROPERTY_CLASSIFICATION_]  DEFAULT ((-1)),
	[PRICE_UOM_] [int] NOT NULL CONSTRAINT [DF_OOCKE1_FILTERPROPERTY_PRICE_UOM_]  DEFAULT ((-1)),
	[BOOLEAN_PARAM_] [int] NOT NULL CONSTRAINT [DF_OOCKE1_FILTERPROPERTY_BOOLEAN_PARAM_]  DEFAULT ((-1)),
	[INTEGER_PARAM_] [int] NOT NULL CONSTRAINT [DF_OOCKE1_FILTERPROPERTY_INTEGER_PARAM_]  DEFAULT ((-1)),
	[STRING_PARAM_] [int] NOT NULL CONSTRAINT [DF_OOCKE1_FILTERPROPERTY_STRING_PARAM_]  DEFAULT ((-1)),
	[DECIMAL_PARAM_] [int] NOT NULL CONSTRAINT [DF_OOCKE1_FILTERPROPERTY_DECIMAL_PARAM_]  DEFAULT ((-1)),
	[DATE_TIME_PARAM_] [int] NOT NULL CONSTRAINT [DF_OOCKE1_FILTERPROPERTY_DATE_TIME_PARAM_]  DEFAULT ((-1)),
	[CLAUSE] [nvarchar](4000) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DATE_PARAM_] [int] NOT NULL CONSTRAINT [DF_OOCKE1_FILTERPROPERTY_DATE_PARAM_]  DEFAULT ((-1)),
	[IS_MAIN] [bit] NULL,
	[ADDRESS_TYPE_] [int] NOT NULL CONSTRAINT [DF_OOCKE1_FILTERPROPERTY_ADDRESS_TYPE_]  DEFAULT ((-1)),
	[OBJUSAGE_] [int] NOT NULL CONSTRAINT [DF_OOCKE1_FILTERPROPERTY_OBJUSAGE_]  DEFAULT ((-1)),
	[CONTACT_] [int] NOT NULL DEFAULT ((-1)),
	[OFFSET_IN_HOURS] [int] NULL,
	[CONTRACT_TYPE_] [int] NOT NULL DEFAULT ((-1)),
	[TOTAL_AMOUNT_] [int] NOT NULL DEFAULT ((-1)),
	[PRIORITY_] [int] NOT NULL DEFAULT ((-1)),
	[CONTRACT_STATE_] [int] NOT NULL DEFAULT ((-1)),
	[SUPPLIER_] [int] NOT NULL DEFAULT ((-1)),
	[SALES_REP_] [int] NOT NULL DEFAULT ((-1)),
	[CUSTOMER_] [int] NOT NULL DEFAULT ((-1)),
 CONSTRAINT [OOCKE1_FILTERPROPERTY_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_FILTERPROPERTY_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_FILTERPROPERTY_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[SCHEDULED_START] [datetime] NULL,
	[SCHEDULED_END] [datetime] NULL,
	[PROCESS_STATE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[ACTIVITY_STATE] [smallint] NULL,
	[ACTIVITY_TYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[ACTIVITY_NUMBER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[SALES_TAX_TYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[PRICE_UOM] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CLASSIFICATION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[ACCOUNT_CATEGORY] [smallint] NULL,
	[ACCOUNT_TYPE] [smallint] NULL,
	[INTEGER_PARAM] [int] NULL,
	[STRING_PARAM] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DECIMAL_PARAM] [decimal](19, 9) NULL,
	[DATE_TIME_PARAM] [datetime] NULL,
	[DATE_PARAM] [datetime] NULL,
	[BOOLEAN_PARAM] [bit] NULL,
	[OBJUSAGE] [smallint] NULL,
	[ADDRESS_TYPE] [smallint] NULL,
	[CONTACT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[PRIORITY] [smallint] NULL,
	[CONTRACT_STATE] [smallint] NULL,
	[CONTRACT_TYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[TOTAL_AMOUNT] [decimal](19, 9) NULL,
	[CUSTOMER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[SUPPLIER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[SALES_REP] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
 CONSTRAINT [OOCKE1_FILTERPROPERTY__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_FORECAST]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_FORECAST](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_FO__CATEG__22D77424]  DEFAULT ((-1)),
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_FO__CREAT__23CB985D]  DEFAULT ((-1)),
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_FO__EXTER__24BFBC96]  DEFAULT ((-1)),
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_FO__MODIF__25B3E0CF]  DEFAULT ((-1)),
	[ORGANIZATIONAL_UNIT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_FO__OWNER__26A80508]  DEFAULT ((-1)),
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_FO__USER___279C2941]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_FO__USER___28904D7A]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_FO__USER___298471B3]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_FO__USER___2A7895EC]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_FO__USER___2B6CBA25]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_FO__USER___2C60DE5E]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_FORECAST_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_FORECAST_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_FORECAST_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_FORECAST__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_FORECASTPERIOD]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_FORECASTPERIOD](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_FO__CATEG__759AB959]  DEFAULT ((-1)),
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_FO__CREAT__768EDD92]  DEFAULT ((-1)),
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_FO__EXTER__778301CB]  DEFAULT ((-1)),
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_FO__MODIF__78772604]  DEFAULT ((-1)),
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_FO__OWNER__796B4A3D]  DEFAULT ((-1)),
	[PERSON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_FO__USER___7A5F6E76]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_FO__USER___7B5392AF]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_FO__USER___7C47B6E8]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_FO__USER___7D3BDB21]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_FO__USER___7E2FFF5A]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_FO__USER___7F242393]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_FORECASTPERIOD_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_FORECASTPERIOD_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_FORECASTPERIOD_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_FORECASTPERIOD__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_INDEXENTRY]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_INDEXENTRY](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_IN__CREAT__02D4B8E6]  DEFAULT ((-1)),
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[INDEXED_OBJECT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[KEYWORDS] [text] COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_IN__MODIF__03C8DD1F]  DEFAULT ((-1)),
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_IN__OWNER__04BD0158]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_INDEXENTRY_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_INDEXENTRY_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_INDEXENTRY_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_INDEXENTRY__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_INVENTORYITEM]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_INVENTORYITEM](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_IN__CATEG__5BB0CA24]  DEFAULT ((-1)),
	[CONFIG_TYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_IN__CREAT__5CA4EE5D]  DEFAULT ((-1)),
	[CURRENT_CONFIG] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_IN__EXTER__5D991296]  DEFAULT ((-1)),
	[INVENTORY_ITEM_TYPE] [smallint] NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_IN__MODIF__5E8D36CF]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_IN__OWNER__5F815B08]  DEFAULT ((-1)),
	[PRODUCT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[PRODUCT_SERIAL_NUMBER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_IN__PRODU__60757F41]  DEFAULT ((-1)),
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_IN__USER___6169A37A]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_IN__USER___625DC7B3]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_IN__USER___6351EBEC]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_IN__USER___64461025]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_IN__USER___653A345E]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_IN__USER___662E5897]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_INVENTORYITEM_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_INVENTORYITEM_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_INVENTORYITEM_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[PRODUCT_SERIAL_NUMBER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_INVENTORYITEM__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_INVOLVEDOBJECT]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_INVOLVEDOBJECT](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[INVOLVED] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[INVOLVED_ROLE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY_] [int] NOT NULL DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL DEFAULT ((-1)),
	[MODIFIED_AT] [datetime] NOT NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_INVOLVEDOBJECT_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_INVOLVEDOBJECT_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_INVOLVEDOBJECT_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_INVOLVEDOBJECT__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_JOIN_ACTCONTAINSWRE]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_JOIN_ACTCONTAINSWRE](
	[WORK_REPORT_ENTRY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACTIVITY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_JOIN_ACTCONTAINSWRE_PK] PRIMARY KEY CLUSTERED 
(
	[WORK_REPORT_ENTRY] ASC,
	[ACTIVITY] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_JOIN_ACTGCONTAINSWRE]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_JOIN_ACTGCONTAINSWRE](
	[WORK_REPORT_ENTRY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACTIVITY_GROUP] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_JOIN_ACTGCONTAINSWRE_PK] PRIMARY KEY CLUSTERED 
(
	[WORK_REPORT_ENTRY] ASC,
	[ACTIVITY_GROUP] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_JOIN_FCPERIODHASLEAD]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_JOIN_FCPERIODHASLEAD](
	[LEAD] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[FORECAST_PERIOD] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_JOIN_FCPERIODHASLEAD_PK] PRIMARY KEY CLUSTERED 
(
	[LEAD] ASC,
	[FORECAST_PERIOD] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_JOIN_FCPERIODHASOPTY]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_JOIN_FCPERIODHASOPTY](
	[OPPORTUNITY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[FORECAST_PERIOD] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_JOIN_FCPERIODHASOPTY_PK] PRIMARY KEY CLUSTERED 
(
	[OPPORTUNITY] ASC,
	[FORECAST_PERIOD] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_JOIN_FCPERIODHASQUOTE]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_JOIN_FCPERIODHASQUOTE](
	[QUOTE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[FORECAST_PERIOD] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_JOIN_FCPERIODHASQUOTE_PK] PRIMARY KEY CLUSTERED 
(
	[QUOTE] ASC,
	[FORECAST_PERIOD] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_JOIN_FILTERINCLUDESACCT]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_JOIN_FILTERINCLUDESACCT](
	[FILTERED_ACCOUNT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCOUNT_FILTER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_JOIN_FILTERINCLUDESACCT_PK] PRIMARY KEY CLUSTERED 
(
	[FILTERED_ACCOUNT] ASC,
	[ACCOUNT_FILTER] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_JOIN_FILTERINCLUDESACT]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_JOIN_FILTERINCLUDESACT](
	[FILTERED_ACTIVITY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACTIVITY_FILTER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_JOIN_FILTERINCLUDESACT_PK] PRIMARY KEY CLUSTERED 
(
	[FILTERED_ACTIVITY] ASC,
	[ACTIVITY_FILTER] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_JOIN_FILTERINCLUDESADDR]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_JOIN_FILTERINCLUDESADDR](
	[FILTERED_ADDRESS] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ADDRESS_FILTER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_JOIN_FILTERINCLUDESADDR_PK] PRIMARY KEY CLUSTERED 
(
	[FILTERED_ADDRESS] ASC,
	[ADDRESS_FILTER] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_JOIN_FILTERINCLUDESPROD]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_JOIN_FILTERINCLUDESPROD](
	[FILTERED_PRODUCT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[PRODUCT_FILTER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
 CONSTRAINT [OOCKE1_JOIN_FILTERINCLUDESPROD_PK] PRIMARY KEY CLUSTERED 
(
	[FILTERED_PRODUCT] ASC,
	[PRODUCT_FILTER] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_JOIN_FINDERHASIDXACCT]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_JOIN_FINDERHASIDXACCT](
	[OBJECT_FINDER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[INDEX_ENTRY_ACCOUNT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
 CONSTRAINT [OOCKE1_JOIN_FINDERHASIDXACCT_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_FINDER] ASC,
	[INDEX_ENTRY_ACCOUNT] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_JOIN_FINDERHASIDXACT]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_JOIN_FINDERHASIDXACT](
	[OBJECT_FINDER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[INDEX_ENTRY_ACTIVITY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
 CONSTRAINT [OOCKE1_JOIN_FINDERHASIDXACT_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_FINDER] ASC,
	[INDEX_ENTRY_ACTIVITY] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_JOIN_FINDERHASIDXBLDG]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_JOIN_FINDERHASIDXBLDG](
	[OBJECT_FINDER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[INDEX_ENTRY_BUILDING] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
 CONSTRAINT [OOCKE1_JOIN_FINDERHASIDXBLDG_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_FINDER] ASC,
	[INDEX_ENTRY_BUILDING] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_JOIN_FINDERHASIDXCONTR]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_JOIN_FINDERHASIDXCONTR](
	[OBJECT_FINDER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[INDEX_ENTRY_CONTRACT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
 CONSTRAINT [OOCKE1_JOIN_FINDERHASIDXCONTR_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_FINDER] ASC,
	[INDEX_ENTRY_CONTRACT] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_JOIN_FINDERHASIDXDEP]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_JOIN_FINDERHASIDXDEP](
	[OBJECT_FINDER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[INDEX_ENTRY_DEPOT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
 CONSTRAINT [OOCKE1_JOIN_FINDERHASIDXDEP_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_FINDER] ASC,
	[INDEX_ENTRY_DEPOT] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_JOIN_FINDERHASIDXDOC]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_JOIN_FINDERHASIDXDOC](
	[OBJECT_FINDER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[INDEX_ENTRY_DOCUMENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
 CONSTRAINT [OOCKE1_JOIN_FINDERHASIDXDOC_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_FINDER] ASC,
	[INDEX_ENTRY_DOCUMENT] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_JOIN_FINDERHASIDXPROD]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_JOIN_FINDERHASIDXPROD](
	[OBJECT_FINDER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[INDEX_ENTRY_PRODUCT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
 CONSTRAINT [OOCKE1_JOIN_FINDERHASIDXPROD_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_FINDER] ASC,
	[INDEX_ENTRY_PRODUCT] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_JOIN_FINDERHASSEARCHRES]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_JOIN_FINDERHASSEARCHRES](
	[OBJECT_FINDER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[INDEX_ENTRY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_JOIN_FINDERHASSEARCHRES_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_FINDER] ASC,
	[INDEX_ENTRY] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_JOIN_PLHASASSPLE]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_JOIN_PLHASASSPLE](
	[PRICE_LIST_ENTRY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[PRICE_LEVEL] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_JOIN_PLHASASSPLE_PK] PRIMARY KEY CLUSTERED 
(
	[PRICE_LIST_ENTRY] ASC,
	[PRICE_LEVEL] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_JOIN_RESCONTAINSWRE]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_JOIN_RESCONTAINSWRE](
	[WORK_REPORT_ENTRY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[RESOURCE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_JOIN_RESCONTAINSWRE_PK] PRIMARY KEY CLUSTERED 
(
	[WORK_REPORT_ENTRY] ASC,
	[RESOURCE] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_LINKABLEITEMLINK]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_LINKABLEITEMLINK](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CATEGORY_] [int] NOT NULL DEFAULT ((-1)),
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK_] [int] NOT NULL DEFAULT ((-1)),
	[LINK_TO] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[LINK_TYPE] [smallint] NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY_] [int] NOT NULL DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL DEFAULT ((-1)),
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL DEFAULT ((-1)),
	[VALID_FROM] [datetime] NULL,
	[VALID_TO] [datetime] NULL,
	[MODIFIED_AT] [datetime] NOT NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_LINKABLEITEMLINK_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_LINKABLEITEMLINK_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_LINKABLEITEMLINK_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_LINKABLEITEMLINK__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_MEDIA]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_MEDIA](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_ME__CREAT__1CC99513]  DEFAULT ((-1)),
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_ME__MODIF__1DBDB94C]  DEFAULT ((-1)),
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_ME__OWNER__1EB1DD85]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[MEDIA] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[URI] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CONTENT] [image] NULL,
	[CONTENT_MIME_TYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CONTENT_NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[AUTHOR] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[VERSION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
 CONSTRAINT [OOCKE1_MEDIA_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_MEDIA_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_MEDIA_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_MEDIA__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_MMSSLIDE]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_MMSSLIDE](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_MM__CATEG__0200903E]  DEFAULT ((-1)),
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_MM__CREAT__02F4B477]  DEFAULT ((-1)),
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_MM__EXTER__03E8D8B0]  DEFAULT ((-1)),
	[MEDIA_OBJECT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_MM__MODIF__04DCFCE9]  DEFAULT ((-1)),
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_MM__OWNER__05D12122]  DEFAULT ((-1)),
	[SLIDE_ORDER] [int] NULL,
	[TEXT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_MM__USER___06C5455B]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_MM__USER___07B96994]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_MM__USER___08AD8DCD]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_MM__USER___09A1B206]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_MM__USER___0A95D63F]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_MM__USER___0B89FA78]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_MMSSLIDE_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_MMSSLIDE_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_MMSSLIDE_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_MMSSLIDE__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_MODELELEMENT]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_MODELELEMENT](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[ANNOTATION] [nvarchar](3000) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_MO__CATEG__798058D6]  DEFAULT ((-1)),
	[CONTAINER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_MO__CREAT__7A747D0F]  DEFAULT ((-1)),
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[ELEMENT_ORDER] [int] NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_MO__EXTER__7B68A148]  DEFAULT ((-1)),
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_MO__MODIF__7C5CC581]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_MO__OWNER__7D50E9BA]  DEFAULT ((-1)),
	[QUALIFIED_NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[STEREOTYPE_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_MO__STERE__7E450DF3]  DEFAULT ((-1)),
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_MO__USER___7F39322C]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_MO__USER___002D5665]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_MO__USER___01217A9E]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_MO__USER___02159ED7]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_MO__USER___0309C310]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_MO__USER___03FDE749]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[MAX_LENGTH] [int] NULL,
	[MULTIPLICITY] [smallint] NULL,
	[TYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[UPPER_BOUND] [int] NULL,
	[EXPOSED_END] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[IS_CHANGEABLE] [bit] NULL,
	[REFERENCED_END] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[SCOPE] [smallint] NULL,
	[VISIBILITY] [smallint] NULL,
	[TAG_VALUE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[IS_ABSTRACT] [bit] NULL,
	[SUPERTYPE_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_MO__SUPER__04F20B82]  DEFAULT ((-1)),
	[EXCEPTION_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_MO__EXCEP__05E62FBB]  DEFAULT ((-1)),
	[IS_QUERY] [bit] NULL,
	[SEMANTICS] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CONST_VALUE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[IS_SINGLETON] [bit] NULL,
	[IS_DERIVED] [bit] NULL,
	[IS_CLUSTERED] [bit] NULL,
	[NAMESPACE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[LABEL_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_MO__LABEL__06DA53F4]  DEFAULT ((-1)),
	[DIRECTION] [smallint] NULL,
	[AGGREGATION] [smallint] NULL,
	[IS_NAVIGABLE] [bit] NULL,
	[QUALIFIER_NAME_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_MO__QUALI__07CE782D]  DEFAULT ((-1)),
	[QUALIFIER_TYPE_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_MO__QUALI__08C29C66]  DEFAULT ((-1)),
	[EVALUATION_POLICY] [smallint] NULL,
	[EXPRESSION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[LANGUAGE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
 CONSTRAINT [OOCKE1_MODELELEMENT_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_MODELELEMENT_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_MODELELEMENT_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[STEREOTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[SUPERTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXCEPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[LABEL] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[QUALIFIER_NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[QUALIFIER_TYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
 CONSTRAINT [OOCKE1_MODELELEMENT__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_NOTE]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_NOTE](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_NO__CREAT__5192630D]  DEFAULT ((-1)),
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_NO__MODIF__52868746]  DEFAULT ((-1)),
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_NO__OWNER__537AAB7F]  DEFAULT ((-1)),
	[TEXT] [text] COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[TITLE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_NOTE_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_NOTE_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_NOTE_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_NOTE__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_OBJECTFINDER]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_OBJECTFINDER](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[ALL_WORDS] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[AT_LEAST_ONE_OF_THE_WORDS] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY_] [int] NOT NULL DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL DEFAULT ((-1)),
	[SEARCH_EXPRESSION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[WITHOUT_WORDS] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_AT] [datetime] NOT NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_OBJECTFINDER_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_OBJECTFINDER_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_OBJECTFINDER_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_OBJECTFINDER__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_ORGANIZATION]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_ORGANIZATION](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_OR__CATEG__0FAF9117]  DEFAULT ((-1)),
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_OR__CREAT__10A3B550]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_OR__EXTER__1197D989]  DEFAULT ((-1)),
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_OR__MODIF__128BFDC2]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[ORGANIZATION_STATE] [smallint] NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_OR__OWNER__138021FB]  DEFAULT ((-1)),
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_OR__USER___14744634]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_OR__USER___15686A6D]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_OR__USER___165C8EA6]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_OR__USER___1750B2DF]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_OR__USER___1844D718]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_OR__USER___1938FB51]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_ORGANIZATION_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_ORGANIZATION_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_ORGANIZATION_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_ORGANIZATION__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_ORGANIZATIONALUNIT]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_ORGANIZATIONALUNIT](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_OR__CATEG__6895C3F6]  DEFAULT ((-1)),
	[COST_CENTER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_OR__CREAT__6989E82F]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_OR__EXTER__6A7E0C68]  DEFAULT ((-1)),
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_OR__MODIF__6B7230A1]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[ORGANIZATIONAL_UNIT_STATE] [smallint] NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_OR__OWNER__6C6654DA]  DEFAULT ((-1)),
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_OR__USER___6D5A7913]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_OR__USER___6E4E9D4C]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_OR__USER___6F42C185]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_OR__USER___7036E5BE]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_OR__USER___712B09F7]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_OR__USER___721F2E30]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_ORGANIZATIONALUNIT_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_ORGANIZATIONALUNIT_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_ORGANIZATIONALUNIT_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_ORGANIZATIONALUNIT__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_ORGUNITRELSHIP]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_ORGUNITRELSHIP](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_OR__CATEG__2E141CA6]  DEFAULT ((-1)),
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_OR__CREAT__2F0840DF]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_OR__EXTER__2FFC6518]  DEFAULT ((-1)),
	[FROM_UNIT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_OR__MODIF__30F08951]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[ORG_UNIT_RELATIONSHIP_STATE] [smallint] NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_OR__OWNER__31E4AD8A]  DEFAULT ((-1)),
	[RELATIONSHIP_TYPE] [smallint] NULL,
	[TO_UNIT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_OR__USER___32D8D1C3]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_OR__USER___33CCF5FC]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_OR__USER___34C11A35]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_OR__USER___35B53E6E]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_OR__USER___36A962A7]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_OR__USER___379D86E0]  DEFAULT ((-1)),
	[WEIGHT] [decimal](19, 9) NULL,
	[WEIGHT_IS_PERCENTAGE] [bit] NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_ORGUNITRELSHIP_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_ORGUNITRELSHIP_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_ORGUNITRELSHIP_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_ORGUNITRELSHIP__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_PRICELEVEL]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_PRICELEVEL](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[BASED_ON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__CATEG__4675B61C]  DEFAULT ((-1)),
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__CREAT__4769DA55]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__EXTER__485DFE8E]  DEFAULT ((-1)),
	[IS_FINAL] [bit] NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__MODIF__495222C7]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__OWNER__4A464700]  DEFAULT ((-1)),
	[PAYMENT_METHOD_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__PAYME__4B3A6B39]  DEFAULT ((-1)),
	[PRICE_CURRENCY] [smallint] NULL,
	[PRICE_USAGE_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__PRICE__4C2E8F72]  DEFAULT ((-1)),
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[SHIPPING_METHOD_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__SHIPP__4D22B3AB]  DEFAULT ((-1)),
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__USER___4E16D7E4]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__USER___4F0AFC1D]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__USER___4FFF2056]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__USER___50F3448F]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__USER___51E768C8]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__USER___52DB8D01]  DEFAULT ((-1)),
	[VALID_FROM] [datetime] NULL,
	[VALID_TO] [datetime] NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[PRODUCT_PHASE_KEY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[DEFAULT_OFFSET_VALID_TO_HH] [int] NULL,
	[DEFAULT_OFFSET_VALID_FROM_HH] [int] NULL,
 CONSTRAINT [OOCKE1_PRICELEVEL_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_PRICELEVEL_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_PRICELEVEL_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[PAYMENT_METHOD] [smallint] NULL,
	[PRICE_USAGE] [smallint] NULL,
	[SHIPPING_METHOD] [smallint] NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_PRICELEVEL__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_PRICEMODIFIER]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_PRICEMODIFIER](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__CREAT__4106F589]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__MODIF__41FB19C2]  DEFAULT ((-1)),
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__OWNER__42EF3DFB]  DEFAULT ((-1)),
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[QUANTITY_FROM] [decimal](19, 9) NULL,
	[QUANTITY_TO] [decimal](19, 9) NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[PRICE_MULTIPLIER] [decimal](19, 9) NULL,
	[PRICE_OFFSET] [decimal](19, 9) NULL,
	[ROUNDING_FACTOR] [decimal](19, 9) NULL,
	[DISCOUNT] [decimal](19, 9) NULL,
	[DISCOUNT_IS_PERCENTAGE] [bit] NULL,
 CONSTRAINT [OOCKE1_PRICEMODIFIER_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_PRICEMODIFIER_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_PRICEMODIFIER_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_PRICEMODIFIER__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_PRICINGRULE]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_PRICINGRULE](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__CATEG__766EE201]  DEFAULT ((-1)),
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__CREAT__7763063A]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__EXTER__78572A73]  DEFAULT ((-1)),
	[GET_PRICE_LEVEL_SCRIPT] [text] COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[IS_DEFAULT] [bit] NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__MODIF__794B4EAC]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__OWNER__7A3F72E5]  DEFAULT ((-1)),
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__USER___7B33971E]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__USER___7C27BB57]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__USER___7D1BDF90]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__USER___7E1003C9]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__USER___7F042802]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__USER___7FF84C3B]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_PRICINGRULE_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_PRICINGRULE_]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_PRICINGRULE_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_PRICINGRULE__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_PRODUCT]    Script Date: 07/08/2008 12:06:24 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_PRODUCT](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[ALLOW_MODIFICATION] [bit] NULL,
	[ALLOW_REMOVAL] [bit] NULL,
	[ALTERNATE_PRODUCT_NUMBER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__ALTER__0D875183]  DEFAULT ((-1)),
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__CATEG__0E7B75BC]  DEFAULT ((-1)),
	[CLASSIFICATION_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__CLASS__0F6F99F5]  DEFAULT ((-1)),
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__CREAT__1063BE2E]  DEFAULT ((-1)),
	[DEFAULT_POSITIONS] [int] NULL,
	[DEF_PRICE_LEVEL] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DEFAULT_QUANTITY] [decimal](19, 9) NULL,
	[DEFAULT_UOM] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DETAILED_DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DISCOUNT] [decimal](19, 9) NULL,
	[DISCOUNT_IS_PERCENTAGE] [bit] NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__EXTER__1157E267]  DEFAULT ((-1)),
	[ITEM_NUMBER] [bigint] NULL,
	[MAX_POSITIONS] [int] NULL,
	[MAX_QUANTITY] [decimal](19, 9) NULL,
	[MIN_MAX_QUANTITY_HANDLING] [smallint] NULL,
	[MIN_POSITIONS] [int] NULL,
	[MIN_QUANTITY] [decimal](19, 9) NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__MODIF__124C06A0]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OFFSET_QUANTITY] [decimal](19, 9) NULL,
	[OVERRIDE_PRICE] [bit] NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__OWNER__13402AD9]  DEFAULT ((-1)),
	[PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[PRICE_UOM_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__PRICE__14344F12]  DEFAULT ((-1)),
	[PRICING_RULE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[PRODUCT_NUMBER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[PRODUCT_STATE] [smallint] NULL,
	[PRODUCT_USAGE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[SALES_TAX_TYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__USER___1528734B]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__USER___161C9784]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__USER___1710BBBD]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__USER___1804DFF6]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__USER___18F9042F]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__USER___19ED2868]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[PRODUCT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CURRENT_CONFIG] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CONFIG_TYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[NET_WEIGHT_KILOGRAM] [decimal](19, 9) NULL,
	[IS_STOCK_ITEM] [bit] NULL,
	[PROFILE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[VERSION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[GROSS_WEIGHT_KILOGRAM] [decimal](19, 9) NULL,
	[PRODUCT_DIMENSION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[PICTURE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXPIRES_ON] [datetime] NULL,
	[ACTIVE_ON] [datetime] NULL,
 CONSTRAINT [OOCKE1_PRODUCT_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_PRODUCT_]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_PRODUCT_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[ALTERNATE_PRODUCT_NUMBER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CLASSIFICATION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[PRICE_UOM] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_PRODUCT__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_PRODUCTAPPLICATION]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_PRODUCTAPPLICATION](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__CATEG__53659CE6]  DEFAULT ((-1)),
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__CREAT__5459C11F]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__EXTER__554DE558]  DEFAULT ((-1)),
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__MODIF__56420991]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__OWNER__57362DCA]  DEFAULT ((-1)),
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[UNDERLYING] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__USER___582A5203]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__USER___591E763C]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__USER___5A129A75]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__USER___5B06BEAE]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__USER___5BFAE2E7]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__USER___5CEF0720]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_PRODUCTAPPLICATION_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_PRODUCTAPPLICATION_]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_PRODUCTAPPLICATION_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_PRODUCTAPPLICATION__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_PRODUCTBASEPRICE]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_PRODUCTBASEPRICE](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__CATEG__2A2E7D29]  DEFAULT ((-1)),
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__CREAT__2B22A162]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DISCOUNT] [decimal](19, 9) NULL,
	[DISCOUNT_IS_PERCENTAGE] [bit] NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__EXTER__2C16C59B]  DEFAULT ((-1)),
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__MODIF__2D0AE9D4]  DEFAULT ((-1)),
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__OWNER__2DFF0E0D]  DEFAULT ((-1)),
	[PRICE] [decimal](19, 9) NULL,
	[PRICE_CURRENCY] [smallint] NULL,
	[PRICE_LEVEL_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__PRICE__2EF33246]  DEFAULT ((-1)),
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[QUANTITY_FROM] [decimal](19, 9) NULL,
	[QUANTITY_TO] [decimal](19, 9) NULL,
	[UOM] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OBJUSAGE_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__OBJUS__2FE7567F]  DEFAULT ((-1)),
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__USER___30DB7AB8]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__USER___31CF9EF1]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__USER___32C3C32A]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__USER___33B7E763]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__USER___34AC0B9C]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__USER___35A02FD5]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_PRODUCTBASEPRICE_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_PRODUCTBASEPRICE_]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_PRODUCTBASEPRICE_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[PRICE_LEVEL] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OBJUSAGE] [smallint] NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_PRODUCTBASEPRICE__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_PRODUCTCLASS]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_PRODUCTCLASS](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__CATEG__52FB8892]  DEFAULT ((-1)),
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__CREAT__53EFACCB]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__EXTER__54E3D104]  DEFAULT ((-1)),
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__MODIF__55D7F53D]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__OWNER__56CC1976]  DEFAULT ((-1)),
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__USER___57C03DAF]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__USER___58B461E8]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__USER___59A88621]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__USER___5A9CAA5A]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__USER___5B90CE93]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__USER___5C84F2CC]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_PRODUCTCLASS_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_PRODUCTCLASS_]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_PRODUCTCLASS_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_PRODUCTCLASS__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_PRODUCTCLASSREL]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_PRODUCTCLASSREL](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__CATEG__3F5EA439]  DEFAULT ((-1)),
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__CREAT__4052C872]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__EXTER__4146ECAB]  DEFAULT ((-1)),
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__MODIF__423B10E4]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__OWNER__432F351D]  DEFAULT ((-1)),
	[RELATIONSHIP_TO] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[RELATIONSHIP_TYPE] [smallint] NULL,
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__USER___44235956]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__USER___45177D8F]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__USER___460BA1C8]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__USER___46FFC601]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__USER___47F3EA3A]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__USER___48E80E73]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_PRODUCTCLASSREL_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_PRODUCTCLASSREL_]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_PRODUCTCLASSREL_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_PRODUCTCLASSREL__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_PRODUCTCONFIG]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_PRODUCTCONFIG](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__CREAT__4CCDADF0]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[IS_DEFAULT] [bit] NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__MODIF__4DC1D229]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__OWNER__4EB5F662]  DEFAULT ((-1)),
	[VALID_FROM] [datetime] NULL,
	[VALID_TO] [datetime] NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[CONFIG_TYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
 CONSTRAINT [OOCKE1_PRODUCTCONFIG_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_PRODUCTCONFIG_]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_PRODUCTCONFIG_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_PRODUCTCONFIG__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_PRODUCTCONFTYPESET]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_PRODUCTCONFTYPESET](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__CREAT__7D70E54B]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__MODIF__7E650984]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__OWNER__7F592DBD]  DEFAULT ((-1)),
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_PRODUCTCONFTYPESET_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_PRODUCTCONFTYPESET_]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_PRODUCTCONFTYPESET_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_PRODUCTCONFTYPESET__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_PRODUCTPHASE]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_PRODUCTPHASE](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__CATEG__5DCFB36E]  DEFAULT ((-1)),
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__CREAT__5EC3D7A7]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__EXTER__5FB7FBE0]  DEFAULT ((-1)),
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__MODIF__60AC2019]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__OWNER__61A04452]  DEFAULT ((-1)),
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[PRODUCT_PHASE_KEY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__USER___6294688B]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__USER___63888CC4]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__USER___647CB0FD]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__USER___6570D536]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__USER___6664F96F]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__USER___67591DA8]  DEFAULT ((-1)),
	[VALID_FROM] [datetime] NULL,
	[VALID_TO] [datetime] NULL,
	[MODIFIED_AT] [datetime] NOT NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
 CONSTRAINT [OOCKE1_PRODUCTPHASE_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_PRODUCTPHASE_]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_PRODUCTPHASE_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
 CONSTRAINT [OOCKE1_PRODUCTPHASE__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_PRODUCTREFERENCE]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_PRODUCTREFERENCE](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CONFIG_TYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__CREAT__22585137]  DEFAULT ((-1)),
	[CURRENT_CONFIG] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__MODIF__234C7570]  DEFAULT ((-1)),
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__OWNER__244099A9]  DEFAULT ((-1)),
	[PRODUCT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[PRODUCT_SERIAL_NUMBER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__PRODU__2534BDE2]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_PRODUCTREFERENCE_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_PRODUCTREFERENCE_]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_PRODUCTREFERENCE_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[PRODUCT_SERIAL_NUMBER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_PRODUCTREFERENCE__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_PROPERTY]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_PROPERTY](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__CREAT__6267E954]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DOMAIN] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__MODIF__635C0D8D]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__OWNER__645031C6]  DEFAULT ((-1)),
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[BOOLEAN_VALUE] [bit] NULL,
	[STRING_VALUE] [nvarchar](4000) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DATE_TIME_VALUE] [datetime] NULL,
	[DECIMAL_VALUE] [decimal](19, 9) NULL,
	[URI_VALUE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[REFERENCE_VALUE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DATE_VALUE] [datetime] NULL,
	[INTEGER_VALUE] [int] NULL,
 CONSTRAINT [OOCKE1_PROPERTY_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_PROPERTY_]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_PROPERTY_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_PROPERTY__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_PROPERTYSET]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_PROPERTYSET](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__CREAT__32A3C799]  DEFAULT ((-1)),
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__MODIF__3397EBD2]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_PR__OWNER__348C100B]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_PROPERTYSET_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_PROPERTYSET_]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_PROPERTYSET_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_PROPERTYSET__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_QUICKACCESS]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_QUICKACCESS](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[ACTION_NAME] [nvarchar](400) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[ACTION_PARAM_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_QU__ACTIO__6CE577C7]  DEFAULT ((-1)),
	[ACTION_TYPE] [smallint] NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_QU__CREAT__6DD99C00]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[ICON_KEY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_QU__MODIF__6ECDC039]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_QU__OWNER__6FC1E472]  DEFAULT ((-1)),
	[REFERENCE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_QUICKACCESS_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_QUICKACCESS_]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_QUICKACCESS_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[ACTION_PARAM] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_QUICKACCESS__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_RASARTIFACTCONTEXT]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_RASARTIFACTCONTEXT](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[ASSET_CONTEXT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__CATEG__790135E9]  DEFAULT ((-1)),
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__CREAT__79F55A22]  DEFAULT ((-1)),
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__EXTER__7AE97E5B]  DEFAULT ((-1)),
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__MODIF__7BDDA294]  DEFAULT ((-1)),
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__OWNER__7CD1C6CD]  DEFAULT ((-1)),
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__USER___7DC5EB06]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__USER___7EBA0F3F]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__USER___7FAE3378]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__USER___00A257B1]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__USER___01967BEA]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__USER___028AA023]  DEFAULT ((-1)),
	[VP] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_RASARTIFACTCONTEXT_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_RASARTIFACTCONTEXT_]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_RASARTIFACTCONTEXT_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_RASARTIFACTCONTEXT__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_RASARTIFACTDEP]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_RASARTIFACTDEP](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[ARTIFACT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__CATEG__5FCB73CB]  DEFAULT ((-1)),
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__CREAT__60BF9804]  DEFAULT ((-1)),
	[DEPENDENCY_TYPE] [smallint] NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__EXTER__61B3BC3D]  DEFAULT ((-1)),
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__MODIF__62A7E076]  DEFAULT ((-1)),
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__OWNER__639C04AF]  DEFAULT ((-1)),
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__USER___649028E8]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__USER___65844D21]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__USER___6678715A]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__USER___676C9593]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__USER___6860B9CC]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__USER___6954DE05]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_RASARTIFACTDEP_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_RASARTIFACTDEP_]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_RASARTIFACTDEP_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_RASARTIFACTDEP__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_RASCLASSIFICATIELT]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_RASCLASSIFICATIELT](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__CATEG__06062B4C]  DEFAULT ((-1)),
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__CREAT__06FA4F85]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__EXTER__07EE73BE]  DEFAULT ((-1)),
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__MODIF__08E297F7]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__OWNER__09D6BC30]  DEFAULT ((-1)),
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__USER___0ACAE069]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__USER___0BBF04A2]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__USER___0CB328DB]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__USER___0DA74D14]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__USER___0E9B714D]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__USER___0F8F9586]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[DESC_GROUP_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__DESC___1083B9BF]  DEFAULT ((-1)),
 CONSTRAINT [OOCKE1_RASCLASSIFICATIELT_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_RASCLASSIFICATIELT_]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_RASCLASSIFICATIELT_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[DESC_GROUP] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
 CONSTRAINT [OOCKE1_RASCLASSIFICATIELT__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_RASDESCRIPTOR]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_RASDESCRIPTOR](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[ASSET_CONTEXT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__CATEG__180FCCEE]  DEFAULT ((-1)),
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__CREAT__1903F127]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__EXTER__19F81560]  DEFAULT ((-1)),
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__MODIF__1AEC3999]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__OWNER__1BE05DD2]  DEFAULT ((-1)),
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__USER___1CD4820B]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__USER___1DC8A644]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__USER___1EBCCA7D]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__USER___1FB0EEB6]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__USER___20A512EF]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__USER___21993728]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_RASDESCRIPTOR_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_RASDESCRIPTOR_]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_RASDESCRIPTOR_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_RASDESCRIPTOR__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_RASPROFILE]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_RASPROFILE](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__CATEG__0349BA32]  DEFAULT ((-1)),
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__CREAT__043DDE6B]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__EXTER__053202A4]  DEFAULT ((-1)),
	[ID_HISTORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__MODIF__062626DD]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__OWNER__071A4B16]  DEFAULT ((-1)),
	[PARENT_PROFILE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__USER___080E6F4F]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__USER___09029388]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__USER___09F6B7C1]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__USER___0AEADBFA]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__USER___0BDF0033]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__USER___0CD3246C]  DEFAULT ((-1)),
	[VERSION_MAJOR] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[VERSION_MINOR] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_RASPROFILE_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_RASPROFILE_]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_RASPROFILE_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_RASPROFILE__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_RASSOLUTIONPART]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_RASSOLUTIONPART](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__CATEG__42BA33D1]  DEFAULT ((-1)),
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__CREAT__43AE580A]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__EXTER__44A27C43]  DEFAULT ((-1)),
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__MODIF__4596A07C]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__OWNER__468AC4B5]  DEFAULT ((-1)),
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__USER___477EE8EE]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__USER___48730D27]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__USER___49673160]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__USER___4A5B5599]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__USER___4B4F79D2]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__USER___4C439E0B]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[RELATED_DIAGRAM] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[RELATED_MODEL] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OPERATION_DEF_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__OPERA__4D37C244]  DEFAULT ((-1)),
	[ARTIFACT_TYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DIGEST_NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DIGEST_VALUE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[VERSION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
 CONSTRAINT [OOCKE1_RASSOLUTIONPART_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_RASSOLUTIONPART_]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_RASSOLUTIONPART_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[OPERATION_DEF] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
 CONSTRAINT [OOCKE1_RASSOLUTIONPART__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_RASVARPOINT]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_RASVARPOINT](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[ASSET_CONTEXT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__CATEG__6855CCD4]  DEFAULT ((-1)),
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__CREAT__6949F10D]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__EXTER__6A3E1546]  DEFAULT ((-1)),
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__MODIF__6B32397F]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__OWNER__6C265DB8]  DEFAULT ((-1)),
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__USER___6D1A81F1]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__USER___6E0EA62A]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__USER___6F02CA63]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__USER___6FF6EE9C]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__USER___70EB12D5]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__USER___71DF370E]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_RASVARPOINT_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_RASVARPOINT_]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_RASVARPOINT_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_RASVARPOINT__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_RATING]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_RATING](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__CREAT__4E36D375]  DEFAULT ((-1)),
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__MODIF__4F2AF7AE]  DEFAULT ((-1)),
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RA__OWNER__501F1BE7]  DEFAULT ((-1)),
	[RATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[RATING_LEVEL] [smallint] NULL,
	[RATING_TYPE] [smallint] NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_RATING_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_RATING_]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_RATING_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_RATING__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_RELATEDPRODUCT]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_RELATEDPRODUCT](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RE__CATEG__27BC24D2]  DEFAULT ((-1)),
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RE__CREAT__28B0490B]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RE__EXTER__29A46D44]  DEFAULT ((-1)),
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RE__MODIF__2A98917D]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RE__OWNER__2B8CB5B6]  DEFAULT ((-1)),
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[PRODUCT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[RELATIONSHIP_TYPE] [smallint] NULL,
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RE__USER___2C80D9EF]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RE__USER___2D74FE28]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RE__USER___2E692261]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RE__USER___2F5D469A]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RE__USER___30516AD3]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RE__USER___31458F0C]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_RELATEDPRODUCT_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_RELATEDPRODUCT_]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_RELATEDPRODUCT_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_RELATEDPRODUCT__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_RESOURCE]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_RESOURCE](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CALENDAR] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RE__CATEG__67B6AE56]  DEFAULT ((-1)),
	[CONTACT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RE__CREAT__68AAD28F]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RE__EXTER__699EF6C8]  DEFAULT ((-1)),
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RE__MODIF__6A931B01]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OVERTIME_RATE] [decimal](19, 9) NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RE__OWNER__6B873F3A]  DEFAULT ((-1)),
	[RATE_CURRENCY] [smallint] NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[STANDARD_RATE] [decimal](19, 9) NULL,
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RE__USER___6C7B6373]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RE__USER___6D6F87AC]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RE__USER___6E63ABE5]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RE__USER___6F57D01E]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RE__USER___704BF457]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RE__USER___71401890]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_RESOURCE_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_RESOURCE_]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_RESOURCE_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_RESOURCE__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_RESOURCEASSIGNMENT]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_RESOURCEASSIGNMENT](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CALENDAR] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RE__CREAT__4D77B966]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RE__MODIF__4E6BDD9F]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RE__OWNER__4F6001D8]  DEFAULT ((-1)),
	[RESRC] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[RESOURCE_ORDER] [smallint] NULL,
	[RESOURCE_ROLE] [smallint] NULL,
	[WORK_DURATION_PERCENTAGE] [smallint] NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_RESOURCEASSIGNMENT_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_RESOURCEASSIGNMENT_]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_RESOURCEASSIGNMENT_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_RESOURCEASSIGNMENT__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_REVENUEREPORT]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_REVENUEREPORT](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RE__CATEG__6969EC9E]  DEFAULT ((-1)),
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RE__CREAT__6A5E10D7]  DEFAULT ((-1)),
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RE__EXTER__6B523510]  DEFAULT ((-1)),
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RE__MODIF__6C465949]  DEFAULT ((-1)),
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RE__OWNER__6D3A7D82]  DEFAULT ((-1)),
	[REPORT_NUMBER] [smallint] NULL,
	[REPORTED_REVENUE] [decimal](19, 9) NULL,
	[REPORTING_CURRENCY] [smallint] NULL,
	[REPORTING_QUARTER] [smallint] NULL,
	[REPORTING_YEAR] [smallint] NULL,
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RE__USER___6E2EA1BB]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RE__USER___6F22C5F4]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RE__USER___7016EA2D]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RE__USER___710B0E66]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RE__USER___71FF329F]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_RE__USER___72F356D8]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_REVENUEREPORT_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_REVENUEREPORT_]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_REVENUEREPORT_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_REVENUEREPORT__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_SALESTAXTYPE]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_SALESTAXTYPE](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_SA__CATEG__02DFA5DE]  DEFAULT ((-1)),
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_SA__CREAT__03D3CA17]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DETAILED_DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_SA__EXTER__04C7EE50]  DEFAULT ((-1)),
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_SA__MODIF__05BC1289]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_SA__OWNER__06B036C2]  DEFAULT ((-1)),
	[RATE] [decimal](19, 9) NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_SA__USER___07A45AFB]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_SA__USER___08987F34]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_SA__USER___098CA36D]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_SA__USER___0A80C7A6]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_SA__USER___0B74EBDF]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_SA__USER___0C691018]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_SALESTAXTYPE_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_SALESTAXTYPE_]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_SALESTAXTYPE_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_SALESTAXTYPE__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_SEGMENT]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_SEGMENT](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_SE__OWNER__383CA55E]  DEFAULT ((-1)),
 CONSTRAINT [OOCKE1_SEGMENT_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_SEGMENT_]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_SEGMENT_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
 CONSTRAINT [OOCKE1_SEGMENT__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_SIMPLEBOOKING]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_SIMPLEBOOKING](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[BOOKING_DATE] [datetime] NULL,
	[BOOKING_STATUS] [smallint] NULL,
	[BOOKING_TYPE] [smallint] NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_SI__CREAT__7E3C8300]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_SI__MODIF__7F30A739]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[ORIGIN_CONTEXT_PARAMS_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_SI__ORIGI__0024CB72]  DEFAULT ((-1)),
	[ORIGIN_CONTEXT_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_SI__ORIGI__0118EFAB]  DEFAULT ((-1)),
	[ORIGIN_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_SI__OWNER__020D13E4]  DEFAULT ((-1)),
	[POSITION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[QUANTITY] [decimal](19, 9) NULL,
	[QUANTITY_UOM] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[VALUE_DATE] [datetime] NULL,
	[MODIFIED_AT] [datetime] NOT NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
 CONSTRAINT [OOCKE1_SIMPLEBOOKING_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_SIMPLEBOOKING_]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_SIMPLEBOOKING_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[ORIGIN_CONTEXT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[ORIGIN_CONTEXT_PARAMS] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
 CONSTRAINT [OOCKE1_SIMPLEBOOKING__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_SUBSCRIPTION]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_SUBSCRIPTION](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_SU__CREAT__5B50D771]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EVENT_TYPE_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_SU__EVENT__5C44FBAA]  DEFAULT ((-1)),
	[FILTER_NAME0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[FILTER_NAME1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[FILTER_NAME2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[FILTER_NAME3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[FILTER_NAME4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[FILTER_VALUE0_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_SU__FILTE__5D391FE3]  DEFAULT ((-1)),
	[FILTER_VALUE1_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_SU__FILTE__5E2D441C]  DEFAULT ((-1)),
	[FILTER_VALUE2_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_SU__FILTE__5F216855]  DEFAULT ((-1)),
	[FILTER_VALUE3_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_SU__FILTE__60158C8E]  DEFAULT ((-1)),
	[FILTER_VALUE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_SU__FILTE__6109B0C7]  DEFAULT ((-1)),
	[IS_ACTIVE] [bit] NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_SU__MODIF__61FDD500]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_SU__OWNER__62F1F939]  DEFAULT ((-1)),
	[TOPIC] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_SUBSCRIPTION_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_SUBSCRIPTION_]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_SUBSCRIPTION_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EVENT_TYPE] [smallint] NULL,
	[FILTER_VALUE0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[FILTER_VALUE1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[FILTER_VALUE2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[FILTER_VALUE3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[FILTER_VALUE4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_SUBSCRIPTION__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_TOPIC]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_TOPIC](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_TO__CATEG__7EEF1969]  DEFAULT ((-1)),
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_TO__CREAT__7FE33DA2]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_TO__EXTER__00D761DB]  DEFAULT ((-1)),
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_TO__MODIF__01CB8614]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_TO__OWNER__02BFAA4D]  DEFAULT ((-1)),
	[PERFORM_ACTION_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_TO__PERFO__03B3CE86]  DEFAULT ((-1)),
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[TOPIC_PATH_PATTERN] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_TO__USER___04A7F2BF]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_TO__USER___059C16F8]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_TO__USER___06903B31]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_TO__USER___07845F6A]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_TO__USER___087883A3]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_TO__USER___096CA7DC]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_TOPIC_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_TOPIC_]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_TOPIC_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[PERFORM_ACTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_TOPIC__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_UOM]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_UOM](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[BASE_UOM] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_UO__CREAT__2C4BCFC5]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DETAILED_DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[IS_SCHEDULE_BASE_UOM] [bit] NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_UO__MODIF__2D3FF3FE]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_UO__OWNER__2E341837]  DEFAULT ((-1)),
	[QUANTITY] [decimal](19, 9) NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[UOM_SCHEDULE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_UOM_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_UOM_]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_UOM_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_UOM__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_UOMSCHEDULE]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_UOMSCHEDULE](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_UO__CREAT__680BB411]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DETAILED_DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_UO__MODIF__68FFD84A]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_UO__OWNER__69F3FC83]  DEFAULT ((-1)),
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_UOMSCHEDULE_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_UOMSCHEDULE_]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_UOMSCHEDULE_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_UOMSCHEDULE__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_USERHOME]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_USERHOME](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CHART0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CHART1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CHART2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CHART3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CONTACT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_US__CREAT__5C64F73B]  DEFAULT ((-1)),
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_US__MODIF__5D591B74]  DEFAULT ((-1)),
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_US__OWNER__5E4D3FAD]  DEFAULT ((-1)),
	[PRIMARY_GROUP] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[SEND_MAIL_SUBJECT_PREFIX] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[SETTINGS] [text] COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[STORE_SETTINGS_ON_LOGOFF] [bit] NULL,
	[WEB_ACCESS_URL] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_USERHOME_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_USERHOME_]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_USERHOME_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_USERHOME__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_VOTE]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_VOTE](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[ASSIGNED_TO] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_VO__CREAT__7BF2B12D]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_VO__MODIF__7CE6D566]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_VO__OWNER__7DDAF99F]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_VOTE_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_VOTE_]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_VOTE_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_VOTE__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_WFACTIONLOGENTRY]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_WFACTIONLOGENTRY](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CORRELATION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_WF__CREAT__5EA24568]  DEFAULT ((-1)),
	[DESCRIPTION] [text] COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_WF__MODIF__5F9669A1]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_WF__OWNER__608A8DDA]  DEFAULT ((-1)),
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_WFACTIONLOGENTRY_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_WFACTIONLOGENTRY_]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_WFACTIONLOGENTRY_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_WFACTIONLOGENTRY__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_WFPROCESS]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_WFPROCESS](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CATEGORY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_WF__CATEG__5BEFF5EF]  DEFAULT ((-1)),
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_WF__CREAT__5CE41A28]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DISABLED] [bit] NULL,
	[DISABLED_REASON] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_WF__EXTER__5DD83E61]  DEFAULT ((-1)),
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_WF__MODIF__5ECC629A]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_WF__OWNER__5FC086D3]  DEFAULT ((-1)),
	[PRIORITY] [int] NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN0] [bit] NULL,
	[USER_BOOLEAN1] [bit] NULL,
	[USER_BOOLEAN2] [bit] NULL,
	[USER_BOOLEAN3] [bit] NULL,
	[USER_BOOLEAN4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_WF__USER___60B4AB0C]  DEFAULT ((-1)),
	[USER_CODE0] [smallint] NULL,
	[USER_CODE1] [smallint] NULL,
	[USER_CODE2] [smallint] NULL,
	[USER_CODE3] [smallint] NULL,
	[USER_CODE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_WF__USER___61A8CF45]  DEFAULT ((-1)),
	[USER_DATE0] [datetime] NULL,
	[USER_DATE1] [datetime] NULL,
	[USER_DATE2] [datetime] NULL,
	[USER_DATE3] [datetime] NULL,
	[USER_DATE4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_WF__USER___629CF37E]  DEFAULT ((-1)),
	[USER_DATE_TIME0] [datetime] NULL,
	[USER_DATE_TIME1] [datetime] NULL,
	[USER_DATE_TIME2] [datetime] NULL,
	[USER_DATE_TIME3] [datetime] NULL,
	[USER_DATE_TIME4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_WF__USER___639117B7]  DEFAULT ((-1)),
	[USER_NUMBER0] [decimal](19, 9) NULL,
	[USER_NUMBER1] [decimal](19, 9) NULL,
	[USER_NUMBER2] [decimal](19, 9) NULL,
	[USER_NUMBER3] [decimal](19, 9) NULL,
	[USER_NUMBER4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_WF__USER___64853BF0]  DEFAULT ((-1)),
	[USER_STRING0] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING1] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING2] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING3] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_STRING4_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_WF__USER___65796029]  DEFAULT ((-1)),
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IS_SYNCHRONOUS] [bit] NULL,
	[MAX_RETRIES] [int] NULL,
 CONSTRAINT [OOCKE1_WFPROCESS_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_WFPROCESS_]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_WFPROCESS_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CATEGORY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[EXTERNAL_LINK] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[USER_BOOLEAN4] [bit] NULL,
	[USER_CODE4] [smallint] NULL,
	[USER_DATE4] [datetime] NULL,
	[USER_DATE_TIME4] [datetime] NULL,
	[USER_NUMBER4] [decimal](19, 9) NULL,
	[USER_STRING4] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_WFPROCESS__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_WFPROCESSINSTANCE]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_WFPROCESSINSTANCE](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_WF__CREAT__0452ED04]  DEFAULT ((-1)),
	[FAILED] [bit] NULL,
	[LAST_ACTIVITY_ON] [datetime] NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_WF__MODIF__0547113D]  DEFAULT ((-1)),
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_WF__OWNER__063B3576]  DEFAULT ((-1)),
	[PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[PROCESS] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[STARTED_ON] [datetime] NULL,
	[STEP_COUNTER] [int] NULL,
	[TARGET_OBJECT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_WFPROCESSINSTANCE_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_WFPROCESSINSTANCE_]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_WFPROCESSINSTANCE_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_WFPROCESSINSTANCE__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_WORKRECORD]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_WORKRECORD](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACCESS_LEVEL_BROWSE] [smallint] NULL,
	[ACCESS_LEVEL_DELETE] [smallint] NULL,
	[ACCESS_LEVEL_UPDATE] [smallint] NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[BILLABLE_AMOUNT] [decimal](19, 9) NULL,
	[BILLING_CURRENCY] [smallint] NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_WO__CREAT__48B30449]  DEFAULT ((-1)),
	[DEPOT_SELECTOR] [smallint] NULL,
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DURATION_CALCULATION_MODE] [smallint] NULL,
	[DURATION_HOURS] [int] NULL,
	[DURATION_MINUTES] [int] NULL,
	[ENDED_AT] [datetime] NULL,
	[IS_BILLABLE] [bit] NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_WO__MODIF__49A72882]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER_] [int] NOT NULL CONSTRAINT [DF__OOCKE1_WO__OWNER__4A9B4CBB]  DEFAULT ((-1)),
	[PAUSE_DURATION_HOURS] [int] NULL,
	[PAUSE_DURATION_MINUTES] [int] NULL,
	[RATE] [decimal](19, 9) NULL,
	[RATE_TYPE] [smallint] NULL,
	[STARTED_AT] [datetime] NULL,
	[WORK_CB] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_WORKRECORD_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCKE1_WORKRECORD_]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCKE1_WORKRECORD_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[OWNER] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCKE1_WORKRECORD__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCSE1_AUTHENTICATIONCONTEXT]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCSE1_AUTHENTICATIONCONTEXT](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[COMPLETION] [smallint] NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCSE1_AU__CREAT__7001DB94]  DEFAULT ((-1)),
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCSE1_AU__MODIF__70F5FFCD]  DEFAULT ((-1)),
	[REALM] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[SUBJECT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCSE1_AUTHENTICATIONCONTEXT_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCSE1_AUTHENTICATIONCONTEXT_]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCSE1_AUTHENTICATIONCONTEXT_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCSE1_AUTHENTICATIONCONTEXT__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCSE1_CREDENTIAL]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCSE1_CREDENTIAL](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCSE1_CR__CREAT__2529D0EA]  DEFAULT ((-1)),
	[ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[LOCKED] [bit] NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCSE1_CR__MODIF__261DF523]  DEFAULT ((-1)),
	[RESET_CREDENTIAL_] [int] NOT NULL CONSTRAINT [DF__OOCSE1_CR__RESET__2712195C]  DEFAULT ((-1)),
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[SUBJECT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[PASSWD] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[ADDRESS] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[TEMPLATE] [smallint] NULL,
 CONSTRAINT [OOCSE1_CREDENTIAL_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCSE1_CREDENTIAL_]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCSE1_CREDENTIAL_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[RESET_CREDENTIAL] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCSE1_CREDENTIAL__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCSE1_PERMISSION]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCSE1_PERMISSION](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACTION_] [int] NOT NULL CONSTRAINT [DF__OOCSE1_PE__ACTIO__5657182A]  DEFAULT ((-1)),
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCSE1_PE__CREAT__574B3C63]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCSE1_PE__MODIF__583F609C]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[PRIVILEGE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCSE1_PERMISSION_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCSE1_PERMISSION_]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCSE1_PERMISSION_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[ACTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCSE1_PERMISSION__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCSE1_POLICY]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCSE1_POLICY](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCSE1_PO__CREAT__3EE9A2ED]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCSE1_PO__MODIF__3FDDC726]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCSE1_POLICY_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCSE1_POLICY_]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCSE1_POLICY_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCSE1_POLICY__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCSE1_PRINCIPAL]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCSE1_PRINCIPAL](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[AUTH_CREDENTIAL_] [int] NOT NULL CONSTRAINT [DF__OOCSE1_PR__AUTH___535AAFEE]  DEFAULT ((-1)),
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCSE1_PR__CREAT__544ED427]  DEFAULT ((-1)),
	[CREDENTIAL] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DISABLED] [bit] NULL,
	[IS_MEMBER_OF_] [int] NOT NULL CONSTRAINT [DF__OOCSE1_PR__IS_ME__5542F860]  DEFAULT ((-1)),
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCSE1_PR__MODIF__56371C99]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[SUBJECT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[LAST_LOGIN_AT] [datetime] NULL,
	[GRANTED_ROLE_] [int] NOT NULL CONSTRAINT [DF__OOCSE1_PR__GRANT__572B40D2]  DEFAULT ((-1)),
 CONSTRAINT [OOCSE1_PRINCIPAL_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCSE1_PRINCIPAL_]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCSE1_PRINCIPAL_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[AUTH_CREDENTIAL] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[IS_MEMBER_OF] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[GRANTED_ROLE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
 CONSTRAINT [OOCSE1_PRINCIPAL__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCSE1_PRIVILEGE]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCSE1_PRIVILEGE](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[ACTION_] [int] NOT NULL CONSTRAINT [DF__OOCSE1_PR__ACTIO__20651BCD]  DEFAULT ((-1)),
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCSE1_PR__CREAT__21594006]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCSE1_PR__MODIF__224D643F]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCSE1_PRIVILEGE_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCSE1_PRIVILEGE_]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCSE1_PRIVILEGE_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[ACTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCSE1_PRIVILEGE__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCSE1_REALM]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCSE1_REALM](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCSE1_RE__CREAT__78222049]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCSE1_RE__MODIF__79164482]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCSE1_REALM_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCSE1_REALM_]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCSE1_REALM_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCSE1_REALM__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCSE1_ROLE]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCSE1_ROLE](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCSE1_RO__CREAT__5EEC5E2B]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DISABLED] [bit] NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCSE1_RO__MODIF__5FE08264]  DEFAULT ((-1)),
	[NAME] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCSE1_ROLE_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCSE1_ROLE_]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCSE1_ROLE_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCSE1_ROLE__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCSE1_SEGMENT]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCSE1_SEGMENT](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCSE1_SEGMENT_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCSE1_SEGMENT_]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCSE1_SEGMENT_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCSE1_SEGMENT__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCSE1_SUBJECT]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCSE1_SUBJECT](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[CREATED_AT] [datetime] NULL,
	[CREATED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCSE1_SU__CREAT__3B191209]  DEFAULT ((-1)),
	[DESCRIPTION] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_AT] [datetime] NULL,
	[MODIFIED_BY_] [int] NOT NULL CONSTRAINT [DF__OOCSE1_SU__MODIF__3C0D3642]  DEFAULT ((-1)),
	[P$$PARENT] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCSE1_SUBJECT_PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [dbo].[OOCSE1_SUBJECT_]    Script Date: 07/08/2008 12:06:25 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOCSE1_SUBJECT_](
	[OBJECT_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[IDX] [int] NOT NULL,
	[CREATED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[MODIFIED_BY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOCSE1_SUBJECT__PK] PRIMARY KEY CLUSTERED 
(
	[OBJECT_ID] ASC,
	[IDX] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[OOM0BASE_AUTHORITY]    Script Date: 02/18/2008 18:48:37 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOM0BASE_AUTHORITY](
	[AUTHORITY_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOM0BASE_AUTHORITY_PK] PRIMARY KEY CLUSTERED 
(
	[AUTHORITY_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[OOM0BASE_PROVIDER]    Script Date: 02/18/2008 18:48:37 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[OOM0BASE_PROVIDER](
	[PROVIDER_ID] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
	[AUTHORITY] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NULL,
	[DTYPE] [nvarchar](256) COLLATE SQL_Latin1_General_CP1_CS_AS NOT NULL,
 CONSTRAINT [OOM0BASE_PROVIDER_PK] PRIMARY KEY CLUSTERED 
(
	[PROVIDER_ID] ASC
)WITH (PAD_INDEX  = OFF, IGNORE_DUP_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO