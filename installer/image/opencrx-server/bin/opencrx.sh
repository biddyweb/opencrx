#!/bin/sh

export CATALINA_HOME=@@INSTALLDIR@@/apache-tomcat-6.0.29

# START or STOP openCRX Server
# ----------------------------

if [ "$1" = "START" ] ; then

  # Start HSQLDB
  if [ -e @@INSTALLDIR@@/data/crx_2_8/startdb.sh ] ; then 
    @@INSTALLDIR@@/data/crx_2_8/startdb.sh START &
    sleep 3
  fi

  # Start TomEE
  export JAVA_HOME=@@JAVA_HOME@@
  export JAVA_OPTS="$JAVA_OPTS -Xmx800M"
  export JAVA_OPTS="$JAVA_OPTS -XX:MaxPermSize=128m"
  export JAVA_OPTS="$JAVA_OPTS -Dtomcat.server.port=@@TOMCAT_SERVER_PORT@@"
  export JAVA_OPTS="$JAVA_OPTS -Djava.protocol.handler.pkgs=org.openmdx.kernel.url.protocol"
  export JAVA_OPTS="$JAVA_OPTS -Dopencrx.CRX.jdbc.driverName=org.hsqldb.jdbcDriver"
  export JAVA_OPTS="$JAVA_OPTS -Dopencrx.CRX.jdbc.url=jdbc:hsqldb:hsql://127.0.0.1:@@HSQLDB_PORT@@/CRX_2_8"
  export JAVA_OPTS="$JAVA_OPTS -Dopencrx.CRX.jdbc.userName=sa"
  export JAVA_OPTS="$JAVA_OPTS -Dopencrx.CRX.jdbc.password=manager99"
  export JAVA_OPTS="$JAVA_OPTS -Dorg.opencrx.maildir=@@INSTALLDIR@@/apache-tomcat-6.0.29/maildir"
  export JAVA_OPTS="$JAVA_OPTS -Dorg.opencrx.airsyncdir=@@INSTALLDIR@@/apache-tomcat-6.0.29/airsyncdir"  
  export LOGGING_MANAGER="-Djava.util.logging.manager=java.util.logging.LogManager"  
  cd $CATALINA_HOME
  rm -Rf temp
  mkdir temp
  rm -Rf work
  ./bin/catalina.sh run

fi

if [ "$1" = "STOP" ] ; then

  # Stop HSQLDB
  if [ -e @@INSTALLDIR@@/data/crx_2_8/startdb.sh ] ; then 
    @@INSTALLDIR@@/data/crx_2_8/startdb.sh STOP
  fi
  
  # Stop TomEE
  pkill -f '.*tomcat\.server\.port=@@TOMCAT_SERVER_PORT@@.*'

fi
