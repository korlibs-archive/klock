REM free some space
dir

REM dir /s C:\ProgramData\chocolatey

REM choco uninstall all

choco uninstall -y -f cmake cmake.install DotNet4.5 DotNet4.6 windows-sdk-10.0 winscp winscp.install ruby microsoft-build-tools visualstudio2017-installer visualstudio2017-workload-netcorebuildtools visualstudio2017-workload-vctools visualstudio2017-workload-webbuildtools visualstudio2017buildtools

dir

choco list --local-only
choco install jdk8 -y -params "installdir=c:\\java8"

del c:\java8\src.zip
del c:\java8\javafx-src.zip
del /s C:\ProgramData\chocolatey\lib\ruby\

dir c:\java8
dir c:\java8\lib

refreshenv

SET JAVA_HOME=c:\java8
CALL gradlew.bat -s -i mingwX64Test
