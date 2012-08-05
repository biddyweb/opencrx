rem START or STOP HSQLDB
rem --------------------

if ""%1"" == ""START"" goto start
if ""%1"" == ""START-HSQLDB"" goto start-hsqldb
if ""%1"" == ""STOP"" goto stop
goto end

:start
start "HSQLDB ($HSQLDB_PORT)" "$INSTALL_PATH\data\crx\startdb.bat" START-HSQLDB
goto end

:start-hsqldb
set JAVA_HOME=$JDKPath
set JAVA_HOME=%JAVA_HOME:/=\%
set PATH=%JAVA_HOME%\bin;%PATH%
cd $INSTALL_PATH\data\crx
java -Xmx800M -cp ..\hsqldb.jar org.hsqldb.server.Server --port $HSQLDB_PORT --database.0 file:crx --dbname.0 CRX
goto end

:stop
set JAVA_HOME=$JDKPath
set JAVA_HOME=%JAVA_HOME:/=\%
set PATH=%JAVA_HOME%\bin;%PATH%
cd $INSTALL_PATH\data\crx
java -jar ../sqltool.jar --sql "shutdown;" --rcFile=sqltool.rc CRX
goto end

:end

exit
