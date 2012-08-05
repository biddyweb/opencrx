#!/bin/sh
gunzip @@INSTALLDIR@@/repository/distribution/*.gz
cd "@@INSTALLDIR@@"
export ANT_HOME=@@ANT_HOME@@
export JAVA_HOME=@@JAVA_HOME@@
export PATH=$JAVA_HOME/bin:$ANT_HOME/bin:$PATH
ant -f "@@INSTALLDIR@@/bin/postinstaller.xml" -logfile "@@INSTALLDIR@@/build.log" -Dexpand.target=expand-tar postinstall
