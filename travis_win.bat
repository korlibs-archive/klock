choco install jdk8 -y -params "installdir=c:\\java8"
SET JAVA_HOME=c:\java8
CALL gradlew.bat -s -i mingwX64Test
