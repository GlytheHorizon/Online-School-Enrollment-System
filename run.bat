@echo off
cls
echo.
echo   ============================================
echo      Online School Enrollment System
echo              RUN SCRIPT
echo   ============================================
echo.
echo   [INFO] Make sure you have built first using: build
echo.
echo   Press Enter to launch the application...
pause >nul
java -cp "build;lib\*" school.enrollment.Main
echo.
echo   ============================================
echo   Application closed.
pause >nul
