set JAVA_HOME=@@JAVA_HOME@@
set JAVA_HOME=%JAVA_HOME:/=\%
set PATH=%JAVA_HOME%\bin;%PATH%
cd @@INSTALLDIR@@\opencrx\data\crx_2_7
java -cp ..\hsqldb.jar org.hsqldb.util.DatabaseManagerSwing --url jdbc:hsqldb:hsql://127.0.0.1:@@HSQLDB_PORT@@/CRX_2_7 --user sa --password manager99
 