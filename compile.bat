@echo off
echo ========================================================
echo Compiling Faculty Technical Management System (FTMS)...
echo ========================================================
if not exist out mkdir out

powershell -NoProfile -Command "Get-ChildItem -Recurse -Filter *.java -Path src | ForEach-Object { '\"' + $_.FullName.Replace('\', '/') + '\"' } | Out-File -Encoding ascii sources.txt"
javac -cp "lib/mysql-connector-j-26.7.0.jar;src" -d out @sources.txt
set COMPILE_STATUS=%ERRORLEVEL%
if exist sources.txt del sources.txt

if %COMPILE_STATUS% EQU 0 (
    echo Compilation successful! Class files generated in out/
) else (
    echo Compilation FAILED!
)
pause
