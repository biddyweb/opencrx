@echo off
set ANT_HOME=@@ANT_HOME@@
set JAVA_HOME=@@JAVA_HOME@@
set PATH=%JAVA_HOME%\bin;%ANT_HOME%\bin;%PATH%
start "Build openCRX SDK" /WAIT cmd /C "ant -f ""@@INSTALLDIR@@\bin\postinstaller.xml"" -logfile ""@@INSTALLDIR@@\build.log"" -Dexpand.target=expand-zip postinstall"
