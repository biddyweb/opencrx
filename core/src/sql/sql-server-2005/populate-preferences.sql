
DELETE FROM prefs_Preference  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, object__class, name, description, absolute_path, parent, string_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:singleValueAttribute', 0, 'org:openmdx:preferences1:StringPreference', 'singleValueAttribute', NULL, 'PERSISTENCE/singleValueAttribute', 'PERSISTENCE', 'p$$assigned_to_title'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, object__class, name, description, absolute_path, parent, string_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:columnNameFrom', 0, 'org:openmdx:preferences1:StringPreference', 'columnNameFrom', NULL, 'PERSISTENCE/columnNameFrom', 'PERSISTENCE', 'resource'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, object__class, name, description, absolute_path, parent, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:columnNameTo', 0, 'org:openmdx:preferences1:StringPreference', 'columnNameTo', NULL, 'PERSISTENCE/columnNameTo', 'PERSISTENCE', 'resrc'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:columnNameFrom', 1, 'p$$resource_parent'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:columnNameTo', 1, 'p$$resrc_parent'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:columnNameFrom', 2, 'default_price_level'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:columnNameTo', 2, 'def_price_level'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:columnNameFrom', 3, 'p$$default_price_level_parent'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:columnNameTo', 3, 'p$$def_price_level_parent'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:columnNameFrom', 4, 'complementary_product'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:columnNameTo', 4, 'compl_prod'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:columnNameFrom', 5, 'p$$complementary_product_parent'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:columnNameTo', 5, 'p$$compl_prod_parent'
)  
GO

INSERT INTO prefs_Preference
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:columnNameFrom', 6, 'to_be_fulfilled_by_organizational_unit'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:columnNameTo', 6, 'fulfil_org_unit'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:columnNameFrom', 7, 'p$$to_be_fulfilled_by_organizational_unit_parent'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:columnNameTo', 7, 'p$$fulfil_org_unit_parent'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:columnNameFrom', 8, 'organizational_unit'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:columnNameTo', 8, 'org_unit'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:columnNameFrom', 9, 'p$$organizational_unit_parent'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:columnNameTo', 9, 'p$$org_unit_parent'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:columnNameFrom', 10, 'organizational_unit_relationship_state'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:columnNameTo', 10, 'org_unit_relationship_state'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:columnNameFrom', 11, 'cc_group_recipient'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:columnNameTo', 11, 'cc_g_recipient'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:columnNameFrom', 12, 'p$$cc_group_recipient_parent'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:columnNameTo', 12, 'p$$cc_g_recipient_parent'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:columnNameFrom', 13, 'bcc_group_recipient'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:columnNameTo', 13, 'bcc_g_recipient'
) 
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:columnNameFrom', 14, 'p$$bcc_group_recipient_parent'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:columnNameTo', 14, 'p$$bcc_g_recipient_parent'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:columnNameFrom', 15, 'to_group_recipient'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:columnNameTo', 15, 'to_g_recipient'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:columnNameFrom', 16, 'p$$to_group_recipient_parent'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:columnNameTo', 16, 'p$$to_g_recipient_parent'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:columnNameFrom', 17, 'reporting_account'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:columnNameTo', 17, 'rep_acct'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:columnNameFrom', 18, 'p$$reporting_account_parent'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:columnNameTo', 18, 'p$$rep_acct_parent'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:columnNameFrom', 19, 'reporting_contact'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:columnNameTo', 19, 'rep_contact'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:columnNameFrom', 20, 'p$$reporting_contact_parent'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:columnNameTo', 20, 'p$$rep_contact_parent'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:columnNameFrom', 21, 'building_complex'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:columnNameTo', 21, 'building_cmplx'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:columnNameFrom', 22, 'p$$building_complex_parent'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:columnNameTo', 22, 'p$$building_cmplx_parent'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:columnNameFrom', 23, 'involved_contact'
)  
INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:columnNameTo', 23, 'inv_contact'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:columnNameFrom', 24, 'p$$involved_contact_parent'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:columnNameTo', 24, 'p$$inv_contact_parent'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:columnNameFrom', 25, 'usage'
) 
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:columnNameTo', 25, 'objusage'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:columnNameFrom', 26, 'object__class'
) 
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:columnNameTo', 26, 'dtype'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:columnNameFrom', 27, 'p$$object__class'
) 
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:columnNameTo', 27, 'p$$dtype'
)  
GO


INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, object__class, name, description, absolute_path, parent, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 000, 'org:openmdx:preferences1:StringPreference', 'type', NULL, 'PERSISTENCE/type', 'PERSISTENCE', 'xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/organization/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 001, 'xri:@openmdx:org.opencrx.kernel.uom1/provider/:*/segment/:*/uomSchedule/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 002, 'xri:@openmdx:org.opencrx.kernel.uom1/provider/:*/segment/:*/uom/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 003, 'xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/lead/:*/position/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 004, 'xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/lead/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 005, 'xri:@openmdx:org.opencrx.kernel.workflow1/provider/:*/segment/:*/wfProcess/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 006, 'xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/product/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 007, 'xri:@openmdx:org:openmdx:security:authentication1/provider/:*/segment/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 008, 'xri:@openmdx:org:opencrx:kernel:document1/provider/:*/segment/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 009, 'xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/account/:*/revenueReport/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 010, 'xri:@openmdx:org.opencrx.kernel.home1/provider/:*/segment/:*/userHome/:*/subscription/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 011, 'xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activity/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 012, 'xri:@openmdx:org.opencrx.kernel.workflow1/provider/:*/segment/:*/topic/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 013, 'xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activity/:*/emailRecipient/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 014, 'xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activity/:*/mailingRecipient/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 015, 'xri:@openmdx:org.opencrx.kernel.code1/provider/:*/segment/:*/valueContainer/:*/entry/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 016, 'xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/:*/:*/basePrice/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 017, 'xri:@openmdx:org.opencrx.kernel.code1/provider/:*/segment/:*/valueContainer/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 018, 'xri:@openmdx:org.opencrx.kernel.home1/provider/:*/segment/:*/userHome/:*/syncProfile/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 019, 'xri:@openmdx:org.opencrx.kernel.home1/provider/:*/segment/:*/userHome/:*/syncProfile/:*/feed/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 020, 'xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activity/:*/meetingParty/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 021, 'xri:@openmdx:org.opencrx.kernel.document1/provider/:*/segment/:*/document/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 022, 'xri:@openmdx:org.opencrx.kernel.home1/provider/:*/segment/:*/userHome/:*/eMailAccount/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 023, 'xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/account/:*/contactRelationship/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 024, 'xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activity/:*/phoneCallRecipient/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 025, 'xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activity/:*/meetingActivityParty/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 026, 'xri:@openmdx:org.opencrx.kernel.building1/provider/:*/segment/:*/building/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 027, 'xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/account/:*/assignedActivity/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 028, 'xri:@openmdx:org.opencrx.kernel.document1/provider/:*/segment/:*/folder/:*/folderEntry/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 029, 'xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/salesTaxType/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 030, 'xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/priceLevel/:*/assignedAccount/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 031, 'xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/priceLevel/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 032, 'xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/competitor/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 033, 'xri:@openmdx:org.opencrx.kernel.building1/provider/:*/segment/:*/building/:*/buildingUnit/:*'
)  
GO

