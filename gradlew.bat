@rem Gradle wrapper launcher
@echo off
set DIR=%~dp0
if not exist "%DIR%\gradle\wrapper\gradle-wrapper.jar" (
  echo Missing gradle\wrapper\gradle-wrapper.jar. Install Gradle 9.2.1 and run: gradle wrapper --gradle-version 9.2.1
  exit /b 1
)
java -classpath "%DIR%\gradle\wrapper\gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain %*
