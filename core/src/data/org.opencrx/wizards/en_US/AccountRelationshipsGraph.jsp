<%@  page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %><%
/*
 * ====================================================================
 * Project:     opencrx, http://www.opencrx.org/
 * Name:        $Id: AccountRelationshipsGraph.jsp,v 1.10 2009/06/09 14:18:15 wfro Exp $
 * Description: Draw membership graph for an account
 * Revision:    $Revision: 1.10 $
 * Owner:       CRIXP Corp., Switzerland, http://www.crixp.com
 * Date:        $Date: 2009/06/09 14:18:15 $
 * ====================================================================
 *
 * This software is published under the BSD license
 * as listed below.
 *
 * Copyright (c) 2008, CRIXP Corp., Switzerland
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in
 * the documentation and/or other materials provided with the
 * distribution.
 *
 * * Neither the name of CRIXP Corp. nor the names of the contributors
 * to openCRX may be used to endorse or promote products derived
 * from this software without specific prior written permission
 *
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * ------------------
 *
 * This product includes software developed by the Apache Software
 * Foundation (http://www.apache.org/).
 *
 * This product includes software developed by contributors to
 * openMDX (http://www.openmdx.org/)
 */
%><%@ page session="true" import="
java.util.*,
java.io.*,
java.text.*,
java.math.*,
java.net.*,
java.sql.*,
javax.naming.Context,
javax.naming.InitialContext,
org.openmdx.base.accessor.jmi.cci.*,
org.openmdx.base.exception.*,
org.openmdx.portal.servlet.*,
org.openmdx.portal.servlet.attribute.*,
org.openmdx.portal.servlet.view.*,
org.openmdx.portal.servlet.texts.*,
org.openmdx.portal.servlet.control.*,
org.openmdx.portal.servlet.reports.*,
org.openmdx.portal.servlet.wizards.*,
org.openmdx.base.naming.*,
org.openmdx.base.query.*,
org.openmdx.application.log.*
" %>

