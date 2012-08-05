#!/bin/sh

# RUN or STOP openCRX Server
# ----------------------------

if [ "$1" = "run" ] ; then

  # Start HSQLDB
  if [ -e $INSTALL_PATH/data/crx/startdb.sh ] ; then 
    $INSTALL_PATH/data/crx/startdb.sh START &
    sleep 3
  fi

  # Start TomEE
  export JAVA_HOME=$JDKPath
  cd ..
  rm -Rf temp
  mkdir temp
  rm -Rf work
  ./bin/catalina.sh run

fi

if [ "$1" = "stop" ] ; then

  # Stop HSQLDB
  if [ -e $INSTALL_PATH/data/crx/startdb.sh ] ; then 
    $INSTALL_PATH/data/crx/startdb.sh STOP
  fi
  
  # Stop TomEE
  cd ..
  ./bin/catalina.sh stop

fi
