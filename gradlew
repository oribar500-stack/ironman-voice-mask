#!/bin/sh
APP_HOME=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar
if [ ! -f "$CLASSPATH" ]; then
  echo "Missing gradle/wrapper/gradle-wrapper.jar. Install Gradle 9.2.1 and run: gradle wrapper --gradle-version 9.2.1" >&2
  exit 1
fi
exec java -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
