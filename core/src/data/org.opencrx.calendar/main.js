/*
CalDavZAP - the open source CalDAV Web Client
Copyright (C) 2011-2013
    Jan Mate <jan.mate@inf-it.com>
    Andrej Lezo <andrej.lezo@inf-it.com>
    Matej Mihalik <matej.mihalik@inf-it.com>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>.
*/

var globalEventList=new EventList();
var globalTODOlist=new GlobalTODOlist();
var globalAppleSupport = new AppleSupportNextDateArray();
var isResourceNumber=false;
var maxAlarmValue=2147000000;

var countPreviousEvents=0;
var countPreviousCalendars=0;
var globalThreadCounter=0;
var maximumThreadNumber=16;

var isDaylight=false;
var globalResourceCalDAVList=new ResourceCalDAVList();

var globalEventIntervalID=null;
var globalResourceRefreshNumber=0;
var globalResourceRefreshNumberTodo=0;
var globalCalDAVInitLoad=true;
var globalCalDAVResourceSync=false;
var globalCalDAVCollectionSync=false;
var globalCalendarNumber=0;
var globalOnlyCalendarNumber=0;
var globalTodoCalendarNumber=0;
var globalOnlyCalendarNumberCount=0;
var globalCalendarNumberCount=0;

var cleanResourceCalDAVListTemplate=null;
var cleanResourceCalDAVTODOListTemplate=null;
var cleanVcalendarTemplate=null;
var cleanVtodoTemplate=null;
var origResourceCalDAVListTemplate=null;
var origResourceCalDAVTODOListTemplate=null;
var origVcalendarTemplate=null;
var origVtodoTemplate=null;
var origVtodoLoaderTemplate=null;
var origLoaderTemplate=null;
var globalSessionTimeZone=null;
var globalCalDAVQs=null;
var globalCalDAVTODOQs=null;
var globalVisibleCalDAVCollections=new Array();
var globalVisibleCalDAVTODOCollections=new Array();
var isResourceComplete=false;
var prevFunctionArray=new Array();
var prevFunctionArrayIterator=0;
var globalWorkerArray=new Array();
var globalIntervalArray=new Array();
var globalResourceErrorCounter=0;
var globalCurrentLoadingResource='';
var timeZonesEnabled=new Array();
var cLcouny=42;
var timezoneKeys=new Array();
var timelist=new Array();
var minelems=[0,15,30,45];
var frequencies = ["SECONDLY", "MINUTELY", "HOURLY", "DAILY", "WEEKLY", "MONTHLY", "YEARLY"];
var globalEventDateStart='';
var globalEventDateEnd='';
var globalViewsList={'month':false, 'multiWeek':false, 'agendaWeek':false, 'agendaDay':false};
var globalToday=new Date();
var isCalDAVLoaded=false;
var isCalDAVAvaible=true;
var globalLoadedLimit = new Date();
var globalToLoadedLimit = new Date();
var globalLimitLoading='';
var globalBeginPast = new Date();
var globalBeginFuture = new Date();
var globalLoadedLimitTodo = new Date();
var globalToLoadedLimitTodo = new Date();
var globalDefaultCalendarCollectionActiveAll = false;
var globalDefaultTodoCalendarCollectionActiveAll = false;

var globalCalEvent=null;
var globalCalTodo=null;
var globalJsEvent=null;
var globalRevertFunction=null;
var globalPrevDragEventAllDay=null;
var globalPrevDate='';

