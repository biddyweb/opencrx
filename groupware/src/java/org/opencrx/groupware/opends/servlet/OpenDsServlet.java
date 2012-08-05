package org.opencrx.groupware.opends.servlet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.opencrx.groupware.generic.Util;
import org.opencrx.groupware.opends.backends.OpenCrxBackend;
import org.opencrx.kernel.generic.SecurityKeys;
import org.opends.server.types.DirectoryEnvironmentConfig;
import org.opends.server.util.EmbeddedUtils;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.kernel.id.UUIDs;

public class OpenDsServlet extends HttpServlet {

    //-----------------------------------------------------------------------
    protected void copyR(
        ServletConfig config,
        String sourceDir,
        File targetDir
    ) throws IOException {
        Set<String> paths = config.getServletContext().getResourcePaths(sourceDir);
        if(paths != null) {
            for(String path: paths) {
                File targetFile = new File(targetDir, path);
                if(!path.endsWith("/")) {
                    try {
                        targetFile.getParentFile().mkdirs();
                        InputStream source = config.getServletContext().getResource(path).openStream();
                        FileOutputStream target = new FileOutputStream(targetFile);
                        int b;
                        while((b = source.read()) != -1) {
                            target.write(b);
                        }
                        source.close();
                        target.close();
                    }
                    catch(Exception e) {}
                }
                else {
                    targetFile.mkdirs();
                    this.copyR(
                        config, 
                        path, 
                        targetDir
                    );
                }
            }
        }
        else {
            new File(targetDir, sourceDir).mkdirs();
        }
    }
    
    //-----------------------------------------------------------------------
    @Override
    public void init(
        ServletConfig config            
    ) throws ServletException {
        super.init();        
        try {
            Util.createModel();
            PersistenceManagerFactory persistenceManagerFactory = Util.getPersistenceManagerFactory();
            // Validate connection. This also initializes the factory
            PersistenceManager pm = persistenceManagerFactory.getPersistenceManager(
                SecurityKeys.ROOT_PRINCIPAL,
                UUIDs.getGenerator().next().toString()
            ); 
            String providerName = config.getInitParameter("provider");
            providerName = providerName == null 
                ? "CRX" 
                : (providerName.indexOf("/") > 0 ? providerName.substring(providerName.indexOf("/") + 1) : providerName);            
            pm.getObjectById(
                "xri:@openmdx:org.opencrx.kernel.admin1/provider/" + providerName + "/segment/Root"
            );
            OpenCrxBackend.persistenceManagerFactory = persistenceManagerFactory;
            // Prepare server root. Copy WEB-INF/config to temp directory
            File tempDir = (File)config.getServletContext().getAttribute("javax.servlet.context.tempdir");
            this.copyR(
                config, 
                "/WEB-INF/config/", 
                tempDir
            );
            this.copyR(
                config, 
                "/WEB-INF/db/", 
                tempDir
            );
            this.copyR(
                config, 
                "/WEB-INF/logs/", 
                tempDir
            );
            DirectoryEnvironmentConfig dsConfig = new DirectoryEnvironmentConfig();
            dsConfig.setServerRoot(
                new File(tempDir, "WEB-INF")
            );
            File lockDir = new File(tempDir, "WEB-INF/lock");
            lockDir.mkdirs();
            dsConfig.setLockDirectory(
                lockDir
            );            
            EmbeddedUtils.startServer(dsConfig);
        }
        catch(Exception e) {
            new ServiceException(e).log();
            throw new ServletException("Can not initialize OpenDS server", e);
        }        
    }

    //-----------------------------------------------------------------------
    @Override
    public void destroy(
    ) {
        try {
            EmbeddedUtils.stopServer(
                OpenDsServlet.class.getName(), 
                null
            );
        } catch(Exception e) {}
        super.destroy();
    }
    
    //-----------------------------------------------------------------------
    // Members
    //-----------------------------------------------------------------------
    private static final long serialVersionUID = -3001365317648208705L;
    
}
