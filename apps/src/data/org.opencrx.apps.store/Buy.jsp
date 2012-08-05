<%@ page import="org.opencrx.apps.store.common.util.RequestHelper,
                 org.opencrx.apps.store.common.PrimaryKey,
                 org.opencrx.apps.store.common.IStandardObject,
                 org.opencrx.apps.store.objects.Product,
                 org.opencrx.apps.store.manager.ProductManager,
                 org.opencrx.apps.store.objects.Order,
                 org.opencrx.apps.store.common.util.SessionHelper,
                 org.opencrx.apps.store.manager.OrderManager,
                 org.opencrx.apps.store.objects.OrderItem,
                 org.opencrx.apps.store.manager.OrderItemManager,
                 org.opencrx.apps.store.common.util.ResponseHelper"%>
 <%--
  Stores the specified product in the current cart of the user
  User: Omar Al Zabir
  Date: Nov 6, 2004
  Time: 1:00:44 AM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // 1. Get the product ID from URL
    final RequestHelper requestHelper = new RequestHelper( request );
	final SessionHelper sessionHelper = new SessionHelper(request);
    final PrimaryKey productID = new PrimaryKey( requestHelper.getParameter( IStandardObject.PRIMARY_KEY ) );

    // 2. Get the product
    final ProductManager productManager = new ProductManager(sessionHelper.getApplicationContext());
    Product product = productManager.get( productID );

    // 3. Get the cart
    Order order = sessionHelper.getOrder();

    // 4. If no order started yet, initiate an order
    final OrderManager orderManager = new OrderManager(sessionHelper.getApplicationContext());
    if( null == order )
    {
        // 4.1 Initiate a new order
        order = orderManager.startNew( sessionHelper.getCurrentUser().getKey() );
        sessionHelper.setOrder( order );
    }

    // 5. Add the current product in the order
    final OrderItemManager itemManager = new OrderItemManager(sessionHelper.getApplicationContext());
    final OrderItem item = itemManager.newOrder( order.getKey(), productID );

    // 6. Go back to shop page
    final ResponseHelper responseHelper = new ResponseHelper( response );
    out.println( responseHelper.redirect( responseHelper.shopUrl( product.getCategoryID() ) ) );
%>