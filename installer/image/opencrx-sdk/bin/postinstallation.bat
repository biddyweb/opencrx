@echo off
set ANT_HOME=$ANTPath
set JAVA_HOME=$JDKPath
set PATH=%JAVA_HOME%\bin;%ANT_HOME%\bin;%PATH%
start "Build openCRX SDK" /WAIT cmd /C "ant -f ""$INSTALL_PATH\bin\postinstaller.xml"" -logfile ""$INSTALL_PATH\build.log"" postinstall"