<%!

	private static String getNodeTitle(
		org.opencrx.kernel.account1.jmi1.Account account,
		ApplicationContext app
	) {
	  StringBuilder title = new StringBuilder();
	  boolean isDisabled = account.isDisabled() != null && account.isDisabled().booleanValue();

	  title.append(
	    isDisabled ? "<del>" : ""
	  ).append(
		  app.getHtmlEncoder().encode(new ObjectReference(account, app).getTitle(), false)
		).append(
		  isDisabled ? "</del>" : ""
		);
		return title.toString();
  }

	private static String getClickableNodeTitle(
		org.opencrx.kernel.account1.jmi1.Account account,
		ApplicationContext app,
		String requestId
	) {
    ObjectReference accountRef = new ObjectReference(account, app);
	  boolean isDisabled = account.isDisabled() != null && account.isDisabled().booleanValue();
		String test = account.refMofId();
		int plusLocation = test.indexOf("+");
		while (plusLocation >= 0) {
			test = test.substring(0, plusLocation) + "%2B" + test.substring(plusLocation + 1);
			plusLocation = test.indexOf("+");
		}
		String encodedAccountXri = test;

	  StringBuilder title = new StringBuilder();
		title.append(
		  "<a href='../../" + accountRef.getSelectObjectAction().getEncodedHRef(requestId) + "'>*</a> <a href='AccountRelationshipsGraph.jsp?xri=" + encodedAccountXri + "&requestId=" + requestId + "'>" + getNodeTitle(account,app) + "</a>"
		);
		return title.toString();
  }

	private static String getJSON(
		Set parents,
		org.opencrx.kernel.account1.jmi1.Account account,
		org.opencrx.kernel.account1.jmi1.AccountMembership membership,
		String relationshipKey,
		Map M,
		String indent,
		int level,
		Map memberRoleTexts,
		HttpServletResponse response,
		ApplicationContext app,
		String requestId
	) {
	  StringBuilder json = new StringBuilder();
		if(level > 0) {
			String relationships = new String();
			if (membership != null && memberRoleTexts != null) {
			  for(Iterator roles = membership.getMemberRole().iterator(); roles.hasNext(); ) {
			    if (relationships.length() > 0) {
			      relationships += ", ";
			    }
			    relationships += (String)memberRoleTexts.get(new Short(((Short)roles.next()).shortValue()));
			  }
			}

			String accountId = account.refGetPath().getBase();
			if (accountId.indexOf("+") > 0) {
			  System.out.println("accountId="+accountId);
			}
			json.append(
				"\n" + indent
			).append(
				"{id:\"" + accountId + "\""
			).append(
				 ",name:\"" + getClickableNodeTitle(account, app, requestId) + "\""
			).append(
				",children:["
			);
			Map C = (Map)M.get(account);
			if(C != null) {
				String separator = "";
				for(Iterator i = C.keySet().iterator(); i.hasNext(); ) {
					org.opencrx.kernel.account1.jmi1.Account child = (org.opencrx.kernel.account1.jmi1.Account)i.next();
					if(child != null && !parents.contains(child)) {
						Set parentsWithChild = new HashSet(parents);
						parentsWithChild.add(child);
						String jsonChild = getJSON(
							parentsWithChild,
							child,
							(org.opencrx.kernel.account1.jmi1.AccountMembership)C.get(child),
							accountId,
							M,
							indent + "  ",
							level - 1,
							memberRoleTexts,
							response,
							app,
							requestId
						);
						if(jsonChild.length() > 0) {
							json.append(
								separator
							).append(
								jsonChild
							);
							separator = ",";
						}
					}
				}
		  }
			json.append(
			  "]"
			).append(
				",data:[" + (membership == null ? "" : "{key:\"" + relationshipKey + "\",value:\"" + (relationships.length() == 0 ? "-" : relationships) + "\"}") + "]"
			);
			json.append("}");
		}
		return json.toString();
	}

	private static void addNode(
		org.opencrx.kernel.account1.jmi1.AccountMembership membership,
		org.opencrx.kernel.account1.jmi1.Account accountFrom,
		org.opencrx.kernel.account1.jmi1.Account accountTo,
		Map M
	) {
		Map C = (Map)M.get(accountFrom);
		if(C == null) {
			M.put(
				accountFrom,
				C = new HashMap()
			);
		}
		org.opencrx.kernel.account1.jmi1.AccountMembership N = (org.opencrx.kernel.account1.jmi1.AccountMembership)C.get(accountTo);
		if(N == null) {
			C.put(
				accountTo,
				N = membership
			);
		}
	}

	private static void addRelationships(
		org.opencrx.kernel.account1.jmi1.Account account,
		Map M,
		int level,
		javax.jdo.PersistenceManager pm
	) {
		if(level >= 0) {
			org.opencrx.kernel.account1.jmi1.Account1Package accountPkg = org.opencrx.kernel.utils.Utils.getAccountPackage(pm);
			int[] maxCounts = new int[]{5, 5, 25};
			int maxCount = maxCounts[level];

 			org.opencrx.kernel.account1.cci2.AccountMembershipQuery membershipQuery = accountPkg.createAccountMembershipQuery();
      org.openmdx.compatibility.datastore1.jmi1.QueryFilter queryFilter =
          (org.openmdx.compatibility.datastore1.jmi1.QueryFilter)pm.newInstance(org.openmdx.compatibility.datastore1.jmi1.QueryFilter.class);
      Collection memberships = null;
 			int count = 0;

			// acountFrom=account [ignore mebers if account is disabled!!!]
			if ((account.isDisabled() == null) || (!account.isDisabled().booleanValue())) {
  			membershipQuery.forAllDisabled().isFalse();
  			membershipQuery.distance().greaterThanOrEqualTo(new Integer(-1));
  			membershipQuery.distance().lessThanOrEqualTo(new Integer(1));
  			membershipQuery.thereExistsAccountFrom().equalTo(account);
  			// HINT_DBOBJECT allows to qualify the DbObject to use.
  			// For distance +/-1 memberships use ACCTMEMBERSHIP1 instead of ACCTMEMBERSHIP
        queryFilter.setClause(
          "(" + org.openmdx.application.dataprovider.layer.persistence.jdbc.Database_1_Attributes.HINT_DBOBJECT + "1 */ (1=1) ) and " +
          "( " +
          "v.member IN ( " +
          "  select distinct(member) from oocke1_tobj_acctmembership1 m, oocke1_account a " +
          "  where " +
          "   ((m.disabled is null) or (m.disabled = false)) and " +
          "   ((m.account_to   = a.object_id) and ((a.disabled is null) or (a.disabled = false))) " +
          "  ) " +
          ") "
        );
  			membershipQuery.thereExistsContext().equalTo(queryFilter);
  			memberships = account.getAccountMembership(membershipQuery);
  			for(Iterator i = memberships.iterator(); i.hasNext(); ) {
  				org.opencrx.kernel.account1.jmi1.AccountMembership membership = (org.opencrx.kernel.account1.jmi1.AccountMembership)i.next();
  				addNode(
  					membership,
  					membership.getAccountFrom(),
  					membership.getAccountTo(),
  					M
  				);
  				addNode(
  					membership,
  					membership.getAccountTo(),
  					membership.getAccountFrom(),
  					M
  				);
  				if (membership.getAccountTo() != null) {
  	  				addRelationships(
  	  					membership.getAccountTo(),
  	  					M,
  	  					level - 1,
  	  					pm
  	  				);
  	  				count++;
    			    }
  				if(count > maxCount) break;
  			}
  	  }

			// acountTo=account
			membershipQuery = accountPkg.createAccountMembershipQuery();
			membershipQuery.forAllDisabled().isFalse();
			membershipQuery.distance().greaterThanOrEqualTo(new Integer(-1));
			membershipQuery.distance().lessThanOrEqualTo(new Integer(1));
			membershipQuery.thereExistsAccountTo().equalTo(account);
			// HINT_DBOBJECT allows to qualify the DbObject to use.
			// For distance +/-1 memberships use ACCTMEMBERSHIP1 instead of ACCTMEMBERSHIP
      queryFilter.setClause(
    		  org.openmdx.application.dataprovider.layer.persistence.jdbc.Database_1_Attributes.HINT_DBOBJECT + "1 */ (1=1)"
      );
			membershipQuery.thereExistsContext().equalTo(queryFilter);
			memberships = account.getAccountMembership(membershipQuery);
			count = 0;
			for(Iterator i = memberships.iterator(); i.hasNext(); ) {
				org.opencrx.kernel.account1.jmi1.AccountMembership membership = (org.opencrx.kernel.account1.jmi1.AccountMembership)i.next();
				addNode(
					membership,
					membership.getAccountFrom(),
					membership.getAccountTo(),
					M
				);
				addNode(
					membership,
					membership.getAccountTo(),
					membership.getAccountFrom(),
					M
				);
				if (membership.getAccountFrom() != null) {
  				addRelationships(
  					membership.getAccountFrom(),
  					M,
  					level - 1,
  					pm
  				);
  				count++;
  		  }
				if(count > maxCount) break;
			}
		}
	}

