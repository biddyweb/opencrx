@echo off

set JAVA_HOME=$JDKPath
set ANT_HOME=$ANTPath
set ANT_OPTS=-Xmx512m 
set JRE_16=$JDKPath\jre
set PATH=%JAVA_HOME%\bin;%ANT_HOME%\bin;%PATH%
