@echo off
"@@INSTALLDIR@@\bin\7z.exe" x -o"@@INSTALLDIR@@" "@@INSTALLDIR@@\repository\distribution\opencrx-@@OPENCRX_VERSION@@-core.jre-1.5.zip"
"@@INSTALLDIR@@\bin\7z.exe" x -o"@@INSTALLDIR@@" "@@INSTALLDIR@@\repository\distribution\opencrx-@@OPENCRX_VERSION@@-store.jre-1.5.zip"
"@@INSTALLDIR@@\bin\7z.exe" x -o"@@INSTALLDIR@@" "@@INSTALLDIR@@\repository\distribution\opencrx-@@OPENCRX_VERSION@@-groupware.jre-1.5.zip"
"@@INSTALLDIR@@\bin\7z.exe" x -o"@@INSTALLDIR@@\opencrx-@@OPENCRX_VERSION@@\opt"
"@@INSTALLDIR@@\bin\7z.exe" x -o"@@INSTALLDIR@@\opencrx-@@OPENCRX_VERSION@@\opt" "@@INSTALLDIR@@\repository\distribution\openmdx-@@OPENMDX_VERSION@@-core.jre-1.5.zip"
"@@INSTALLDIR@@\bin\7z.exe" x -o"@@INSTALLDIR@@\opencrx-@@OPENCRX_VERSION@@\opt" "@@INSTALLDIR@@\repository\distribution\openmdx-@@OPENMDX_VERSION@@-portal.jre-1.5.zip"
"@@INSTALLDIR@@\bin\7z.exe" x -o"@@INSTALLDIR@@\opencrx-@@OPENCRX_VERSION@@\opt" "@@INSTALLDIR@@\repository\distribution\openmdx-@@OPENMDX_VERSION@@-security.jre-1.5.zip"
"@@INSTALLDIR@@\bin\7z.exe" x -o"@@INSTALLDIR@@\opencrx-@@OPENCRX_VERSION@@\opt" "@@INSTALLDIR@@\repository\distribution\openmdx-@@OPENMDX_VERSION@@-websphere.websphere-6.zip"
set ANT_HOME=@@ANT_HOME@@
set JAVA_HOME=@@JAVA_HOME@@
set PATH=%JAVA_HOME%\bin;%ANT_HOME%\bin;%PATH%
cd "@@INSTALLDIR@@\opencrx-@@OPENCRX_VERSION@@\core"
cmd /C ant install-src
cmd /C ant assemble
cd "@@INSTALLDIR@@\opencrx-@@OPENCRX_VERSION@@\groupware"
cmd /C ant install-src
cmd /C ant assemble
cd "@@INSTALLDIR@@\opencrx-@@OPENCRX_VERSION@@\store"
cmd /C ant install-src
cmd /C ant assemble
