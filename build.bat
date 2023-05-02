@echo off

set "FRAMEWORK_DIR=D:\ITU\Framework\framework"
set "TEST_FRAMEWORK_DIR=D:\ITU\Framework\test-framework"

cd "%FRAMEWORK_DIR%"

javac -d . *.java

jar cf framework.jar *

cd ..

set "WEB_INF_DIR=%TEST_FRAMEWORK_DIR%\WEB-INF"

copy "%FRAMEWORK_DIR%\framework.jar" "%WEB_INF_DIR%\lib\"

cd "%WEB_INF_DIR%\classes"

javac -cp "%WEB_INF_DIR%\lib\framework.jar" -d . *.java

cd "..\.."

set "WAR_FILE=%TEST_FRAMEWORK_DIR%\frameworkTest.war"

jar cf "%WAR_FILE%" .

copy "%WAR_FILE%" "C:\Program Files\Apache Software Foundation\Tomcat 8.5\webapps"
