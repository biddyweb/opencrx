<%@ page import="org.opencrx.store.common.PrimaryKey,
                 org.opencrx.store.common.util.RequestHelper,
                 org.opencrx.store.common.IStandardObject,
                 org.opencrx.store.manager.ProductManager,
                 org.opencrx.store.common.util.SessionHelper,
                 org.opencrx.store.common.util.ResponseHelper,
                 org.opencrx.store.objects.Product"%>
 <%--
  Delete the specified product and go back to shop page
  User: Omar Al Zabir
  Date: Nov 6, 2004
  Time: 12:26:01 AM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // 1. Get the product ID from URL
    RequestHelper requestHelper = new RequestHelper( request );
	SessionHelper sessionHelper = new SessionHelper(request);
    PrimaryKey key = new PrimaryKey( requestHelper.getParameter( IStandardObject.PRIMARY_KEY ) );

    // 2. Delete the product
    ProductManager manager = new ProductManager(sessionHelper.getOpenCrxContext());
    Product product = manager.get( key );
    manager.delete( key );

    // 3. Return to shop page
    ResponseHelper responseHelper = new ResponseHelper( response );
    out.println( responseHelper.redirect( responseHelper.shopUrl( product.getCategoryID() )) );
%>