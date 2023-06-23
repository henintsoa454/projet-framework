@echo off

set "FRAMEWORK_DIR=E:\Framework\framework"
set "TEST_FRAMEWORK_DIR=E:\Framework\test-framework"
set "TEMPORARY_DIR=E:\Framework\temporary"
set "TOMCAT_DIR=C:\Program Files\Apache Software Foundation\Tomcat 8.5_Tomcat8.5"

del /s /q "%TEMPORARY_DIR%\*"

cd "%FRAMEWORK_DIR%"

javac -parameters -d . *.java

jar cf framework.jar *

cd "%TEMPORARY_DIR%"

mkdir "WEB-INF"
mkdir "WEB-INF\classes"
mkdir "WEB-INF\lib"

copy /y "%FRAMEWORK_DIR%\framework.jar" "WEB-INF\lib"

xcopy /s /e /y "%TEST_FRAMEWORK_DIR%\WEB-INF\classes" "WEB-INF\classes"

copy /y "%TEST_FRAMEWORK_DIR%\WEB-INF\lib\*" "WEB-INF\lib"

copy /y "%TEST_FRAMEWORK_DIR%\WEB-INF\web.xml" "WEB-INF"

cd "%TEST_FRAMEWORK_DIR%"

for %%F in (*.jsp) do (
    copy /y "%%F" "%TEMPORARY_DIR%"
)

cd "%TEMPORARY_DIR%\WEB-INF\classes"

javac -parameters -cp "../lib\servlet-api.jar;../lib\framework.jar" -d . *.java

cd ../..

jar cf "frameworkTest.war" *

del /q "%TOMCAT_DIR%\webapps\frameworkTest\*"

copy /y "frameworkTest.war" "%TOMCAT_DIR%\webapps"

echo "Done."
