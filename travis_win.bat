REM free some space
dir
choco list --local-only
choco install jdk8 -y -params "installdir=c:\\java8"
del c:\java8\lib\src.zip
SET JAVA_HOME=c:\java8
CALL gradlew.bat -s -i mingwX64Test
