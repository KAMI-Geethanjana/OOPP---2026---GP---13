@echo off
echo ========================================================
echo Launching FTMS Portal...
echo ========================================================
java -cp "ftms/lib/mysql-connector-j-26.7.0.jar;ftms/out;ftms/src" Main
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Application exited with error code %ERRORLEVEL%.
    pause
)
