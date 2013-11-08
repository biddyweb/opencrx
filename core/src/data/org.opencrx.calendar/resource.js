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

function CalDAVloadResources(resourceCalDAVList, forceLoad)
{
	if(forceLoad!=true && globalWindowFocus==false)
		return false;

	if(!(resourceCalDAVList instanceof Array))
		resourceCalDAVList=[resourceCalDAVList];

	var isLast=false;
	CalDAVnetFindResource(resourceCalDAVList[0], forceLoad, 0);
}

// ResourceCalDAVList Class
function ResourceCalDAVList()
{
	this.collections=new Array();
	this.TodoCollections=new Array();
	this.calendarsLoaded=null;
	this.counterList=new Array();
	this.sortedTodoCollections=new Array();
	this.sortedCollections=new Array();

	this.reset=function()
	{
		this.TodoCollections.splice(0, this.TodoCollections.length);
		this.collections.splice(0, this.collections.length);
		this.counterList=new Array();
		this.sortedTodoCollections=new Array();
		this.sortedCollections=new Array();
	}

	// resource header value
	this.getHeaderValue=function(inputResource)
	{
		var re=new RegExp('^(https?://)([^@/]+(?:@[^@/]+)?)@([^/]+).*/([^/]*)/','i');
		var tmp=inputResource.accountUID.match(re);
		var tmp_host=tmp[3];	// hostname [%H]
		var tmp_host_wo_port=tmp[3].replace(RegExp(':[0-9]+$'),'');	// hostname without port [%h]
		var tmp_domain=tmp_host_wo_port.replace(RegExp('^[^.]+\\.'), '');	// domain name [%D]
		var tmp_domain_min=tmp_host_wo_port.match(RegExp('^([^.]+\\.)*?((?:[^.]+\\.)?[^.]+)$'))[2];	// domain name min. (only 1 or 2 level domain string) [%d]
		var tmp_principal=decodeURIComponent(tmp[4]);	// principal username [%P]
		var tmp_principal_wo_domain=decodeURIComponent(tmp[4]).replace(RegExp('(@.*)?$'),'');	// principal username without @domain.com [%p]
		var tmp_user=inputResource.userAuth.userName;	// login name [%U]
		var tmp_user_wo_domain=inputResource.userAuth.userName.replace(RegExp('@.*$'),'');	// login name without @domain.com [%u]

		if(!inputResource.subscription && (  typeof inputResource.hrefLabel!='string' || inputResource.hrefLabel==''))
			inputResource.hrefLabel='%d/%p [%u]';
		else if(inputResource.subscription && (  typeof inputResource.hrefLabel!='string' || inputResource.hrefLabel==''))
			inputResource.hrefLabel=localization[globalInterfaceLanguage].txtSubscribed;

		var result=inputResource.hrefLabel;
		result=result.replace(RegExp('%H', 'g'), tmp_host);
		result=result.replace(RegExp('%h', 'g'), tmp_host_wo_port);
		result=result.replace(RegExp('%D', 'g'), tmp_domain);
		result=result.replace(RegExp('%d', 'g'), tmp_domain_min);
		result=result.replace(RegExp('%P', 'g'), tmp_principal);
		result=result.replace(RegExp('%p', 'g'), tmp_principal_wo_domain);
		result=result.replace(RegExp('%U', 'g'), tmp_user);
		result=result.replace(RegExp('%u', 'g'), tmp_user_wo_domain);
		return result;
	}

	this.getSortKey=function(inputResource, resourceSortMode, index)
	{
		var re=new RegExp('^(https?://)([^@/]+(?:@[^@/]+)?)@([^/]+)(.*/)([^/]+/)([^/]+/)([^/]*)', 'i');
		var tmp=inputResource.uid.match(re);
		var firstPart='';
		if(!inputResource.subscription)
			firstPart='A';
		else
			firstPart='B';
		if(typeof resourceSortMode=='undefined' || resourceSortMode)
			firstPart+=tmp[1]+tmp[3]+'/'+tmp[5];
		else
		{
			if(tmp[5].charAt(tmp[5].length-1)=='/')
			tmp[5]=tmp[5].substring(0, tmp[5].length-1);
			
			firstPart+=index.pad(String(globalAccountSettings.length).length);
		}
		return firstPart+' '+inputResource.userAuth.userName+' '+inputResource.displayvalue;
	}

	// Resource list is not sorted, instead "insert sort" is performed
	this.insertResource=function(inputResource, index, isEvent)
	{
		inputResource.sortkey=this.getSortKey(inputResource, globalSettings.resourcealphabetsorting, index);
		var collObject={};
		var todoString = '';
		var makeChecked=false;
		if(isEvent)
			collObject = this.collections;
		else
		{
			collObject = this.TodoCollections;
			todoString = 'TODO';
		}
		// do not insert entry with duplicate UID
		for(var i=0;i<collObject.length;i++)
			if(collObject[i].uid==inputResource.uid)
			{
				collObject[i].urlArray={};
				if(collObject[i].displayvalue==inputResource.displayvalue && collObject[i].permissions.read_only==inputResource.permissions.read_only)
				{
					collObject[i]=$.extend(inputResource, {fcSource: collObject[i].fcSource, syncToken: collObject[i].syncToken, forceSyncPROPFIND: collObject[i].forceSyncPROPFIND});
					return 0;
				}
				else
				{
					makeChecked=$('#ResourceCalDAV'+todoString+'List').find('[data-id='+jqueryEscapeSelector(inputResource.uid)+']').find('input[type=checkbox]').prop('checked');
					// the collection name is changed and must be moved to correct place (we first remove it and then reinsert)
					this.removeResource(inputResource.uid, false,isEvent);
					break;
				}
			}

		// create header
		var headerObject={headerOnly: true, displayvalue: this.getHeaderValue(inputResource), index:0};
		// find the index where to insert the new resource
		if(isEvent)
			var insertIndex=this.collections.length;
		else
			var insertIndex=this.TodoCollections.length;
		for(var i=0;i<collObject.length;i++)
			if(collObject[i].headerOnly==undefined && collObject[i].sortkey.customCompare(inputResource.sortkey, globalSortAlphabet, 1, false)==1)
			{
				insertIndex=i;
				// if the object predecessor is header which is different from current header we must go upward
				if(i>0 && collObject[i-1].headerOnly==true && collObject[i-1].displayvalue!=headerObject.displayvalue)
					--insertIndex;
				break;
			}
		// check for header existence
		var headerMiss=1;
		for(var i=0;i<collObject.length;i++)
			if(collObject[i].headerOnly==true && collObject[i].displayvalue==headerObject.displayvalue)
			{
				
				headerMiss=0;
				break;
			}
		// insert header if not exists
		if(headerMiss)
		{	
			headerObject.index=insertIndex;
			collObject.splice(insertIndex, 0, headerObject);
		}

		// insert the resource
		if(collObject.length==1 && globalCalDAVInitLoad)
			$('#SystemCalDAV .fc-header-center ').addClass('r_operate_all');

		this.counterList[inputResource.uid+' '+inputResource.listType]={collectionLength: 0, counter: 0, uid: inputResource.uid, isLoading: false, isSaving: false};
		
		collObject.splice(insertIndex+headerMiss, 0, inputResource);
		
		if(!isEvent)
		{
			globalEventList.displayTodosArray[inputResource.uid]=new Array();
			globalEventList.todos[inputResource.uid]={};
		}
		else
		{
			globalEventList.displayEventsArray[inputResource.uid]=new Array();
			globalEventList.events[inputResource.uid]={};
		}

		var str=inputResource.href.substring(0, inputResource.href.length-1);
		var resList = '#ResourceCalDAV'+todoString+'List';
		var resHeader = '.resourceCalDAV'+todoString+'_header';
		var resItem = '.resourceCalDAV'+todoString+'_item';
		if(headerMiss)
		{
			if(!inputResource.subscription)
				var str2=str.substring(0, str.lastIndexOf('/'));
			else 
				var str2='subscribed';

			var newElement=$('#ResourceCalDAV'+todoString+'ListTemplate').find('.resourceCalDAV'+todoString+'_header').clone().wrap('<div>');
			newElement.attr('data-id',str2);
			newElement.append(headerObject.displayvalue);
			if(todoString=='')
				newElement.find('input[type=checkbox]').attr('onclick','resourceChBoxClick(this, \''+resList+'\', \''+resHeader+'\', false);if(isCalDAVLoaded){$(this).prop(\'checked\')?enableResource($(this).parent()):disableResource($(this).parent());}');
			else
				newElement.find('input[type=checkbox]').attr('onclick','resourceChBoxClick(this, \''+resList+'\', \''+resHeader+'\', false);if(isCalDAVLoaded){$(this).prop(\'checked\')?enableResourceTodo($(this).parent()):disableResourceTodo($(this).parent());}');

			if(typeof inputResource.showHeader!='undefined' && !inputResource.showHeader)
				newElement.css('display', 'none');

			newElement=newElement.parent().html();
			$('#ResourceCalDAV'+todoString+'List').children().eq(insertIndex).after(newElement);
		}
		// insert the resource to interface
		var newElement = $('#ResourceCalDAV'+todoString+'ListTemplate').find('.resourceCalDAV'+todoString+'_item').clone().wrap('<div>');
		var par=inputResource.uid.split('/');

		if(inputResource.permissions.read_only)
			newElement.addClass('resourceCalDAV_item_ro');

		newElement.attr('data-id', inputResource.uid);
		if(globalCalDAVInitLoad)
			newElement.addClass('r_operate');

		newElement.html("<div class='resourceCalDAVColor' style='background:"+inputResource.ecolor+";'></div><input type='checkbox' name="+inputResource.uid+" />"+$('<div/>').text(inputResource.displayvalue).html());
		if(todoString=='')
			newElement.find('input[type=checkbox]').attr({'data-id':inputResource.uid, 'onclick':'var evt = arguments[0];evt.stopPropagation();collectionChBoxClick(this, \''+resList+'\', \''+resHeader+'\', \''+resItem+'\', null, false);if(isCalDAVLoaded){$(this).prop(\'checked\')?enableCalendar(\''+inputResource.uid+'\'):disableCalendar(\''+inputResource.uid+'\');}'});
		else
			newElement.find('input[type=checkbox]').attr({'data-id':inputResource.uid, 'onclick':'var evt = arguments[0];evt.stopPropagation();collectionChBoxClick(this, \''+resList+'\', \''+resHeader+'\', \''+resItem+'\', null, false);if(isCalDAVLoaded){$(this).prop(\'checked\')?enableCalendarTodo(\''+inputResource.uid+'\'):disableCalendarTodo(\''+inputResource.uid+'\');}'});
		newElement=newElement.parent().html();
		$('#ResourceCalDAV'+todoString+'List').children().eq(insertIndex+headerMiss).after(newElement);
		// restore the checked state
		if(makeChecked)
			$('#ResourceCalDAV'+todoString+'List').children().eq(insertIndex+headerMiss+1).find('input[type=checkbox]').prop('checked', true);
		if(!globalCalDAVInitLoad)
			CalDAVnetLoadCollection(inputResource, false, false, insertIndex+headerMiss-1, collObject);
	}

	this.removeOldResources=function(inputUidBase, inputTimestamp)
	{
		var nextCandidateToActive=null;
		var tmp=inputUidBase.match(RegExp('^(https?://)(.*)', 'i'));
		var inputHref=tmp[2];
		for(var i=this.collections.length-1;i>=0;i--)
		{
			if(this.collections[i]!=undefined && !this.collections[i].subscription && this.collections[i].timestamp!=undefined && this.collections[i].accountUID.indexOf(inputHref)!=-1 && this.collections[i].timestamp<inputTimestamp)
			{
				var uidRemoved=this.collections[i].uid;
				//if(globalEventList.displayEventsArray[uidRemoved].length>0) 
				//{
					$('#calendar').fullCalendar('removeEventSource', this.collections[i].fcSource);
					if(globalSettings.displayhiddenevents)
					{
						for(var k=1;k<globalResourceCalDAVList.collections.length;k++)
						{
							if(globalResourceCalDAVList.collections[k].uid!=undefined)
							{
								var pos=globalVisibleCalDAVCollections.indexOf(globalResourceCalDAVList.collections[k].uid);
								if(pos==-1)
									$("#SystemCalDAV div [data-res-id='"+globalResourceCalDAVList.collections[k].uid+"']").addClass('checkCalDAV_hide');
							}
						}
					}
				//}
				var item=$('#ResourceCalDAVList').find('[data-id^="'+jqueryEscapeSelector(this.collections[i].uid)+'"]');
				var item_prev=item.prev();
				
				var item_was_selected=item.hasClass('resourceCalDAV_item_selected');
				if(item_was_selected)
				{
					// select the nearest candidate to load
					if((i+1)<=(this.collections.length-1))
					{
						if(this.collections[i+1].headerOnly!=true)
							nextCandidateToActive=this.collections[i+1];
						else if((i+2)<=(this.collections.length-1))
							nextCandidateToActive=this.collections[i+2];
					}
					if(nextCandidateToActive==null && (i-1)>0)
					{
						if(this.collections[i-1].headerOnly!=true)
							nextCandidateToActive=this.collections[i-1];
						else if((i-2)>0)
							nextCandidateToActive=this.collections[i-2];
					}
				}
				
				item.remove();
				this.collections.splice(i, 1);

				// if next item is undefined or it is a header and the previous item is header delete it
				if((this.collections[i]==undefined || this.collections[i].headerOnly==true) && this.collections[i-1].headerOnly==true)
				{
					item_prev.remove();
					this.collections.splice(--i, 1);
				}
				// make another resource active
				if(nextCandidateToActive!=null)
					$('#ResourceCalDAVList').find('.resourceCalDAV_item[data-id^="'+nextCandidateToActive.uid+'"]').addClass('resourceCalDAV_item_selected');
			}
		}
		var nextCandidateToActive=null;
		for(var i=this.TodoCollections.length-1;i>=0;i--)
		{
			if(this.TodoCollections[i]!=undefined && !this.TodoCollections[i].subscription && this.TodoCollections[i].timestamp!=undefined && this.TodoCollections[i].accountUID.indexOf(inputHref)!=-1 && this.TodoCollections[i].timestamp<inputTimestamp)
			{
				var uidRemoved=this.TodoCollections[i].uid;
				//if(globalEventList.displayEventsArray[uidRemoved].length>0) 
				//{
					$('#todoList').fullCalendar('removeEventSource', this.TodoCollections[i].fcSource);
					
				//}
				var item=$('#ResourceCalDAVTODOList').find('[data-id^="'+jqueryEscapeSelector(this.TodoCollections[i].uid)+'"]');
				var item_prev=item.prev();
				
				var item_was_selected=item.hasClass('resourceCalDAV_item_selected');
				if(item_was_selected)
				{
					// select the nearest candidate to load
					if((i+1)<=(this.TodoCollections.length-1))
					{
						if(this.TodoCollections[i+1].headerOnly!=true)
							nextCandidateToActive=this.TodoCollections[i+1];
						else if((i+2)<=(this.TodoCollections.length-1))
							nextCandidateToActive=this.TodoCollections[i+2];
					}
					if(nextCandidateToActive==null && (i-1)>0)
					{
						if(this.TodoCollections[i-1].headerOnly!=true)
							nextCandidateToActive=this.TodoCollections[i-1];
						else if((i-2)>0)
							nextCandidateToActive=this.TodoCollections[i-2];
					}
				}
				
				item.remove();
				this.TodoCollections.splice(i, 1);

				// if next item is undefined or it is a header and the previous item is header delete it
				if((this.TodoCollections[i]==undefined || this.TodoCollections[i].headerOnly==true) && this.TodoCollections[i-1].headerOnly==true)
				{
					item_prev.remove();
					this.TodoCollections.splice(--i, 1);
				}
				// make another resource active
				if(nextCandidateToActive!=null)
					$('#ResourceCalDAVTODOList').find('.resourceCalDAVTODO_item[data-id^="'+nextCandidateToActive.uid+'"]').addClass('resourceCalDAV_item_selected');
			}
		}
	}

	this.removeResource=function(inputUid, loadNext,isEvent)
	{
		if(isEvent)
		{
			for(var i=this.collections.length-1;i>=0;i--)
				if(this.collections[i].uid==inputUid)
				{
					var uidRemoved=this.collections[i].uid;
					var item=$('#ResourceCalDAVList').find('[data-id^="'+jqueryEscapeSelector(this.collections[i].uid)+'"]');
					var item_prev=item.prev();
					item.remove();
					this.collections.splice(i, 1);

					// if next item is undefined or it is a header and the previous item is header delete it
					if((this.collections[i]==undefined || this.collections[i].headerOnly==true) && this.collections[i-1].headerOnly==true)
					{
						item_prev.remove();
						this.collections.splice(i, 1);
					}
				}
		}
		else
			for(var i=this.TodoCollections.length-1;i>=0;i--)
				if(this.TodoCollections[i].uid==inputUid)
				{
					var uidRemoved=this.TodoCollections[i].uid;
					var item=$('#ResourceCalDAVTODOList').find('[data-id^="'+jqueryEscapeSelector(this.TodoCollections[i].uid)+'"]');
					var item_prev=item.prev();
					item.remove();
					this.TodoCollections.splice(i, 1);

					// if next item is undefined or it is a header and the previous item is header delete it
					if((this.TodoCollections[i]==undefined || this.TodoCollections[i].headerOnly==true) && this.TodoCollections[i-1].headerOnly==true)
					{
						item_prev.remove();
						this.TodoCollections.splice(i, 1);
					}
				}
	}

	this.getCollectionByUID=function(inputUID)
	{
		for(var i=0;i<this.collections.length;i++)
		{
			if(this.collections[i].uid==inputUID)
				return this.collections[i];
		}
		for(var i=0;i<this.TodoCollections.length;i++)
		{
			if(this.TodoCollections[i].uid==inputUID)
				return this.TodoCollections[i];
		}
		return null;
	}
	this.getEventCollectionByUID=function(inputUID)
	{
		for(var i=0;i<this.collections.length;i++)
		{
			if(this.collections[i].uid==inputUID)
				return this.collections[i];
		}
		return null;
	}
	this.getTodoCollectionByUID=function(inputUID)
	{
		for(var i=0;i<this.TodoCollections.length;i++)
		{
			if(this.TodoCollections[i].uid==inputUID)
				return this.TodoCollections[i];
		}
		return null;
	}
	this.getTodoCollectionAndIndexByUID=function(inputUID)
	{
		for(var i=0;i<this.TodoCollections.length;i++)
		{
			if(this.TodoCollections[i].uid==inputUID)
				return {coll:this.TodoCollections[i],index:i};
		}
		return null;
	}

	this.getResources=function()
	{
		return this.collections;
	}

	this.getSyncResourceArray=function()
	{
		return this.syncResourceArray;
	}
}
