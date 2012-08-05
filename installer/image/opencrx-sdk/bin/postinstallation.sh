#!/bin/sh
gunzip @@INSTALLDIR@@/repository/distribution/*.gz
cd "@@INSTALLDIR@@"
tar xf "@@INSTALLDIR@@/repository/distribution/opencrx-@@OPENCRX_VERSION@@-core.jre-1.5.tar"
tar xf "@@INSTALLDIR@@/repository/distribution/opencrx-@@OPENCRX_VERSION@@-store.jre-1.5.tar"
tar xf "@@INSTALLDIR@@/repository/distribution/opencrx-@@OPENCRX_VERSION@@-groupware.jre-1.5.tar"
mkdir "@@INSTALLDIR@@/opencrx-@@OPENCRX_VERSION@@/opt"
cd "@@INSTALLDIR@@/opencrx-@@OPENCRX_VERSION@@/opt"
tar xf "@@INSTALLDIR@@/repository/distribution/openmdx-@@OPENMDX_VERSION@@-core.jre-1.5.tar"
tar xf "@@INSTALLDIR@@/repository/distribution/openmdx-@@OPENMDX_VERSION@@-portal.jre-1.5.tar"
tar xf "@@INSTALLDIR@@/repository/distribution/openmdx-@@OPENMDX_VERSION@@-security.jre-1.5.tar"
tar xf "@@INSTALLDIR@@/repository/distribution/openmdx-@@OPENMDX_VERSION@@-websphere.websphere-6.tar"
export ANT_HOME=@@ANT_HOME@@
export JAVA_HOME=@@JAVA_HOME@@
export PATH=$JAVA_HOME/bin:$ANT_HOME/bin:$PATH
cd "@@INSTALLDIR@@/opencrx-@@OPENCRX_VERSION@@/core"
ant install-src
ant assemble
cd "@@INSTALLDIR@@/opencrx-@@OPENCRX_VERSION@@/groupware"
ant install-src
ant assemble
cd "@@INSTALLDIR@@/opencrx-@@OPENCRX_VERSION@@/store"
ant install-src
ant assemble
