@echo off
rem The only build path on Windows (Architecture AD-15). Keep in step with build.sh.
rem
rem   build.cmd                      compile, build build\obi2pbi.jar, run every test
rem   build.cmd test [filter]        the same, running only tests whose name contains the filter
rem   build.cmd compare ^<estate^> [table ...] [--kb ^<folder^>] [--source ^<estate folder^>] [--where column=v1,v2]
rem                                  [--where-not column=value] [--show-explained n]
rem   build.cmd compare-all [--out ^<report file^>] [--no-extract]
rem                                  extract the three sample estates into kb\, compare every table with the
rem                                  reference, and write documents\implementation-artifacts\kb-comparison-report.md
rem   build.cmd skills               copy each folder of skills\ unchanged to .agents\skills\ and .claude\skills\
rem                                  (the agent and its skills are edited only under skills\; a test compares the copies)
setlocal enabledelayedexpansion
cd /d "%~dp0"

set MAIN_OUT=build\classes\main
set TEST_OUT=build\classes\test
set JAR=build\obi2pbi.jar

set ACTION=%1
if "%ACTION%"=="" set ACTION=build

if "%ACTION%"=="build" goto build
if "%ACTION%"=="test" goto test
if "%ACTION%"=="compare" goto compare
if "%ACTION%"=="compare-all" goto compareall
if "%ACTION%"=="skills" goto skills
echo usage: build.cmd [build ^| test [filter] ^| compare ^<estate^> [table ...] ^| compare-all ^| skills] 1>&2
exit /b 2

:compile
if exist build\classes rmdir /s /q build\classes
if exist %JAR% del %JAR%
mkdir %MAIN_OUT%
mkdir %TEST_OUT%
rem javac reads backslashes in an argument file as escapes, so the paths are written with forward slashes.
if exist build\main-sources.txt del build\main-sources.txt
if exist build\test-sources.txt del build\test-sources.txt
for /r src\main\java %%f in (*.java) do (
    set "P=%%f"
    echo "!P:\=/!">> build\main-sources.txt
)
for /r src\test\java %%f in (*.java) do (
    set "P=%%f"
    echo "!P:\=/!">> build\test-sources.txt
)
javac --release 11 -encoding UTF-8 -Xlint:all -Werror -d %MAIN_OUT% @build\main-sources.txt || exit /b 1
jar --create --file %JAR% --main-class obi2pbi.cli.Main -C %MAIN_OUT% . || exit /b 1
javac --release 11 -encoding UTF-8 -Xlint:all -Werror -cp %MAIN_OUT% -d %TEST_OUT% @build\test-sources.txt || exit /b 1
exit /b 0

:build
call :compile || exit /b 1
java -cp "%MAIN_OUT%;%TEST_OUT%" obi2pbi.TestRunner || exit /b 1
echo built %JAR%
exit /b 0

:test
call :compile || exit /b 1
shift
java -cp "%MAIN_OUT%;%TEST_OUT%" obi2pbi.TestRunner %1 %2 %3 || exit /b 1
exit /b 0

:compare
if not exist %TEST_OUT% (call :compile || exit /b 1)
java -cp "%MAIN_OUT%;%TEST_OUT%" obi2pbi.KbCompare %2 %3 %4 %5 %6 %7 %8 %9
exit /b %ERRORLEVEL%

:compareall
call :compile || exit /b 1
java -cp "%MAIN_OUT%;%TEST_OUT%" obi2pbi.KbCompareReport %2 %3 %4 %5
exit /b %ERRORLEVEL%

:skills
for /d %%d in (skills\*) do (
    set "NAME=%%~nxd"
    if /i "!NAME:~0,5!"=="bmad-" (
        echo skills\!NAME!: a skill here may not be named bmad-* 1>&2
        exit /b 2
    )
    for %%c in (.agents\skills .claude\skills) do (
        if exist "%%c\!NAME!" rmdir /s /q "%%c\!NAME!"
        xcopy /e /i /q /y "%%d" "%%c\!NAME!" >nul || exit /b 1
    )
    echo copied skills\!NAME!
)
exit /b 0