var globalWindowFocus=true;
var globalLoginUsername='';
var globalLoginPassword='';
var isUserLogged=false;
var globalActiveApp='';
var globalAvailableAppsArray=new Array();
var globalEnableAppSwitch=true;
var globalAppName='CalDavZAP';
var globalVersion='0.9.42';
var globalVersionCheckURL=(location.protocol=='file:' ? 'http:' : location.protocol)+'//www.inf-it.com/versioncheck/'+globalAppName+'/?v='+globalVersion;
var globalXClientHeader=globalAppName+' '+globalVersion+' (Inf-IT CalDAV Web Client)';
var globalResourceNumberCount=0;
var globalResourceNumber=0;
var globalResourceIntervalID=null;
var globalFirstLoadNextApp=false;
var globalObjectLoading=false;
var settingsLoaded=false;
var globalKBNavigationPaddingRate=0.2;
var globalParallelAjaxCallCardDAVEnabled=true;
var globalParallelAjaxCallCalDAVEnabled=true;
var SVG_select='<svg version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:a="http://ns.adobe.com/AdobeSVGViewerExtensions/3.0/" x="0px" y="0px" width="19px" height="19px" viewBox="0 0 19 19" overflow="visible" enable-background="new 0 0 19 19" xml:space="preserve"><defs></defs><rect x="2" fill="#585858" width="17" height="19"/><polygon fill="#FFFFFF" points="14,7 10.5,13 7,7 "/><rect fill="#FFFFFF" width="2" height="19"/></svg>';
var SVG_select_b='<svg version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:a="http://ns.adobe.com/AdobeSVGViewerExtensions/3.0/" x="0px" y="0px" width="19px" height="19px" viewBox="0 0 19 19" overflow="visible" enable-background="new 0 0 19 19" xml:space="preserve"><defs></defs><rect x="2" fill="#585858" width="17" height="19"/><polygon fill="#FFFFFF" points="14,7 10.5,13 7,7 "/><rect fill="#2D2D2D" width="2" height="19"/></svg>';
var SVG_select_dis='<svg version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:a="http://ns.adobe.com/AdobeSVGViewerExtensions/3.0/" x="0px" y="0px" width="22px" height="19px" viewBox="0 0 22 19" overflow="visible" enable-background="new 0 0 22 19" xml:space="preserve"><defs></defs><rect fill="#FFFFFF" width="22" height="19"/></svg>';
var globalSettings={
			resourcealphabetsorting: (typeof globalResourceAlphabetSorting!='undefined' && globalResourceAlphabetSorting!=null && globalResourceAlphabetSorting!='') ? globalResourceAlphabetSorting : true,
			usejqueryauth: (typeof globalUseJqueryAuth!='undefined' && globalUseJqueryAuth!=null && globalUseJqueryAuth!='') ? globalUseJqueryAuth : false,
			settingstype: (typeof globalSettingsType!='undefined' && globalSettingsType!=null && globalSettingsType!='') ? globalSettingsType : 'principal-URL',
			defaultactiveapp: (typeof globalDefaultActiveApp!='undefined' && globalDefaultActiveApp!=null && globalDefaultActiveApp!='') ? globalDefaultActiveApp : null,
			isLastdefaultactiveapp : false,
			datepickerfirstdayofweek: (typeof globalDatepickerFirstDayOfWeek!='undefined' && globalDatepickerFirstDayOfWeek!=null && globalDatepickerFirstDayOfWeek!='') ? globalDatepickerFirstDayOfWeek : 1,
			syncresourcesinterval:(typeof globalSyncResourcesInterval!='undefined' && globalSyncResourcesInterval!=null && globalSyncResourcesInterval!='') ? globalSyncResourcesInterval :300000 ,
			datepickerformat:(typeof globalDatepickerFormat!='undefined' && globalDatepickerFormat!=null && globalDatepickerFormat!='') ? globalDatepickerFormat : localization[globalInterfaceLanguage]._default_datepicker_format_ ,
			backgroundsync:(typeof globalBackgroundSync!='undefined' && globalBackgroundSync!=null && globalBackgroundSync!='') ? globalBackgroundSync : true ,
			enablekbnavigation:(typeof globalEnableKbNavigation!='undefined' && globalEnableKbNavigation!=null && globalEnableKbNavigation!='') ? globalEnableKbNavigation : true ,
			rewritetimezonecomponent: (typeof globalRewriteTimezoneComponent!='undefined' && globalRewriteTimezoneComponent!=null && globalRewriteTimezoneComponent!='') ? globalRewriteTimezoneComponent : true,
			removeunknowntimezone: (typeof globalRemoveUnknownTimezone!='undefined' && globalRemoveUnknownTimezone!=null && globalRemoveUnknownTimezone!='') ? globalRemoveUnknownTimezone : false,
			mozillasupport: (typeof globalMozillaSupport!='undefined' && globalMozillaSupport!=null && globalMozillaSupport!='') ? globalMozillaSupport : false,
			appleremindersmode: (typeof globalAppleRemindersMode!='undefined' && globalAppleRemindersMode!=null && globalAppleRemindersMode!='') ? globalAppleRemindersMode : false,
			titleformatmonth: localization[globalInterfaceLanguage]._default_title_format_month_,
			titleformatweek: localization[globalInterfaceLanguage]._default_title_format_week_,
			titleformatday: localization[globalInterfaceLanguage]._default_title_format_day_,
			titleformattable: localization[globalInterfaceLanguage]._default_title_format_table_,
			columnformatagenda: localization[globalInterfaceLanguage]._default_column_format_agenda_,
			timeformatlist: localization[globalInterfaceLanguage]._default_time_format_list_,
			activecalendarcollections: (typeof globalActiveCalendarCollections!='undefined' && globalActiveCalendarCollections!=null && globalActiveCalendarCollections!='') ? globalActiveCalendarCollections : new Array(),
			activetodocollections: (typeof globalActiveTodoCollections!='undefined' && globalActiveTodoCollections!=null && globalActiveTodoCollections!='') ? globalActiveTodoCollections : new Array(),
			todolistfilterselected: (typeof globalTodoListFilterSelected!='undefined' && globalTodoListFilterSelected!=null && globalTodoListFilterSelected!='') ? globalTodoListFilterSelected : ['filterAction', 'filterProgress'],
			activeview: (typeof globalActiveView!='undefined' && globalActiveView!=null && globalActiveView!='') ? globalActiveView : 'multiWeek',
			islastactiveview : true,
			calendarselected: (typeof globalCalendarSelected!='undefined' && globalCalendarSelected!=null && globalCalendarSelected!='') ? globalCalendarSelected : '',
			todocalendarselected: (typeof globalTodoCalendarSelected!='undefined' && globalTodoCalendarSelected!=null && globalTodoCalendarSelected!='') ? globalTodoCalendarSelected : '',
			timezone: (typeof globalTimeZone!='undefined' && globalTimeZone!=null && globalTimeZone!='') ? globalTimeZone : 'local',
			islasttimezone:true,
			openformmode: (typeof globalOpenFormMode!='undefined' && globalOpenFormMode!=null && globalOpenFormMode!='') ? globalOpenFormMode : 'double',
			calendarstartofbusiness: (typeof globalCalendarStartOfBusiness!='undefined' && globalCalendarStartOfBusiness!=null && globalCalendarStartOfBusiness!='') ? globalCalendarStartOfBusiness : 8,
			calendarendofbusiness: (typeof globalCalendarEndOfBusiness!='undefined' && globalCalendarEndOfBusiness!=null && globalCalendarEndOfBusiness!='') ? globalCalendarEndOfBusiness : 17,
			ampmformat: (typeof globalAMPMFormat!='undefined' && globalAMPMFormat!=null && globalAMPMFormat!='') ? globalAMPMFormat : localization[globalInterfaceLanguage]._default_AMPM_format_,
			timeformatagenda: (typeof globalTimeFormatAgenda!='undefined' && globalTimeFormatAgenda!=null && globalTimeFormatAgenda!='') ? globalTimeFormatAgenda : null,
			timeformatbasic: (typeof globalTimeFormatBasic!='undefined' && globalTimeFormatBasic!=null && globalTimeFormatBasic!='') ? globalTimeFormatBasic : null,
			displayhiddenevents: (typeof globalDisplayHiddenEvents!='undefined' && globalDisplayHiddenEvents!=null && globalDisplayHiddenEvents!='') ? globalDisplayHiddenEvents : false,
			timezonesupport: (typeof globalTimeZoneSupport!='undefined' && globalTimeZoneSupport!=null && globalTimeZoneSupport!='') ? globalTimeZoneSupport : true,
			timezonesenabled: (typeof globalTimeZonesEnabled!='undefined' && globalTimeZonesEnabled!=null && globalTimeZonesEnabled!='') ? globalTimeZonesEnabled : [],
			showhiddenalarms: (typeof globalShowHiddenAlarms!='undefined' && globalShowHiddenAlarms!=null && globalShowHiddenAlarms!='') ? globalShowHiddenAlarms : false,
			ignorecompletedorcancelledalarms :(typeof globalIgnoreCompletedOrCancelledAlarms!='undefined' && globalIgnoreCompletedOrCancelledAlarms!=null && globalIgnoreCompletedOrCancelledAlarms!='') ? globalIgnoreCompletedOrCancelledAlarms : true,
			weekenddays :(typeof globalWeekendDays!='undefined' && globalWeekendDays!=null && globalWeekendDays!='') ? globalWeekendDays : [0, 6],
			eventstartpastlimit : (typeof globalEventStartPastLimit!='undefined' && globalEventStartPastLimit!=null && globalEventStartPastLimit!='') ? globalEventStartPastLimit : 3,
			eventstartfuturelimit : (typeof globalEventStartFutureLimit!='undefined' && globalEventStartFutureLimit!=null && globalEventStartFutureLimit!='') ? globalEventStartFutureLimit : 3,
			compatibility:(typeof globalCompatibility!='undefined' && globalCompatibility!=null && globalCompatibility!='') ? globalCompatibility : {anniversaryOutputFormat: ['apple']},
			contactstorefn:(typeof globalContactStoreFN!='undefined' && globalContactStoreFN!=null && globalContactStoreFN!='') ? globalContactStoreFN : {anniversaryOutputFormat: ['prefix',' last',' middle',' first',' suffix']},
			urihandlertel:(typeof globalUriHandlerTel!='undefined' && globalUriHandlerTel!=null && globalUriHandlerTel!='') ? globalUriHandlerTel : {anniversaryOutputFormat: 'tel:'},
			urihandleremail:(typeof globalUriHandlerEmail!='undefined' && globalUriHandlerEmail!=null && globalUriHandlerEmail!='') ? globalUriHandlerEmail : 'mailto:',
			urihandlerurl:(typeof globalUriHandlerUrl!='undefined' && globalUriHandlerUrl!=null && globalUriHandlerUrl!='') ? globalUriHandlerUrl : 'http://',
			urihandlerprofile:(typeof globalUriHandlerProfile!='undefined' && globalUriHandlerProfile!=null && globalUriHandlerProfile!='') ? globalUriHandlerProfile : {'twitter': 'http://twitter.com/%u', 'facebook': 'http://www.facebook.com/%u', 'flickr': 'http://www.flickr.com/photos/%u', 'linkedin': 'http://www.linkedin.com/in/%u', 'myspace': 'http://www.myspace.com/%u', 'sinaweibo': 'http://weibo.com/n/%u'},
			addresscountryequivalence:(typeof globalAddressCountryEquivalence!='undefined' && globalAddressCountryEquivalence!=null && globalAddressCountryEquivalence!='') ? globalAddressCountryEquivalence : [{country: 'de', regex: '^\\W*Deutschland\\W*$'}, {country: 'sk', regex: '^\\W*Slovensko\\W*$'}],
			addressbookselected: (typeof globalAddressbookSelected!='undefined' && globalAddressbookSelected!=null && globalAddressbookSelected!='') ? globalAddressbookSelected : '',
			collectionsort: (typeof globalCollectionSort!='undefined' && globalCollectionSort!=null && globalCollectionSort!='') ? globalCollectionSort : ['last','middle','first'],
			collectiondisplay: (typeof globalCollectionDisplay!='undefined' && globalCollectionDisplay!=null && globalCollectionDisplay!='') ? globalCollectionDisplay :['last',' middle',' first'],
			collectiondisplayorg: (typeof globalCollectionDisplayOrg!='undefined' && globalCollectionDisplayOrg!=null && globalCollectionDisplayOrg!='') ? globalCollectionDisplayOrg :true,
			defaultaddresscountry: (typeof globalDefaultAddressCountry!='undefined' && globalDefaultAddressCountry!=null && globalDefaultAddressCountry!='') ? globalDefaultAddressCountry :'us',
			addresscountryfavorites: (typeof globalAddressCountryFavorites!='undefined' && globalAddressCountryFavorites!=null && globalAddressCountryFavorites!='') ? globalAddressCountryFavorites :[],
			activeaddressbookcollections: (typeof globalActiveAddressbookCollections!='undefined' && globalActiveAddressbookCollections!=null && globalActiveAddressbookCollections!='') ? globalActiveAddressbookCollections : new Array()
};
//var globalSettingsXML = '<settings><resourcealphabetsorting type="boolean">true</resourcealphabetsorting><usejqueryauth type="boolean">false</usejqueryauth><settingstype type="string">calendar-home-set</settingstype><syncresourcesinterval type="number">300000</syncresourcesinterval><backgroundsync type="boolean">true</backgroundsync><enablekbnavigation type="boolean">true</enablekbnavigation><defaultactiveapp type="string">CalDavZAP</defaultactiveapp><datepickerfirstdayofweek type="number">1</datepickerfirstdayofweek><datepickerformat type="string"></datepickerformat><rewritetimezonecomponent type="boolean">true</rewritetimezonecomponent><removeunknowntimezone type="boolean">false</removeunknowntimezone><mozillasupport type="boolean">false</mozillasupport><appleremindersmode type="boolean">true</appleremindersmode><titleformatmonth type="string">MMM yyyy</titleformatmonth><titleformatweek type="string">d[ MMM][ yyyy]{ \'&#8212;\' d MMM yyyy}</titleformatweek><titleformatday type="string">ddd, d MMM yyyy</titleformatday><titleformattable type="string">ddd, MMM yyyy</titleformattable><columnformatagenda type="string">ddd, d MMM</columnformatagenda><timeformatlist type="string">d.M.yyyy</timeformatlist><calendarstartofbusiness type="number">8</calendarstartofbusiness><calendarendofbusiness type="number">17</calendarendofbusiness><eventstartpastlimit type="number">2</eventstartpastlimit><timezone type="string">Europe/Berlin</timezone><activeview type="string">multiWeek</activeview><openformmode type="string">double</openformmode><timeformatbasic type="string"/><timeformatagenda type="string"/><weekenddays type="number"><item>1</item><item>2</item></weekenddays><ampmformat type="boolean">false</ampmformat><displayhiddenevents type="boolean">false</displayhiddenevents><showhiddenalarms type="boolean">false</showhiddenalarms><ignorecompletedorcancelledalarms type="boolean">true</ignorecompletedorcancelledalarms><timezonesupport type="boolean">true</timezonesupport><timezonesenabled type="string"><item>Europe/Bratislava</item></timezonesenabled/><compatibility type="string"><anniversaryOutputFormat><item>apple</item><item>other</item></anniversaryOutputFormat></compatibility><contactstorefn type="string"><item>prefix</item><item>last</item><item>middle</item><item>first</item></contactstorefn><urihandlertel type="string">tel:</urihandlertel><urihandleremail type="string">mailto:</urihandleremail><urihandlerurl type="string">http://</urihandlerurl><urihandlerprofile type="string"><twitter url="http://twitter.com/%u" /><facebook url="http://www.facebook.com/%u" /></urihandlerprofile><addresscountryequivalence type="string"><item name="sk" regex= "^\\W*Slovenskooo\\W*$"/><item name="gb" regex="^\\W*Eugen\\W*$"/></addresscountryequivalence><collectionsort type="string"><item>last</item><item>middle</item><item>first</item></collectionsort><collectiondisplay type="string"><item>last</item><item>middle</item><item>first</item></collectiondisplay><collectiondisplayorg type="boolean">true</collectiondisplayorg><defaultaddresscountry type="string">us</defaultaddresscountry><addresscountryfavorites type="string"><item>de</item><item>sk</item></addresscountryfavorites></settings>';
// Timepicker hack (prevent IE to re-open the datepicker on date click + focus)
var globalTmpTimePickerHackTime=new Object();

