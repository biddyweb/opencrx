#!/bin/sh

# START or STOP HSQLDB
# --------------------

if [ "$1" = "START" ] ; then

  cd @@INSTALLDIR@@/data/crx_2_4
  java -Xmx800M -Dhsqldb.port=@@HSQLDB_PORT@@ -cp ../hsqldb.jar org.hsqldb.Server -port @@HSQLDB_PORT@@ -database.0 file:crx_2_4 -dbname.0 CRX_2_4

fi

if [ "$1" = "STOP" ] ; then

  pkill -f '.*hsqldb\.port=@@HSQLDB_PORT@@.*'
  
fi
