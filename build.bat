@echo off
cls
echo.
echo   ============================================
echo      Online School Enrollment System
echo              BUILD SCRIPT
echo   ============================================
echo.
echo   [INFO] Compiling Java source files...
echo   [INFO] Output: .\build\
echo   [INFO] Libraries: .\lib\*.jar
echo.
echo   [!] Please wait, compiling...
echo   [!] Do not click anything until this finishes.
echo.

javac -d build -cp "lib\*" @sources.txt > compile_out.txt 2> compile_errors.txt
if %errorlevel% equ 0 (
    echo   [OK] Compilation successful!
    echo.
    echo   To run the application, type: run
) else (
    echo   [FAILED] Compilation failed!
    echo   Check compile_errors.txt for details.
)
echo.
echo   ============================================
echo.
