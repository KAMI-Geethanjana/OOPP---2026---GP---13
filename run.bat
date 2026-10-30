
@echo off
echo ========================================================
echo Launching FTMS Portal...
echo ========================================================

java -cp "lib/mysql-connector-j-26.7.0.jar;out;src" Main

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Application exited with error code %ERRORLEVEL%.
    pause
)
