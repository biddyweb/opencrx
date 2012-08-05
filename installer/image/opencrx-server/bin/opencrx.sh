#!/bin/sh

# START or STOP openCRX
# ---------------------

if [ "$1" = "START" ] ; then

  if [ -e @@INSTALLDIR@@/data/crx_2_4/startdb.sh ] ; then 
    @@INSTALLDIR@@/data/crx_2_4/startdb.sh START &
    sleep 3
  fi
  if [ -e @@INSTALLDIR@@/bin/tomcat.sh ] ; then 
    export JAVA_OPTS="$JAVA_OPTS -Dopencrx.CRX.jdbc.driverName=org.hsqldb.jdbcDriver"
    export JAVA_OPTS="$JAVA_OPTS -Dopencrx.CRX.jdbc.url=jdbc:hsqldb:hsql://127.0.0.1:@@HSQLDB_PORT@@/CRX_2_4"
    export JAVA_OPTS="$JAVA_OPTS -Dopencrx.CRX.jdbc.userName=sa"
    export JAVA_OPTS="$JAVA_OPTS -Dopencrx.CRX.jdbc.password=manager99"
    @@INSTALLDIR@@/bin/tomcat.sh START
  fi

fi

if [ "$1" = "STOP" ] ; then

  if [ -e @@INSTALLDIR@@/bin/tomcat.sh ] ; then
    @@INSTALLDIR@@/bin/tomcat.sh STOP
  fi
  if [ -e @@INSTALLDIR@@/data/crx_2_4/startdb.sh ] ; then 
    @@INSTALLDIR@@/data/crx_2_4/startdb.sh STOP
  fi
    
fi
