@echo off

rem START or STOP openCRX Server
rem ----------------------------

set CATALINA_HOME=@@INSTALLDIR@@\apache-tomcat-6.0.29

if ""%1"" == ""START"" goto start
if ""%1"" == ""STOP"" goto stop
if ""%1"" == ""START-OPENCRX"" goto start-opencrx
goto end

:start
start "openCRX Server @@VERSION@@ (@@TOMCAT_HTTP_PORT@@)" "@@INSTALLDIR@@\bin\opencrx.bat" START-OPENCRX
goto end

:start-opencrx

rem Start HSQLDB
if exist "@@INSTALLDIR@@\data\crx_2_8\startdb.bat" (
  start "HSQLDB" "@@INSTALLDIR@@\data\crx_2_8\startdb.bat" START
  ping 127.0.0.1 > nul  
)

rem Start TomEE
set JAVA_HOME=@@JAVA_HOME@@
set JAVA_HOME=%JAVA_HOME:/=\%
set JAVA_OPTS=%JAVA_OPTS% -Xmx800M 
set JAVA_OPTS=%JAVA_OPTS% -XX:MaxPermSize=128m
set JAVA_OPTS=%JAVA_OPTS% -Djava.protocol.handler.pkgs=org.openmdx.kernel.url.protocol
set JAVA_OPTS=%JAVA_OPTS% -Dopencrx.CRX.jdbc.driverName=org.hsqldb.jdbcDriver
set JAVA_OPTS=%JAVA_OPTS% -Dopencrx.CRX.jdbc.url=jdbc:hsqldb:hsql://127.0.0.1:@@HSQLDB_PORT@@/CRX_2_8
set JAVA_OPTS=%JAVA_OPTS% -Dopencrx.CRX.jdbc.userName=sa
set JAVA_OPTS=%JAVA_OPTS% -Dopencrx.CRX.jdbc.password=manager99
set JAVA_OPTS=%JAVA_OPTS% -Dorg.opencrx.maildir=@@INSTALLDIR@@\apache-tomcat-6.0.29\maildir
set JAVA_OPTS=%JAVA_OPTS% -Dorg.opencrx.airsyncdir=@@INSTALLDIR@@\apache-tomcat-6.0.29\airsyncdir
set LOGGING_MANAGER=-Djava.util.logging.manager=java.util.logging.LogManager
cd %CATALINA_HOME%
rmdir /s /q temp
rmdir /s /q work
mkdir temp
bin\catalina.bat run
goto end

:stop

rem Stop HSQLDB
if exist "@@INSTALLDIR@@\data\crx_2_8\startdb.bat" (
	start "HSQLDB" "@@INSTALLDIR@@\data\crx_2_8\startdb.bat" STOP
  	ping 127.0.0.1 > nul  
)

rem Stop TomEE
taskkill -FI "WINDOWTITLE eq openCRX Server @@VERSION@@ (@@TOMCAT_HTTP_PORT@@)*"
goto end

:end

exit
