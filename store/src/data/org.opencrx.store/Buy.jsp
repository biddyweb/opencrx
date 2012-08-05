<%@ page import="org.opencrx.store.common.util.RequestHelper,
                 org.opencrx.store.common.PrimaryKey,
                 org.opencrx.store.common.IStandardObject,
                 org.opencrx.store.objects.Product,
                 org.opencrx.store.manager.ProductManager,
                 org.opencrx.store.objects.Order,
                 org.opencrx.store.common.util.SessionHelper,
                 org.opencrx.store.manager.OrderManager,
                 org.opencrx.store.objects.OrderItem,
                 org.opencrx.store.manager.OrderItemManager,
                 org.opencrx.store.common.util.ResponseHelper"%>
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
    final ProductManager productManager = new ProductManager(sessionHelper.getOpenCrxContext());
    Product product = productManager.get( productID );

    // 3. Get the cart
    Order order = sessionHelper.getOrder();

    // 4. If no order started yet, initiate an order
    final OrderManager orderManager = new OrderManager(sessionHelper.getOpenCrxContext());
    if( null == order )
    {
        // 4.1 Initiate a new order
        order = orderManager.startNew( sessionHelper.getCurrentUser().getKey() );
        sessionHelper.setOrder( order );
    }

    // 5. Add the current product in the order
    final OrderItemManager itemManager = new OrderItemManager(sessionHelper.getOpenCrxContext());
    final OrderItem item = itemManager.newOrder( order.getKey(), productID );

    // 6. Go back to shop page
    final ResponseHelper responseHelper = new ResponseHelper( response );
    out.println( responseHelper.redirect( responseHelper.shopUrl( product.getCategoryID() ) ) );
%>