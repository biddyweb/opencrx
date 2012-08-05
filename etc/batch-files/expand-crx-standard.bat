@echo off
rename D:\pgm\jboss-4.0.2\server\default\deploy\opencrx-core-CRX-web.ear opencrx-core-CRX-web.ear.zip
D:\pgm\7-Zip\7z x D:\pgm\jboss-4.0.2\server\default\deploy\opencrx-core-CRX-web.ear.zip -oD:\pgm\jboss-4.0.2\server\default\deploy\opencrx-core-CRX-web.ear
if errorlevel 1 goto errorWEBEAR
del D:\pgm\jboss-4.0.2\server\default\deploy\opencrx-core-CRX-web.ear.zip

rename D:\pgm\jboss-4.0.2\server\default\deploy\opencrx-core-CRX-web.ear\opencrx-core-CRX.war opencrx-core-CRX.war.zip
D:\pgm\7-Zip\7z x D:\pgm\jboss-4.0.2\server\default\deploy\opencrx-core-CRX-web.ear\opencrx-core-CRX.war.zip -oD:\pgm\jboss-4.0.2\server\default\deploy\opencrx-core-CRX-web.ear\opencrx-core-CRX.war
if errorlevel 1 goto errorWEBWAR
del D:\pgm\jboss-4.0.2\server\default\deploy\opencrx-core-CRX-web.ear\opencrx-core-CRX.war.zip

rename D:\pgm\jboss-4.0.2\server\default\deploy\opencrx-core-CRX-Root-web.ear opencrx-core-CRX-Root-web.ear.zip
D:\pgm\7-Zip\7z x D:\pgm\jboss-4.0.2\server\default\deploy\opencrx-core-CRX-Root-web.ear.zip -oD:\pgm\jboss-4.0.2\server\default\deploy\opencrx-core-CRX-Root-web.ear
if errorlevel 1 goto errorROOTWEBEAR
del D:\pgm\jboss-4.0.2\server\default\deploy\opencrx-core-CRX-Root-web.ear.zip

rename D:\pgm\jboss-4.0.2\server\default\deploy\opencrx-core-CRX-Root-web.ear\opencrx-core-CRX-Root.war opencrx-core-CRX-Root.war.zip
D:\pgm\7-Zip\7z x D:\pgm\jboss-4.0.2\server\default\deploy\opencrx-core-CRX-Root-web.ear\opencrx-core-CRX-Root.war.zip -oD:\pgm\jboss-4.0.2\server\default\deploy\opencrx-core-CRX-Root-web.ear\opencrx-core-CRX-Root.war
if errorlevel 1 goto errorROOTWEBWAR
del D:\pgm\jboss-4.0.2\server\default\deploy\opencrx-core-CRX-Root-web.ear\opencrx-core-CRX-Root.war.zip
goto end

:errorWEBEAR
rename D:\pgm\jboss-4.0.2\server\default\deploy\opencrx-core-CRX-web.ear.zip opencrx-core-CRX-web.ear
echo error - please verify directory names
goto end

:errorWEBWAR
rename D:\pgm\jboss-4.0.2\server\default\deploy\opencrx-core-CRX-web.ear\opencrx-core-CRX.war.zip opencrx-core-CRX.war
echo error - please verify directory names
goto end

:errorROOTWEBEAR
rename D:\pgm\jboss-4.0.2\server\default\deploy\opencrx-core-CRX-Root-web.ear.zip opencrx-core-CRX-Root-web.ear
echo error - please verify directory names
goto end

:errorROOTWEBWAR
rename D:\pgm\jboss-4.0.2\server\default\deploy\opencrx-core-CRX-Root-web.ear\opencrx-core-CRX-Root.war.zip opencrx-core-CRX-Root.war
echo error - please verify directory names
goto end

:end