INSERT INTO prefs_Preference
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 034, 'xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/account/:*/assignedBudget/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 035, 'xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/addressGroup/:*/member/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 036, 'xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activity/:*/taskParty/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 037, 'xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activity/:*/incidentParty/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 038, 'xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/priceLevel/:*/priceModifier/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 039, 'xri:@openmdx:org.opencrx.kernel.home1/provider/:*/segment/:*/userHome/:*/assignedActivity/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 040, 'xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activityTracker/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 041, 'xri:@openmdx:org:opencrx:kernel:building1/provider/:*/segment/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 042, 'xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/organization/:*/ouRelationship/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 043, 'xri:@openmdx:org.opencrx.kernel.forecast1/provider/:*/segment/:*/budget/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 044, 'xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activity/:*/workReportEntry/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 045, 'xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activityTracker/:*/followUp/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 046, 'xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/addressGroup/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 047, 'xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activity/:*/assignedResource/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 048, 'xri:@openmdx:org:opencrx:kernel:code1/provider/:*/segment/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 049, 'xri:@openmdx:org:opencrx:kernel:product1/provider/:*/segment/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 050, 'xri:@openmdx:org:opencrx:kernel:admin1/provider/:*/segment/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 051, 'xri:@openmdx:org:opencrx:kernel:region1/provider/:*/segment/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 052, 'xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/searchIndexEntry/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 053, 'xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/priceListEntry/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 054, 'xri:@openmdx:org.opencrx.kernel.home1/provider/:*/segment/:*/userHome/:*/wfProcessInstance/:*/actionLog/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 055, 'xri:@openmdx:org:opencrx:kernel:reservation1/provider/:*/segment/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 056, 'xri:@openmdx:org.opencrx.kernel.building1/provider/:*/segment/:*/buildingComplex/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 057, 'xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/workReportEntry/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 058, 'xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activityCategory/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 059, 'xri:@openmdx:org:opencrx:kernel:contact1/provider/:*/segment/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 060, 'xri:@openmdx:org.opencrx.kernel.home1/provider/:*/segment/:*/userHome/:*/syncProfile/:*/feed/:*/syncData/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 061, 'xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/account/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 062, 'xri:@openmdx:org.opencrx.kernel.home1/provider/:*/segment/:*/userHome/:*/wfProcessInstance/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 063, 'xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/organization/:*/organizationalUnit/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 064, 'xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activity/:*/effortEstimate/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 065, 'xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/:*/:*/position/:*/productApplication/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 066, 'xri:@openmdx:org:opencrx:kernel:account1/provider/:*/segment/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 067, 'xri:@openmdx:org:opencrx:kernel:model1/provider/:*/segment/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 068, 'xri:@openmdx:org:opencrx:kernel:depot1/provider/:*/segment/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 069, 'xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activity/:*/assignedResource/:*/workRecord/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 070, 'xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activity/:*/vote/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 071, 'xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/:*/:*/filteredActivity/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 072, 'xri:@openmdx::*/provider/:*/segment/:*/:*/:*/additionalExternalLink/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 073, 'xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/additionalExternalLink/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 074, 'xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/:*/:*/additionalExternalLink/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 075, 'xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/:*/:*/:*/:*/additionalExternalLink/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 076, 'xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/accountFilter/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 077, 'xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/accountFilter/:*/accountFilterProperty/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 078, 'xri:@openmdx:org:opencrx:kernel:forecast1/provider/:*/segment/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 079, 'xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/organization/:*/contactMembership/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 080, 'xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/organization/:*/organizationalUnit/:*/contactMembership/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 081, 'xri:@openmdx::*/provider/:*/segment/:*/:*/:*/address/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 082, 'xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/address/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 083, 'xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/:*/:*/address/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 084, 'xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/:*/:*/activityNote/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 085, 'xri:@openmdx:org:opencrx:kernel:workflow1/provider/:*/segment/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 086, 'xri:@openmdx:org:opencrx:kernel:uom1/provider/:*/segment/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 087, 'xri:@openmdx:org:opencrx:kernel:activity1/provider/:*/segment/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 088, 'xri:@openmdx:org:opencrx:kernel:home1/provider/:*/segment/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 089, 'xri:@openmdx:org:opencrx:kernel:contract1/provider/:*/segment/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 090, 'xri:@openmdx:org:opencrx:kernel:addess1/provider/:*/segment/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 091, 'xri:@openmdx:org:openmdx:security:realm1/provider/:*/segment/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 092, 'xri:@openmdx:org:opencrx:security:identity1/provider/:*/segment/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 093, 'xri:@openmdx:org:openmdx:security:authorization1/provider/:*/segment/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 094, 'xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/:*/:*/position/:*/deliveryInformation/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 095, 'xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activityCreator/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 096, 'xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activityFilter/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 097, 'xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activityFilter/:*/filterProperty/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 098, 'xri:@openmdx::*/provider/:*/segment/:*/:*/:*/attachedDocument/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 099, 'xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/attachedDocument/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 100, 'xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/:*/:*/attachedDocument/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 101, 'xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/:*/:*/:*/:*/attachedDocument/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 102, 'xri:@openmdx::*/provider/:*/segment/:*/:*/:*/rating/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 103, 'xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/rating/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 104, 'xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/:*/:*/rating/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 105, 'xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/:*/:*/:*/:*/rating/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 106, 'xri:@openmdx::*/provider/:*/segment/:*/:*/:*/note/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 107, 'xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/note/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 108, 'xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/:*/:*/note/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 109, 'xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/:*/:*/:*/:*/note/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 110, 'xri:@openmdx::*/provider/:*/segment/:*/:*/:*/deliveryRequest/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 111, 'xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/deliveryRequest/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 112, 'xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/opportunity/:*/position/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 113, 'xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activityProcess/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 114, 'xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activityProcess/:*/state/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 115, 'xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activityProcess/:*/transition/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 116, 'xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activityProcess/:*/transition/:*/action/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 117, 'xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activityMilestone/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 118, 'xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/organization/:*/organizationalUnit/:*/creditLimit/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 119, 'xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/organization/:*/creditLimit/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 120, 'xri:@openmdx::*/provider/:*/segment/:*/:*/:*/additionalDescription/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 121, 'xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/additionalDescription/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 122, 'xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/:*/:*/additionalDescription/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 123, 'xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/account/:*/member/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 124, 'xri:@openmdx::*/provider/:*/segment/:*/:*/:*/media/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 125, 'xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/media/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 126, 'xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/:*/:*/media/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 127, 'xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/:*/:*/:*/:*/media/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 128, 'xri:@openmdx:org.opencrx.kernel.home1/provider/:*/segment/:*/userHome/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 129, 'xri:@openmdx:org.opencrx.kernel.home1/provider/:*/segment/:*/userHome/:*/alert/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 130, 'xri:@openmdx:org.opencrx.kernel.home1/provider/:*/segment/:*/userHome/:*/quickAccess/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 131, 'xri:@openmdx:org.opencrx.kernel.home1/provider/:*/segment/:*/userHome/:*/accessHistory/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 132, 'xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activityType/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 133, 'xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/calendar/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 134, 'xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/resource/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 135, 'xri:@openmdx:org.opencrx.kernel.home1/provider/:*/segment/:*/userHome/:*/assignedContract/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 136, 'xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/account/:*/assignedContract/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 137, 'xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/resource/:*/workReportEntry/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 138, 'xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/resource/:*/assignedActivity/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 139, 'xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/quote/:*/position/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 140, 'xri:@openmdx::*/provider/:*/segment/:*/:*/:*/chart/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 141, 'xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/:*/:*/activityFilter/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 142, 'xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/:*/:*/activityFilter/:*/filterProperty/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 143, 'xri:@openmdx::*/provider/:*/segment/:*/:*/:*/productReference/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 144, 'xri:@openmdx::*/provider/:*/segment/:*/:*/:*/property/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 145, 'xri:@openmdx::*/provider/:*/segment/:*/audit/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 146, 'xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/account/:*/product/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 147, 'xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activity/:*/assignedGroup/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 148, 'xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activity/:*/activityLinkTo/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 149, 'xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activity/:*/activityLinkFrom/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 150, 'xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/salesOrder/:*/position/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 151, 'xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activity/:*/followUp/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 152, 'xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/product/:*/productPhase/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 153, 'xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/simpleBooking/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 154, 'xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/calendar/:*/weekDay/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 155, 'xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/calendar/:*/calendarDay/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 156, 'xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/invoice/:*/position/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 157, 'xri:@openmdx:org.opencrx.kernel.model1/provider/:*/segment/:*/element/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 158, 'xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/entity/:*/bookingText/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 159, 'xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/product/:*/definingProfile/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 160, 'xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/product/:*/relatedProduct/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 161, 'xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/product/:*/classificationElement/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 162, 'xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/product/:*/classificationElement/:*/descriptor/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 163, 'xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/product/:*/solutionPart/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 164, 'xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/product/:*/solutionPart/:*/dependency/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 165, 'xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/product/:*/solutionPart/:*/artifactContext/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 166, 'xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/product/:*/solutionPart/:*/vp/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 167, 'xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/entity/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 168, 'xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/entityRelationship/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 169, 'xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/depotType/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 170, 'xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/entity/:*/depotGroup/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 171, 'xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/entity/:*/depotHolder/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 172, 'xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/cb/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 173, 'xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/entity/:*/bookingPeriod/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 174, 'xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/booking/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 175, 'xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/entity/:*/depotHolder/:*/depot/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 176, 'xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/entity/:*/depotHolder/:*/depot/:*/position/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 177, 'xri:@openmdx::*/provider/:*/segment/:*/:*/:*/configuration/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 178, 'xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/property/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 179, 'xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/entity/:*/depotHolder/:*/depot/:*/report/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 180, 'xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/entity/:*/depotHolder/:*/depot/:*/report/:*/itemPosition/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 181, 'xri:@openmdx:org.opencrx.kernel.home1/provider/:*/segment/:*/userHome/:*/exportProfile/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 182, 'xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/opportunity/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 183, 'xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/:*/:*/:*/:*/additionalDescription/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 184, 'xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/:*/:*/:*/:*/:*/:*/additionalDescription/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 185, 'xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/priceLevel/:*/accountFilterProperty/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 186, 'xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/priceLevel/:*/productFilterProperty/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 187, 'xri:@openmdx:org.opencrx.kernel.document1/provider/:*/segment/:*/folder/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 188, 'xri:@openmdx:org.opencrx.kernel.document1/provider/:*/segment/:*/document/:*/revision/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 189, 'xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/configuration/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 190, 'xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/:*/:*/configuration/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 191, 'xri:@openmdx::*/provider/:*/segment/:*/:*/:*/propertySetEntry/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 192, 'xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/propertySetEntry/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 193, 'xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/:*/:*/propertySetEntry/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 194, 'xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/:*/:*/:*/:*/basePrice/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 195, 'xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/account/:*/ouMembership/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 196, 'xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/quote/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 197, 'xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/salesOrder/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 198, 'xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/:*/:*/property/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 199, 'xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/invoice/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 200, 'xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/pricingRule/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 201, 'xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/:*/:*/:*/:*/property/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 202, 'xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/calculationRule/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 203, 'xri:@openmdx::*/provider/:*/segment/:*/:*/:*/depotReference/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 204, 'xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/depotReference/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 205, 'xri:@openmdx::*/provider/:*/segment/:*/:*/:*/propertySet/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 206, 'xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/propertySet/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 207, 'xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/:*/:*/propertySet/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 208, 'xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/:*/:*/:*/:*/propertySet/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 209, 'xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/:*/:*/removedPosition/:*/productApplication/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 210, 'xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/:*/:*/removedPosition/:*/deliveryInformation/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 211, 'xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/configurationTypeSet/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 212, 'xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/configurationTypeSet/:*/configurationType/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 213, 'xri:@openmdx:org.opencrx.kernel.forecast1/provider/:*/segment/:*/budget/:*/customer/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 214, 'xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activityTracker/:*/workReportEntry/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 215, 'xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/contractRole/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 216, 'xri:@openmdx:org.opencrx.kernel.forecast1/provider/:*/segment/:*/budget/:*/position/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 217, 'xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/lead/:*/removedPosition/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 218, 'xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/opportunity/:*/removedPosition/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 219, 'xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/quote/:*/removedPosition/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 220, 'xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/salesOrder/:*/removedPosition/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 221, 'xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/invoice/:*/removedPosition/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 222, 'xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/lead/:*/positionModification/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 223, 'xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/opportunity/:*/positionModification/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 224, 'xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/quote/:*/positionModification/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 225, 'xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/salesOrder/:*/positionModification/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 226, 'xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/invoice/:*/positionModification/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 227, 'xri:@openmdx:org.openmdx.security.realm1/provider/:*/segment/:*/realm/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 228, 'xri:@openmdx:org.openmdx.security.realm1/provider/:*/segment/:*/realm/:*/principal/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 229, 'xri:@openmdx:org.openmdx.security.authorization1/provider/:*/segment/:*/policy/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 230, 'xri:@openmdx:org.openmdx.security.authorization1/provider/:*/segment/:*/policy/:*/role/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1',  'PERSISTENCE:type', 231, 'xri:@openmdx:org.openmdx.security.authorization1/provider/:*/segment/:*/policy/:*/role/:*/permission/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 232, 'xri:@openmdx:org.openmdx.security.authorization1/provider/:*/segment/:*/policy/:*/privilege/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 233, 'xri:@openmdx:org.opencrx.security.identity1/provider/:*/segment/:*/subject/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 234, 'xri:@openmdx:org.openmdx.security.authentication1/provider/:*/segment/:*/credential/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 235, 'xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/entity/:*/depotHolder/:*/depot/:*/report/:*/itemPosition/:*/singleBooking/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 236, 'xri:@openmdx:org.opencrx.kernel.depot1/provider/:*/segment/:*/entity/:*/depotHolder/:*/depot/:*/report/:*/itemPosition/:*/simpleBooking/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 237, 'xri:@openmdx:org.opencrx.kernel.building1/provider/:*/segment/:*/building/:*/assignedAddress/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 238, 'xri:@openmdx:org.opencrx.kernel.admin1/provider/:*/segment/:*/configuration/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 239, 'xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/productClassification/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 240, 'xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/productClassification/:*/relationship/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 241, 'xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/account/:*/accountMembership/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 242, 'xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activityMilestone/:*/workReportEntry/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 243, 'xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activityCategory/:*/workReportEntry/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 244, 'xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/contractFilter/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 245, 'xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/contractFilter/:*/filterProperty/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 246, 'xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/contractFilter/:*/filteredContract/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 247, 'xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/productFilter/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 248, 'xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/productFilter/:*/productFilterProperty/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 249, 'xri:@openmdx::*/provider/:*/segment/:*/:*/:*/documentFolderAssignment/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 250, 'xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/documentFolderAssignment/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 251, 'xri:@openmdx:org.opencrx.kernel.product1/provider/:*/segment/:*/product/:*/assignedAccount/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 252, 'xri:@openmdx::*/provider/:*/segment/:*/indexEntry/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 253, 'xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/:*/:*/documentFolderAssignment/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 254, 'xri:@openmdx::*/provider/:*/segment/:*/:*/:*/:*/:*/:*/:*/:*/:*/documentFolderAssignment/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 255, 'xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/address/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 256, 'xri:@openmdx:org.opencrx.kernel.building1/provider/:*/segment/:*/buildingComplex/:*/facility/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 257, 'xri:@openmdx:org.opencrx.kernel.building1/provider/:*/segment/:*/building/:*/facility/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 258, 'xri:@openmdx:org.opencrx.kernel.building1/provider/:*/segment/:*/building/:*/buildingUnit/:*/facility/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 259, 'xri:@openmdx:org.opencrx.kernel.building1/provider/:*/segment/:*/facility/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 260, 'xri:@openmdx:org.opencrx.kernel.building1/provider/:*/segment/:*/buildingUnit/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 261, 'xri:@openmdx:org.opencrx.kernel.building1/provider/:*/segment/:*/inventoryItem/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 262, 'xri:@openmdx:org.opencrx.kernel.building1/provider/:*/segment/:*/buildingComplex/:*/facility/:*/itemLinkTo/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 263, 'xri:@openmdx:org.opencrx.kernel.building1/provider/:*/segment/:*/building/:*/facility/:*/itemLinkTo/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 264, 'xri:@openmdx:org.opencrx.kernel.building1/provider/:*/segment/:*/building/:*/buildingUnit/:*/facility/:*/itemLinkTo/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 265, 'xri:@openmdx:org.opencrx.kernel.building1/provider/:*/segment/:*/inventoryItem/:*/itemLinkTo/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 266, 'xri:@openmdx:org.opencrx.kernel.building1/provider/:*/segment/:*/buildingComplex/:*/facility/:*/itemLinkFrom/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 267, 'xri:@openmdx:org.opencrx.kernel.building1/provider/:*/segment/:*/building/:*/facility/:*/itemLinkFrom/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 268, 'xri:@openmdx:org.opencrx.kernel.building1/provider/:*/segment/:*/building/:*/buildingUnit/:*/facility/:*/itemLinkFrom/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 269, 'xri:@openmdx:org.opencrx.kernel.building1/provider/:*/segment/:*/inventoryItem/:*/itemLinkFrom/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 270, 'xri:@openmdx:org.opencrx.kernel.building1/provider/:*/segment/:*/inventoryItem/:*/assignedAccount/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 271, 'xri:@openmdx:org.opencrx.kernel.building1/provider/:*/segment/:*/inventoryItem/:*/booking/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 272, 'xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/addressFilter/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 273, 'xri:@openmdx:org.opencrx.kernel.account1/provider/:*/segment/:*/addressFilter/:*/addressFilterProperty/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 274, 'xri:@openmdx:org.opencrx.kernel.building1/provider/:*/segment/:*/buildingComplex/:*/assignedAddress/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 275, 'xri:@openmdx:org.opencrx.kernel.building1/provider/:*/segment/:*/building/:*/buildingUnit/:*/assignedAddress/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 276, 'xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activityTracker/:*/activityCreator/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 277, 'xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activityCategory/:*/activityCreator/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 278, 'xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activityMilestone/:*/activityCreator/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 279, 'xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activityCategory/:*/followUp/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 280, 'xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activityMilestone/:*/followUp/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 281, 'xri:@openmdx:org.opencrx.kernel.document1/provider/:*/segment/:*/document/:*/link/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 282, 'xri:@openmdx:org.opencrx.kernel.document1/provider/:*/segment/:*/document/:*/lock/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 283, 'xri:@openmdx:org.opencrx.kernel.document1/provider/:*/segment/:*/document/:*/attachment/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 284, 'xri:@openmdx:org.opencrx.kernel.home1/provider/:*/segment/:*/userHome/:*/objectFinder/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 285, 'xri:@openmdx:org.opencrx.kernel.activity1/provider/:*/segment/:*/activity/:*/involvedObject/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 286, 'xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/lead/:*/linkTo/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 287, 'xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/opportunity/:*/linkTo/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 288, 'xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/quote/:*/linkTo/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 289, 'xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/salesOrder/:*/linkTo/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 290, 'xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/invoice/:*/linkTo/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 291, 'xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/lead/:*/linkFrom/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 292, 'xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/opportunity/:*/linkFrom/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 293, 'xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/quote/:*/linkFrom/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 294, 'xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/salesOrder/:*/linkFrom/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 295, 'xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/invoice/:*/linkFrom/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 296, 'xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/lead/:*/assignedAccount/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 297, 'xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/opportunity/:*/assignedAccount/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 298, 'xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/quote/:*/assignedAccount/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 299, 'xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/salesOrder/:*/assignedAccount/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 300, 'xri:@openmdx:org.opencrx.kernel.contract1/provider/:*/segment/:*/invoice/:*/assignedAccount/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 301, 'xri:@openmdx:org.opencrx.kernel.forecast1/provider/:*/segment/:*/budget/:*/broker/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 302, 'xri:@openmdx:org.opencrx.kernel.forecast1/provider/:*/segment/:*/budget/:*/salesRep/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:type', 303, 'xri:@openmdx:org.opencrx.kernel.forecast1/provider/:*/segment/:*/budget/:*/position/:*/productFilterProperty/:*'
)  
GO


INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, object__class, name, description, absolute_path, parent, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 000, 'org:openmdx:preferences1:StringPreference', 'typeName', NULL, 'PERSISTENCE/typeName', 'PERSISTENCE', 'organization'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 001, 'uomSchedule'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 002, 'uom'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 003, 'leadPos'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 004, 'lead'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 005, 'wfProcess'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 006, 'product'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 007, 'authentications'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 008, 'docs'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 009, 'revenueReport'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 010, 'subscription'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 011, 'activity'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 012, 'topic'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 013, 'emailRecipient'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 014, 'mailingRecipient'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 015, 'valueContainerEntry'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 016, 'price1'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 017, 'valueContainer'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 018, 'calendarProfile'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 019, 'calendarFeed'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 020, 'meetingParty'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 021, 'doc'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 022, 'eMailAccount'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 023, 'contactRelationship'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 024, 'phoneCallRecipient'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 025, 'meetingActivityParty'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 026, 'building'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 027, 'accountAssignedActivity'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 028, 'folderEntry'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 029, 'salesTaxType'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 030, 'assignedAccount'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 031, 'priceLevel'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 032, 'competitor'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 033, 'buildingUnit'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 034, 'accountAssignedBudget'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 035, 'addressGroupMember'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 036, 'taskParty'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 037, 'incidentParty'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 038, 'priceModifier'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 039, 'userHomeAssignedActivity'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 040, 'activityTracker'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 041, 'buildings'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 042, 'ouRelationship'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 043, 'budget'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 044, 'workReportEntry5'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 045, 'followUp1'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 046, 'addressGroup'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 047, 'assignedResource'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 048, 'codes'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 049, 'products'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 050, 'admins'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 051, 'regions'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 052, 'searchIndexEntry'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 053, 'priceListEntry'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 054, 'actionLog'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 055, 'reservations'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 056, 'buildingComplex'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 057, 'workReportEntry6'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 058, 'activityCategory'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 059, 'contacts'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 060, 'syncData'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 061, 'account'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 062, 'wfProcessInstance'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 063, 'organizationalUnit'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 064, 'effortEstimate'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 065, 'productApplication'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 066, 'accounts'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 067, 'models'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 068, 'depots'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 069, 'workRecord'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 070, 'vote'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 071, 'filteredActivity1'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 072, 'externalLink1'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 073, 'externalLink2'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 074, 'externalLink3'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 075, 'externalLink4'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 076, 'accountFilter'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 077, 'accountFilterProperty2'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 078, 'forecasts'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 079, 'oContactMembership'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 080, 'ouContactMembership'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 081, 'address1'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 082, 'address2'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 083, 'address3'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 084, 'activityNote1'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 085, 'workflows'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 086, 'uoms'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 087, 'activities'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 088, 'homes'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 089, 'contracts'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 090, 'addresses'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 091, 'realms'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 092, 'identities'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 093, 'authorizations'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 094, 'deliveryInfo'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 095, 'activityCreator'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 096, 'activityFilter1'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 097, 'activityFilterProperty1'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 098, 'doc1'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 099, 'doc2'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 100, 'doc3'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 101, 'doc4'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 102, 'rating1'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 103, 'rating2'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 104, 'rating3'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 105, 'rating4'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 106, 'note1'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 107, 'note2'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 108, 'note3'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 109, 'note4'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 110, 'deliveryRequest1'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 111, 'deliveryRequest2'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 112, 'opportunityPos'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 113, 'activityProcess'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 114, 'activityProcessState'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 115, 'activityProcessTransition'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 116, 'activityProcessAction'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 117, 'activityMilestone'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 118, 'organizationalUnitCreditLimit'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 119, 'organizationCreditLimit'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 120, 'descr1'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 121, 'descr2'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 122, 'descr3'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 123, 'accountMember'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 124, 'media1'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 125, 'media2'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 126, 'media3'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 127, 'media4'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 128, 'userHome'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 129, 'alert'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 130, 'quickAccess'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 131, 'accessHistory'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 132, 'activityType'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 133, 'calendar'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 134, 'resource'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 135, 'userHomeAssignedContract'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 136, 'accountAssignedContract'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 137, 'workReportEntry1'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 138, 'assignedActivity'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 139, 'quotePos'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 140, 'chart'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 141, 'activityFilter2'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 142, 'activityFilterProperty2'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 143, 'productRef1'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 144, 'p1'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 145, 'audit'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 146, 'accountProduct'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 147, 'assignedGroup'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 148, 'activityLinkTo'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 149, 'activityLinkFrom'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 150, 'salesOrderPos'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 151, 'followUp'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 152, 'productPhase'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 153, 'simpleBooking'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 154, 'weekDay'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 155, 'calendarDay'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 156, 'invoicePos'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 157, 'modelElement'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 158, 'bookingText'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 159, 'productDefiningProfile'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 160, 'relatedProduct'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 161, 'productCassificationElement'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 162, 'classificationElementDescriptor'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 163, 'productSolutionPart'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 164, 'solutionPartDependency'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 165, 'solutionPartArtifactContext'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 166, 'solutionPartVp'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 167, 'depotEntity'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 168, 'depotEntityRelationship'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 169, 'depotType'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 170, 'depotGroup'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 171, 'depotHolder'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 172, 'cb'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 173, 'bookingPeriod'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 174, 'booking'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 175, 'depot'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 176, 'depotPos'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 177, 'config1'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 178, 'p2'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 179, 'depotReport'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 180, 'depotReportItemPosition'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 181, 'exportProfile'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 182, 'opportunity'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 183, 'additionalDescription4'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 184, 'additionalDescription5'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 185, 'accountFilterProperty1'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 186, 'productFilterProperty1'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 187, 'docFolder'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 188, 'docRevision'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 189, 'config2'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 190, 'config3'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 191, 'propertySetEntry1'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 192, 'propertySetEntry2'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 193, 'propertySetEntry3'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 194, 'price2'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 195, 'accountOuMembership'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 196, 'quote'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 197, 'salesOrder'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 198, 'p3'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 199, 'invoice'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 200, 'pricingRule'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 201, 'p4'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 202, 'calcRule'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 203, 'depotRef1'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 204, 'depotRef2'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 205, 'ps1'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 206, 'ps2'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 207, 'ps3'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 208, 'ps4'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 209, 'productApplicationRem'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 210, 'deliveryInfoRem'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 211, 'configTypeSet'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 212, 'configType'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 213, 'assignedCustomer'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 214, 'workReportEntry2'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 215, 'contractRole'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 216, 'budgetPosition'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 217, 'leadPosRem'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 218, 'opportunityPosRem'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 219, 'quotePosRem'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 220, 'salesOrderPosRem'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 221, 'invoicePosRem'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 222, 'leadPosMod'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 223, 'opportunityPosMod'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 224, 'quotePosMod'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 225, 'salesOrderPosMod'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 226, 'invoicePosMod'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 227, 'realm'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 228, 'principal'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 229, 'policy'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 230, 'role'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1',  'PERSISTENCE:typeName', 231, 'permission'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 232, 'privilege'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 233, 'subject'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 234, 'credential'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 235, 'reportItemSingleBooking'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 236, 'reportItemSimpleBooking'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 237, 'assignedAddress'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 238, 'componentConfig'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 239, 'productClass'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 240, 'productClassRel'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 241, 'accountMembership'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 242, 'workReportEntry3'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 243, 'workReportEntry4'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 244, 'contractFilter'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 245, 'contractFilterProperty'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 246, 'filteredContract'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 247, 'productFilter'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 248, 'productFilterProperty2'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 249, 'docFolderAss1'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 250, 'docFolderAss2'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 251, 'assignedAccount8'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 252, 'indexEntry'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 253, 'docFolderAss3'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 254, 'docFolderAss4'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 255, 'address4'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 256, 'facility'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 257, 'facility1'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 258, 'facility2'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 259, 'facility3'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 260, 'buildingUnit1'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 261, 'inventoryItem'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 262, 'itemLinkTo'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 263, 'itemLinkTo1'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 264, 'itemLinkTo2'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 265, 'itemLinkTo3'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 266, 'itemLinkFrom'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 267, 'itemLinkFrom1'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 268, 'itemLinkFrom2'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 269, 'itemLinkFrom3'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 270, 'assignedAccount9'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 271, 'booking1'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 272, 'addressFilter'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 273, 'addressFilterProperty'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 274, 'assignedAddress1'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 275, 'assignedAddress2'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 276, 'activityCreator1'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 277, 'activityCreator2'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 278, 'activityCreator3'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 279, 'followUp2'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 280, 'followUp3'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 281, 'documentLink'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 282, 'documentLock'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 283, 'documentAttachment'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 284, 'objectFinder'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 285, 'involvedObject'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 286, 'linkToLead'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 287, 'linkToOpportunity'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 288, 'linkToQuote'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 289, 'linkToSalesOrder'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 290, 'linkToInvoice'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 291, 'linkFromLead'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 292, 'linkFromOpportunity'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 293, 'linkFromQuote'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 294, 'linkFromSalesOrder'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 295, 'linkFromInvoice'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 296, 'assignedAccountLead'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 297, 'assignedAccountOpportunity'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 298, 'assignedAccountQuote'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 299, 'assignedAccountSalesOrder'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 300, 'assignedAccountInvoice'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 301, 'assignedBroker'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 302, 'assignedSalesRep'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:typeName', 303, 'productFilterProperty3'
)  
GO


INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, object__class, name, description, absolute_path, parent, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 000, 'org:openmdx:preferences1:StringPreference', 'dbObject', NULL, 'PERSISTENCE/dbObject', 'PERSISTENCE', 'OOCKE1_ORGANIZATION'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, object__class, name, description, absolute_path, parent, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 000, 'org:openmdx:preferences1:StringPreference', 'dbObject2', NULL, 'PERSISTENCE/dbObject2', 'PERSISTENCE', 'OOCKE1_ORGANIZATION_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 001, 'OOCKE1_UOMSCHEDULE'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 001, 'OOCKE1_UOMSCHEDULE_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 002, 'OOCKE1_UOM'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 002, 'OOCKE1_UOM_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 003, 'OOCKE1_CONTRACTPOSITION'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 003, 'OOCKE1_CONTRACTPOSITION_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 004, 'OOCKE1_CONTRACT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 004, 'OOCKE1_CONTRACT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 005, 'OOCKE1_WFPROCESS'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 005, 'OOCKE1_WFPROCESS_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 006, 'OOCKE1_PRODUCT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 006, 'OOCKE1_PRODUCT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 007, 'DUMMY'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 008, 'OOCKE1_SEGMENT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 008, 'OOCKE1_SEGMENT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 009, 'OOCKE1_REVENUEREPORT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 009, 'OOCKE1_REVENUEREPORT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 010, 'OOCKE1_SUBSCRIPTION'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 010, 'OOCKE1_SUBSCRIPTION_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 011, 'OOCKE1_ACTIVITY'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 011, 'OOCKE1_ACTIVITY_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 012, 'OOCKE1_TOPIC'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 012, 'OOCKE1_TOPIC_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 013, 'OOCKE1_ACTIVITYPARTY'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 013, 'OOCKE1_ACTIVITYPARTY_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 014, 'OOCKE1_ACTIVITYPARTY'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 014, 'OOCKE1_ACTIVITYPARTY_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 015, 'OOCKE1_CODEVALUEENTRY'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 015, 'OOCKE1_CODEVALUEENTRY_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 016, 'OOCKE1_PRODUCTBASEPRICE'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 016, 'OOCKE1_PRODUCTBASEPRICE_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 017, 'OOCKE1_CODEVALUECONTAINER'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 017, 'OOCKE1_CODEVALUECONTAINER_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 018, 'OOCKE1_CALENDARPROFILE'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 018, 'OOCKE1_CALENDARPROFILE_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 019, 'OOCKE1_CALENDARFEED'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 019, 'OOCKE1_CALENDARFEED_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 020, 'OOCKE1_ACTIVITYPARTY'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 020, 'OOCKE1_ACTIVITYPARTY_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 021, 'OOCKE1_DOCUMENT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 021, 'OOCKE1_DOCUMENT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 022, 'OOCKE1_EMAILACCOUNT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 022, 'OOCKE1_EMAILACCOUNT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 023, 'OOCKE1_CONTACTREL'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 023, 'OOCKE1_CONTACTREL_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 024, 'OOCKE1_ACTIVITYPARTY'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 024, 'OOCKE1_ACTIVITYPARTY_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 025, 'OOCKE1_ACTIVITYPARTY'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 025, 'OOCKE1_ACTIVITYPARTY_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 026, 'OOCKE1_BUILDINGUNIT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 026, 'OOCKE1_BUILDINGUNIT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 027, ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 028, ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 029, 'OOCKE1_SALESTAXTYPE'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 029, 'OOCKE1_SALESTAXTYPE_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 030, 'OOCKE1_ACCOUNTASSIGNMENT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 030, 'OOCKE1_ACCOUNTASSIGNMENT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 031, 'OOCKE1_PRICELEVEL'
) 
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 031, 'OOCKE1_PRICELEVEL_'
) 
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 032, 'OOCKE1_COMPETITOR'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 032, 'OOCKE1_COMPETITOR_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 033, 'OOCKE1_BUILDINGUNIT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 033, 'OOCKE1_BUILDINGUNIT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 034, ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 035, 'OOCKE1_ADDRESSGROUPMEMBER'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 035, 'OOCKE1_ADDRESSGROUPMEMBER_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 036, 'OOCKE1_ACTIVITYPARTY'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 036, 'OOCKE1_ACTIVITYPARTY_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 037, 'OOCKE1_ACTIVITYPARTY'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 037, 'OOCKE1_ACTIVITYPARTY_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 038, 'OOCKE1_PRICEMODIFIER'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 038, 'OOCKE1_PRICEMODIFIER_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 039, ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 040, 'OOCKE1_ACTIVITYGROUP'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 040, 'OOCKE1_ACTIVITYGROUP_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 041, 'OOCKE1_SEGMENT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 041, 'OOCKE1_SEGMENT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 042, 'OOCKE1_ORGUNITRELSHIP'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 042, 'OOCKE1_ORGUNITRELSHIP_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 043, 'OOCKE1_BUDGET'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 043, 'OOCKE1_BUDGET_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 044, ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 045, ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 046, 'OOCKE1_ADDRESSGROUP'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 046, 'OOCKE1_ADDRESSGROUP_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 047, 'OOCKE1_RESOURCEASSIGNMENT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 047, 'OOCKE1_RESOURCEASSIGNMENT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 048, 'OOCKE1_SEGMENT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 048, 'OOCKE1_SEGMENT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 049, 'OOCKE1_SEGMENT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 049, 'OOCKE1_SEGMENT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 050, 'OOCKE1_SEGMENT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 050, 'OOCKE1_SEGMENT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 051, 'OOCKE1_SEGMENT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 051, 'OOCKE1_SEGMENT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 052, ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 053, ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 054, 'OOCKE1_WFACTIONLOGENTRY'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 054, 'OOCKE1_WFACTIONLOGENTRY_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 055, 'OOCKE1_SEGMENT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 055, 'OOCKE1_SEGMENT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 056, 'OOCKE1_BUILDINGUNIT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 056, 'OOCKE1_BUILDINGUNIT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 057, ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 058, 'OOCKE1_ACTIVITYGROUP'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 058, 'OOCKE1_ACTIVITYGROUP_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 059, 'OOCKE1_SEGMENT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 059, 'OOCKE1_SEGMENT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 060, 'OOCKE1_NOTE'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 060, 'OOCKE1_NOTE_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 061, 'OOCKE1_ACCOUNT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 061, 'OOCKE1_ACCOUNT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 062, 'OOCKE1_WFPROCESSINSTANCE'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 062, 'OOCKE1_WFPROCESSINSTANCE_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 063, 'OOCKE1_ORGANIZATIONALUNIT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 063, 'OOCKE1_ORGANIZATIONALUNIT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 064, 'OOCKE1_ACTIVITYEFFORTESTI'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 064, 'OOCKE1_ACTIVITYEFFORTESTI_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 065, 'OOCKE1_PRODUCTAPPLICATION'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 065, 'OOCKE1_PRODUCTAPPLICATION_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 066, 'OOCKE1_SEGMENT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 066, 'OOCKE1_SEGMENT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 067, 'OOCKE1_SEGMENT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 067, 'OOCKE1_SEGMENT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 068, 'OOCKE1_SEGMENT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 068, 'OOCKE1_SEGMENT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 069, 'OOCKE1_WORKRECORD'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 069, 'OOCKE1_WORKRECORD_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 070, 'OOCKE1_VOTE'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 070, 'OOCKE1_VOTE_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 071, ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 072, 'OOCKE1_ADDITIONALEXTLINK'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 072, 'OOCKE1_ADDITIONALEXTLINK_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 073, 'OOCKE1_ADDITIONALEXTLINK'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 073, 'OOCKE1_ADDITIONALEXTLINK_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 074, 'OOCKE1_ADDITIONALEXTLINK'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 074, 'OOCKE1_ADDITIONALEXTLINK_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 075, 'OOCKE1_ADDITIONALEXTLINK'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 075, 'OOCKE1_ADDITIONALEXTLINK_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 076, 'OOCKE1_FILTER'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 076, 'OOCKE1_FILTER_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 077, 'OOCKE1_FILTERPROPERTY'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 077, 'OOCKE1_FILTERPROPERTY_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 078, 'OOCKE1_SEGMENT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 078, 'OOCKE1_SEGMENT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 079, 'OOCKE1_CONTACTMEMBERSHIP'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 079, 'OOCKE1_CONTACTMEMBERSHIP_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 080, 'OOCKE1_CONTACTMEMBERSHIP'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 080, 'OOCKE1_CONTACTMEMBERSHIP_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 081, 'OOCKE1_ADDRESS'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 081, 'OOCKE1_ADDRESS_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 082, 'OOCKE1_ADDRESS'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 082, 'OOCKE1_ADDRESS_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 083, 'OOCKE1_ADDRESS'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 083, 'OOCKE1_ADDRESS_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 084, ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 085, 'OOCKE1_SEGMENT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 085, 'OOCKE1_SEGMENT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 086, 'OOCKE1_SEGMENT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 086, 'OOCKE1_SEGMENT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 087, 'OOCKE1_SEGMENT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 087, 'OOCKE1_SEGMENT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 088, 'OOCKE1_SEGMENT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 088, 'OOCKE1_SEGMENT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 089, 'OOCKE1_SEGMENT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 089, 'OOCKE1_SEGMENT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 090, 'OOCKE1_SEGMENT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 090, 'OOCKE1_SEGMENT_'
)  
GO