function isAvaible(app)
{
	return globalAvailableAppsArray.indexOf(app)!=-1
}

function loadAllResources()
{
	if(globalResourceIntervalID==null)
		netFindResource(globalAccountSettings[0], 0, true, 0);
}

function reloadResources() 
{
	if((isAvaible('CardDavMATE') && (globalCardDAVInitLoad || globalCardDAVResourceSync)) || (isAvaible('CalDavZAP') && (globalCalDAVInitLoad || globalCalDAVResourceSync))
	|| (isAvaible('CalDavZAP') && globalLimitLoading!=''))
		return false;
	if(globalWindowFocus==false)
		return false;
	globalCardDAVResourceSync=true;
	globalCalDAVResourceSync=true;
	if(isAvaible('CalDavZAP'))
	{
		globalCalDAVResourceSync = true;
		globalToday.setHours(0);
		globalToday.setMinutes(0);
		globalToday.setSeconds(0); 
		globalToday.setMilliseconds(0);

		var currentToday=new Date();
		currentToday.setHours(0);
		currentToday.setMinutes(0);
		currentToday.setSeconds(0); 
		currentToday.setMilliseconds(0);
		if(currentToday.getTime()!=globalToday.getTime())
		{
			globalViewsList['month']=true;
			globalViewsList['multiWeek']=true;
			globalViewsList['agendaWeek']=true;
			globalViewsList['agendaDay']=true;
			$('#calendar').fullCalendar('updateGrid');
			$('#calendar').fullCalendar('gotoDate', currentToday);
			globalViewsList[$('#calendar').fullCalendar('getView').name]=false;
			$('#todoList').fullCalendar('gotoDate', currentToday);
			if(currentToday.getTime()>globalToday.getTime())
			{
				getNextMonths($('#calendar').fullCalendar('getView').end);
				getNextMonthsTodo();
			}
			else //support for timezone with backward time flow
			{
				getPrevMonths($('#calendar').fullCalendar('getView').start);
				getPrevMonthsTodo();
			}
				
			globalToday=currentToday;
			
		}
	}
	netFindResource(globalAccountSettings[0], 0, false, 0);
	checkBeforeClose(false);
}
function ifLoadCollections()
{
	if((isAvaible('CardDavMATE') && (globalCardDAVInitLoad || globalCardDAVResourceSync)) || (isAvaible('CalDavZAP') && (globalCalDAVInitLoad || globalCalDAVResourceSync)))
		return false;
		
	var changeCounter = 0;
	if(isAvaible('CardDavMATE'))
		for(var i=0; i<globalResourceCardDAVList.collections.length;i++)
			if(globalResourceCardDAVList.collections[i].uid!=undefined && globalResourceCardDAVList.collections[i].someChanged)
			changeCounter++;
	
	if(isAvaible('CalDavZAP'))
		for(var i=0; i<globalResourceCalDAVList.collections.length;i++)
			if(globalResourceCalDAVList.collections[i].uid!=undefined && globalResourceCalDAVList.collections[i].someChanged)
			changeCounter++;
	
	if(isAvaible('CalDavZAP'))
		for(var i=0; i<globalResourceCalDAVList.TodoCollections.length;i++)
			if(globalResourceCalDAVList.TodoCollections[i].uid!=undefined && globalResourceCalDAVList.TodoCollections[i].someChanged)
			changeCounter++;
	if(changeCounter>0)
	{
		loadNextApplication(false);
	}
}

function loadNextApplication(forceLoad)
{
	if(!globalFirstLoadNextApp)
	{
		if(isAvaible('CardDavMATE'))
			setAddressbookNumber();
		if(isAvaible('CalDavZAP'))
			setCalendarNumber();
		globalFirstLoadNextApp=true;
			
	}
	if(isAvaible('CardDavMATE') && !globalCardDAVCollectionSync && globalResourceCardDAVList.collections.length>0)
	{
		globalCardDAVCollectionSync=true;
		CardDAVnetLoadCollection(globalResourceCardDAVList.collections[0], forceLoad, false, null, 0, globalResourceCardDAVList.collections,true);
	}
	else if(isAvaible('CalDavZAP') && !globalCalDAVCollectionSync && globalResourceCalDAVList.collections.length>0)
	{
		globalCalDAVCollectionSync=true;
		CalDAVnetLoadCollection(globalResourceCalDAVList.collections[0], forceLoad, true, 0, globalResourceCalDAVList.collections);
	}
	else if(isAvaible('Projects') && !globalProjectSync && !isProjectsLoaded && getLoggedUser()!=null)
	{
		$('#MainLoaderInner').html('Loading projects');
		globalProjectSync=true;
		netLoadXSLT('projects/transformations.xsl');
	}
	else if(isAvaible('Settings') && !globalSettingsSync && !isSettingsLoaded && getLoggedUser()!=null)
	{
		globalSettingsSync = true;
		if(!isSettingsLoaded)
			loadNextApplication(false);
		//TODO
	}
	else
	{
		if((isAvaible('CalDavZAP') && !isCalDAVLoaded) || (isAvaible('CardDavMATE') && !isCardDAVLoaded))
			$('#MainLoader').fadeOut(1200, function(){$('#MainLoader').css('left','50px');});
		if(isAvaible('CardDavMATE'))
		{
			globalCardDAVCollectionSync=false;
			if(!isCardDAVLoaded)
				isCardDAVLoaded=true;
		}
		if(isAvaible('CalDavZAP'))
		{
			globalCalDAVCollectionSync=false;
			if(!isCalDAVLoaded)
				isCalDAVLoaded=true;
		}
		if(isAvaible('Projects'))
		{
			globalProjectSync=false;
			isProjectsLoaded = true;
		}
		if(isAvaible('Settings'))
		{
			globalSettingsSync=false;
			isSettingsLoaded = true;
		}
	}
}

function checkForApplication(inputApp)
{
	if(!globalEnableAppSwitch || globalObjectLoading)
		return false;

	globalEnableAppSwitch=false;
	globalActiveApp=inputApp;
	if(inputApp=='CalDavZAP')
	{
		$('#SystemCardDAV, #SystemCalDAVTODO, #SystemSettings, #SystemProjects').animate({opacity : 0}, 666, function(){
			$('#SystemCardDAV, #SystemCalDAVTODO, #SystemSettings, #SystemProjects').css('visibility','hidden');
		});
		$('#SystemCalDAV').css('visibility','visible');
		$('#SystemCalDAV').animate({opacity : 1}, 666, function(){globalEnableAppSwitch=true;});
	}
	else if(inputApp=='CalDavTODO')
	{
		$('#SystemCardDAV, #SystemCalDAV, #SystemSettings, #SystemProjects').animate({opacity : 0}, 666, function(){
			$('#SystemCardDAV, #SystemCalDAV, #SystemSettings, #SystemProjects').css('visibility','hidden');
		});
		$('#SystemCalDAVTODO').css('visibility','visible');
		$('#SystemCalDAVTODO').animate({opacity : 1}, 666, function(){globalEnableAppSwitch=true;});
	}
	else if(inputApp=='CardDavMATE')
	{
		$('#SystemCalDAV, #SystemCalDAVTODO, #SystemSettings, #SystemProjects').animate({opacity : 0},666,function(){
			$('#SystemCalDAV, #SystemCalDAVTODO, #SystemSettings, #SystemProjects').css('visibility','hidden');
		});
		$('#SystemCardDAV').css('visibility','visible');
		$('#SystemCardDAV').animate({opacity : 1}, 666, function(){globalEnableAppSwitch=true;});
	}
	else if(inputApp=='Settings')
	{
		if(!$('#ResourceSettingsList').find('.resourceSettings_selected').length)
			$('#ResourceSettingsList').children().eq(0).children().trigger('click');

		$('#SystemCardDAV, #SystemCalDAV, #SystemCalDAVTODO, #SystemProjects').animate({opacity : 0},666,function(){
			$('#SystemCardDAV, #SystemCalDAV, #SystemCalDAVTODO, #SystemProjects').css('visibility','hidden');
		});
		$('#SystemSettings').css('visibility','visible');
		$('#SystemSettings').animate({opacity : 1}, 666, function(){globalEnableAppSwitch=true;});
	}
	else if(inputApp=='Projects')
	{
		$('#SystemCardDAV, #SystemCalDAV, #SystemCalDAVTODO, #SystemSettings').animate({opacity : 0},666,function(){
			$('#SystemCardDAV, #SystemCalDAV, #SystemCalDAVTODO, #SystemSettings').css('visibility','hidden');
		});
		$('#SystemProjects').css('visibility','visible');
		$('#SystemProjects').animate({opacity : 1}, 666, function(){globalEnableAppSwitch=true;});
	}
}


function getLoggedUser()
{
	for(var i=0; i<globalAccountSettings.length;i++)
		if(globalAccountSettings[i].href.indexOf(globalLoginUsername)!=-1)
			return globalAccountSettings[i];
	return null;
}

function login()
{
	$('#LoginLoader').fadeTo(1200, 1, function(){
		globalLoginUsername=$('#LoginPage').find('[data-type="system_username"]').val();
		globalLoginPassword=$('#LoginPage').find('[data-type="system_password"]').val();
		loadConfig();
	});
}

