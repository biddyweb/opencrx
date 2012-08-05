<%@ page import="org.opencrx.store.common.util.RequestHelper,
                 org.opencrx.store.common.util.ResponseHelper,
                 org.opencrx.store.common.util.SessionHelper,
                 org.opencrx.store.dal.ReportDataAccess,
                 java.util.List,
                 org.opencrx.store.common.PrimaryKey"%>
 <%--
  Created by IntelliJ IDEA.
  User: Omar Al Zabir
  Date: Nov 22, 2004
  Time: 7:59:45 PM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    final RequestHelper requestHelper = new RequestHelper( request );
    final ResponseHelper responseHelper = new ResponseHelper( response );
    final SessionHelper sessionHelper = new SessionHelper( request );

    if( sessionHelper.isAdmin() )
    {
        // 1. Load the orders placed within the specified date range
        final ReportDataAccess reportDataAccess = new ReportDataAccess();
        List items = reportDataAccess.GetProductSaleByCategory(requestHelper.getCurrentCategoryID());
        request.setAttribute( "ProductList", items );
        %>
        <h1>Sale by category</h1>

        <jsp:forward page="ProductSaleList.jsp" />
        <%
    }
    else
    {
%>
Admin view only
<%
    }
%>
