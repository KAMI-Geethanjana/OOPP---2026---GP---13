@echo off
echo ========================================================
echo Compiling Faculty Technical Management System (FTMS)...
echo ========================================================
if not exist ftms\out mkdir ftms\out

powershell -NoProfile -Command "Get-ChildItem -Recurse -Filter *.java -Path ftms\src | ForEach-Object { '\"' + $_.FullName.Replace('\', '/') + '\"' } | Out-File -Encoding ascii sources.txt"
javac -cp "ftms/lib/mysql-connector-j-26.7.0.jar;ftms/src" -d ftms/out @sources.txt
set COMPILE_STATUS=%ERRORLEVEL%
if exist sources.txt del sources.txt

if %COMPILE_STATUS% EQU 0 (
    echo Compilation successful! Class files generated in ftms/out/
) else (
    echo Compilation FAILED!
)
pause
