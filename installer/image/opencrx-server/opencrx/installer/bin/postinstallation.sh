#!/bin/sh
cd "@@INSTALLDIR@@"
export ANT_HOME=@@ANT_HOME@@
export JAVA_HOME=@@JAVA_HOME@@
export PATH=$JAVA_HOME/bin:$ANT_HOME/bin:$PATH
ant -f "@@INSTALLDIR@@/opencrx/installer/bin/postinstaller.xml" postinstall