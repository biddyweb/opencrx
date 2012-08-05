import org.openmdx.portal.servlet.*;
import org.openmdx.portal.servlet.texts.*;
import org.openmdx.portal.servlet.view.*;
import org.openmdx.portal.servlet.control.*;

p.write("<div id=\"breadcrum\">");
p.write("  <div id=\"breadcrumBorder\">");
org.openmdx.portal.servlet.control.NavigationControl.paintClose(
    p,
    forEditing
);
org.openmdx.portal.servlet.control.NavigationControl.paintPrint(
    p,
    forEditing
);
org.openmdx.portal.servlet.control.NavigationControl.paintHeaderHider(
    p,
    forEditing
);
org.openmdx.portal.servlet.control.NavigationControl.paintSelectPerspectives(
    p,
    forEditing
);
org.opencrx.kernel.portal.control.NavigationControl.paintAlertBox(
    p,
    forEditing
);
org.opencrx.kernel.portal.control.NavigationControl.paintRssLinks(
	p,
	forEditing
);
org.openmdx.portal.servlet.control.NavigationControl.paintToggleViewPort(
    p,
    forEditing
);
org.openmdx.portal.servlet.control.NavigationControl.paintBreadcrum(
    p,
    forEditing
);
p.write("  </div> <!-- breadcrumBorder -->");
p.write("</div> <!-- breadcrum -->");