INSERT INTO prefs_Preference
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 091, 'DUMMY'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 092, 'DUMMY'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 093, 'DUMMY'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 094, 'OOCKE1_DELIVERYINFO'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 094, 'OOCKE1_DELIVERYINFO_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 095, 'OOCKE1_ACTIVITYCREATOR'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 095, 'OOCKE1_ACTIVITYCREATOR_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 096, 'OOCKE1_FILTER'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 096, 'OOCKE1_FILTER_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 097, 'OOCKE1_FILTERPROPERTY'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 097, 'OOCKE1_FILTERPROPERTY_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 098, 'OOCKE1_DOCUMENTATTACHMENT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 098, 'OOCKE1_DOCUMENTATTACHMENT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 099, 'OOCKE1_DOCUMENTATTACHMENT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 099, 'OOCKE1_DOCUMENTATTACHMENT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 100, 'OOCKE1_DOCUMENTATTACHMENT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 100, 'OOCKE1_DOCUMENTATTACHMENT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 101, 'OOCKE1_DOCUMENTATTACHMENT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 101, 'OOCKE1_DOCUMENTATTACHMENT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 102, 'OOCKE1_RATING'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 102, 'OOCKE1_RATING_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 103, 'OOCKE1_RATING'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 103, 'OOCKE1_RATING_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 104, 'OOCKE1_RATING'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 104, 'OOCKE1_RATING_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 105, 'OOCKE1_RATING'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 105, 'OOCKE1_RATING_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 106, 'OOCKE1_NOTE'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 106, 'OOCKE1_NOTE_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 107, 'OOCKE1_NOTE'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 107, 'OOCKE1_NOTE_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 108, 'OOCKE1_NOTE'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 108, 'OOCKE1_NOTE_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 109, 'OOCKE1_NOTE'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 109, 'OOCKE1_NOTE_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 110, 'OOCKE1_DELIVERYREQUEST'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 110, 'OOCKE1_DELIVERYREQUEST_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 111, 'OOCKE1_DELIVERYREQUEST'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 111, 'OOCKE1_DELIVERYREQUEST_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 112, 'OOCKE1_CONTRACTPOSITION'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 112, 'OOCKE1_CONTRACTPOSITION_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 113, 'OOCKE1_ACTIVITYPROCESS'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 113, 'OOCKE1_ACTIVITYPROCESS_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 114, 'OOCKE1_ACTIVITYPROCSTATE'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 114, 'OOCKE1_ACTIVITYPROCSTATE_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 115, 'OOCKE1_ACTIVITYPROCTRANS'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 115, 'OOCKE1_ACTIVITYPROCTRANS_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 116, 'OOCKE1_ACTIVITYPROCACTION'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 116, 'OOCKE1_ACTIVITYPROCACTION_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 117, 'OOCKE1_ACTIVITYGROUP'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 117, 'OOCKE1_ACTIVITYGROUP_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 118, 'OOCKE1_CREDITLIMIT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 118, 'OOCKE1_CREDITLIMIT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 119, 'OOCKE1_CREDITLIMIT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 119, 'OOCKE1_CREDITLIMIT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 120, 'OOCKE1_DESCRIPTION'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 120, 'OOCKE1_DESCRIPTION_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 121, 'OOCKE1_DESCRIPTION'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 121, 'OOCKE1_DESCRIPTION_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 122, 'OOCKE1_DESCRIPTION'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 122, 'OOCKE1_DESCRIPTION_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 123, 'OOCKE1_ACCOUNTASSIGNMENT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 123, 'OOCKE1_ACCOUNTASSIGNMENT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 124, 'OOCKE1_MEDIA'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 124, 'OOCKE1_MEDIA_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 125, 'OOCKE1_MEDIA'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 125, 'OOCKE1_MEDIA_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 126, 'OOCKE1_MEDIA'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 126, 'OOCKE1_MEDIA_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 127, 'OOCKE1_MEDIA'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 127, 'OOCKE1_MEDIA_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 128, 'OOCKE1_USERHOME'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 128, 'OOCKE1_USERHOME_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 129, 'OOCKE1_ALERT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 129, 'OOCKE1_ALERT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 130, 'OOCKE1_QUICKACCESS'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 130, 'OOCKE1_QUICKACCESS_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 131, 'OOCKE1_ACCESSHISTORY'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 131, 'OOCKE1_ACCESSHISTORY_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 132, 'OOCKE1_ACTIVITYTYPE'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 132, 'OOCKE1_ACTIVITYTYPE_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 133, 'OOCKE1_CALENDAR'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 133, 'OOCKE1_CALENDAR_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 134, 'OOCKE1_RESOURCE'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 134, 'OOCKE1_RESOURCE_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 135, ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 136, ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 137, ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 138, ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 139, 'OOCKE1_CONTRACTPOSITION'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 139, 'OOCKE1_CONTRACTPOSITION_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 140, 'OOCKE1_MEDIA'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 140, 'OOCKE1_MEDIA_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 141, 'OOCKE1_FILTER'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 141, 'OOCKE1_FILTER_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 142, 'OOCKE1_FILTERPROPERTY'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 142, 'OOCKE1_FILTERPROPERTY_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 143, 'OOCKE1_PRODUCTREFERENCE'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 143, 'OOCKE1_PRODUCTREFERENCE_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 144, 'OOCKE1_PROPERTY'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 144, 'OOCKE1_PROPERTY_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 145, 'OOCKE1_AUDITENTRY'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 145, 'OOCKE1_AUDITENTRY_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 146, ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 147, 'OOCKE1_ACTIVITYGROUPASS'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 147, 'OOCKE1_ACTIVITYGROUPASS_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 148, 'OOCKE1_ACTIVITYLINK'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 148, 'OOCKE1_ACTIVITYLINK_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 149, ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 150, 'OOCKE1_CONTRACTPOSITION'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 150, 'OOCKE1_CONTRACTPOSITION_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 151, 'OOCKE1_ACTIVITYFOLLOWUP'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 151, 'OOCKE1_ACTIVITYFOLLOWUP_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 152, 'OOCKE1_PRODUCTPHASE'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 152, 'OOCKE1_PRODUCTPHASE_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 153, 'OOCKE1_SIMPLEBOOKING'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 153, 'OOCKE1_SIMPLEBOOKING_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 154, 'OOCKE1_CALENDARDAY'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 154, 'OOCKE1_CALENDARDAY_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 155, 'OOCKE1_CALENDARDAY'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 155, 'OOCKE1_CALENDARDAY_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 156, 'OOCKE1_CONTRACTPOSITION'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 156, 'OOCKE1_CONTRACTPOSITION_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 157, 'OOCKE1_MODELELEMENT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 157, 'OOCKE1_MODELELEMENT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 158, 'OOCKE1_BOOKINGTEXT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 158, 'OOCKE1_BOOKINGTEXT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 159, 'OOCKE1_RASPROFILE'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 159, 'OOCKE1_RASPROFILE_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 160, 'OOCKE1_RELATEDPRODUCT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 160, 'OOCKE1_RELATEDPRODUCT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 161, 'OOCKE1_RASCLASSIFICATIELT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 161, 'OOCKE1_RASCLASSIFICATIELT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 162, 'OOCKE1_RASDESCRIPTOR'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 162, 'OOCKE1_RASDESCRIPTOR_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 163, 'OOCKE1_RASSOLUTIONPART'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 163, 'OOCKE1_RASSOLUTIONPART_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 164, 'OOCKE1_RASARTIFACTDEP'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 164, 'OOCKE1_RASARTIFACTDEP_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 165, 'OOCKE1_RASARTIFACTCONTEXT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 165, 'OOCKE1_RASARTIFACTCONTEXT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 166, 'OOCKE1_RASVARPOINT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 166, 'OOCKE1_RASVARPOINT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 167, 'OOCKE1_DEPOTENTITY'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 167, 'OOCKE1_DEPOTENTITY_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 168, 'OOCKE1_DEPOTENTITYREL'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 168, 'OOCKE1_DEPOTENTITYREL_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 169, 'OOCKE1_DEPOTTYPE'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 169, 'OOCKE1_DEPOTTYPE_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 170, 'OOCKE1_DEPOTGROUP'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 170, 'OOCKE1_DEPOTGROUP_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 171, 'OOCKE1_DEPOTHOLDER'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 171, 'OOCKE1_DEPOTHOLDER_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 172, 'OOCKE1_COMPOUNDBOOKING'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 172, 'OOCKE1_COMPOUNDBOOKING_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 173, 'OOCKE1_BOOKINGPERIOD'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 173, 'OOCKE1_BOOKINGPERIOD_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 174, 'OOCKE1_BOOKING'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 174, 'OOCKE1_BOOKING_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 175, 'OOCKE1_DEPOT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 175, 'OOCKE1_DEPOT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 176, 'OOCKE1_DEPOTPOSITION'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 176, 'OOCKE1_DEPOTPOSITION_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 177, 'OOCKE1_PRODUCTCONFIG'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 177, 'OOCKE1_PRODUCTCONFIG_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 178, 'OOCKE1_PROPERTY'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 178, 'OOCKE1_PROPERTY_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 179, 'OOCKE1_DEPOTREPORT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 179, 'OOCKE1_DEPOTREPORT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 180, 'OOCKE1_DEPOTREPORTITEM'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 180, 'OOCKE1_DEPOTREPORTITEM_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 181, 'OOCKE1_EXPORTPROFILE'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 181, 'OOCKE1_EXPORTPROFILE_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 182, 'OOCKE1_CONTRACT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 182, 'OOCKE1_CONTRACT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 183, 'OOCKE1_DESCRIPTION'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 183, 'OOCKE1_DESCRIPTION_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 184, 'OOCKE1_DESCRIPTION'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 184, 'OOCKE1_DESCRIPTION_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 185, 'OOCKE1_FILTERPROPERTY'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 185, 'OOCKE1_FILTERPROPERTY_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 186, 'OOCKE1_FILTERPROPERTY'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 186, 'OOCKE1_FILTERPROPERTY_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 187, 'OOCKE1_DOCUMENTFOLDER'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 187, 'OOCKE1_DOCUMENTFOLDER_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 188, 'OOCKE1_MEDIA'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 188, 'OOCKE1_MEDIA_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 189, 'OOCKE1_PRODUCTCONFIG'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 189, 'OOCKE1_PRODUCTCONFIG_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 190, 'OOCKE1_PRODUCTCONFIG'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 190, 'OOCKE1_PRODUCTCONFIG_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 191, ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 192, ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 193, ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 194, 'OOCKE1_PRODUCTBASEPRICE'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 194, 'OOCKE1_PRODUCTBASEPRICE_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 195, ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 196, 'OOCKE1_CONTRACT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 196, 'OOCKE1_CONTRACT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 197, 'OOCKE1_CONTRACT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 197, 'OOCKE1_CONTRACT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 198, 'OOCKE1_PROPERTY'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 198, 'OOCKE1_PROPERTY_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 199, 'OOCKE1_CONTRACT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 199, 'OOCKE1_CONTRACT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 200, 'OOCKE1_PRICINGRULE'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 200, 'OOCKE1_PRICINGRULE_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 201, 'OOCKE1_PROPERTY'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 201, 'OOCKE1_PROPERTY_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 202, 'OOCKE1_CALCULATIONRULE'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 202, 'OOCKE1_CALCULATIONRULE_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 203, 'OOCKE1_DEPOTREFERENCE'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 203, 'OOCKE1_DEPOTREFERENCE_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 204, 'OOCKE1_DEPOTREFERENCE'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 204, 'OOCKE1_DEPOTREFERENCE_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 205, 'OOCKE1_PROPERTYSET'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 205, 'OOCKE1_PROPERTYSET_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 206, 'OOCKE1_PROPERTYSET'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 206, 'OOCKE1_PROPERTYSET_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 207, 'OOCKE1_PROPERTYSET'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 207, 'OOCKE1_PROPERTYSET_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 208, 'OOCKE1_PROPERTYSET'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 208, 'OOCKE1_PROPERTYSET_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 209, 'OOCKE1_PRODUCTAPPLICATION'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 209, 'OOCKE1_PRODUCTAPPLICATION_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 210, 'OOCKE1_DELIVERYINFO'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 210, 'OOCKE1_DELIVERYINFO_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 211, 'OOCKE1_PRODUCTCONFTYPESET'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 211, 'OOCKE1_PRODUCTCONFTYPESET_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 212, 'OOCKE1_PRODUCTCONFIG'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 212, 'OOCKE1_PRODUCTCONFIG_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 213, 'OOCKE1_ACCOUNTASSIGNMENT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 213, 'OOCKE1_ACCOUNTASSIGNMENT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 214, ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 215, 'DUMMY'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 216, 'OOCKE1_BUDGETPOSITION'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 216, 'OOCKE1_BUDGETPOSITION_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 217, 'OOCKE1_CONTRACTPOSITION'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 217, 'OOCKE1_CONTRACTPOSITION_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 218, 'OOCKE1_CONTRACTPOSITION'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 218, 'OOCKE1_CONTRACTPOSITION_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 219, 'OOCKE1_CONTRACTPOSITION'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 219, 'OOCKE1_CONTRACTPOSITION_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 220, 'OOCKE1_CONTRACTPOSITION'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 220, 'OOCKE1_CONTRACTPOSITION_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 221, 'OOCKE1_CONTRACTPOSITION'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 221, 'OOCKE1_CONTRACTPOSITION_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 222, 'OOCKE1_CONTRACTPOSMOD'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 222, 'OOCKE1_CONTRACTPOSMOD_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 223, 'OOCKE1_CONTRACTPOSMOD'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 223, 'OOCKE1_CONTRACTPOSMOD_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 224, 'OOCKE1_CONTRACTPOSMOD'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 224, 'OOCKE1_CONTRACTPOSMOD_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 225, 'OOCKE1_CONTRACTPOSMOD'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 225, 'OOCKE1_CONTRACTPOSMOD_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 226, 'OOCKE1_CONTRACTPOSMOD'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 226, 'OOCKE1_CONTRACTPOSMOD_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 227, ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 228, ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 229, ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 230, ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 231, ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 232, ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 233, ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 234, ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 235, ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 236, ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 237, ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 238, 'OOCKE1_COMPONENTCONFIG'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 238, 'OOCKE1_COMPONENTCONFIG_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 239, 'OOCKE1_PRODUCTCLASS'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 239, 'OOCKE1_PRODUCTCLASS_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 240, 'OOCKE1_PRODUCTCLASSREL'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 240, 'OOCKE1_PRODUCTCLASSREL_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 241, ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 242, ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 243, ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 244, 'OOCKE1_FILTER'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 244, 'OOCKE1_FILTER_'
)  
GO

