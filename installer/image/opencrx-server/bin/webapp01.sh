#!/bin/sh

# START or STOP openCRX
# ---------------------

if [ "$1" = "START" ] ; then

  if [ -e @@INSTALLDIR@@/opencrx/data/crx_2_7/startdb.sh ] ; then 
    @@INSTALLDIR@@/opencrx/data/crx_2_7/startdb.sh START &
    sleep 3
  fi
  export JAVA_OPTS="$JAVA_OPTS -Dopencrx.CRX.jdbc.driverName=org.hsqldb.jdbcDriver"
  export JAVA_OPTS="$JAVA_OPTS -Dopencrx.CRX.jdbc.url=jdbc:hsqldb:hsql://127.0.0.1:@@HSQLDB_PORT@@/CRX_2_7"
  export JAVA_OPTS="$JAVA_OPTS -Dopencrx.CRX.jdbc.userName=sa"
  export JAVA_OPTS="$JAVA_OPTS -Dopencrx.CRX.jdbc.password=manager99"
  export JAVA_OPTS="$JAVA_OPTS -Dorg.opencrx.maildir=@@INSTALLDIR@@/apache-tomcat-6/maildir"
  export JAVA_OPTS="$JAVA_OPTS -Dorg.opencrx.airsyncdir=@@INSTALLDIR@@/apache-tomcat-6/airsyncdir"

fi

if [ "$1" = "STOP" ] ; then

  if [ -e @@INSTALLDIR@@/opencrx/data/crx_2_7/startdb.sh ] ; then 
    @@INSTALLDIR@@/opencrx/data/crx_2_7/startdb.sh STOP
  fi
    
fi
