<%@ page import="org.opencrx.store.manager.UserManager,
                 org.opencrx.store.objects.User,
                 org.opencrx.store.common.util.SessionHelper,
                 org.opencrx.store.common.util.RequestHelper,
                 org.opencrx.store.common.util.ResponseHelper"%>
 <%--
  Login page which shows login box when no user logged in. Also shows current user
  information when user is logged in.

  User: Omar Al Zabir
  Date: Nov 1, 2004
  Time: 3:25:00 PM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%!
    private final static String USERNAME = "userName";
    private final static String PASSWORD = "password";
    private final static String ACTION_TYPE = "LoginAction";
%>
<!-- Login box -->
<h2 class="sidebar-title">Login</h2>
<div class="sidebar-block" >

<%
    final SessionHelper sessionHelper = new SessionHelper( request );
    final RequestHelper requestHelper = new RequestHelper( request );
    final ResponseHelper responseHelper = new ResponseHelper( response );

    // 1. Get the action performed
    final String action = requestHelper.getAction();

    boolean showContent = true;

    // 2. If this is login action, then perform login
    if( requestHelper.isActionPerformed( ACTION_TYPE ))
    {
        if( action.equals(RequestHelper.ACTION_LOGIN) )
        {
            // 2.1 Retrieve use rname and password from the page
            final String userName = request.getParameter(USERNAME);
            final String password = request.getParameter(PASSWORD);

            // 2.2 Authenticate
            final UserManager manager = new UserManager(sessionHelper.getOpenCrxContext());
            if( manager.login( userName, password ) )
            {
                // 2.2.1 success
                final User user = manager.getUserByName( userName );
                sessionHelper.setUser( user );

                // 2.2.2 Redirect to same page
                out.println( responseHelper.redirect( request.getRequestURI() ) );
                showContent = false;
            }
            else
            {
                // 2.2.2 failure
                %>
                <font color="red">Invalid username or password.</font>
                <%
            }
        }
        else if( action.equals( RequestHelper.ACTION_LOGOUT ))
        {
            // 2.3 Logout requestes, clear session and reload the page
            session.invalidate();
            out.println("<p class=\"information\">Logout in progress...</p>");
            out.println( new ResponseHelper( response ).redirect( request.getRequestURI() ) );
        }
    }

    if( showContent )
    {
%>
<form method="post">
<%
        if( sessionHelper.isUserLoggedIn() )
        {
            // user logged in, show user info
            final User user = sessionHelper.getCurrentUser();
%>
        <p>Welcome, <%= user.getName() %>. What would you like to buy today?</p>
        <p align="right">
        <input type="submit" name="<%= RequestHelper.ACTION %>" value="<%= RequestHelper.ACTION_LOGOUT %>">
        </p>
<%
        }
        else
        {
            // Show the login dialog box only when there is no user logged in
%>
<table border="0">
<tr>
<td nowrap="true">User Name:</td><td><input type="text" name="<%= USERNAME %>" cols="20"></td>
</tr>
<tr>
<td>Password:</td><td><input type="password" name="<%= PASSWORD %>"  cols="20"></td>
</tr>
<tr>
<td><input type="submit" name="<%= RequestHelper.ACTION %>" value="<%= RequestHelper.ACTION_LOGIN %>"></td>
<td> </td>
</tr>
</table>
<%
        }
%>
<%= requestHelper.actionType( ACTION_TYPE ) %>
</form>
<%
    }
%>

</div>
