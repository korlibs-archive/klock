REM free some space
dir

dir /s C:\ProgramData\chocolatey

choco uninstall all

choco list --local-only
choco install jdk8 -y -params "installdir=c:\\java8"

del c:\java8\src.zip
del c:\java8\javafx-src.zip

dir c:\java8
dir c:\java8\lib

SET JAVA_HOME=c:\java8
CALL gradlew.bat -s -i mingwX64Test
