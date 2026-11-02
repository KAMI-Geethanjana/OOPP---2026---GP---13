
@echo off
echo ========================================================
echo Launching Admin & Notices Module (TG2086)...
echo ========================================================

java -cp "lib/mysql-connector-j-26.7.0.jar;out;src" tg2086.gui.AdminDashboardUI

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Application exited with error code %ERRORLEVEL%.
    pause
)
