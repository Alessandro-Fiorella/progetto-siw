#!/usr/bin/env bash
MAVEN_WRAPPER_DIR="$(cd "`dirname "$0"`" && pwd)/.mvn/wrapper"
MAVEN_PROJECTBASEDIR="$(cd "`dirname "$0"`" && pwd)"

if [ -z "$JAVA_HOME" ]; then
  if command -v java >/dev/null 2>&1; then
    _java=java
  else
    echo "ERROR: JAVA_HOME is not set and java is not on PATH." >&2
    exit 1
  fi
else
  _java="$JAVA_HOME/bin/java"
fi

exec "$_java" -cp "$MAVEN_WRAPPER_DIR/maven-wrapper.jar" -Dmaven.multiModuleProjectDirectory="$MAVEN_PROJECTBASEDIR" org.apache.maven.wrapper.MavenWrapperMain "$@"
