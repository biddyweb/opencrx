#!/bin/sh

# [openCRX]
export JAVA_OPTS="$JAVA_OPTS -Xmx800M"
export JAVA_OPTS="$JAVA_OPTS -XX:MaxPermSize=256m"
export JAVA_OPTS="$JAVA_OPTS -Dfile.encoding=UTF-8"
export JAVA_OPTS="$JAVA_OPTS -Djava.protocol.handler.pkgs=org.openmdx.kernel.url.protocol"
export JAVA_OPTS="$JAVA_OPTS -Dorg.openmdx.kernel.collection.InternalizedKeyMap.TrustInternalization=true"
export JAVA_OPTS="$JAVA_OPTS -Dorg.opencrx.maildir=$CATALINA_BASE/maildir"
export JAVA_OPTS="$JAVA_OPTS -Dorg.opencrx.airsyncdir=$CATALINA_BASE/airsyncdir"
# export JAVA_OPTS="$JAVA_OPTS -Dorg.opencrx.usesendmail.CRX=false"
# export JAVA_OPTS="$JAVA_OPTS -Dorg.opencrx.mediadir=$CATALINA_BASE/mediadir"
# export JAVA_OPTS="$JAVA_OPTS -Dorg.openmdx.persistence.jdbc.useLikeForOidMatching=false"
# export JAVA_OPTS="$JAVA_OPTS -Djavax.jdo.option.TransactionIsolationLevel=read-committed"
export CLASSPATH=$CLASSPATH:$CATALINA_BASE/bin/openmdx-system.jar
# [openCRX]

echo "Using JAVA_OPTS:       $JAVA_OPTS"
