package org.opencrx.application.imap;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.opencrx.kernel.generic.SecurityKeys;
import org.opencrx.kernel.utils.Utils;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.kernel.id.UUIDs;

public class IMAPServlet extends HttpServlet {

    //-----------------------------------------------------------------------
    @Override
    public void init(
        ServletConfig config            
    ) throws ServletException {
        super.init();        
        try {
            Utils.getModel();
            PersistenceManagerFactory persistenceManagerFactory = Utils.getPersistenceManagerFactory();
            String providerName = config.getInitParameter("provider");
            providerName = providerName == null 
                ? "CRX" 
                : (providerName.indexOf("/") > 0 ? providerName.substring(providerName.indexOf("/") + 1) : providerName);            
            String portNumber = config.getInitParameter("port");
            String isDebug = config.getInitParameter("debug");
            String delayOnStartup = config.getInitParameter("delayOnStartup");
            // Validate connection. This also initializes the factory
            PersistenceManager pm = persistenceManagerFactory.getPersistenceManager(
                SecurityKeys.ROOT_PRINCIPAL,
                UUIDs.getGenerator().next().toString()
            );
            pm.getObjectById(
                "xri:@openmdx:org.opencrx.kernel.admin1/provider/" + providerName + "/segment/Root"
            );
            IMAPServer.startServer(
                persistenceManagerFactory,
                providerName,
                portNumber == null ? 143 : (portNumber.startsWith("imap:") ? Integer.valueOf(portNumber.substring(5)) : Integer.valueOf(portNumber)),
                isDebug == null ? false : Boolean.valueOf(isDebug),
                delayOnStartup == null ? 0 : Integer.valueOf(delayOnStartup)
            );
            try {
                pm.close();
            } 
            catch(Exception e) {}
        }
        catch(Exception e) {
            new ServiceException(e).log();
            throw new ServletException("Can not initialize IMAP server", e);
        }
    }

    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    private static final long serialVersionUID = 3271417510604705711L;
    
}
