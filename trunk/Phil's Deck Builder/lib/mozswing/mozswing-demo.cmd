@echo off

if "%JAVA_HOME%" == "" goto noJavaHome
if not exist "%JAVA_HOME%\bin\java.exe" goto noJavaHome
if "%_JAVACMD%" == "" set _JAVACMD=%JAVA_HOME%\bin\java.exe

:noJavaHome
if "%_JAVACMD%" == "" set _JAVACMD=javaw.exe

set CURRDIR=%~sdp0

if not exist "%CURRDIR%\src" goto addLibJars
rem running from source code
set CP=%CURRDIR%bin;%CURRDIR%build\classes;%CURRDIR%build\dist\mozswing.jar

:addLibJars
set CP=%CP%;%CURRDIR%lib\commons-logging-1.1.jar;%CURRDIR%lib\mozdom4java.jar;%CURRDIR%lib\MozillaGlue-1.9.jar;%CURRDIR%lib\MozillaInterfaces-1.9.jar;%CURRDIR%lib\jna.jar;%CURRDIR%lib\mozswing.jar

rem @echo %CP%
rem pause

start "MozSwing Demo" /b "%_JAVACMD%" -cp %CP% org.mozilla.browser.MozillaWindow

