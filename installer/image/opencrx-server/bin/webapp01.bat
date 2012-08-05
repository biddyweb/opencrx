@echo off

rem START or STOP openCRX
rem ----------------------

if ""%1"" == ""START"" goto start
if ""%1"" == ""STOP"" goto stop
goto end

:start
if exist "@@INSTALLDIR@@\opencrx\data\crx_2_8\startdb.bat" (
  start "HSQLDB" "@@INSTALLDIR@@\opencrx\data\crx_2_8\startdb.bat" START
  ping 127.0.0.1 > nul  
)
set JAVA_OPTS=%JAVA_OPTS% -Dopencrx.CRX.jdbc.driverName=org.hsqldb.jdbcDriver
set JAVA_OPTS=%JAVA_OPTS% -Dopencrx.CRX.jdbc.url=jdbc:hsqldb:hsql://127.0.0.1:@@HSQLDB_PORT@@/CRX_2_8
set JAVA_OPTS=%JAVA_OPTS% -Dopencrx.CRX.jdbc.userName=sa
set JAVA_OPTS=%JAVA_OPTS% -Dopencrx.CRX.jdbc.password=manager99
set JAVA_OPTS=%JAVA_OPTS% -Dorg.opencrx.maildir=@@INSTALLDIR@@\apache-tomcat-6\maildir
set JAVA_OPTS=%JAVA_OPTS% -Dorg.opencrx.airsyncdir=@@INSTALLDIR@@\apache-tomcat-6\airsyncdir
goto end

:stop

if exist "@@INSTALLDIR@@\opencrx\data\crx_2_8\startdb.bat" (
	start "HSQLDB" "@@INSTALLDIR@@\opencrx\data\crx_2_8\startdb.bat" STOP
)
goto end

:end

