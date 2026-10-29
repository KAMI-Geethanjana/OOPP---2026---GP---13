@echo off
echo ========================================================
echo Launching Admin & Notices Module (TG2086)...
echo ========================================================
java -cp "ftms/lib/mysql-connector-j-26.7.0.jar;ftms/out;ftms/src" tg2086.gui.AdminDashboardUI
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Application exited with error code %ERRORLEVEL%.
    pause
)