function logout()
{
	if(globalResourceIntervalID!=null)
		clearInterval(globalResourceIntervalID);
	if(globalFirstLoadNextApp)
		globalFirstLoadNextApp=false;
	settingsLoaded=false;
	//save settings
	checkBeforeClose(false);
	globalResourceIntervalID=null;
	globalLoginUsername='';
	globalLoginPassword='';
	globalResourceNumber=0;
	globalResourceNumberCount=0;
	$('#LoginPage').fadeTo(2000, 1, function(){
		if(typeof isCalDAVLoaded!='undefined' && isCalDAVLoaded)
		{
			logoutCalDAV();
			isCalDAVLoaded=false;
		}

		if(typeof isCardDAVLoaded!='undefined' && isCardDAVLoaded)
		{
			logoutCardDAV();
			isCardDAVLoaded=false;
		}
		if(typeof isProjectsLoaded!='undefined' && isProjectsLoaded)
		{
			 logoutProjects();
			isProjectsLoaded = false;
		}
		if(typeof isSettingsLoaded!='undefined' && isSettingsLoaded)
		{
			 logoutSettings();
			isSettingsLoaded = false;
		}

		for(var i=globalAccountSettings.length-1;i>=0;i--)
			if(globalAccountSettings[i].type=='network')
				globalAccountSettings.splice(i, 1);

		if(typeof globalDemoMode=='undefined')
		{
			$('[data-type="system_username"]').val('').change();
			$('[data-type="system_password"]').val('').change();
		}

		$('.integration_d').hide();

		isUserLogged=false;

		if(globalSettings.defaultactiveapp==null)
		{
			if(isAvaible('CalDavZAP'))
				globalActiveApp='CalDavZAP';
			else if(isAvaible('CardDavMATE'))
				globalActiveApp='CardDavMATE';
		}
		else
			globalActiveApp=globalSettings.defaultactiveapp;

		if(isAvaible('CardDavMATE'))
			mainCardDAV();
		if(isAvaible('CalDavZAP'))
			mainCalDAV();
		if(isAvaible('Settings')) 
			mainSettings();
		if(isAvaible('Projects')) 
			mainProjects();
	});
}

function init()
{
	// browser check
	if(($.browser.msie && parseInt($.browser.version, 10)<9) || $.browser.opera)
		$('#login_message').css('display','').find('td').text(localization[globalInterfaceLanguage].unsupportedBrowser);

	if(typeof globalDemoMode!='undefined')
	{
		if(typeof globalDemoMode.userName!=undefined)
			$('[data-type="system_username"]').val(globalDemoMode.userName).change();
		if(typeof globalDemoMode.userPassword!=undefined)
			$('[data-type="system_password"]').val(globalDemoMode.userPassword).change();
	}

	if(globalAvailableAppsArray.length==2 && isAvaible('CalDavZAP'))
		setLogoCalDAV();

	loadConfig();
}

function run()
{
	isUserLogged=true;
	window.onfocus=function(){globalWindowFocus=true;}
	window.onblur=function(){if(globalSettings.backgroundsync==false) globalWindowFocus=false;}
	$('#LoginPage').fadeOut(2000);

	if(typeof globalAccountSettings=='undefined')
	{
		console.log('Error: \'no account configured\': see config.js!');
		return false;
	}

	if(typeof globalNewVersionNotifyUsers=='undefined' || globalNewVersionNotifyUsers!=null)
		netVersionCheck();

	// Automatically detect crossDomain settings
	var detectedHref=location.protocol+'//'+location.hostname+(location.port ? ':'+location.port : '');
	for(var i=0;i<globalAccountSettings.length;i++)
	{
		if(globalAccountSettings[i].crossDomain==undefined || typeof globalAccountSettings[i].crossDomain!='boolean')
		{
			if(globalAccountSettings[i].href.indexOf(detectedHref)==0)
				globalAccountSettings[i].crossDomain=false;
			else
				globalAccountSettings[i].crossDomain=true;

			console.log("Info: [account: '"+globalAccountSettings[i].href.replace('\/\/', '//'+globalAccountSettings[i].userAuth.userName+'@')+"'] crossDomain set to: '"+(globalAccountSettings[i].crossDomain==true ? 'true' : 'false')+"'");
		}
	}

	if(typeof globalAvailableAppsArray!='undefined' && globalAvailableAppsArray!=null && globalAvailableAppsArray.length>1)
	{
		$('.integration_d').css('display', 'block');
		if(globalAvailableAppsArray.indexOf('CalDavZAP')!=-1) {
			$('#intCaldav').attr({'alt':localization[globalInterfaceLanguage].txtCalendar, 'title':localization[globalInterfaceLanguage].txtCalendar});
			$('#intCaldav').css('display', 'block');
		}
		if(globalAvailableAppsArray.indexOf('CalDavTODO')!=-1) {
			$('#intCaldavTodo').attr({'alt':localization[globalInterfaceLanguage].txtTodo, 'title':localization[globalInterfaceLanguage].txtTodo});
			$('#intCaldavTodo').css('display', 'block');
		}
		if(globalAvailableAppsArray.indexOf('CardDavMATE')!=-1) {
			$('#intCarddav').attr({'alt':localization[globalInterfaceLanguage].txtAddressbook, 'title':localization[globalInterfaceLanguage].txtAddressbook});
			$('#intCarddav').css('display', 'block');
		}
		if(globalAvailableAppsArray.indexOf('Projects')!=-1) {
			$('#intProjects').attr({'alt':localization[globalInterfaceLanguage].txtProjects, 'title':localization[globalInterfaceLanguage].txtProjects});
			$('#intProjects').css('display', 'block');
		}
		if(globalAvailableAppsArray.indexOf('Settings')!=-1) {
			$('#intSettings').attr({'alt':localization[globalInterfaceLanguage].txtSettings, 'title':localization[globalInterfaceLanguage].txtSettings});
			$('#intSettings').css('display', 'block');
		}
	}
}

function loadConfig()
{
	if(isUserLogged)	// !!!!!! kedy moze toto nastat? nexapem ...
		return false;

	var configLoaded=true;
	// Automatically detect crossDomain settings
	var detectedHref=location.protocol+'//'+location.hostname+(location.port ? ':'+location.port : '');

	// check username and password against the server and create config from globalNetworkCheckSettings
	if(typeof globalNetworkCheckSettings!='undefined' && globalNetworkCheckSettings!=null)
	{
		if(globalLoginUsername=='' || globalLoginPassword=='')
		{
			$('#LoginPage').fadeTo(500, 1, function(){if(typeof globalDemoMode=='undefined') $('[data-type="system_username"]').focus()});
			$('#LoginLoader').fadeOut(1200);
			return false;
		}
		else
		{
			if(globalNetworkCheckSettings.crossDomain==undefined || typeof globalNetworkCheckSettings.crossDomain!='boolean')
			{
				if(globalNetworkCheckSettings.href.indexOf(detectedHref)==0)
					globalNetworkCheckSettings.crossDomain=false;
				else
					globalNetworkCheckSettings.crossDomain=true;

				console.log("Info: [globalNetworkCheckSettings: '"+globalNetworkCheckSettings.href+"'] crossDomain set to: '"+(globalNetworkCheckSettings.crossDomain==true ? 'true' : 'false')+"'");
			}

// !!!! preco sa riesi s logout buttonom prave tu?
			// show the logout button
			if(typeof globalAvailableAppsArray!='undefined' && globalAvailableAppsArray!=null && globalAvailableAppsArray.length>1)
			{
				$('#intLogout').css('display', 'block');
				$('#intLogout').attr({'alt':localization[globalInterfaceLanguage].altLogout, 'title':localization[globalInterfaceLanguage].altLogout});
			}
			else
			{
				$('#Logout').css('display', 'block');
				$('#logoutShower').css('display', 'block');
				$('#logoutSettings').css('display', 'block');
			}

			netCheckAndCreateConfiguration(globalNetworkCheckSettings);
			return true;
		}
	}

	// load the configuration XML(s) from the network
	if(typeof globalNetworkAccountSettings!='undefined' && globalNetworkAccountSettings!=null)
	{
		if(globalLoginUsername=='' || globalLoginPassword=='')
		{
			$('#LoginPage').fadeTo(500, 1, function(){if(typeof globalDemoMode=='undefined') $('[data-type="system_username"]').focus()});
			$('#LoginLoader').fadeOut(1200);
			return false;
		}
		else
		{
			if(globalNetworkAccountSettings.crossDomain==undefined || typeof globalNetworkAccountSettings.crossDomain!='boolean')
			{
				if(globalNetworkAccountSettings.href.indexOf(detectedHref)==0)
					globalNetworkAccountSettings.crossDomain=false;
				else
					globalNetworkAccountSettings.crossDomain=true;

				console.log("Info: [globalNetworkAccountSettings: '"+globalNetworkAccountSettings.href+"'] crossDomain set to: '"+(globalNetworkAccountSettings.crossDomain==true ? 'true' : 'false')+"'");
			}
// !!!! preco sa riesi s logout buttonom prave tu?
			// show the logout button
			if(typeof globalAvailableAppsArray!='undefined' && globalAvailableAppsArray!=null && globalAvailableAppsArray.length>1)
			{
				$('#intLogout').css('display', 'block');
				$('#intLogout').attr({'alt':localization[globalInterfaceLanguage].altLogout, 'title':localization[globalInterfaceLanguage].altLogout});
			}
			else
			{
				$('#Logout').css('display', 'block');
				$('#logoutShower').css('display', 'block');
				$('#logoutSettings').css('display', 'block');
			}
			netLoadConfiguration(globalNetworkAccountSettings);
			return true;
		}
	}

	if((typeof globalNetworkAccountSettings=='undefined' || globalNetworkAccountSettings==null) && (typeof globalNetworkCheckSettings=='undefined' || globalNetworkCheckSettings==null) && (typeof globalAccountSettings!='undefined' && globalAccountSettings!=null) && globalAccountSettings.length>0)
	{
		var delegCount=0, delegIndex=0;
		for(var i=0; i<globalAccountSettings.length; i++)
			if((typeof globalAccountSettings[i].delegation=='boolean' && globalAccountSettings[i].delegation) || (globalAccountSettings[i].delegation instanceof Array && globalAccountSettings[i].delegation.length>0))
				delegIndex=i;

		for(var i=0; i<globalAccountSettings.length; i++)
			if((typeof globalAccountSettings[i].delegation=='boolean' && globalAccountSettings[i].delegation) || (globalAccountSettings[i].delegation instanceof Array && globalAccountSettings[i].delegation.length>0))
			{
				delegCount++;
				DAVresourceDelegation(globalAccountSettings[i], i, delegIndex);
			}

		if(delegCount==0)
		{
			// start the client
			if(isAvaible('CardDavMATE'))
				runCardDAV();
			if(isAvaible('CalDavZAP'))
				runCalDAV();
			if(isAvaible('Projects'))
				runProjects();
			if(isAvaible('Settings'))
				runSettings();

			globalResourceNumber=globalAccountSettings.length;
			loadAllResources();
		}
	}
}

