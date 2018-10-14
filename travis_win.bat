REM free some space
dir

dir /s C:\ProgramData\chocolatey

REM choco uninstall all

choco uninstall -y -f cmake cmake.install
choco uninstall -y -f DotNet4.5 DotNet4.6
choco uninstall -y -f windows-sdk-10.0
choco uninstall -y -f winscp winscp.install
choco uninstall -y -f ruby

choco list --local-only
choco install jdk8 -y -params "installdir=c:\\java8"

del c:\java8\src.zip
del c:\java8\javafx-src.zip
del /s C:\ProgramData\chocolatey\lib\ruby\

dir c:\java8
dir c:\java8\lib

SET JAVA_HOME=c:\java8
CALL gradlew.bat -s -i mingwX64Test
