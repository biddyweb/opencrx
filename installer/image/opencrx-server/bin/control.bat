@echo off

set JAVA_HOME=@@JAVA_HOME@@
set JAVA_HOME=%JAVA_HOME:/=\%

rem START or STOP Services
rem ----------------------
rem Check if argument is STOP or START

if not ""%1"" == ""START"" goto stop

rem Start MySQL
net start opencrx-@@VERSION@@-mysql

rem Start Tomcat
start "opencrx-@@VERSION@@" "@@INSTALLDIR@@\bin\tomcat.bat" START

goto end

:stop

rem Stop Tomcat
start "Stop Tomcat" "@@INSTALLDIR@@\bin\tomcat.bat" STOP

rem Stop MySQL
net stop opencrx-@@VERSION@@-mysql


:end