function globalMain()
{
	/*************************** BAD HACKS SECTION ***************************/
	// here we fix the cross OS/cross broser problems (unfixable in pure CSS)
	if($.browser.webkit && !!window.chrome)	/* Chrome */
	{
		if(navigator.platform.toLowerCase().indexOf('win')==0)	/* Windows version */
		{
			$('#LoginPage, #vCardTemplate, #event_details_template, #todo_details_template, #SettingsTemplate').find('input').css('text-indent', '2px');
			$('#LoginPage, #vCardTemplate, #event_details_template, #todo_details_template, #SettingsTemplate').find('select').css({'padding-left': '0px', 'padding-right': '13px'});
		}
		else	/* non-Windows version */
			$('#LoginPage, #vCardTemplate, #event_details_template, #todo_details_template, #SettingsTemplate').find('input').css('text-indent', '1px');
	}
	else if($.browser.msie)	/* IE */
	{
		if(parseInt($.browser.version, 10)==10)	/* IE 10 (because there are no more conditional comments) */
		{
			$('select').css({'padding-top': '1px', 'padding-left': '0px', 'padding-right': '0px'});
			$('textarea').css('padding-top', '3px');
			$('input[type=button]').css('padding-top', '2px');
		}

		// ADD SVG to login screen
		var newSVG=$(SVG_select).attr('data-type', 'select_icon').css({'pointer-events': 'none', 'z-index': '1', 'display': 'inline', 'margin-left': '-19px', 'vertical-align': 'top', 'background-color': '#ffffff'});	// background-color = stupid IE9 bug
		$('#Login').find('select[data-type="language"]').after($($('<div>').append($(newSVG).clone()).html()));
	}
	else if($.browser.mozilla)
	{
		// ADD SVG to login screen
		var newSVG=$(SVG_select).attr('data-type', 'select_icon').css({'pointer-events': 'none', 'z-index': '1', 'display': 'inline', 'margin-left': '-19px', 'vertical-align': 'top', 'background-color': '#ffffff'});	// background-color = stupid IE9 bug
		$('#Login').find('select[data-type="language"]').after($($('<div>').append($(newSVG).clone()).html()));
	}
	/*************************** END OF BAD HACKS SECTION ***************************/

	/* language selector */
	var lang_num=0;
	var language_option=$('#Login').find('[data-type="language"]').find('option');
	$('#Login').find('[data-type="language"]').html('');

	if(typeof globalInterfaceCustomLanguages!='undefined' && globalInterfaceCustomLanguages.length!=undefined && globalInterfaceCustomLanguages.length>0)
	{
		for(var i=0; i<globalInterfaceCustomLanguages.length; i++)
			if(localization[globalInterfaceCustomLanguages[i]]!=undefined)
			{
				var tmp=language_option;
				tmp.attr('data-type',globalInterfaceCustomLanguages[i]);
				tmp.text(localization[globalInterfaceCustomLanguages[i]]['_name_']);
				$('#Login').find('[data-type="language"]').append(tmp.clone());
				lang_num++;
			}
	}

	if(lang_num==0)	// no language option, use the default (all languages from localization.js)
		for(var loc in localization)
		{
			var tmp=language_option;
			tmp.attr('data-type',loc);
			tmp.text(localization[loc]['_name_']);	// translation
			$('#Login').find('[data-type="language"]').append(tmp.clone());
		}

	if(typeof globalEnabledApps=='undefined' || globalEnabledApps==null)
	{
		if(typeof isCalDAVAvaible!='undefined' && isCalDAVAvaible!=null && isCalDAVAvaible)
		{
			globalAvailableAppsArray[globalAvailableAppsArray.length]='CalDavZAP';
			globalAvailableAppsArray[globalAvailableAppsArray.length]='CalDavTODO';
		}
		if(typeof isCardDAVAvaible!='undefined' && isCardDAVAvaible!=null && isCardDAVAvaible)
			globalAvailableAppsArray[globalAvailableAppsArray.length]='CardDavMATE';
		if(typeof isSettingsAvaible!='undefined' && isSettingsAvaible!=null && isSettingsAvaible)
			globalAvailableAppsArray[globalAvailableAppsArray.length]='Settings';
		if(typeof isProjectsAvaible!='undefined' && isProjectsAvaible!=null && isProjectsAvaible)
			globalAvailableAppsArray[globalAvailableAppsArray.length]='Projects';
	}
	else
	{
		if(typeof isCalDAVAvaible!='undefined' && isCalDAVAvaible!=null && isCalDAVAvaible && (globalEnabledApps.indexOf('CalDavZAP')!=-1 || globalEnabledApps.indexOf('CalDavTODO')!=-1 ))
		{
			globalAvailableAppsArray[globalAvailableAppsArray.length]='CalDavZAP';
			globalAvailableAppsArray[globalAvailableAppsArray.length]='CalDavTODO';
		}
		if(typeof isCardDAVAvaible!='undefined' && isCardDAVAvaible!=null && isCardDAVAvaible && globalEnabledApps.indexOf('CardDavMATE')!=-1)
			globalAvailableAppsArray[globalAvailableAppsArray.length]='CardDavMATE';
		if(typeof isSettingsAvaible!='undefined' && isSettingsAvaible!=null && isSettingsAvaible && globalEnabledApps.indexOf('Settings')!=-1)
			globalAvailableAppsArray[globalAvailableAppsArray.length]='Settings';
		if(typeof isProjectsAvaible!='undefined' && isProjectsAvaible!=null && isProjectsAvaible && globalEnabledApps.indexOf('Projects')!=-1)
			globalAvailableAppsArray[globalAvailableAppsArray.length]='Projects';
	}

	if(globalSettings.defaultactiveapp==null)
	{
		if(isAvaible('CardDavMATE'))
			globalActiveApp='CardDavMATE';
		else if(isAvaible('CalDavZAP'))
			globalActiveApp='CalDavZAP';
	}
	else
		globalActiveApp=globalSettings.defaultactiveapp;

	// create backup from the original editor objects (needed for localization switching)
	if(isAvaible('CardDavMATE'))
	{
		globalMainCardDAV();
	}

	if(isAvaible('CalDavZAP'))
	{
		globalMainCalDAV();
	}
	
	if(isAvaible('Projects'))
	{
		globalMainProjects();
	}
	
	if(isAvaible('Settings'))
	{
		globalMainSettings();
	}

	// select the globalInterfaceLanguage in the interface
	$('[data-type="language"]').find('[data-type='+globalInterfaceLanguage+']').prop('selected',true);

//	if(globalActiveApp=='CalDavZAP' || globalActiveApp=='CalDavTODO')
	if(isAvaible('CardDavMATE'))
		mainCardDAV();
	if(isAvaible('CalDavZAP'))
		mainCalDAV();
	if(isAvaible('Settings')) 
		mainSettings();
	if(isAvaible('Projects')) 
		mainProjects();
}

