@echo off
setlocal
set MAVEN_WRAPPER_DIR=%~dp0\.mvn\wrapper
set MAVEN_PROJECTBASEDIR=%~dp0
set MAVEN_WRAPPER_JAR=%MAVEN_WRAPPER_DIR%\maven-wrapper.jar

if defined JAVA_HOME (
  set "_JAVA=%JAVA_HOME%\bin\java"
) else (
  where java >nul 2>nul
  if errorlevel 1 (
    echo ERROR: JAVA_HOME is not set and java is not on PATH.
    exit /b 1
  ) else (
    set "_JAVA=java"
  )
)

"%_JAVA%" -cp "%MAVEN_WRAPPER_JAR%" -Dmaven.multiModuleProjectDirectory="%MAVEN_PROJECTBASEDIR%" org.apache.maven.wrapper.MavenWrapperMain %*
endlocal
