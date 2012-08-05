#!/bin/sh

# [openCRX]
export JAVA_OPTS="$JAVA_OPTS -Xmx800M"
export JAVA_OPTS="$JAVA_OPTS -XX:MaxPermSize=256m"
export JAVA_OPTS="$JAVA_OPTS -Djava.protocol.handler.pkgs=org.openmdx.kernel.url.protocol"
export JAVA_OPTS="$JAVA_OPTS -Dorg.opencrx.maildir=$CATALINA_BASE/maildir"
export JAVA_OPTS="$JAVA_OPTS -Dorg.opencrx.airsyncdir=$CATALINA_BASE/airsyncdir"
# export JAVA_OPTS="$JAVA_OPTS -Dorg.openmdx.persistence.jdbc.useLikeForOidMatching=false"
# export JAVA_OPTS="$JAVA_OPTS -Djavax.jdo.option.TransactionIsolationLevel=read-committed"
export CLASSPATH=$CLASSPATH:$CATALINA_BASE/bin/openmdx-system.jar
# [openCRX]

# [openCRX META-INF/context.xml configuration]
export JAVA_OPTS="$JAVA_OPTS -Dopencrx.CRX.jdbc.driverName=org.hsqldb.jdbcDriver"
export JAVA_OPTS="$JAVA_OPTS -Dopencrx.CRX.jdbc.url=jdbc:hsqldb:hsql://127.0.0.1:$HSQLDB_PORT/CRX"
export JAVA_OPTS="$JAVA_OPTS -Dopencrx.CRX.jdbc.userName=sa"
export JAVA_OPTS="$JAVA_OPTS -Dopencrx.CRX.jdbc.password=manager99"
# [openCRX META-INF/context.xml configuration]

echo "Using JAVA_OPTS:       $JAVA_OPTS"
