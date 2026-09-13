@echo off
setlocal EnableExtensions
cd /d "%~dp0"

echo ============================================================
echo  Iron Man Voice Mask 1.21.11 - Build JAR
echo ============================================================
echo.

where java >nul 2>nul
if errorlevel 1 (
  echo [ERROR] Java was not found in PATH.
  echo Install 64-bit Java 21 and run this file again.
  pause
  exit /b 1
)

for /f "tokens=3" %%V in ('java -version 2^>^&1 ^| findstr /i "version"') do set JAVA_VER=%%~V
echo [OK] Java detected: %JAVA_VER%
echo.

set "GRADLE_VERSION=9.2.1"
set "TOOLS=%CD%\.build-tools"
set "GRADLE_HOME=%TOOLS%\gradle-%GRADLE_VERSION%"
set "GRADLE_ZIP=%TOOLS%\gradle-%GRADLE_VERSION%-bin.zip"

if not exist "%TOOLS%" mkdir "%TOOLS%"

if not exist "%GRADLE_HOME%\bin\gradle.bat" (
  echo [1/4] Downloading Gradle %GRADLE_VERSION%...
  powershell -NoProfile -ExecutionPolicy Bypass -Command ^
    "$ErrorActionPreference='Stop'; Invoke-WebRequest -UseBasicParsing -Uri 'https://services.gradle.org/distributions/gradle-%GRADLE_VERSION%-bin.zip' -OutFile '%GRADLE_ZIP%'"
  if errorlevel 1 (
    echo.
    echo [ERROR] Gradle download failed. Check your internet connection.
    pause
    exit /b 1
  )

  echo [2/4] Extracting Gradle...
  powershell -NoProfile -ExecutionPolicy Bypass -Command ^
    "$ErrorActionPreference='Stop'; Expand-Archive -LiteralPath '%GRADLE_ZIP%' -DestinationPath '%TOOLS%' -Force"
  if errorlevel 1 (
    echo.
    echo [ERROR] Could not extract Gradle.
    pause
    exit /b 1
  )
)

if not exist "gradle\wrapper\gradle-wrapper.jar" (
  echo [3/4] Creating the Gradle wrapper...
  call "%GRADLE_HOME%\bin\gradle.bat" wrapper --gradle-version %GRADLE_VERSION%
  if errorlevel 1 goto BUILD_FAILED
)

echo [4/4] Building the Minecraft mod...
call gradlew.bat --no-daemon clean build
if errorlevel 1 goto BUILD_FAILED

powershell -NoProfile -ExecutionPolicy Bypass -Command ^
  "$ErrorActionPreference='Stop'; $j = Get-ChildItem -LiteralPath 'build\libs' -Filter '*.jar' ^| Where-Object { $_.Name -notmatch '(sources|dev|javadoc)' } ^| Sort-Object Length -Descending ^| Select-Object -First 1; if(-not $j){ throw 'No release JAR found in build\libs' }; Copy-Item -LiteralPath $j.FullName -Destination 'ironman-voice-mask-1.0.0.jar' -Force; Write-Host ('[SUCCESS] JAR created: ' + (Join-Path (Get-Location) 'ironman-voice-mask-1.0.0.jar'))"
if errorlevel 1 goto BUILD_FAILED

echo.
echo ============================================================
echo BUILD SUCCESSFUL
echo Your mod is:
echo %CD%\ironman-voice-mask-1.0.0.jar
echo ============================================================
echo.
pause
exit /b 0

:BUILD_FAILED
echo.
echo ============================================================
echo BUILD FAILED
echo Copy the error shown above and send it to ChatGPT.
echo ============================================================
echo.
pause
exit /b 1
