rem START or STOP HSQLDB
rem --------------------

if ""%1"" == ""START"" goto start
if ""%1"" == ""START-HSQLDB"" goto start-hsqldb
if ""%1"" == ""STOP"" goto stop
goto end

:start
start "HSQLDB (@@HSQLDB_PORT@@)" "@@INSTALLDIR@@\data\crx_2_5\startdb.bat" START-HSQLDB
goto end

:start-hsqldb
set JAVA_HOME=@@JAVA_HOME@@
set JAVA_HOME=%JAVA_HOME:/=\%
set PATH=%JAVA_HOME%\bin
cd @@INSTALLDIR@@\data\crx_2_5
java -Xmx800M -cp ..\hsqldb.jar org.hsqldb.Server -port @@HSQLDB_PORT@@ -database.0 file:crx_2_5 -dbname.0 CRX_2_5
goto end

:stop
taskkill -FI "WINDOWTITLE eq HSQLDB (@@HSQLDB_PORT@@)*"
goto end

:end

exit
