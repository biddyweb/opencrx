@echo off

rem START or STOP openCRX
rem ----------------------

if ""%1"" == ""START"" goto start
if ""%1"" == ""STOP"" goto stop
goto end

:start
if exist "@@INSTALLDIR@@\data\crx_2_4\startdb.bat" (
  start "HSQLDB" "@@INSTALLDIR@@\data\crx_2_4\startdb.bat" START
  ping 127.0.0.1 > nul  
)
set JAVA_OPTS=%JAVA_OPTS% -Dopencrx.CRX.jdbc.driverName=org.hsqldb.jdbcDriver
set JAVA_OPTS=%JAVA_OPTS% -Dopencrx.CRX.jdbc.url=jdbc:hsqldb:hsql://127.0.0.1:@@HSQLDB_PORT@@/CRX_2_4
set JAVA_OPTS=%JAVA_OPTS% -Dopencrx.CRX.jdbc.userName=sa
set JAVA_OPTS=%JAVA_OPTS% -Dopencrx.CRX.jdbc.password=manager99
start "Tomcat" "@@INSTALLDIR@@\bin\tomcat.bat" START
goto end

:stop

start "Tomcat" "@@INSTALLDIR@@\bin\tomcat.bat" STOP
if exist "@@INSTALLDIR@@\data\crx_2_4\startdb.bat" (
	start "HSQLDB" "@@INSTALLDIR@@\data\crx_2_4\startdb.bat" STOP
)
goto end

:end

exit
