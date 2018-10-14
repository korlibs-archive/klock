REM free some space
dir
dir c:\
dir "c:\Program Files"
dir "c:\Program Files (x86)"

REM dir /s C:\ProgramData\chocolatey
REM choco uninstall all
REM choco uninstall -y -f cmake cmake.install DotNet4.5 DotNet4.6 windows-sdk-10.0 winscp winscp.install ruby microsoft-build-tools visualstudio2017-workload-netcorebuildtools visualstudio2017-workload-vctools visualstudio2017-workload-webbuildtools visualstudio2017buildtools

RD /s /q "c:\Program Files\IIS"
RD /s /q "c:\Program Files\Java"
RD /s /q "c:\Program Files\Microsoft"
RD /s /q "c:\Program Files\Microsoft Visual Studio"
RD /s /q "c:\Program Files\Microsoft Visual Studio 14.0"

dir

choco list --local-only
choco install jdk8 -y -params "installdir=c:\\java8"

del c:\java8\src.zip
del c:\java8\javafx-src.zip
del /s C:\ProgramData\chocolatey\lib\ruby\

dir c:\java8
dir c:\java8\lib

CALL refreshenv

SET JAVA_HOME=c:\java8
CALL gradlew.bat -s -i mingwX64Test