function resetSettings()
{
	globalSettings={};
	globalSettings={
			resourcealphabetsorting: (typeof globalResourceAlphabetSorting!='undefined' && globalResourceAlphabetSorting!=null && globalResourceAlphabetSorting!='') ? globalResourceAlphabetSorting : true,
			usejqueryauth: (typeof globalUseJqueryAuth!='undefined' && globalUseJqueryAuth!=null && globalUseJqueryAuth!='') ? globalUseJqueryAuth : false,
			settingstype: (typeof globalSettingsType!='undefined' && globalSettingsType!=null && globalSettingsType!='') ? globalSettingsType : 'principal-URL',
			defaultactiveapp: (typeof globalDefaultActiveApp!='undefined' && globalDefaultActiveApp!=null && globalDefaultActiveApp!='') ? globalDefaultActiveApp : null,
			isLastdefaultactiveapp : false,
			datepickerfirstdayofweek: (typeof globalDatepickerFirstDayOfWeek!='undefined' && globalDatepickerFirstDayOfWeek!=null && globalDatepickerFirstDayOfWeek!='') ? globalDatepickerFirstDayOfWeek : 1,
			syncresourcesinterval:(typeof globalSyncResourcesInterval!='undefined' && globalSyncResourcesInterval!=null && globalSyncResourcesInterval!='') ? globalSyncResourcesInterval :300000 ,
			datepickerformat:(typeof globalDatepickerFormat!='undefined' && globalDatepickerFormat!=null && globalDatepickerFormat!='') ? globalDatepickerFormat : localization[globalInterfaceLanguage]._default_datepicker_format_ ,
			backgroundsync:(typeof globalBackgroundSync!='undefined' && globalBackgroundSync!=null && globalBackgroundSync!='') ? globalBackgroundSync : true ,
			enablekbnavigation:(typeof globalEnableKbNavigation!='undefined' && globalEnableKbNavigation!=null && globalEnableKbNavigation!='') ? globalEnableKbNavigation : true ,
			rewritetimezonecomponent: (typeof globalRewriteTimezoneComponent!='undefined' && globalRewriteTimezoneComponent!=null && globalRewriteTimezoneComponent!='') ? globalRewriteTimezoneComponent : true,
			removeunknowntimezone: (typeof globalRemoveUnknownTimezone!='undefined' && globalRemoveUnknownTimezone!=null && globalRemoveUnknownTimezone!='') ? globalRemoveUnknownTimezone : false,
			mozillasupport: (typeof globalMozillaSupport!='undefined' && globalMozillaSupport!=null && globalMozillaSupport!='') ? globalMozillaSupport : false,
			appleremindersmode: (typeof globalAppleRemindersMode!='undefined' && globalAppleRemindersMode!=null && globalAppleRemindersMode!='') ? globalAppleRemindersMode : false,
			titleformatmonth: localization[globalInterfaceLanguage]._default_title_format_month_,
			titleformatweek: localization[globalInterfaceLanguage]._default_title_format_week_,
			titleformatday: localization[globalInterfaceLanguage]._default_title_format_day_,
			titleformattable: localization[globalInterfaceLanguage]._default_title_format_table_,
			columnformatagenda: localization[globalInterfaceLanguage]._default_column_format_agenda_,
			timeformatlist: localization[globalInterfaceLanguage]._default_time_format_list_,
			activecalendarcollections: (typeof globalActiveCalendarCollections!='undefined' && globalActiveCalendarCollections!=null && globalActiveCalendarCollections!='') ? globalActiveCalendarCollections : new Array(),
			activetodocollections: (typeof globalActiveTodoCollections!='undefined' && globalActiveTodoCollections!=null && globalActiveTodoCollections!='') ? globalActiveTodoCollections : new Array(),
			todolistfilterselected: (typeof globalTodoListFilterSelected!='undefined' && globalTodoListFilterSelected!=null && globalTodoListFilterSelected!='') ? globalTodoListFilterSelected : ['filterAction', 'filterProgress'],
			activeview: (typeof globalActiveView!='undefined' && globalActiveView!=null && globalActiveView!='') ? globalActiveView : 'multiWeek',
			islastactiveview : true,
			calendarselected: (typeof globalCalendarSelected!='undefined' && globalCalendarSelected!=null && globalCalendarSelected!='') ? globalCalendarSelected : '',
			todocalendarselected: (typeof globalTodoCalendarSelected!='undefined' && globalTodoCalendarSelected!=null && globalTodoCalendarSelected!='') ? globalTodoCalendarSelected : '',
			timezone: (typeof globalTimeZone!='undefined' && globalTimeZone!=null && globalTimeZone!='') ? globalTimeZone : 'local',
			islasttimezone:true,
			openformmode: (typeof globalOpenFormMode!='undefined' && globalOpenFormMode!=null && globalOpenFormMode!='') ? globalOpenFormMode : 'double',
			calendarstartofbusiness: (typeof globalCalendarStartOfBusiness!='undefined' && globalCalendarStartOfBusiness!=null && globalCalendarStartOfBusiness!='') ? globalCalendarStartOfBusiness : 8,
			calendarendofbusiness: (typeof globalCalendarEndOfBusiness!='undefined' && globalCalendarEndOfBusiness!=null && globalCalendarEndOfBusiness!='') ? globalCalendarEndOfBusiness : 17,
			ampmformat: (typeof globalAMPMFormat!='undefined' && globalAMPMFormat!=null && globalAMPMFormat!='') ? globalAMPMFormat : localization[globalInterfaceLanguage]._default_AMPM_format_,
			timeformatagenda: (typeof globalTimeFormatAgenda!='undefined' && globalTimeFormatAgenda!=null && globalTimeFormatAgenda!='') ? globalTimeFormatAgenda : null,
			timeformatbasic: (typeof globalTimeFormatBasic!='undefined' && globalTimeFormatBasic!=null && globalTimeFormatBasic!='') ? globalTimeFormatBasic : null,
			displayhiddenevents: (typeof globalDisplayHiddenEvents!='undefined' && globalDisplayHiddenEvents!=null && globalDisplayHiddenEvents!='') ? globalDisplayHiddenEvents : false,
			timezonesupport: (typeof globalTimeZoneSupport!='undefined' && globalTimeZoneSupport!=null && globalTimeZoneSupport!='') ? globalTimeZoneSupport : true,
			timezonesenabled: (typeof globalTimeZonesEnabled!='undefined' && globalTimeZonesEnabled!=null && globalTimeZonesEnabled!='') ? globalTimeZonesEnabled : [],
			showhiddenalarms: (typeof globalShowHiddenAlarms!='undefined' && globalShowHiddenAlarms!=null && globalShowHiddenAlarms!='') ? globalShowHiddenAlarms : false,
			ignorecompletedorcancelledalarms :(typeof globalIgnoreCompletedOrCancelledAlarms!='undefined' && globalIgnoreCompletedOrCancelledAlarms!=null && globalIgnoreCompletedOrCancelledAlarms!='') ? globalIgnoreCompletedOrCancelledAlarms : true,
			weekenddays :(typeof globalWeekendDays!='undefined' && globalWeekendDays!=null && globalWeekendDays!='') ? globalWeekendDays : [0, 6],
			eventstartpastlimit : (typeof globalEventStartPastLimit!='undefined' && globalEventStartPastLimit!=null && globalEventStartPastLimit!='') ? globalEventStartPastLimit : 3,
			eventstartfuturelimit : (typeof globalEventStartFutureLimit!='undefined' && globalEventStartFutureLimit!=null && globalEventStartFutureLimit!='') ? globalEventStartFutureLimit : 3,
			compatibility:(typeof globalCompatibility!='undefined' && globalCompatibility!=null && globalCompatibility!='') ? globalCompatibility : {anniversaryOutputFormat: ['apple']},
			contactstorefn:(typeof globalContactStoreFN!='undefined' && globalContactStoreFN!=null && globalContactStoreFN!='') ? globalContactStoreFN : {anniversaryOutputFormat: ['prefix',' last',' middle',' first',' suffix']},
			urihandlertel:(typeof globalUriHandlerTel!='undefined' && globalUriHandlerTel!=null && globalUriHandlerTel!='') ? globalUriHandlerTel : {anniversaryOutputFormat: 'tel:'},
			urihandleremail:(typeof globalUriHandlerEmail!='undefined' && globalUriHandlerEmail!=null && globalUriHandlerEmail!='') ? globalUriHandlerEmail : 'mailto:',
			urihandlerurl:(typeof globalUriHandlerUrl!='undefined' && globalUriHandlerUrl!=null && globalUriHandlerUrl!='') ? globalUriHandlerUrl : 'http://',
			urihandlerprofile:(typeof globalUriHandlerProfile!='undefined' && globalUriHandlerProfile!=null && globalUriHandlerProfile!='') ? globalUriHandlerProfile : {'twitter': 'http://twitter.com/%u', 'facebook': 'http://www.facebook.com/%u', 'flickr': 'http://www.flickr.com/photos/%u', 'linkedin': 'http://www.linkedin.com/in/%u', 'myspace': 'http://www.myspace.com/%u', 'sinaweibo': 'http://weibo.com/n/%u'},
			addresscountryequivalence:(typeof globalAddressCountryEquivalence!='undefined' && globalAddressCountryEquivalence!=null && globalAddressCountryEquivalence!='') ? globalAddressCountryEquivalence : [{country: 'de', regex: '^\\W*Deutschland\\W*$'}, {country: 'sk', regex: '^\\W*Slovensko\\W*$'}],
			addressbookselected: (typeof globalAddressbookSelected!='undefined' && globalAddressbookSelected!=null && globalAddressbookSelected!='') ? globalAddressbookSelected : '',
			collectionsort: (typeof globalCollectionSort!='undefined' && globalCollectionSort!=null && globalCollectionSort!='') ? globalCollectionSort : ['last','middle','first'],
			collectiondisplay: (typeof globalCollectionDisplay!='undefined' && globalCollectionDisplay!=null && globalCollectionDisplay!='') ? globalCollectionDisplay :['last',' middle',' first'],
			collectiondisplayorg: (typeof globalCollectionDisplayOrg!='undefined' && globalCollectionDisplayOrg!=null && globalCollectionDisplayOrg!='') ? globalCollectionDisplayOrg :true,
			defaultaddresscountry: (typeof globalDefaultAddressCountry!='undefined' && globalDefaultAddressCountry!=null && globalDefaultAddressCountry!='') ? globalDefaultAddressCountry :'us',
			addresscountryfavorites: (typeof globalAddressCountryFavorites!='undefined' && globalAddressCountryFavorites!=null && globalAddressCountryFavorites!='') ? globalAddressCountryFavorites :[],
			activeaddressbookcollections: (typeof globalActiveAddressbookCollections!='undefined' && globalActiveAddressbookCollections!=null && globalActiveAddressbookCollections!='') ? globalActiveAddressbookCollections : new Array()
	};
}

