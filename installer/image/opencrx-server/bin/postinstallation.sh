#!/bin/sh
mkdir @@INSTALLDIR@@/mysql-5/mysqld
chmod a+rw @@INSTALLDIR@@/mysql-5/mysqld
mkdir @@INSTALLDIR@@/mysql-5/log
chmod a+rw @@INSTALLDIR@@/mysql-5/log
mkdir @@INSTALLDIR@@/mysql-5/temp
chmod a+rw @@INSTALLDIR@@/mysql-5/temp
chmod -R a+rw @@INSTALLDIR@@/mysql-5/data
mkdir @@INSTALLDIR@@/apache-tomcat-6/temp
mkdir @@INSTALLDIR@@/apache-tomcat-6/maildir
