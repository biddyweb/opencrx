<%@ page import="org.opencrx.store.common.util.RequestHelper,
                 org.opencrx.store.common.PrimaryKey,
                 org.opencrx.store.common.IStandardObject,
                 org.opencrx.store.manager.ProductManager,
                 org.opencrx.store.objects.Product,
                 org.opencrx.store.common.util.ResponseHelper,
                 org.opencrx.store.common.util.SessionHelper,
                 org.opencrx.store.objects.Order,
                 org.opencrx.store.manager.OrderItemManager,
                 org.opencrx.store.manager.OrderManager"%>
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
    final ProductManager manager = new ProductManager(sessionHelper.getOpenCrxContext());
    final Product product = manager.get( productKey );

    // 3. Get current order being processed
    Order order = sessionHelper.getOrder();
    
    // 4. If no order initiated yet, start a new one
    if( null == order )
    {
        final OrderManager orderManager = new OrderManager(sessionHelper.getOpenCrxContext());
        order = orderManager.startNew( sessionHelper.getCurrentUser().getKey() );
        sessionHelper.setOrder( order );
    }

    // 5. Add the order itme
    final OrderItemManager orderItemManager = new OrderItemManager(sessionHelper.getOpenCrxContext());
    orderItemManager.newOrder( order.getKey(), productKey, product.getUnitPrice() );

    // 6. Return to shop page
    final ResponseHelper responseHelper = new ResponseHelper( response );
    out.println("<html><body>");
    out.println( responseHelper.redirect( responseHelper.shopUrl( product.getCategoryID() )) );
    out.println("</body></html>");
%>