%>

<%
	request.setCharacterEncoding("UTF-8");
	ApplicationContext app = (ApplicationContext)session.getValue(WebKeys.APPLICATION_KEY);
	ViewsCache viewsCache = (ViewsCache)session.getValue(WebKeys.VIEW_CACHE_KEY_SHOW);
	String requestId =  request.getParameter(Action.PARAMETER_REQUEST_ID);
  String objectXri = request.getParameter("xri");
	if(app==null || objectXri==null || viewsCache.getViews().isEmpty()) {
    response.sendRedirect(
       request.getContextPath() + "/" + WebKeys.SERVLET_NAME
    );
    return;
  }
  javax.jdo.PersistenceManager pm = app.getPmData();
	Texts_1_0 texts = app.getTexts();

	try {
		Codes codes = app.getCodes();
		short currentLocale = app.getCurrentLocaleAsIndex();

		Path objectPath = new Path(objectXri);
		String providerName = objectPath.get(2); // e.g. CRX
		String segmentName = objectPath.get(4);  // e.g. Standard

		RefObject_1_0 obj = (RefObject_1_0)pm.getObjectById(new Path(objectXri));
		org.opencrx.kernel.account1.jmi1.Account account = (org.opencrx.kernel.account1.jmi1.Account)obj;

		// Derive membership matrix M for account. M has the structure M[accountFrom,accountTo] where
		// a matrix element is the distance of the relationship accountFrom->accountTo starting from account.
		Map M = new HashMap();
		addRelationships(
			account,
			M,
			2,
			pm
		);
		HashSet parents = new HashSet();
		parents.add(account);
		String json =  getJSON(
			parents,
			account,
			null, // membership
			null, // relationship key
			M,
			"",
			//Math.min(M.size(), 4),
			Math.min(Math.max(1, M.size()), 4),
			app.getCodes().getLongText(
				"org:opencrx:kernel:account1:AccountMembership:memberRole",
				app.getCurrentLocaleAsIndex(),
				true,
				true
			),
			response,
			app,
			requestId
		);
		//System.out.println("JSON1=" + json);
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<title>Relationships Graph</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<meta name="label" content="Relationships Graph">
	<meta name="toolTip" content="Relationships Graph">
	<meta name="targetType" content="_blank">
	<meta name="forClass" content="org:opencrx:kernel:account1:Contact">
	<meta name="forClass" content="org:opencrx:kernel:account1:LegalEntity">
	<meta name="forClass" content="org:opencrx:kernel:account1:UnspecifiedAccount">
	<meta name="forClass" content="org:opencrx:kernel:account1:Group">
	<meta name="order" content="8702">
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<link rel='shortcut icon' href='../../images/favicon.ico' />
	<!-- JIT -->
	<link href="../../_style/infovis.css" rel="stylesheet" type="text/css" />
	<style type="text/css" media="all">
		#infovis {
		background-color:#222;
		width:900px;
		height:500px;
		}
		.node {
		background-color: #222;
		color:orange;
		font-weight:bold;
		cursor:pointer;
		padding:2px;
		}
	</style>
	<script language="javascript" type="text/javascript" src="../../javascript/jit/mootools-beta-1.2b2.js"></script>
	<!--[if IE]><script language="javascript" type="text/javascript" src="../../javascript/jit/excanvas.js"></script><![endif]-->
	<script language="javascript" type="text/javascript" src="../../javascript/jit/Hypertree.js"></script>
	<script language="javascript" type="text/javascript" src="../../javascript/jit/infovis.js"></script>
	<script type="text/javascript">
		function init() {
			var json =  <%= json %>;
			Infovis.initLayout();
			var C = new Canvas('infovis', '#fff', '#fff');
			var B = new Hypertree(C);
			B.controller = {
				onBeforeCompute:function(F){
				},
        getName:function(G,F){
          for(var H=0;H<G.data.length;H++){
            var I=G.data[H];
            if(I.key==F.name){
              return I.value
            }
          }
          for(var H=0;H<F.data.length;H++){
            var I=F.data[H];
            if(I.key==G.name){
              return I.value
            }
          }
        },
        getId:function(G,F){
          for(var H=0;H<G.data.length;H++){
            var I=G.data[H];
            if(I.key==F.id){
              return I.value
            }
          }
          for(var H=0;H<F.data.length;H++){
            var I=F.data[H];
            if(I.key==G.id){
              return I.value
            }
          }
        },
				onCreateLabel:function(H,F){
					$(H).setHTML(F.name).addEvents({
						click:function(I){
							B.onClick(I.event)
						}
					});
					var G=$(H);
					E[F.id]=new Fx.Tween(
						G,
						"opacity",
						{
							duration:300,
							transition:Fx.Transitions.linear,
							wait:false
						}
					);
					G.setOpacity(0.8);
					G.set("html",F.name).addEvents({
						mouseenter:function(){
							E[F.id].start(0.8,1)
						},
						mouseleave:function(){
							E[F.id].start(1,0.8)
						}
					})
				},
				onPlaceLabel:function(F,I){
					var H=F.offsetWidth;
					var G=F.style.left.toInt();
					G-=H/2;
					F.style.left=G+"px"
				},
				onAfterCompute:function(){
					var H=GraphUtil.getClosestNodeToOrigin(B.graph,"pos");
					var F=this;
					var G="<h4>"+H.name+"</h4><b>Relationships:</b>";
					G+="<ul>";
					GraphUtil.eachAdjacency(
						B.graph,
						H,
						function(I){
							if(I.data&&I.data.length>0){
								G+="<li>"+I.name+' <div class="relation">(relationship: '+F.getId(H,I)+")</div></li>"
							}
						}
					);
					G+="</ul>";$("inner-details").set("html",G)
				}
			};
			var E = {};
			B.loadTreeFromJSON(json);
			try{
			  B.compute();
			  B.plot();
  			B.prepareCanvasEvents();
  			B.controller.onAfterCompute();
			}catch(e) {
			  $('inner-details').innerHTML = 'root node disabled or no relationships<br>or relationships to disabled accounts only';
			}
		}
	</script>
</head>
<body onload="init();">
  <div id="header"></div>
	<div id="visheader"></div>
	<div id="left">
		<div id="details" class="toggler left-item">
		  Root: <b><%= getClickableNodeTitle(account, app, requestId) %></b><br>
		  <input type="Submit" name="Cancel.Button" tabindex="8020" value="X" onClick="javascript:window.close();" style="float:right;margin:5px 5px 0px 0px;" /><br>
		  Node Limits: 25 - 5 - 5
		</div>
		<div class="element contained-item">
			<div class="inner" id="inner-details"></div>
		</div>
	</div>
	<canvas id="infovis" width="900" height="500"></canvas>
	<div id="label_container"></div>
<%
	}
	catch (Exception e) {
 	  new ServiceException(e).log();
  }
%>
</body>
</html>
