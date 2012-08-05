#include "sql-definitions.tsql"

DELETE FROM OOCKE1_ACCESSHISTORY WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_ACCESSHISTORY_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_ACCESSHISTORY) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_ACCOUNT WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_ACCOUNT_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_ACCOUNT) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_ACCOUNTASSIGNMENT WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_ACCOUNTASSIGNMENT_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_ACCOUNTASSIGNMENT) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_ACTIVITY WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_ACTIVITY_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_ACTIVITY) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_ACTIVITYCREATOR WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_ACTIVITYCREATOR_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_ACTIVITYCREATOR) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_ACTIVITYEFFORTESTI WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_ACTIVITYEFFORTESTI_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_ACTIVITYEFFORTESTI) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_ACTIVITYFOLLOWUP WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_ACTIVITYFOLLOWUP_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_ACTIVITYFOLLOWUP) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_ACTIVITYGROUP WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_ACTIVITYGROUP_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_ACTIVITYGROUP) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_ACTIVITYGROUPASS WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_ACTIVITYGROUPASS_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_ACTIVITYGROUPASS) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_ACTIVITYLINK WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_ACTIVITYLINK_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_ACTIVITYLINK) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_ACTIVITYPARTY WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_ACTIVITYPARTY_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_ACTIVITYPARTY) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_ACTIVITYPROCACTION WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_ACTIVITYPROCACTION_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_ACTIVITYPROCACTION) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_ACTIVITYPROCESS WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_ACTIVITYPROCESS_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_ACTIVITYPROCESS) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_ACTIVITYPROCSTATE WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_ACTIVITYPROCSTATE_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_ACTIVITYPROCSTATE) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_ACTIVITYPROCTRANS WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_ACTIVITYPROCTRANS_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_ACTIVITYPROCTRANS) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_ACTIVITYTYPE WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_ACTIVITYTYPE_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_ACTIVITYTYPE) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_ADDITIONALEXTLINK WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_ADDITIONALEXTLINK_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_ADDITIONALEXTLINK) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_ADDRESS WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_ADDRESS_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_ADDRESS) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_ADDRESSGROUP WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_ADDRESSGROUP_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_ADDRESSGROUP) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_ADDRESSGROUPMEMBER WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_ADDRESSGROUPMEMBER_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_ADDRESSGROUPMEMBER) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_ALERT WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_ALERT_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_ALERT) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_AUDITENTRY WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_AUDITENTRY_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_AUDITENTRY) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_BOOKING WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_BOOKING_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_BOOKING) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_BOOKINGPERIOD WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_BOOKINGPERIOD_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_BOOKINGPERIOD) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_BOOKINGTEXT WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_BOOKINGTEXT_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_BOOKINGTEXT) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_BUDGET WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_BUDGET_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_BUDGET) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_BUILDINGUNIT WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_BUILDINGUNIT_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_BUILDINGUNIT) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_CALCULATIONRULE WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_CALCULATIONRULE_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_CALCULATIONRULE) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_CALENDAR WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_CALENDAR_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_CALENDAR) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_CALENDARDAY WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_CALENDARDAY_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_CALENDARDAY) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_CALENDARFEED WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_CALENDARFEED_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_CALENDARFEED) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_CALENDARPROFILE WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_CALENDARPROFILE_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_CALENDARPROFILE) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_CHART WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_CHART_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_CHART) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_CODEVALUECONTAINER WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_CODEVALUECONTAINER_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_CODEVALUECONTAINER) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_CODEVALUEENTRY WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_CODEVALUEENTRY_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_CODEVALUEENTRY) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_COMPETITOR WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_COMPETITOR_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_COMPETITOR) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_COMPONENTCONFIG WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_COMPONENTCONFIG_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_COMPONENTCONFIG) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_COMPOUNDBOOKING WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_COMPOUNDBOOKING_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_COMPOUNDBOOKING) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_CONTACTMEMBERSHIP WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_CONTACTMEMBERSHIP_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_CONTACTMEMBERSHIP) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_CONTACTREL WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_CONTACTREL_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_CONTACTREL) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_CONTACTROLE WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_CONTACTROLE_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_CONTACTROLE) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_CONTRACT WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_CONTRACT_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_CONTRACT) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_CONTRACTLINK WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_CONTRACTLINK_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_CONTRACTLINK) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_CONTRACTPOSITION WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_CONTRACTPOSITION_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_CONTRACTPOSITION) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_CONTRACTPOSMOD WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_CONTRACTPOSMOD_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_CONTRACTPOSMOD) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_CREDITLIMIT WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_CREDITLIMIT_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_CREDITLIMIT) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_DELIVERYINFO WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_DELIVERYINFO_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_DELIVERYINFO) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_DELIVERYREQUEST WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_DELIVERYREQUEST_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_DELIVERYREQUEST) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_DEPOT WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_DEPOT_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_DEPOT) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_DEPOTENTITY WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_DEPOTENTITY_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_DEPOTENTITY) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_DEPOTENTITYREL WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_DEPOTENTITYREL_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_DEPOTENTITYREL) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_DEPOTGROUP WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_DEPOTGROUP_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_DEPOTGROUP) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_DEPOTHOLDER WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_DEPOTHOLDER_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_DEPOTHOLDER) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_DEPOTPOSITION WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_DEPOTPOSITION_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_DEPOTPOSITION) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_DEPOTREFERENCE WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_DEPOTREFERENCE_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_DEPOTREFERENCE) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_DEPOTREPORT WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_DEPOTREPORT_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_DEPOTREPORT) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_DEPOTREPORTITEM WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_DEPOTREPORTITEM_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_DEPOTREPORTITEM) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_DEPOTTYPE WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_DEPOTTYPE_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_DEPOTTYPE) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_DESCRIPTION WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_DESCRIPTION_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_DESCRIPTION) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_DOCUMENT WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_DOCUMENT_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_DOCUMENT) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_DOCUMENTATTACHMENT WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_DOCUMENTATTACHMENT_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_DOCUMENTATTACHMENT) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_DOCUMENTFOLDER WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_DOCUMENTFOLDER_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_DOCUMENTFOLDER) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_DOCUMENTFOLDERASS WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_DOCUMENTFOLDERASS_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_DOCUMENTFOLDERASS) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_DOCUMENTLINK WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_DOCUMENTLINK_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_DOCUMENTLINK) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_DOCUMENTLOCK WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_DOCUMENTLOCK_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_DOCUMENTLOCK) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_EMAILACCOUNT WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_EMAILACCOUNT_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_EMAILACCOUNT) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_EVENT WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_EVENT_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_EVENT) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_EVENTPART WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_EVENTPART_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_EVENTPART) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_EVENTSLOT WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_EVENTSLOT_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_EVENTSLOT) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_EXPORTPROFILE WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_EXPORTPROFILE_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_EXPORTPROFILE) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_FACILITY WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_FACILITY_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_FACILITY) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_FILTER WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_FILTER_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_FILTER) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_FILTERPROPERTY WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_FILTERPROPERTY_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_FILTERPROPERTY) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_INDEXENTRY WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_INDEXENTRY_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_INDEXENTRY) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_INVENTORYITEM WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_INVENTORYITEM_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_INVENTORYITEM) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_INVOLVEDOBJECT WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_INVOLVEDOBJECT_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_INVOLVEDOBJECT) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_LINKABLEITEMLINK WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_LINKABLEITEMLINK_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_LINKABLEITEMLINK) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_MEDIA WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_MEDIA_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_MEDIA) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_MODELELEMENT WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_MODELELEMENT_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_MODELELEMENT) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_NOTE WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_NOTE_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_NOTE) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_OBJECTFINDER WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_OBJECTFINDER_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_OBJECTFINDER) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_ORGANIZATION WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_ORGANIZATION_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_ORGANIZATION) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_ORGANIZATIONALUNIT WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_ORGANIZATIONALUNIT_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_ORGANIZATIONALUNIT) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_ORGUNITRELSHIP WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_ORGUNITRELSHIP_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_ORGUNITRELSHIP) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_PRICELEVEL WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_PRICELEVEL_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_PRICELEVEL) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_PRICEMODIFIER WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_PRICEMODIFIER_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_PRICEMODIFIER) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_PRICINGRULE WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_PRICINGRULE_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_PRICINGRULE) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_PRODUCT WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_PRODUCT_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_PRODUCT) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_PRODUCTAPPLICATION WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_PRODUCTAPPLICATION_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_PRODUCTAPPLICATION) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_PRODUCTBASEPRICE WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_PRODUCTBASEPRICE_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_PRODUCTBASEPRICE) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_PRODUCTCLASS WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_PRODUCTCLASS_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_PRODUCTCLASS) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_PRODUCTCLASSREL WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_PRODUCTCLASSREL_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_PRODUCTCLASSREL) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_PRODUCTCONFIG WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_PRODUCTCONFIG_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_PRODUCTCONFIG) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_PRODUCTCONFTYPESET WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_PRODUCTCONFTYPESET_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_PRODUCTCONFTYPESET) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_PRODUCTPHASE WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_PRODUCTPHASE_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_PRODUCTPHASE) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_PRODUCTREFERENCE WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_PRODUCTREFERENCE_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_PRODUCTREFERENCE) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_PROPERTY WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_PROPERTY_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_PROPERTY) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_PROPERTYSET WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_PROPERTYSET_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_PROPERTYSET) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_QUICKACCESS WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_QUICKACCESS_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_QUICKACCESS) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_RASARTIFACTCONTEXT WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_RASARTIFACTCONTEXT_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_RASARTIFACTCONTEXT) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_RASARTIFACTDEP WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_RASARTIFACTDEP_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_RASARTIFACTDEP) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_RASCLASSIFICATIELT WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_RASCLASSIFICATIELT_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_RASCLASSIFICATIELT) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_RASDESCRIPTOR WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_RASDESCRIPTOR_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_RASDESCRIPTOR) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_RASPROFILE WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_RASPROFILE_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_RASPROFILE) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_RASSOLUTIONPART WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_RASSOLUTIONPART_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_RASSOLUTIONPART) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_RASVARPOINT WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_RASVARPOINT_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_RASVARPOINT) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_RATING WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_RATING_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_RATING) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_RELATEDPRODUCT WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_RELATEDPRODUCT_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_RELATEDPRODUCT) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_RESOURCE WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_RESOURCE_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_RESOURCE) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_RESOURCEASSIGNMENT WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_RESOURCEASSIGNMENT_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_RESOURCEASSIGNMENT) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_REVENUEREPORT WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_REVENUEREPORT_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_REVENUEREPORT) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_SALESTAXTYPE WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_SALESTAXTYPE_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_SALESTAXTYPE) COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_SEGMENT WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_SEGMENT_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_SEGMENT COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_SIMPLEBOOKING WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_SIMPLEBOOKING_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_SIMPLEBOOKING COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_SUBSCRIPTION WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_SUBSCRIPTION_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_SUBSCRIPTION COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_TOPIC WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_TOPIC_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_TOPIC COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_UOM WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_UOM_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_UOM COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_UOMSCHEDULE WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_UOMSCHEDULE_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_UOMSCHEDULE COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_USERHOME WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_USERHOME_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_USERHOME COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_VOTE WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_VOTE_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_VOTE COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_WFACTIONLOGENTRY WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_WFACTIONLOGENTRY_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_WFACTIONLOGENTRY COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_WFPROCESS WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_WFPROCESS_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_WFPROCESS COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_WFPROCESSINSTANCE WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_WFPROCESSINSTANCE_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_WFPROCESSINSTANCE COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCKE1_WORKRECORD WHERE object_id LIKE '%/{provider}/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCKE1_WORKRECORD_ WHERE object_id NOT IN (SELECT object_id FROM OOCKE1_WORKRECORD COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCSE1_PERMISSION WHERE object_id LIKE '%/{provider}/Root/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCSE1_PERMISSION_ WHERE object_id NOT IN (SELECT object_id FROM OOCSE1_PERMISSION COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCSE1_POLICY WHERE object_id LIKE '%/{provider}/Root/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCSE1_POLICY_ WHERE object_id NOT IN (SELECT object_id FROM OOCSE1_POLICY COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCSE1_PRINCIPAL WHERE object_id LIKE '%/{provider}/Root/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCSE1_PRINCIPAL_ WHERE object_id NOT IN (SELECT object_id FROM OOCSE1_PRINCIPAL COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCSE1_PRIVILEGE WHERE object_id LIKE '%/{provider}/Root/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCSE1_PRIVILEGE_ WHERE object_id NOT IN (SELECT object_id FROM OOCSE1_PRIVILEGE COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCSE1_REALM WHERE object_id LIKE '%/{provider}/Root/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCSE1_REALM_ WHERE object_id NOT IN (SELECT object_id FROM OOCSE1_REALM COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"

DELETE FROM OOCSE1_ROLE WHERE object_id LIKE '%/{provider}/Root/{segment}/%' COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
DELETE FROM OOCSE1_ROLE_ WHERE object_id NOT IN (SELECT object_id FROM OOCSE1_ROLE COMMAND_TERMINATOR 
#include "sql-command-separator.tsql"