function saveSettings(isFormSave)
{
	globalSettings.activeaddressbookcollections.splice(0, globalSettings.activeaddressbookcollections.length);
	globalSettings.activecalendarcollections.splice(0, globalSettings.activecalendarcollections.length);
	globalSettings.activetodocollections.splice(0, globalSettings.activetodocollections.length);
	globalSettings.todolistfilterselected.splice(0, globalSettings.todolistfilterselected.length);

	if(globalSettings.isLastdefaultactiveapp)
		globalSettings.defaultactiveapp=globalActiveApp;

	if(isAvaible('CalDavZAP'))
	{
		for(var i=0;i<globalVisibleCalDAVCollections.length;i++)
		{
			var uidPart=globalVisibleCalDAVCollections[i].match(RegExp('^(https?://)(.*)', 'i'))[1];
			var uidPart2= globalVisibleCalDAVCollections[i].match(RegExp('^(https?://)(.*)', 'i'))[2].split('@')[2];
			globalSettings.activecalendarcollections.splice(globalSettings.activecalendarcollections.length , 0, uidPart+uidPart2);
		}

		for(var i=0;i<globalVisibleCalDAVTODOCollections.length;i++)
		{
			var uidPart=globalVisibleCalDAVTODOCollections[i].match(RegExp('^(https?://)(.*)', 'i'))[1];
			var uidPart2= globalVisibleCalDAVTODOCollections[i].match(RegExp('^(https?://)(.*)', 'i'))[2].split('@')[2];
			globalSettings.activetodocollections.splice(globalSettings.activetodocollections.length , 0, uidPart+uidPart2);
		}
		if(globalSettings.islastactiveview)
		{
			var view= $('#calendar').fullCalendar('getView');
			globalSettings.activeview=view.name;
		}

		if(globalSettings.islasttimezone)
			globalSettings.timezone=globalSessionTimeZone;
		
		var uidSelected=$('#ResourceCalDAVList').find('.resourceCalDAV_item_selected').attr('data-id');
		if(uidSelected!=undefined && uidSelected!='')
		{
			var par=uidSelected.split('/');
			globalSettings.calendarselected=par[par.length-3]+'/'+par[par.length-2]+'/';
		}

		uidSelected=$('#ResourceCalDAVTODOList').find('.resourceCalDAV_item_selected').attr('data-id');
		if(uidSelected!=undefined && uidSelected!='')
		{
			var par=uidSelected.split('/');
			globalSettings.todocalendarselected=par[par.length-3]+'/'+par[par.length-2]+'/';
		}

		var filterArray = $('.fc-filter-option-selected');
		for(var i=0; i<filterArray.length; i++)
			globalSettings.todolistfilterselected.splice(globalSettings.todolistfilterselected.length,0,$($('.fc-filter-option-selected')[i]).attr('data-type'));
	}

	if(isAvaible('CardDavMATE'))
	{
		var visAddrs = dataGetChecked('#ResourceCardDAVList');
		for(var i=0;i<visAddrs.length;i++)
		{
			var uidPart=visAddrs[i].match(RegExp('^(https?://)(.*)', 'i'))[1];
			var uidPart2= visAddrs[i].match(RegExp('^(https?://)(.*)', 'i'))[2].split('@')[2];
			globalSettings.activeaddressbookcollections.splice(globalSettings.activeaddressbookcollections.length , 0, uidPart+uidPart2);
		}
		
		if($('#ResourceCardDAVList').find('.group.resourceCardDAV_selected').length>0)
			var uidASelected=$('#ResourceCardDAVList').find('.group.resourceCardDAV_selected').attr('data-id');
		else if($('#ResourceCardDAVList').find('.resourceCardDAV_selected').length>0)
			var uidASelected=$('#ResourceCardDAVList').find('.resourceCardDAV_selected').attr('data-id');
		else
			var uidASelected='';
		if(uidASelected!=undefined && uidASelected!='')
			globalSettings.addressbookselected=uidASelected;
	}

	if(isAvaible('Settings') && isFormSave)
		globalSettings=applyFormSettings(globalSettings);

	var strobj=JSON.stringify(globalSettings);
	return strobj;
}

function loadXMLSetings(settingsXML)
{
	$(settingsXML).children().each(
	function(ind,elm)
	{
		var type = $(elm).attr('type');
		if($(elm).children().length>0)
		{
			globalSettings[$(elm).prop('tagName').toLowerCase()] = new Array();
			if($(elm).prop('tagName').toLowerCase() == 'urihandlerprofile' || $(elm).prop('tagName').toLowerCase() == 'compatibility')
				globalSettings[$(elm).prop('tagName').toLowerCase()] = {};
			$(elm).children().each(function(pind,pelm)
			{
				if($(elm).prop('tagName').toLowerCase() == 'urihandlerprofile')
				{
					globalSettings[$(elm).prop('tagName').toLowerCase()][$(pelm).prop('tagName').toLowerCase()] = $(pelm).attr('url');
				}
				else if($(elm).prop('tagName').toLowerCase() == 'addresscountryequivalence')
				{
					var eqObject = {};
					eqObject['country'] = $(pelm).attr('name'); 
					eqObject['regex'] = $(pelm).attr('regex'); 
					globalSettings[$(elm).prop('tagName').toLowerCase()].push(eqObject);
				}
				else if($(elm).prop('tagName').toLowerCase() == 'compatibility')
				{
					globalSettings[$(elm).prop('tagName').toLowerCase()][$(pelm).prop('tagName').toLowerCase()] = new Array();
					$(pelm).children().each(function(rind,relm)
					{
						globalSettings[$(elm).prop('tagName').toLowerCase()][$(pelm).prop('tagName').toLowerCase()].push($(relm).text());
					});
				}
				else if($(pelm).text()!='')
				{
					switch(type)
					{
						case 'number':
										globalSettings[$(elm).prop('tagName').toLowerCase()].push(parseInt($(pelm).text(),10));
										break;
						case 'string':
										globalSettings[$(elm).prop('tagName').toLowerCase()].push($(pelm).text());
										break;
						case 'boolean':
										if($(pelm).text() == 'true')
											globalSettings[$(elm).prop('tagName').toLowerCase()].push(true);
										else
											globalSettings[$(elm).prop('tagName').toLowerCase()].push(false);
										break;
						default:
									break;
					}
				}
			});
		}
		else if($(elm).text()!='')
		{
			switch(type)
			{
				case 'number':
								globalSettings[$(elm).prop('tagName').toLowerCase()] = parseInt($(elm).text(),10);
								break;
				case 'string':
								globalSettings[$(elm).prop('tagName').toLowerCase()] = $(elm).text();
								break;
				case 'boolean':
								if($(elm).text() == 'true')
									globalSettings[$(elm).prop('tagName').toLowerCase()] = true;
								else
									globalSettings[$(elm).prop('tagName').toLowerCase()] = false;
								break;
				default:
							break;
			}
		}
	});
}

function loadSettings(strobj)
{
	if(settingsLoaded)
		return false;
	eval('objNew='+strobj);
	if(typeof objNew=='object')
	{
		if(isAvaible('CalDavZAP'))
		{
			if(typeof objNew.activecalendarcollections == 'undefined' || objNew.activecalendarcollections==null)
				globalDefaultCalendarCollectionActiveAll = true;
			if(typeof objNew.activetodocollections == 'undefined' || objNew.activetodocollections==null)
				globalDefaultTodoCalendarCollectionActiveAll = true;
		}
		if(isAvaible('CardDavMATE') && (typeof objNew.activeaddressbookcollections == 'undefined' || objNew.activeaddressbookcollections==null))
			globalDefaultAddressbookCollectionActiveAll = true;
		$.extend(globalSettings,objNew);
	}

	if(isAvaible('CalDavZAP'))
	{
		for(var i=0;i<globalSettings.timezonesenabled.length;i++)
			timeZonesEnabled.push(globalSettings.timezonesenabled[i]);

		if(globalSettings.timezonesupport)
			globalSessionTimeZone=globalSettings.timezone;
		else
			globalSessionTimeZone = 'local';
		$('#timezonePicker').html('<option data-type=""></option>');

		if(globalSettings.timeformatagenda==null)
			if(globalSettings.ampmformat)
				globalSettings.timeformatagenda='h:mm TT{ - h:mm TT}';
			else
				globalSettings.timeformatagenda='H:mm{ - H:mm}';

		if(globalSettings.timeformatbasic==null)
			if(globalSettings.ampmformat)
				globalSettings.timeformatbasic = 'h:mmT{-h:mmT}';
			else
				globalSettings.timeformatbasic = 'H:mm{-H:mm}';
		if(globalSettings.appleremindersmode)
		{
			if(globalSettings.todolistfilterselected.indexOf('filterAction')==-1 && globalSettings.todolistfilterselected.indexOf('filterCompleted')==-1)
			{
				if(globalSettings.todolistfilterselected.indexOf('filterProgress')!=-1)
					globalSettings.todolistfilterselected[globalSettings.todolistfilterselected.indexOf('filterProgress')] = 'filterAction';
				if(globalSettings.todolistfilterselected.indexOf('filterCanceled')!=-1)
					globalSettings.todolistfilterselected[globalSettings.todolistfilterselected.indexOf('filterCanceled')] = 'filterAction';
			}
		}
		if(globalSettings.eventstartfuturelimit == null)
		{
			var now=new Date();
			globalToLoadedLimit = new Date(now.getFullYear(), now.getMonth()+12, 1, 0, 0, 0);
			globalToLoadedLimit.setMilliseconds(0);
			globalBeginFuture = new Date(globalToLoadedLimit.getTime());
			globalBeginFuture.setDate(globalBeginFuture.getDate()+14);
			globalToLoadedLimitTodo = new Date(now.getFullYear(), now.getMonth()+12, 1, 0, 0, 0);
			globalToLoadedLimitTodo.setMilliseconds(0);
		}
		initFullCalendar();
		initTodoList();
	}

	settingsLoaded=true;
	if(!isAvaible(globalSettings.defaultactiveapp))
		globalActiveApp = globalAvailableAppsArray[0];
	else
		globalActiveApp = globalSettings.defaultactiveapp;

	checkForApplication(globalActiveApp);
}

function checkBeforeClose(isFormSave)
{
	if((isAvaible('CalDavZAP') && globalCalDAVInitLoad) || (isAvaible('CardDavMATE') && globalCardDAVInitLoad))
		return false;
	var preSettings=JSON.stringify(globalSettings);
	var settings=saveSettings(isFormSave);
	for(var i=0;i<globalAccountSettings.length;i++)
		if(globalAccountSettings[i].href.indexOf(globalLoginUsername)!=-1 && globalAccountSettings[i].settingsAccount)
		{
			netSaveSettings(globalAccountSettings[i], settings, isFormSave);
			break;
		}
}

window.onload=globalMain;

function GlobalTODOlist()
{
	this.todos=new Array();
	this.reset=function()
	{
		this.todos.splice(0, this.todos.length);
		this.todos=new Array();
	}
}