INSERT INTO prefs_Preference
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 245, 'OOCKE1_FILTERPROPERTY'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 245, 'OOCKE1_FILTERPROPERTY_'
)  
GO

INSERT INTO prefs_Preference
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 246, ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 247, 'OOCKE1_FILTER'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 247, 'OOCKE1_FILTER_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 248, 'OOCKE1_FILTERPROPERTY'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 248, 'OOCKE1_FILTERPROPERTY_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 249, 'OOCKE1_DOCUMENTFOLDERASS'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 249, 'OOCKE1_DOCUMENTFOLDERASS_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 250, 'OOCKE1_DOCUMENTFOLDERASS'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 250, 'OOCKE1_DOCUMENTFOLDERASS_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 251, 'OOCKE1_ACCOUNTASSIGNMENT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 251, 'OOCKE1_ACCOUNTASSIGNMENT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 252, 'OOCKE1_INDEXENTRY'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 252, 'OOCKE1_INDEXENTRY_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 253, 'OOCKE1_DOCUMENTFOLDERASS'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 253, 'OOCKE1_DOCUMENTFOLDERASS_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 254, 'OOCKE1_DOCUMENTFOLDERASS'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 254, 'OOCKE1_DOCUMENTFOLDERASS_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 255, ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 256, 'OOCKE1_FACILITY'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 256, 'OOCKE1_FACILITY_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 257, 'OOCKE1_FACILITY'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 257, 'OOCKE1_FACILITY_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 258, 'OOCKE1_FACILITY'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 258, 'OOCKE1_FACILITY_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 259, ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 260, ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 261, 'OOCKE1_INVENTORYITEM'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 261, 'OOCKE1_INVENTORYITEM_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 262, 'OOCKE1_LINKABLEITEMLINK'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 262, 'OOCKE1_LINKABLEITEMLINK_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 263, 'OOCKE1_LINKABLEITEMLINK'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 263, 'OOCKE1_LINKABLEITEMLINK_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 264, 'OOCKE1_LINKABLEITEMLINK'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 264, 'OOCKE1_LINKABLEITEMLINK_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 265, 'OOCKE1_LINKABLEITEMLINK'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 265, 'OOCKE1_LINKABLEITEMLINK_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 266, ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 267, ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 268, ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 269, ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 270, 'OOCKE1_ACCOUNTASSIGNMENT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 270, 'OOCKE1_ACCOUNTASSIGNMENT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 271, ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 272, 'OOCKE1_FILTER'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 272, 'OOCKE1_FILTER_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 273, 'OOCKE1_FILTERPROPERTY'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 273, 'OOCKE1_FILTERPROPERTY_'
)  
GO

INSERT INTO prefs_Preference
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 274, ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 275, ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 276, ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 277, ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 278, ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 279, ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 280, ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 281, 'OOCKE1_DOCUMENTLINK'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 281, 'OOCKE1_DOCUMENTLINK_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 282, 'OOCKE1_DOCUMENTLOCK'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 282, 'OOCKE1_DOCUMENTLOCK_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 283, 'OOCKE1_MEDIA'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 283, 'OOCKE1_MEDIA_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 284, 'OOCKE1_OBJECTFINDER'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 284, 'OOCKE1_OBJECTFINDER_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 285, 'OOCKE1_INVOLVEDOBJECT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 285, 'OOCKE1_INVOLVEDOBJECT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 286, 'OOCKE1_CONTRACTLINK'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 286, 'OOCKE1_CONTRACTLINK_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 287, 'OOCKE1_CONTRACTLINK'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 287, 'OOCKE1_CONTRACTLINK_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 288, 'OOCKE1_CONTRACTLINK'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 288, 'OOCKE1_CONTRACTLINK_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 289, 'OOCKE1_CONTRACTLINK'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 289, 'OOCKE1_CONTRACTLINK_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 290, 'OOCKE1_CONTRACTLINK'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 290, 'OOCKE1_CONTRACTLINK_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 291, ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 292, ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 293, ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 294, ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 295, ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 296, 'OOCKE1_ACCOUNTASSIGNMENT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 296, 'OOCKE1_ACCOUNTASSIGNMENT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 297, 'OOCKE1_ACCOUNTASSIGNMENT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 297, 'OOCKE1_ACCOUNTASSIGNMENT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 298, 'OOCKE1_ACCOUNTASSIGNMENT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 298, 'OOCKE1_ACCOUNTASSIGNMENT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 299, 'OOCKE1_ACCOUNTASSIGNMENT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 299, 'OOCKE1_ACCOUNTASSIGNMENT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 300, 'OOCKE1_ACCOUNTASSIGNMENT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 300, 'OOCKE1_ACCOUNTASSIGNMENT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 301, 'OOCKE1_ACCOUNTASSIGNMENT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 301, 'OOCKE1_ACCOUNTASSIGNMENT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 302, 'OOCKE1_ACCOUNTASSIGNMENT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 302, 'OOCKE1_ACCOUNTASSIGNMENT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject', 303, 'OOCKE1_FILTERPROPERTY'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObject2', 303, 'OOCKE1_FILTERPROPERTY_'
)  
GO


INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, object__class, name, description, absolute_path, parent, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 000, 'org:openmdx:preferences1:IntegerPreference', 'pathNormalizeLevel', NULL, 'PERSISTENCE/pathNormalizeLevel', 'PERSISTENCE', 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 001, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 002, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 003, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 004, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 005, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 006, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 007, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 008, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 009, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 010, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 011, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 012, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 013, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 014, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 015, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 016, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 017, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 018, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 019, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 020, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 021, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 022, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 023, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 024, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 025, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 026, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 027, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 028, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 029, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 030, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 031, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 032, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 033, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 034, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 035, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 036, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 037, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 038, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 039, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 040, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 041, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 042, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 043, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 044, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 045, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 046, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 047, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 048, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 049, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 050, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 051, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 052, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 053, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 054, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 055, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 056, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 057, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 058, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 059, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 060, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 061, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 062, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 063, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 064, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 065, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 066, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 067, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 068, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 069, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 070, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 071, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 072, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 073, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 074, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 075, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 076, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 077, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 078, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 079, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 080, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 081, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 082, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 083, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 084, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 085, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 086, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 087, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 088, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 089, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 090, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 091, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 092, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 093, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 094, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 095, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 096, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 097, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 098, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 099, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 100, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 101, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 102, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 103, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 104, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 105, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 106, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 107, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 108, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 109, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 110, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 111, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 112, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 113, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 114, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 115, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 116, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 117, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 118, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 119, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 120, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 121, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 122, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 123, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 124, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 125, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 126, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 127, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 128, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 129, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 130, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 131, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 132, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 133, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 134, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 135, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 136, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 137, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 138, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 139, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 140, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 141, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 142, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 143, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 144, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 145, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 146, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 147, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 148, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 149, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 150, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 151, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 152, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 153, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 154, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 155, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 156, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 157, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 158, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 159, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 160, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 161, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 162, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 163, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 164, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 165, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 166, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 167, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 168, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 169, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 170, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 171, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 172, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 173, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 174, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 175, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 176, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 177, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 178, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 179, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 180, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 181, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 182, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 183, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 184, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 185, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 186, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 187, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 188, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 189, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 190, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 191, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 192, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 193, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 194, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 195, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 196, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 197, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 198, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 199, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 200, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 201, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 202, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 203, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 204, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 205, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 206, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 207, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 208, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 209, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 210, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 211, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 212, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 213, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 214, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 215, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 216, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 217, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 218, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 219, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 220, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 221, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 222, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 223, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 224, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 225, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 226, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 227, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 228, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 229, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 230, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 231, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 232, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 233, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 234, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 235, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 236, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 237, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 238, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 239, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 240, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 241, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 242, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 243, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 244, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 245, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 246, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 247, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 248, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 249, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 250, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 251, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 252, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 253, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 254, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 255, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 256, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 257, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 258, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 259, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 260, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 261, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 262, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 263, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 264, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 265, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 266, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 267, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 268, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 269, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 270, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 271, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 272, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 273, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 274, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 275, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 276, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 277, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 278, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 279, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 280, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 281, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 282, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 283, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 284, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 285, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 286, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 287, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 288, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 289, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 290, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 291, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 292, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 293, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 294, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 295, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 296, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 297, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 298, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 299, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 300, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 301, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 302, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value)
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:pathNormalizeLevel', 303, 2
)  
GO


INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, object__class, name, description, absolute_path, parent, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 000, 'org:openmdx:preferences1:StringPreference', 'dbObjectFormat', NULL, 'PERSISTENCE/dbObjectFormat', 'PERSISTENCE', 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 001, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 002, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 003, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 004, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 005, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 006, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 007, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 008, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 009, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 010, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 011, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 012, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 013, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 014, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 015, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 016, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 017, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 018, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 019, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 020, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 021, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 022, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 023, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 024, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 025, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 026, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 027, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 028, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 029, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 030, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 031, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 032, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 033, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 034, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 035, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 036, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 037, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 038, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 039, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 040, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 041, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 042, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 043, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 044, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 045, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 046, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 047, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 048, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 049, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 050, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 051, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 052, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 053, 'slicedWithParentAndIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 054, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 055, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 056, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 057, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 058, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 059, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 060, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 061, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 062, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 063, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 064, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 065, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 066, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 067, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 068, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 069, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 070, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 071, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 072, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 073, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 074, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 075, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 076, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 077, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 078, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 079, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 080, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 081, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 082, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 083, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 084, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 085, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 086, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 087, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 088, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 089, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 090, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 091, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 092, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 093, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 094, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 095, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 096, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 097, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 98, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 099, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 100, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 101, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 102, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 103, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 104, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 105, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 106, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 107, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 108, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 109, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 110, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 111, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 112, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 113, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 114, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 115, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 116, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 117, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 118, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 119, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 120, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 121, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 122, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 123, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 124, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 125, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 126, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 127, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 128, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 129, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 130, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 131, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 132, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 133, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 134, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 135, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 136, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 137, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 138, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 139, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 140, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 141, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 142, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 143, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 144, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 145, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 146, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 147, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 148, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 149, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 150, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 151, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 152, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 153, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 154, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 155, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 156, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 157, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 158, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 159, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 160, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 161, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 162, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 163, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 164, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 165, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 166, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 167, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 168, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 169, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 170, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 171, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 172, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 173, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 174, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 175, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 176, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 177, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 178, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 179, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 180, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 181, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 182, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 183, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 184, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 185, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 186, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 187, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 188, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 189, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 190, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 191, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 192, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 193, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 194, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 195, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 196, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 197, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 198, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 199, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 200, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 201, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 202, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 203, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 204, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 205, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 206, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 207, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 208, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 209, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 210, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 211, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 212, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 213, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 214, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 215, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 216, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 217, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 218, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 219, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 220, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 221, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 222, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 223, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 224, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 225, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 226, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 227, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 228, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 229, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 230, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 231, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 232, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 233, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 234, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 235, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 236, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 237, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 238, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 239, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 240, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 241, 'slicedWithParentAndIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 242, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 243, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 244, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 245, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 246, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 247, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 248, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 249, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 250, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 251, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 252, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 253, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 254, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 255, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 256, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 257, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 258, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 259, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 260, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 261, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 262, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 263, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 264, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 265, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 266, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 267, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 268, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 269, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 270, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 271, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 272, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 273, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 274, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 275, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 276, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 277, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 278, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 279, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 280, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 281, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 282, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 283, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 284, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 285, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 286, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 287, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 288, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 289, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 290, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 291, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 292, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 293, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 294, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 295, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 296, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 297, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 298, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 299, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 300, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 301, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 302, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectFormat', 303, 'slicedWithIdAsKey'
)  
GO


INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, object__class, name, description, absolute_path, parent, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery', 000, 'org:openmdx:preferences1:StringPreference', 'dbObjectForQuery', NULL, 'PERSISTENCE/dbObjectForQuery', 'PERSISTENCE', ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, object__class, name, description, absolute_path, parent, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery2', 000, 'org:openmdx:preferences1:StringPreference', 'dbObjectForQuery2', NULL, 'PERSISTENCE/dbObjectForQuery2', 'PERSISTENCE', ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, object__class, name, description, absolute_path, parent, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectsForQueryJoinColumn', 000, 'org:openmdx:preferences1:StringPreference', 'dbObjectsForQueryJoinColumn', NULL, 'PERSISTENCE/dbObjectsForQueryJoinColumn', 'PERSISTENCE', ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, object__class, name, description, absolute_path, parent, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinTable', 000, 'org:openmdx:preferences1:StringPreference', 'joinTable', NULL, 'PERSISTENCE/joinTable', 'PERSISTENCE', ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, object__class, name, description, absolute_path, parent, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd1', 000, 'org:openmdx:preferences1:StringPreference', 'joinColumnEnd1', NULL, 'PERSISTENCE/joinColumnEnd1', 'PERSISTENCE', ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, object__class, name, description, absolute_path, parent, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd2', 000, 'org:openmdx:preferences1:StringPreference', 'joinColumnEnd2', NULL, 'PERSISTENCE/joinColumnEnd2', 'PERSISTENCE', ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery', 027, 'OOCKE1_ACTIVITY'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery2', 027, 'OOCKE1_ACTIVITY_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinTable', 027, 'OOCKE1_JOIN_ACCTHASASSACT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd1', 027, 'account'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd2', 027, 'assigned_activity'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery', 028, 'OOCKE1_TOBJ_DOCFLDENTRY'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery2', 028, 'OOCKE1_TOBJ_DOCFLDENTRY_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectsForQueryJoinColumn', 028, 'based_on'
)  
GO  

INSERT INTO prefs_Preference
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery', 034, 'OOCKE1_BUDGET'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery2', 034, 'OOCKE1_BUDGET_'
)  
GO

INSERT INTO prefs_Preference
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinTable', 034, 'OOCKE1_JOIN_ACCTHASASSBUDGET'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd1', 034, 'account'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd2', 034, 'assigned_budget'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery', 039, 'OOCKE1_ACTIVITY'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery2', 039, 'OOCKE1_ACTIVITY_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinTable', 039, 'OOCKE1_JOIN_HOMEHASASSACT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd1', 039, 'user_home'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd2', 039, 'assigned_activity'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery', 044, 'OOCKE1_WORKRECORD'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery2', 044, 'OOCKE1_WORKRECORD_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinTable', 044, 'OOCKE1_JOIN_ACTCONTAINSWRE'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd1', 044, 'activity'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd2', 044, 'work_report_entry'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery', 045, 'OOCKE1_ACTIVITYFOLLOWUP'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery2', 045, 'OOCKE1_ACTIVITYFOLLOWUP_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinTable', 045, 'OOCKE1_JOIN_ACTGCONTAINSFLUP'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd1', 045, 'activity_group'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd2', 045, 'follow_up'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery', 052, 'OOCKE1_TOBJ_SEARCHINDEXENTRY'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery2', 052, 'OOCKE1_TOBJ_SEARCHINDEXENTRY_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectsForQueryJoinColumn', 052, 'account'
)  
GO  

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery', 053, 'OOCKE1_TOBJ_PRICELISTENTRY'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery2', 053, 'OOCKE1_TOBJ_PRICELISTENTRY_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectsForQueryJoinColumn', 053, 'base_price'
)  
GO  

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery', 057, 'OOCKE1_WORKRECORD'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery2', 057, 'OOCKE1_WORKRECORD_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinTable', 057, 'OOCKE1_JOIN_SEGCONTAINSWRE'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd1', 057, 'segment'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd2', 057, 'work_report_entry'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery', 071, 'OOCKE1_ACTIVITY'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery2', 071, 'OOCKE1_ACTIVITY_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinTable', 071, 'OOCKE1_JOIN_ACTGCONTAINSACT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd1', 071, 'activity_group'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd2', 071, 'filtered_activity'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery', 084, 'OOCKE1_NOTE'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery2', 084, 'OOCKE1_NOTE_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinTable', 084, 'OOCKE1_JOIN_ACTGCONTAINSNOTE'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd1', 084, 'activity_group'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd2', 084, 'activity_note'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery', 135, 'OOCKE1_CONTRACT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery2', 135, 'OOCKE1_CONTRACT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinTable', 135, 'OOCKE1_JOIN_HOMEHASASSCONTR'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd1', 135, 'user_home'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd2', 135, 'assigned_contract'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery', 136, 'OOCKE1_CONTRACT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery2', 136, 'OOCKE1_CONTRACT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinTable', 136, 'OOCKE1_JOIN_ACCTHASASSCONTR'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd1', 136, 'account'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd2', 136, 'assigned_contract'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery', 137, 'OOCKE1_WORKRECORD'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery2', 137, 'OOCKE1_WORKRECORD_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinTable', 137, 'OOCKE1_JOIN_RESCONTAINSWRE'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd1', 137, 'resource'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd2', 137, 'work_report_entry'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery', 138, 'OOCKE1_ACTIVITY'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery2', 138, 'OOCKE1_ACTIVITY_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinTable', 138, 'OOCKE1_JOIN_RESHASASSIGNEDACT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd1', 138, 'resource'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd2', 138, 'assigned_activity'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery', 146, 'OOCKE1_PRODUCT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery2', 146, 'OOCKE1_PRODUCT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinTable', 146, 'OOCKE1_JOIN_ACCTHASPROD'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd1', 146, 'account'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd2', 146, 'product'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery', 149, 'OOCKE1_TOBJ_ACTIVITYLINKFROM'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery2', 149, 'OOCKE1_TOBJ_ACTIVITYLINKFROM_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectsForQueryJoinColumn', 149, 'link_to'
)  
GO  

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery', 191, 'OOCKE1_TOBJ_PROPERTYSETENTRY'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery2', 191, 'OOCKE1_TOBJ_PROPERTYSETENTRY_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectsForQueryJoinColumn', 191, 'property'
)  
GO  

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery', 192, 'OOCKE1_TOBJ_PROPERTYSETENTRY'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery2', 192, 'OOCKE1_TOBJ_PROPERTYSETENTRY_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectsForQueryJoinColumn', 192, 'property'
)  
GO  

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery', 193, 'OOCKE1_TOBJ_PROPERTYSETENTRY'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery2', 193, 'OOCKE1_TOBJ_PROPERTYSETENTRY_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectsForQueryJoinColumn', 193, 'property'
)  
GO  

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery', 195, 'OOCKE1_ORGANIZATIONALUNIT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery2', 195, 'OOCKE1_ORGANIZATIONALUNIT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinTable', 195, 'OOCKE1_JOIN_CONTACTISMEMBEROF'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd1', 195, 'contact'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd2', 195, 'ou_membership'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery', 214, 'OOCKE1_WORKRECORD'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery2', 214, 'OOCKE1_WORKRECORD_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinTable', 214, 'OOCKE1_JOIN_ACTGCONTAINSWRE'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd1', 214, 'activity_group'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd2', 214, 'work_report_entry'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery', 215, 'OOCKE1_TOBJ_CONTRACTROLE'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery2', 215, 'OOCKE1_TOBJ_CONTRACTROLE_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectsForQueryJoinColumn', 215, 'contract'
)  
GO  

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery', 235, 'OOCKE1_BOOKING'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery2', 235, 'OOCKE1_BOOKING_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinTable', 235, 'OOCKE1_JOIN_DEPREPITMHASBK'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd1', 235, 'item_position'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd2', 235, 'single_booking'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery', 236, 'OOCKE1_SIMPLEBOOKING'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery2', 236, 'OOCKE1_SIMPLEBOOKING_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinTable', 236, 'OOCKE1_JOIN_DEPREPITMHASSBK'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd1', 236, 'item_position'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd2', 236, 'simple_booking'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery', 237, 'OOCKE1_ADDRESS'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery2', 237, 'OOCKE1_ADDRESS_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinTable', 237, 'OOCKE1_JOIN_BUHASADR'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd1', 237, 'building_unit'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd2', 237, 'assigned_address'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery', 241, 'OOCKE1_TOBJ_ACCTMEMBERSHIP'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery2', 241, 'OOCKE1_TOBJ_ACCTMEMBERSHIP_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectsForQueryJoinColumn', 241, 'member'
)  
GO  

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery', 242, 'OOCKE1_WORKRECORD'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery2', 242, 'OOCKE1_WORKRECORD_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinTable', 242, 'OOCKE1_JOIN_ACTGCONTAINSWRE'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd1', 242, 'activity_group'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd2', 242, 'work_report_entry'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery', 243, 'OOCKE1_WORKRECORD'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery2', 243, 'OOCKE1_WORKRECORD_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinTable', 243, 'OOCKE1_JOIN_ACTGCONTAINSWRE'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd1', 243, 'activity_group'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd2', 243, 'work_report_entry'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery', 246, 'OOCKE1_CONTRACT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery2', 246, 'OOCKE1_CONTRACT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinTable', 246, 'OOCKE1_JOIN_FILTERINCLDESCONTR'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd1', 246, 'contract_filter'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd2', 246, 'filtered_contract'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery', 255, 'OOCKE1_ADDRESS'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery2', 255, 'OOCKE1_ADDRESS_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinTable', 255, 'OOCKE1_JOIN_SEGCONTAINSADR'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd1', 255, 'segment'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd2', 255, 'address'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery', 259, 'OOCKE1_FACILITY'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery2', 259, 'OOCKE1_FACILITY_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinTable', 259, 'OOCKE1_JOIN_SEGCONTAINSFAC'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd1', 259, 'segment'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd2', 259, 'facility'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery', 260, 'OOCKE1_BUILDINGUNIT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery2', 260, 'OOCKE1_BUILDINGUNIT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinTable', 260, 'OOCKE1_JOIN_SEGCONTAINSBU'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd1', 260, 'segment'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd2', 260, 'building_unit'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery', 266, 'OOCKE1_TOBJ_LNKITEMLNKFROM'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery2', 266, 'OOCKE1_TOBJ_LNKITEMLNKFROM_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectsForQueryJoinColumn', 266, 'link_to'
)  
GO  

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery', 267, 'OOCKE1_TOBJ_LNKITEMLNKFROM'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery2', 267, 'OOCKE1_TOBJ_LNKITEMLNKFROM_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectsForQueryJoinColumn', 267, 'link_to'
)  
GO  

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery', 268, 'OOCKE1_TOBJ_LNKITEMLNKFROM'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery2', 268, 'OOCKE1_TOBJ_LNKITEMLNKFROM_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectsForQueryJoinColumn', 268, 'link_to'
)  
GO  

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery', 269, 'OOCKE1_TOBJ_LNKITEMLNKFROM'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery2', 269, 'OOCKE1_TOBJ_LNKITEMLNKFROM_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectsForQueryJoinColumn', 269, 'link_to'
)  
GO  

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery', 271, 'OOCKE1_BOOKING'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery2', 271, 'OOCKE1_BOOKING_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinTable', 271, 'OOCKE1_JOIN_IITEMHASBOOKING'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd1', 271, 'inventory_item'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd2', 271, 'booking'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery', 274, 'OOCKE1_ADDRESS'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery2', 274, 'OOCKE1_ADDRESS_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinTable', 274, 'OOCKE1_JOIN_BUHASADR'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd1', 274, 'building_unit'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd2', 274, 'assigned_address'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery', 275, 'OOCKE1_ADDRESS'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery2', 275, 'OOCKE1_ADDRESS_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinTable', 275, 'OOCKE1_JOIN_BUHASADR'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd1', 275, 'building_unit'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd2', 275, 'assigned_address'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery', 276, 'OOCKE1_ACTIVITYCREATOR'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery2', 276, 'OOCKE1_ACTIVITYCREATOR_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinTable', 276, 'OOCKE1_JOIN_ACTGISCREATEDBY'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd1', 276, 'activity_group'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd2', 276, 'activity_creator'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery', 277, 'OOCKE1_ACTIVITYCREATOR'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery2', 277, 'OOCKE1_ACTIVITYCREATOR_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinTable', 277, 'OOCKE1_JOIN_ACTGISCREATEDBY'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd1', 277, 'activity_group'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd2', 277, 'activity_creator'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery', 278, 'OOCKE1_ACTIVITYCREATOR'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery2', 278, 'OOCKE1_ACTIVITYCREATOR_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinTable', 278, 'OOCKE1_JOIN_ACTGISCREATEDBY'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd1', 278, 'activity_group'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd2', 278, 'activity_creator'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery', 279, 'OOCKE1_ACTIVITYFOLLOWUP'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery2', 279, 'OOCKE1_ACTIVITYFOLLOWUP_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinTable', 279, 'OOCKE1_JOIN_ACTGCONTAINSFLUP'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd1', 279, 'activity_group'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd2', 279, 'follow_up'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery', 280, 'OOCKE1_ACTIVITYFOLLOWUP'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery2', 280, 'OOCKE1_ACTIVITYFOLLOWUP_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinTable', 280, 'OOCKE1_JOIN_ACTGCONTAINSFLUP'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd1', 280, 'activity_group'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:joinColumnEnd2', 280, 'follow_up'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery', 291, 'OOCKE1_TOBJ_CONTRACTLNKFROM'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery2', 291, 'OOCKE1_TOBJ_CONTRACTLNKFROM_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectsForQueryJoinColumn', 291, 'link_to'
)  
GO  

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery', 292, 'OOCKE1_TOBJ_CONTRACTLNKFROM'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery2', 292, 'OOCKE1_TOBJ_CONTRACTLNKFROM_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectsForQueryJoinColumn', 292, 'link_to'
)  
GO  

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery', 293, 'OOCKE1_TOBJ_CONTRACTLNKFROM'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery2', 293, 'OOCKE1_TOBJ_CONTRACTLNKFROM_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectsForQueryJoinColumn', 293, 'link_to'
)  
GO  

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery', 294, 'OOCKE1_TOBJ_CONTRACTLNKFROM'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery2', 294, 'OOCKE1_TOBJ_CONTRACTLNKFROM_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectsForQueryJoinColumn', 294, 'link_to'
)  
GO  

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery', 295, 'OOCKE1_TOBJ_CONTRACTLNKFROM'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectForQuery2', 295, 'OOCKE1_TOBJ_CONTRACTLNKFROM_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectsForQueryJoinColumn', 295, 'link_to'
)  
GO  


INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, object__class, name, description, absolute_path, parent, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:dbObjectHint', 000, 'org:openmdx:preferences1:StringPreference', 'dbObjectHint', NULL, 'PERSISTENCE/dbObjectHint', 'PERSISTENCE', ''
)  
GO


INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, object__class, name, description, absolute_path, parent) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:objectIdPattern', 0, 'org:openmdx:preferences1:StringPreference', 'objectIdPattern', NULL, 'PERSISTENCE/objectIdPattern', 'PERSISTENCE'
)  
GO


INSERT INTO prefs_Preference
    (object_rid, object_oid, object_idx, object__class, name, description, absolute_path, parent, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:autonumColumn', 000, 'org:openmdx:preferences1:StringPreference', 'autonumColumn', NULL, 'PERSISTENCE/autonumColumn', 'PERSISTENCE', 'OOCKE1_ACTIVITY.activity_number AS CHAR(20)'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOCKE1', 'PERSISTENCE:autonumColumn', 001, 'OOCKE1_CONTRACTPOSITION.position_number AS CHAR(20)'
)  
GO


INSERT INTO prefs_Preference
    (object_rid, object_oid, object_idx, object__class, name, description, absolute_path, parent, string_value) 
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:columnNameFrom', 000, 'org:openmdx:preferences1:StringPreference', 'columnNameFrom', NULL, 'PERSISTENCE/columnNameFrom', 'PERSISTENCE', 'password'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, object__class, name, description, absolute_path, parent, string_value) 
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:columnNameTo', 000, 'org:openmdx:preferences1:StringPreference', 'columnNameTo', NULL, 'PERSISTENCE/columnNameTo', 'PERSISTENCE', 'passwd'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:columnNameFrom', 001, 'object__class'
) 
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:columnNameTo', 001, 'dtype'
)  
GO


INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, object__class, name, description, absolute_path, parent, string_value) 
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:type', 000, 'org:openmdx:preferences1:StringPreference', 'type', NULL, 'PERSISTENCE/type', 'PERSISTENCE', 'xri:@openmdx:org:opencrx:security:identity1/provider/:*/segment/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:type', 001, 'xri:@openmdx:org.openmdx.security.realm1/provider/:*/segment/:*/realm/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:type', 002, 'xri:@openmdx:org.openmdx.security.realm1/provider/:*/segment/:*/realm/:*/principal/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:type', 003, 'xri:@openmdx:org.openmdx.security.authorization1/provider/:*/segment/:*/policy/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value)
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:type', 004, 'xri:@openmdx:org.openmdx.security.authorization1/provider/:*/segment/:*/policy/:*/role/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOMSE2',  'PERSISTENCE:type', 005, 'xri:@openmdx:org.openmdx.security.authorization1/provider/:*/segment/:*/policy/:*/role/:*/permission/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:type', 006, 'xri:@openmdx:org.openmdx.security.authorization1/provider/:*/segment/:*/policy/:*/privilege/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:type', 007, 'xri:@openmdx:org.opencrx.security.identity1/provider/:*/segment/:*/subject/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:type', 008, 'xri:@openmdx:org.openmdx.security.authentication1/provider/:*/segment/:*/credential/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:type', 009, 'xri:@openmdx:org:openmdx:security:authentication1/provider/:*/segment/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:type', 010, 'xri:@openmdx:org:openmdx:security:realm1/provider/:*/segment/:*'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:type', 011, 'xri:@openmdx:org.openmdx.security.authorization1/provider/:*/segment/:*'
)  
GO



INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, object__class, name, description, absolute_path, parent, string_value) 
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:typeName', 000, 'org:openmdx:preferences1:StringPreference', 'typeName', NULL, 'PERSISTENCE/typeName', 'PERSISTENCE', 'identities'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:typeName', 001, 'realm'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:typeName', 002, 'principal'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:typeName', 003, 'policy'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value)
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:typeName', 004, 'role'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOMSE2',  'PERSISTENCE:typeName', 005, 'permission'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:typeName', 006, 'privilege'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:typeName', 007, 'subject'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:typeName', 008, 'credential'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:typeName', 009, 'authentications'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:typeName', 010, 'realms'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:typeName', 011, 'authorizations'
)  
GO



INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, object__class, name, description, absolute_path, parent, string_value) 
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:dbObject', 000, 'org:openmdx:preferences1:StringPreference', 'dbObject', NULL, 'PERSISTENCE/dbObject', 'PERSISTENCE', 'OOMSE2_SEGMENT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, object__class, name, description, absolute_path, parent, string_value) 
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:dbObject2', 000, 'org:openmdx:preferences1:StringPreference', 'dbObject2', NULL, 'PERSISTENCE/dbObject2', 'PERSISTENCE', ''
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOMSE2','PERSISTENCE:dbObject', 001, 'OOMSE2_REALM'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOMSE2','PERSISTENCE:dbObject2', 001, 'OOMSE2_REALM_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:dbObject', 002, 'OOMSE2_PRINCIPAL'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:dbObject2', 002, 'OOMSE2_PRINCIPAL_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:dbObject', 003, 'OOMSE2_POLICY'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:dbObject2', 003, 'OOMSE2_POLICY_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:dbObject', 004, 'OOMSE2_ROLE'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:dbObject2', 004, 'OOMSE2_ROLE_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:dbObject', 005, 'OOMSE2_PERMISSION'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:dbObject2', 005, 'OOMSE2_PERMISSION_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:dbObject', 006, 'OOMSE2_PRIVILEGE'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:dbObject2', 006, 'OOMSE2_PRIVILEGE_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:dbObject', 007, 'OOMSE2_SUBJECT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:dbObject2', 007, 'OOMSE2_SUBJECT_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:dbObject', 008, 'OOMSE2_CREDENTIAL'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:dbObject2', 008, 'OOMSE2_CREDENTIAL_'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:dbObject', 009, 'OOMSE2_SEGMENT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:dbObject', 010, 'OOMSE2_SEGMENT'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:dbObject', 011, 'OOMSE2_SEGMENT'
)  
GO


INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, object__class, name, description, absolute_path, parent, integer_value) 
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:pathNormalizeLevel', 000, 'org:openmdx:preferences1:IntegerPreference', 'pathNormalizeLevel', NULL, 'PERSISTENCE/pathNormalizeLevel', 'PERSISTENCE', 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value) 
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:pathNormalizeLevel', 001, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value) 
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:pathNormalizeLevel', 002, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value) 
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:pathNormalizeLevel', 003, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value) 
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:pathNormalizeLevel', 004, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value) 
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:pathNormalizeLevel', 005, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value) 
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:pathNormalizeLevel', 006, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value) 
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:pathNormalizeLevel', 007, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value) 
VALUES(
    'preference/OOMSE2',  'PERSISTENCE:pathNormalizeLevel', 008, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value) 
VALUES(
    'preference/OOMSE2',  'PERSISTENCE:pathNormalizeLevel', 009, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value) 
VALUES(
    'preference/OOMSE2',  'PERSISTENCE:pathNormalizeLevel', 010, 2
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, integer_value) 
VALUES(
    'preference/OOMSE2',  'PERSISTENCE:pathNormalizeLevel', 011, 2
)  
GO



INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, object__class, name, description, absolute_path, parent, string_value) 
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:dbObjectFormat', 000, 'org:openmdx:preferences1:StringPreference', 'dbObjectFormat', NULL, 'PERSISTENCE/dbObjectFormat', 'PERSISTENCE', 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:dbObjectFormat', 001, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:dbObjectFormat', 002, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:dbObjectFormat', 003, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:dbObjectFormat', 004, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:dbObjectFormat', 005, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:dbObjectFormat', 006, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:dbObjectFormat', 007, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:dbObjectFormat', 008, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:dbObjectFormat', 009, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:dbObjectFormat', 010, 'slicedWithIdAsKey'
)  
GO

INSERT INTO prefs_Preference 
    (object_rid, object_oid, object_idx, string_value) 
VALUES(
    'preference/OOMSE2', 'PERSISTENCE:dbObjectFormat', 011, 'slicedWithIdAsKey'
)  
GO
