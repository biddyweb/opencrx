<%@ page import="org.opencrx.store.common.util.RequestHelper,
                 org.opencrx.store.common.PrimaryKey,
                 org.opencrx.store.common.IStandardObject,
                 org.opencrx.store.manager.ProductManager,
                 org.opencrx.store.objects.Product,
                 org.opencrx.store.common.util.ResponseHelper,
                 org.opencrx.store.common.util.SessionHelper,
                 org.opencrx.store.manager.OrderItemManager,
                 org.opencrx.store.objects.OrderItem"%>
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
    ProductManager manager = new ProductManager(sessionHelper.getOpenCrxContext());
    OrderItemManager orderItemManager = new OrderItemManager(sessionHelper.getOpenCrxContext());

    OrderItem item = orderItemManager.get( key );
    Product product = manager.get( item.getProductID() );

    // 3. Delete the order item
    orderItemManager.delete( key );

    // 3. Return to shop page
    ResponseHelper responseHelper = new ResponseHelper( response );
    out.println( responseHelper.redirect( responseHelper.shopUrl( product.getCategoryID() )) );
%>