@echo off
echo ========================================================
echo Compiling FTMS Admin Module (TG/2024/2086)...
echo ========================================================
if not exist out mkdir out
javac -cp lib/mysql-connector-j-26.7.0.jar -d out src/Main.java src/config/*.java src/model/*.java src/dao/*.java src/gui/*.java
if %ERRORLEVEL% EQU 0 (
    echo Compilation successful! Class files generated in out/
) else (
    echo Compilation FAILED!
)
pause

