@echo off
echo ========================================================
echo Compiling Faculty Technical Management System (FTMS)...
echo ========================================================
if not exist out mkdir out

set SOURCES=src\Main.java src\config\*.java src\model\*.java src\gui\*.java
if exist src\util\*.java set SOURCES=%SOURCES% src\util\*.java
if exist src\dao\*.java set SOURCES=%SOURCES% src\dao\*.java
if exist src\exception\*.java set SOURCES=%SOURCES% src\exception\*.java

javac -cp "lib/mysql-connector-j-26.7.0.jar" -d out %SOURCES%
if %ERRORLEVEL% EQU 0 (
    echo Compilation successful! Class files generated in out/
) else (
    echo Compilation FAILED!
)
pause

