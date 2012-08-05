#!/bin/sh

# START or STOP HSQLDB
# --------------------

if [ "$1" = "START" ] ; then

  cd @@INSTALLDIR@@/data/crx_2_1
  java -Xmx800M -Dhsqldb.port=@@HSQLDB_PORT@@ -cp ../hsqldb.jar org.hsqldb.Server -port @@HSQLDB_PORT@@ -database.0 file:crx_2_1 -dbname.0 CRX_2_1

fi

if [ "$1" = "STOP" ] ; then

  @@INSTALLDIR@@/bin/zap.sh 'hsqldb.port=@@HSQLDB_PORT@@'
  
fi
