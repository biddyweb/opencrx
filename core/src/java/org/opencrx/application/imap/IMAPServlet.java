package org.opencrx.application.imap;

import javax.jdo.PersistenceManagerFactory;

import org.opencrx.application.adapter.AbstractServer;
import org.opencrx.application.adapter.AbstractServlet;

public class IMAPServlet extends AbstractServlet {

    //-----------------------------------------------------------------------
	@Override
    public String getConfigurationId(
    ) {
		return "IMAPServlet";
    }

    //-----------------------------------------------------------------------
	@Override
    public int getPortNumber(
    	String configuredPortNumber
    ) {
		return configuredPortNumber == null ? 
			143 : 
				(configuredPortNumber.startsWith("imap:") ? 
					Integer.valueOf(configuredPortNumber.substring(5)) : 
						Integer.valueOf(configuredPortNumber));		
    }

    //-----------------------------------------------------------------------
	@Override
    public AbstractServer newServer(
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
    ) {
		return new IMAPServer(
			pmf,
			providerName,
		    bindAddress,
		    portNumber,
		    sslKeystoreFile,
		    sslKeystoreType,
		    sslKeystorePass,
		    sslKeyPass,
			isDebug,
			delayOnStartup
		);
    }
        
    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    private static final long serialVersionUID = 3271417510604705711L;

}
