<%@ page import="org.opencrx.apps.store.common.util.RequestHelper,
                 org.opencrx.apps.store.common.PrimaryKey,
                 org.opencrx.apps.store.common.IStandardObject,
                 org.opencrx.apps.store.manager.ProductManager,
                 org.opencrx.apps.store.objects.Product,
                 org.opencrx.apps.store.common.util.ResponseHelper,
                 org.opencrx.apps.store.common.util.SessionHelper,
                 org.opencrx.apps.store.manager.OrderItemManager,
                 org.opencrx.apps.store.objects.OrderItem"%>
 <%--
  Drops an order item. An order item ID is specified in the request. This is used to remove
  products from cart.
  User: Omar Al Zabir
  Date: Nov 6, 2004
  Time: 11:01:12 AM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // 1. Get the product ID from URL
    RequestHelper requestHelper = new RequestHelper( request );
    SessionHelper sessionHelper = new SessionHelper( request );
    PrimaryKey key = new PrimaryKey( requestHelper.getParameter( IStandardObject.PRIMARY_KEY ) );

    // 2. get the order
    ProductManager manager = new ProductManager(sessionHelper.getApplicationContext());
    OrderItemManager orderItemManager = new OrderItemManager(sessionHelper.getApplicationContext());

    OrderItem item = orderItemManager.get( key );
    Product product = manager.get( item.getProductID() );

    // 3. Delete the order item
    orderItemManager.delete( key );

    // 3. Return to shop page
    ResponseHelper responseHelper = new ResponseHelper( response );
    out.println( responseHelper.redirect( responseHelper.shopUrl( product.getCategoryID() )) );
%>