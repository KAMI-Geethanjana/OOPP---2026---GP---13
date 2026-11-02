@echo off
echo ========================================================
echo Compiling Faculty Technical Management System (FTMS)...
echo ========================================================

set "JAVAC_CMD=javac"
if exist "C:\Users\wosad\.jdks\openjdk-27\bin\javac.exe" set "JAVAC_CMD=C:\Users\wosad\.jdks\openjdk-27\bin\javac.exe"
if exist "%USERPROFILE%\.jdks\openjdk-27\bin\javac.exe" set "JAVAC_CMD=%USERPROFILE%\.jdks\openjdk-27\bin\javac.exe"

if not exist out mkdir out

powershell -NoProfile -Command "Get-ChildItem -Recurse -Filter *.java -Path src | ForEach-Object { '\"' + $_.FullName.Replace('\', '/') + '\"' } | Out-File -Encoding ascii sources.txt"

"%JAVAC_CMD%" -encoding UTF-8 -cp "lib/mysql-connector-j-26.7.0.jar;src" -d out @sources.txt

set COMPILE_STATUS=%ERRORLEVEL%

if exist sources.txt del sources.txt

if %COMPILE_STATUS% EQU 0 (
    echo.
    echo Compilation successful!
    echo Class files generated in out/
) else (
    echo.
    echo Compilation FAILED!
)

pause