function logoutCalDAV()
{
	globalVisibleCalDAVCollections.splice(0, globalVisibleCalDAVCollections.length);
	globalVisibleCalDAVTODOCollections.splice(0, globalVisibleCalDAVTODOCollections.length);
	globalCalendarNumber=0;
	globalOnlyCalendarNumber=0;
	globalTodoCalendarNumber=0;
	globalCalendarNumberCount=0;
	globalLoadedLimit = new Date();
	globalToLoadedLimit = new Date();
	globalLimitLoading='';
	globalBeginPast = new Date();
	globalBeginFuture = new Date();
	globalLoadedLimitTodo = new Date();
	globalToLoadedLimitTodo = new Date();
	globalDefaultCalendarCollectionActiveAll = false;
	globalDefaultTodoCalendarCollectionActiveAll = false;
	globalCalDAVCollectionSync=false;
	globalEventList.reset();
	globalResourceCalDAVList.reset();
	globalTODOlist.reset();
	globalViewsList={'month': false, 'multiWeek': false, 'agendaWeek': false, 'agendaDay': false, 'table': false};
	timeZonesEnabled.splice(0,timeZonesEnabled.length);
	if(globalEventIntervalID!=null)
		clearInterval(globalEventIntervalID);

	$('#EventDisabler, #TodoDisabler, #AlertDisabler').fadeOut(2000, function(){
		$('#timezonePicker, #timezonePickerTODO').prop('disabled', false);
	});
	searchToggle=false;
	$('#main').animate({top: 24}, 0);
	$('#searchForm').hide();
	$('#searchInput').val('');
	$('#searchInput').keyup();
	$('#searchInput').blur();

	$('#calendar').fullCalendar('destroy');
	$('#todoList').fullCalendar('destroy');
	$('#SystemCalDAV').animate({opacity : 0},200,function(){
		$('#SystemCalDAV').css('visibility','hidden');
		$('#eventColor, #todoColor').removeCss('background-color');
	});
}

function mainCalDAV()
{
	localizeCalDAV();
	init();
}

function localizeCalDAV()
{
	globalCalDAVInitLoad = true;
	$('#ResourceCalDAVList').html(origResourceCalDAVListTemplate);
	$('#ResourceCalDAVTODOList').html(origResourceCalDAVTODOListTemplate);
	$('#CAEvent').html(origVcalendarTemplate);
	$('#CATodo').html(origVtodoTemplate);
	$('#todoLoader').html(origVtodoLoaderTemplate);
	$('#MainLoaderInner').html(origLoaderTemplate);
	resetSettings();
	translate();
	initTimepicker();
	$('input[placeholder],textarea[placeholder]').placeholder();
	cleanResourceCalDAVListTemplate=$('#ResourceCalDAVListTemplate').clone().wrap('<div>').parent().html();
	cleanResourceCalDAVTODOListTemplate=$('#ResourceCalDAVTODOListTemplate').clone().wrap('<div>').parent().html();
	cleanVcalendarTemplate=$('#CAEvent .saveLoader').clone().wrap('<div>').parent().html() + $('#repeatConfirmBox').clone().wrap('<div>').parent().html() + $('#event_details_template').clone().wrap('<div>').parent().html();
	cleanVtodoTemplate=$('#repeatConfirmBoxTODO').clone().wrap('<div>').parent().html() + $('#todo_details_template').clone().wrap('<div>').parent().html();
	$('#searchInput, #searchInputTODO').val('');
	globalSettings.itleFormatMonth = localization[globalInterfaceLanguage]._default_title_format_month_;
	globalSettings.titleFormatWeek = localization[globalInterfaceLanguage]._default_title_format_week_;
	globalSettings.titleFormatDay = localization[globalInterfaceLanguage]._default_title_format_day_;
	globalSettings.titleFormatTable = localization[globalInterfaceLanguage]._default_title_format_table_;
	globalSettings.columnFormatAgenda = localization[globalInterfaceLanguage]._default_column_format_agenda_;
	globalSettings.timeFormatList = localization[globalInterfaceLanguage]._default_column_format_agenda_;
}

function runCalDAV()
{
	if(!isUserLogged)
		run();
	var filerArray = $('.fc-filter-option-selected');
	for(var i=0; i<filerArray.length; i++)
		$($('.fc-filter-option-selected')[i]).removeClass('fc-filter-option-selected');
	
	if(globalSettings.timezone != null)
		timeZonesEnabled.push(globalSettings.timezone);

	globalResourceRefreshNumber=0;
	globalResourceRefreshNumberTodo=0;

	$('#MainLoader').css('left','0px');
	$('#MainLoader').fadeIn(200);

	if(typeof globalSubscribedCalendars!='undefined' && globalSubscribedCalendars!=null)
	{
		globalAccountSettings[globalAccountSettings.length]=$.extend({},globalAccountSettings[0]);
		globalAccountSettings[globalAccountSettings.length-1].hrefLabel = globalSubscribedCalendars.hrefLabel;
		globalAccountSettings[globalAccountSettings.length-1].showHeader = globalSubscribedCalendars.showHeader;
		globalAccountSettings[globalAccountSettings.length-1].calendars = globalSubscribedCalendars.calendars;
		globalAccountSettings[globalAccountSettings.length-1].ignoreAlarms = '';
	}
	initKbTodoNavigation();
}

function globalMainCalDAV()
{
	$(window).resize(function(evt){
		if(evt.target!=window)
			return;

		if(typeof globalCalDAVInitLoad!='undefined' && !globalCalDAVInitLoad && !globalResourceRefreshNumber && $('#main').width()!=globalCalWidth) {
			$('#ResizeLoader').show();
		}

		$('#SystemCalDAV .fc-header-title').css('width', $('#main_h_placeholder').width()-$('#SystemCalDAV .fc-header-left').width()-$('#SystemCalDAV .fc-header-right').width()-20);
		$('#ResourceCalDAVList, #ResourceCalDAVTODOList').css('bottom',(globalSettings.timezonesupport ? 19 : 0));
		$('#alertBox').css('left', ($(window).width()/2)-($('#alertBox').width()/2));
		$('#calendar').fullCalendar('option', 'contentHeight', $('#main').height() - 14);
		$('#todoList').fullCalendar('allowSelectEvent',false);
		$('#todoList').fullCalendar('option', 'contentHeight', $('#mainTODO').height() - 14);
		$('#todoList').fullCalendar('allowSelectEvent',true);
		$('#todoList').fullCalendar('selectEvent', null, true);
		if(globalCalDAVQs!=null)
			globalCalDAVQs.cache();
		if(globalCalDAVTODOQs!=null)
			globalCalDAVTODOQs.cache();
		if(globalSettings.displayhiddenevents)
			for(var k=1;k<globalResourceCalDAVList.collections.length;k++)
				if(globalResourceCalDAVList.collections[k].uid!=undefined)
				{
					var pos=globalVisibleCalDAVCollections.indexOf(globalResourceCalDAVList.collections[k].uid);
					if(pos==-1)
						$("#SystemCalDAV div [data-res-id='"+globalResourceCalDAVList.collections[k].uid+"']").addClass('checkCalDAV_hide');
				}
		globalCalWidth = $('#main').width();
	});

	/*************************** BAD HACKS SECTION ***************************/
	if($.browser.msie || $.browser.mozilla)
	{
		var newSVG=$(SVG_select_b).attr('data-type', 'select_icon').css({'pointer-events': 'none', 'z-index': '1', 'display': 'inline', 'margin-left': '-19px', 'vertical-align': 'top', 'background-color': '#ffffff'});	// background-color = stupid IE9 bug
		$('#timezoneWrapper, #timezoneWrapperTODO').find('select').after($($('<div>').append($(newSVG).clone()).html()));
	}
	// INFO LABEL ALIGN WITH UNDELYING SELECT FIX
	if($.browser.webkit && !!$.browser.safari)
		$('.infoSpan[data-type="txt_interval"]').css('padding-left', '3px');
	/*************************** END OF BAD HACKS SECTION ***************************/

	setFirstDay();
	origResourceCalDAVListTemplate = $('#ResourceCalDAVListTemplate').clone().wrap('<div>').parent().html();
	origResourceCalDAVTODOListTemplate = $('#ResourceCalDAVTODOListTemplate').clone().wrap('<div>').parent().html();
	origVcalendarTemplate = $('#CAEvent .saveLoader').clone().wrap('<div>').parent().html() + $('#repeatConfirmBox').clone().wrap('<div>').parent().html() + $('#event_details_template').clone().wrap('<div>').parent().html();
	origVtodoTemplate = $('#repeatConfirmBoxTODO').clone().wrap('<div>').parent().html() + $('#todo_details_template').clone().wrap('<div>').parent().html();
	origVtodoLoaderTemplate=$('#todoLoader .saveLoader').clone().wrap('<div>').parent().html();
	origLoaderTemplate = $('#MainLoaderInner').clone().wrap('<div>').html();

	for(var i in timezones)
		timezoneKeys.push(i);

	timezoneKeys.push('0local');
	timezoneKeys.push('1UTC');

	timezoneKeys.sort();

	timezoneKeys[0] = timezoneKeys[0].substring(1);
	timezoneKeys[1] = timezoneKeys[1].substring(1);

	jQuery.extend(timezones,{'UTC':{}});
}

function setCalendarNumber()
{
	if($('.resourceCalDAV_header:visible').length>1 || (!$('.resourceCalDAV_header:visible').length  && $('.resourceCalDAV_item:visible').length>1))
		$('.addRemoveAllCalDAV').show();
	if($('.resourceCalDAVTODO_header:visible').length>1 || (!$('.resourceCalDAVTODO_header:visible').length  && $('.resourceCalDAVTODO_item:visible').length>1))
		$('.addRemoveAllCalDAVTODO').show();
	initSearchEngine();
	for(var i=0; i<globalResourceCalDAVList.collections.length;i++)
		if(globalResourceCalDAVList.collections[i].uid!=undefined/* && globalResourceCalDAVList.collections[i].syncRequired*/)
		{
			globalCalendarNumber++;
			globalOnlyCalendarNumber++;
		}	
	for(var i=0; i<globalResourceCalDAVList.TodoCollections.length;i++)
		if(globalResourceCalDAVList.TodoCollections[i].uid!=undefined/* && globalResourceCalDAVList.TodoCollections[i].syncRequired*/)
		{
			globalCalendarNumber++;
			globalTodoCalendarNumber++;
		}
}

function setLogoCalDAV()
{
	$('img[data-type="system_logo"]').attr('src', 'images/logo_cdz_' + new Date().getDate().pad(2) + '.svg');
}
