<%@ page import="org.opencrx.apps.store.common.PrimaryKey,
                 org.opencrx.apps.store.common.util.RequestHelper,
                 org.opencrx.apps.store.common.IStandardObject,
                 org.opencrx.apps.store.manager.ProductManager,
                 org.opencrx.apps.store.common.util.SessionHelper,
                 org.opencrx.apps.store.common.util.ResponseHelper,
                 org.opencrx.apps.store.objects.Product"%>
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
    ProductManager manager = new ProductManager(sessionHelper.getApplicationContext());
    Product product = manager.get( key );
    manager.delete( key );

    // 3. Return to shop page
    ResponseHelper responseHelper = new ResponseHelper( response );
    out.println( responseHelper.redirect( responseHelper.shopUrl( product.getCategoryID() )) );
%>