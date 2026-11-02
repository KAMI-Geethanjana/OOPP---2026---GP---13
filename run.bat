@echo off
echo ========================================================
echo Launching FTMS Portal...
echo ========================================================

set "JAVA_CMD=java"
if exist "C:\Users\wosad\.jdks\openjdk-27\bin\java.exe" set "JAVA_CMD=C:\Users\wosad\.jdks\openjdk-27\bin\java.exe"
if exist "%USERPROFILE%\.jdks\openjdk-27\bin\java.exe" set "JAVA_CMD=%USERPROFILE%\.jdks\openjdk-27\bin\java.exe"

"%JAVA_CMD%" -cp "lib/mysql-connector-j-26.7.0.jar;out;src;resources" Main

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Application exited with error code %ERRORLEVEL%.
    pause
)
