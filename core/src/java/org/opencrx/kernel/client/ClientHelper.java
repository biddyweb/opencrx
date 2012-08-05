package org.opencrx.kernel.client;


import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingException;

import org.openmdx.base.accessor.generic.cci.ObjectFactory_1_0;
import org.openmdx.base.accessor.generic.view.Manager_1;
import org.openmdx.base.accessor.jmi.cci.RefPackage_1_0;
import org.openmdx.base.exception.ServiceException;
import org.openmdx.compatibility.application.dataprovider.transport.ejb.cci.Dataprovider_1ConnectionFactoryImpl;
import org.openmdx.compatibility.application.dataprovider.transport.ejb.cci.Dataprovider_1Home;
import org.openmdx.compatibility.base.dataprovider.cci.QualityOfService;
import org.openmdx.compatibility.base.dataprovider.cci.RequestCollection;
import org.openmdx.compatibility.base.dataprovider.cci.ServiceHeader;
import org.openmdx.compatibility.base.dataprovider.transport.adapter.Provider_1;
import org.openmdx.compatibility.base.dataprovider.transport.cci.Dataprovider_1ConnectionFactory;
import org.openmdx.compatibility.base.dataprovider.transport.cci.Dataprovider_1_1Connection;
import org.openmdx.compatibility.base.dataprovider.transport.cci.Provider_1_0;
import org.openmdx.compatibility.base.dataprovider.transport.delegation.Connection_1;

public class ClientHelper {

    //-----------------------------------------------------------------------
    public static ObjectFactory_1_0 createObjectFactory(
        Context componentEnvironment,
        String name
    ) throws ServiceException, NamingException {
        return createObjectFactory(
            componentEnvironment,
            name,
            DEFAULT_PRINCIPAL_NAME
        );
    }
    
    //-----------------------------------------------------------------------
    public static ObjectFactory_1_0 createObjectFactory(
        Context componentEnvironment,
        String name,
        String principalName
    ) throws ServiceException, NamingException {
        Object obj = componentEnvironment.lookup(name);
        Dataprovider_1_1Connection connection = null;
        if(obj instanceof Dataprovider_1ConnectionFactory) {
            Dataprovider_1ConnectionFactory f = (Dataprovider_1ConnectionFactory)componentEnvironment.lookup(name);  
            connection = f.createConnection();
        }
        else if(obj instanceof Dataprovider_1Home){
            try {
                connection = Dataprovider_1ConnectionFactoryImpl.createRemoteConnection(obj);
            }
            catch(Exception e) {
                throw new ServiceException(e);
            }
        }
        Provider_1_0 provider = new Provider_1(
            new RequestCollection(
                new ServiceHeader(principalName, null, false, new QualityOfService()),
                connection
            ),
            false
        );
        return new Manager_1(
            new Connection_1(
                provider,
                false,
                null
            )
        );                
    }

    //-----------------------------------------------------------------------
    public static void loadModels(
        Context componentEnvironment,
        RefPackage_1_0 rootPkg
    ) throws ServiceException {
        List modelPackages = new ArrayList();
        int i = 0;
        while(true) {
            try {
                String modelPackage = (String)componentEnvironment.lookup("modelPackage[" + i + "]");
                modelPackages.add(modelPackage);
            }
            catch(NamingException e) {
                break;
            }
            i++;
        }
        rootPkg.refModel().addModels(modelPackages);
    }

    private static final String DEFAULT_PRINCIPAL_NAME = "guest";
    
}

//--- End of File -----------------------------------------------------------
