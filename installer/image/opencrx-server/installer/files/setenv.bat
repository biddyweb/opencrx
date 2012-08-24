REM [openCRX]
set JAVA_OPTS=%JAVA_OPTS% -Xmx800M 
set JAVA_OPTS=%JAVA_OPTS% -XX:MaxPermSize=256m
set JAVA_OPTS=%JAVA_OPTS% -Djava.protocol.handler.pkgs=org.openmdx.kernel.url.protocol
set JAVA_OPTS=%JAVA_OPTS% -Dorg.opencrx.maildir="%CATALINA_BASE%\maildir"
set JAVA_OPTS=%JAVA_OPTS% -Dorg.opencrx.airsyncdir="%CATALINA_BASE%\airsyncdir"
REM JAVA_OPTS=%JAVA_OPTS% -Dorg.openmdx.persistence.jdbc.useLikeForOidMatching=false
REM JAVA_OPTS=%JAVA_OPTS% -Djavax.jdo.option.TransactionIsolationLevel=read-committed
set "CLASSPATH=%CLASSPATH%;%CATALINA_BASE%\bin\openmdx-system.jar"
REM [openCRX]

echo Using JAVA_OPTS:       "%JAVA_OPTS%"
