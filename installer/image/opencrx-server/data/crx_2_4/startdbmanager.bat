cd @@INSTALLDIR@@\data\crx_2_4
java -cp ..\hsqldb.jar org.hsqldb.util.DatabaseManagerSwing --url jdbc:hsqldb:hsql://127.0.0.1:@@HSQLDB_PORT@@/CRX_2_4 --user sa --password manager99
 