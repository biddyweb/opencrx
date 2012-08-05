<%@ page import="
javax.naming.Context,
java.util.*,
org.opencrx.apps.store.common.util.SessionHelper,
org.opencrx.apps.store.common.util.*,
org.openmdx.base.accessor.jmi.cci.RefPackage_1_0" 
%>
<%--
  Main controller JSP which shows a store with categories and product list.

  This is the entry point to the site.
  
  User: Omar Al Zabir
  Date: Oct 31, 2004
  Time: 2:54:52 PM

--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
	try {
		SessionHelper sessionHelper = new SessionHelper(request);
		if(sessionHelper.getApplicationContext() == null) {
	sessionHelper.setApplicationContext(
		ApplicationContextFactory.createContext(session)
	);
		}
	}
	catch(Exception e) {
		throw new ServletException("can not get OpenCrxContext", e);
	}
%>
<jsp:include page="TemplateBegin.jsp" />

						<!-- Begin .post -->
                        <a name="header"></a>
						<H3 class="post-title">Online store for your daily needs</H3>

                        <!-- Show the category list here -->
                        <jsp:include page="CategoryList.jsp"  />

                        <!-- Show the product list here -->
                        <div class="post">
                        <H4 class="post-title">Products</H4>
                        <jsp:include page="ProductList.jsp"  />
                        </div>

						<!-- End .post -->

<jsp:include page="TemplateMiddle.jsp"/>

                    <jsp:include page="CurrentOrder.jsp" />

                    <%@ include file="Login.jsp" %>

                    <!-- New user registration module -->
                    <jsp:include page="NewUser.jsp" />

                    <!-- Show pending orders for admin -->
                    <jsp:include page="Orders.jsp" />
                    
<jsp:include page="TemplateEnd.jsp" />