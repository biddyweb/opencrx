<%@ page import="org.opencrx.apps.store.common.util.RequestHelper,
                 org.opencrx.apps.store.common.PrimaryKey,
                 org.opencrx.apps.store.common.IStandardObject,
                 org.opencrx.apps.store.manager.ProductManager,
                 org.opencrx.apps.store.objects.Product,
                 org.opencrx.apps.store.common.util.ResponseHelper,
                 org.opencrx.apps.store.common.util.SessionHelper,
                 org.opencrx.apps.store.objects.Order,
                 org.opencrx.apps.store.manager.OrderItemManager,
                 org.opencrx.apps.store.manager.OrderManager"%>
 <%--
  Add order item in current order
  User: Omar Al Zabir
  Date: Nov 6, 2004
  Time: 11:04:18 AM          
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // 1. Get the product ID from URL
    final RequestHelper requestHelper = new RequestHelper( request );
	final SessionHelper sessionHelper = new SessionHelper(request);
    final PrimaryKey productKey = new PrimaryKey( requestHelper.getParameter( IStandardObject.PRIMARY_KEY ), false );

    // 2. Delete the product
    final ProductManager manager = new ProductManager(sessionHelper.getApplicationContext());
    final Product product = manager.get( productKey );

    // 3. Get current order being processed
    Order order = sessionHelper.getOrder();
    
    // 4. If no order initiated yet, start a new one
    if( null == order )
    {
        final OrderManager orderManager = new OrderManager(sessionHelper.getApplicationContext());
        order = orderManager.startNew( sessionHelper.getCurrentUser().getKey() );
        sessionHelper.setOrder( order );
    }

    // 5. Add the order itme
    final OrderItemManager orderItemManager = new OrderItemManager(sessionHelper.getApplicationContext());
    orderItemManager.newOrder( order.getKey(), productKey, product.getUnitPrice() );

    // 6. Return to shop page
    final ResponseHelper responseHelper = new ResponseHelper( response );
    out.println("<html><body>");
    out.println( responseHelper.redirect( responseHelper.shopUrl( product.getCategoryID() )) );
    out.println("</body></html>");
%>
