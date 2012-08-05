package org.opencrx.application.adapter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opencrx.kernel.generic.SecurityKeys;
import org.opencrx.kernel.utils.Utils;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.base.naming.Path;
import org.openmdx.kernel.id.UUIDs;

public abstract class AbstractServlet extends HttpServlet {

	//-----------------------------------------------------------------------
	public abstract AbstractServer newServer(
        PersistenceManagerFactory pmf,
        String providerName,
	    String bindAddress,
	    int portNumber,
	    String sslKeystoreFile,
	    String sslKeystoreType,
	    String sslKeystorePass,
	    String sslKeyPass,
        boolean isDebug,
        int delayOnStartup
	);
	
    //-----------------------------------------------------------------------
	public abstract String getConfigurationId();

    //-----------------------------------------------------------------------
	public abstract int getPortNumber(
		String configuredPortNumber
	);
	
    //-----------------------------------------------------------------------
    @Override
    public void init(
        ServletConfig config            
    ) throws ServletException {
        super.init();        
        try {
            Utils.getModel();
            PersistenceManagerFactory pmf = Utils.getPersistenceManagerFactory();
            this.providerName = config.getInitParameter("provider");
            this.providerName = this.providerName == null  ? 
            	DEFAULT_PROVIDER_NAME : 
            		(this.providerName.indexOf("/") > 0 ? this.providerName.substring(this.providerName.indexOf("/") + 1) : this.providerName
            );            
            String bindAddress = config.getInitParameter("bindAddress");
            String portNumber = config.getInitParameter("port");
            String sslKeystoreFile = config.getInitParameter("sslKeystoreFile");
            String sslKeystoreType = config.getInitParameter("sslKeystoreType");
            String sslKeystorePass = config.getInitParameter("sslKeystorePass");
            String sslKeyPass = config.getInitParameter("sslKeyPass");
            String isDebug = config.getInitParameter("debug");
            String delayOnStartup = config.getInitParameter("delayOnStartup");
            // Validate connection. This also initializes the factory
            PersistenceManager pm = pmf.getPersistenceManager(
                SecurityKeys.ROOT_PRINCIPAL,
                UUIDs.getGenerator().next().toString()
            );
            pm.getObjectById(
                new Path("xri://@openmdx*org.opencrx.kernel.admin1/provider/" + this.providerName + "/segment/Root")
            );
    		String autostartConnectors = System.getProperty("org.openmdx.catalina.core.ExtendedService.autostartConnectors");
    		this.isStopped = (autostartConnectors != null) && !Boolean.valueOf(autostartConnectors).booleanValue();   
            AbstractServer server = this.newServer(
                pmf,
                this.providerName,
        	    bindAddress,
        	    this.getPortNumber(portNumber),
        	    sslKeystoreFile,
        	    sslKeystoreType,
        	    sslKeystorePass,
        	    sslKeyPass,
                isDebug == null ? false : Boolean.valueOf(isDebug),
                delayOnStartup == null ? 0 : Integer.valueOf(delayOnStartup)
            );
            Thread serverThread = new Thread(server);
            serverThread.start();
            if(!this.isStopped) {
            	server.resume();
            }   
            this.server = server;
            try {
                pm.close();
            } 
            catch(Exception e) {}
        }
        catch(Exception e) {
            new ServiceException(e).log();
            throw new ServletException("Can not initialize server", e);
        }
    }

    //-----------------------------------------------------------------------
    protected void handleRequest(
        HttpServletRequest req, 
        HttpServletResponse res
    ) throws ServletException, IOException {
        
        if(COMMAND_PAUSE.equals(req.getPathInfo())) {
        	this.server.pause();
        	this.isStopped = true;
            System.out.println(new Date().toString() + ": " + this.getConfigurationId() + ": paused " + this.providerName);                    
        }
        else if(COMMAND_RESUME.equals(req.getPathInfo())) {
        	this.server.resume();
        	this.isStopped = false;        	
            System.out.println(new Date().toString() + ": " + this.getConfigurationId() + ": resumed " + this.providerName);                    
        }
        
        // Show status and commands
        PrintWriter out = res.getWriter();
        out.println("<html>");
        out.println("<head><meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\"></head>");
        out.println("<body>");
        out.println("<h2>openCRX IMAP Server " + this.providerName + "</h2>");
        out.println("<table>");
        out.println("<tr>");
        out.println("<td><a href=\"" + req.getContextPath() + req.getServletPath() + (this.isStopped ? COMMAND_RESUME : COMMAND_PAUSE) + "\">" + (this.isStopped ? "Resume" : "Pause") + "</a></td><td />");
        out.println("</tr>");
        out.println("</table>");
        out.println("</body>");
        out.println("</html>");
    }

    //-----------------------------------------------------------------------
    protected void doGet(
        HttpServletRequest req, 
        HttpServletResponse res
    ) throws ServletException, IOException {
        this.handleRequest(
            req,
            res
        );
    }
        
    //-----------------------------------------------------------------------
    protected void doPost(
        HttpServletRequest req, 
        HttpServletResponse res
    ) throws ServletException, IOException {
        this.handleRequest(
            req,
            res
        );
    }
        
    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    private static final long serialVersionUID = -781335769801341481L;

    private static final String DEFAULT_PROVIDER_NAME = "CRX";
    
    private static final String COMMAND_PAUSE = "/pause";
    private static final String COMMAND_RESUME = "/resume";

    protected String providerName = null;
    protected AbstractServer server;
    protected boolean isStopped = true;
    
}
