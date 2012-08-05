// Search
p.write("<div style=\"overflow:auto;\">");
p.write("    <form name=\"SearchInput\" id=\"SearchInput\" action=\"./wizards/Search.jsp?" + org.openmdx.portal.servlet.Action.PARAMETER_REQUEST_ID + "=" + p.getView().getRequestId() + "\" style=\"margin-bottom:10px;padding-left:0px;\">");
p.write("      <input type=\"hidden\" name=\"" + org.openmdx.portal.servlet.Action.PARAMETER_REQUEST_ID + "\" value=\"" + p.getView().getRequestId() + "\"/>");
p.write("      <input type=\"text\" name=\"searchExpression\" onmouseover=\"javascript:this.focus();\"/><input type=\"image\" align=\"absbottom\" src=\"images/SearchImg.gif\"/>");
p.write("    </form>");
p.write("    <script language=\"javascript\" type=\"text/javascript\">function setFocusSearch(){try{document.forms.SearchInput.searchExpression.focus();}catch(e){}}; YAHOO.util.Event.addListener(window, \"load\", setFocusSearch);</script>");
p.write("  </div>");
