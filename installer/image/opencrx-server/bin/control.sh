#!/bin/sh

export JAVA_HOME=@@JAVA_HOME@@

# START or STOP Services
# ----------------------
# Check if argument is STOP or START

if [ "$1" = "START" ] ; then

  if [ -f @@INSTALLDIR@@/mysql-5/mysqld/mysqld.pid ] ; then
    echo "mysqld already running"
  else 
    @@MYSQL_HOME@@/mysqld_safe --defaults-file=@@INSTALLDIR@@/mysql-5/my.cnf &
  fi
  @@INSTALLDIR@@/bin/tomcat.sh START

fi

if [ "$1" = "STOP" ] ; then

  @@INSTALLDIR@@/bin/tomcat.sh STOP
  kill `cat @@INSTALLDIR@@/mysql-5/mysqld/mysqld.pid`
  
fi
