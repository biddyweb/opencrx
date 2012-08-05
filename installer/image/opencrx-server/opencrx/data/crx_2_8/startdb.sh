#!/bin/sh

# START or STOP HSQLDB
# --------------------

if [ "$1" = "START" ] ; then

  export JAVA_HOME=@@JAVA_HOME@@
  export PATH=$JAVA_HOME/bin:$PATH
  cd @@INSTALLDIR@@/opencrx/data/crx_2_8
  java -Xmx800M -Dhsqldb.port=@@HSQLDB_PORT@@ -cp ../hsqldb.jar org.hsqldb.server.Server --port @@HSQLDB_PORT@@ --database.0 file:crx_2_8 --dbname.0 CRX_2_8

fi

if [ "$1" = "STOP" ] ; then

  export JAVA_HOME=@@JAVA_HOME@@
  export PATH=$JAVA_HOME/bin:$PATH
  cd @@INSTALLDIR@@/opencrx/data/crx_2_8
  java -jar ../sqltool.jar --sql 'shutdown;' --rcFile=sqltool.rc CRX_2_8
  
fi
