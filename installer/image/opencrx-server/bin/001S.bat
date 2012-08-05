rem START or STOP HSQLDB
rem --------------------

if not ""%1"" == ""START"" goto stop

cd @@INSTALLDIR@@\data\crx_2_2
java -Xmx800M -cp ..\hsqldb.jar org.hsqldb.Server -port @@HSQLDB_PORT@@ -database.0 file:crx_2_2 -dbname.0 CRX_2_2

:stop

:end

exit
