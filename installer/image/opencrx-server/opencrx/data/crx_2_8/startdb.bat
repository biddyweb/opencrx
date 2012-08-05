rem START or STOP HSQLDB
rem --------------------

if ""%1"" == ""START"" goto start
if ""%1"" == ""START-HSQLDB"" goto start-hsqldb
if ""%1"" == ""STOP"" goto stop
goto end

:start
start "HSQLDB (@@HSQLDB_PORT@@)" "@@INSTALLDIR@@\opencrx\data\crx_2_8\startdb.bat" START-HSQLDB
goto end

:start-hsqldb
set JAVA_HOME=@@JAVA_HOME@@
set JAVA_HOME=%JAVA_HOME:/=\%
set PATH=%JAVA_HOME%\bin;%PATH%
cd @@INSTALLDIR@@\opencrx\data\crx_2_8
java -Xmx800M -cp ..\hsqldb.jar org.hsqldb.server.Server --port @@HSQLDB_PORT@@ --database.0 file:crx_2_8 --dbname.0 CRX_2_8
goto end

:stop
set JAVA_HOME=@@JAVA_HOME@@
set JAVA_HOME=%JAVA_HOME:/=\%
set PATH=%JAVA_HOME%\bin;%PATH%
cd @@INSTALLDIR@@\opencrx\data\crx_2_8
java -jar ../sqltool.jar --sql "shutdown;" --rcFile=sqltool.rc CRX_2_8
goto end

:end

exit
