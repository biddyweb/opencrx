rem START or STOP Tomcat
rem --------------------

set CATALINA_HOME=@@INSTALLDIR@@\apache-tomcat-6

if not ""%1"" == ""START"" goto stop

set JAVA_OPTS=-Xmx800M 
set JAVA_OPTS=%JAVA_OPTS% -Djava.protocol.handler.pkgs=org.openmdx.kernel.url.protocol
set JAVA_OPTS=%JAVA_OPTS% -Dorg.openmdx.log.config.filename="%CATALINA_HOME%\server.log.properties"
set JAVA_OPTS=%JAVA_OPTS% -Dorg.opencrx.maildir="%CATALINA_HOME%\maildir"
cd %CATALINA_HOME%
bin\catalina.bat run

goto end

:stop

taskkill -FI "WINDOWTITLE eq opencrx-@@VERSION@@*"

:end

exit
