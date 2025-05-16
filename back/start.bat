@echo off
cd /d "%~dp0"

echo [i] Cleaning and compiling the project...
call mvn clean install

IF %ERRORLEVEL% NEQ 0 (
    echo [i] Build failed. See the error above.
    pause
    exit /b %ERRORLEVEL%
)

echo.
echo [i] Starting Tomcat with Maven...
call mvn tomcat7:run

echo [i] Server stopped or failed. Press any key to exit.
pause