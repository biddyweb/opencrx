#!/bin/sh

# START or STOP openCRX
# ---------------------

if [ "$1" = "START" ] ; then

  if [ -e @@INSTALLDIR@@/data/crx_2_6/startdb.sh ] ; then 
    @@INSTALLDIR@@/data/crx_2_6/startdb.sh START &
    sleep 3
  fi
  export JAVA_OPTS="$JAVA_OPTS -Dopencrx.CRX.jdbc.driverName=org.hsqldb.jdbcDriver"
  export JAVA_OPTS="$JAVA_OPTS -Dopencrx.CRX.jdbc.url=jdbc:hsqldb:hsql://127.0.0.1:@@HSQLDB_PORT@@/CRX_2_6"
  export JAVA_OPTS="$JAVA_OPTS -Dopencrx.CRX.jdbc.userName=sa"
  export JAVA_OPTS="$JAVA_OPTS -Dopencrx.CRX.jdbc.password=manager99"
  export JAVA_OPTS="$JAVA_OPTS -Dorg.opencrx.maildir=@@INSTALLDIR@@/apache-tomcat-6/maildir"
  export JAVA_OPTS="$JAVA_OPTS -Dorg.opencrx.airsyncdir=@@INSTALLDIR@@/apache-tomcat-6/airsyncdir"

fi

if [ "$1" = "STOP" ] ; then

  if [ -e @@INSTALLDIR@@/data/crx_2_6/startdb.sh ] ; then 
    @@INSTALLDIR@@/data/crx_2_6/startdb.sh STOP
  fi
    
fi
