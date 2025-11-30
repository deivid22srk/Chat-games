#!/bin/sh

APP_BASE_NAME=${0##*/}
APP_HOME=$( cd "${APP_HOME:-./}" && pwd -P ) || exit

DEFAULT_JVM_OPTS='"-Xmx64m" "-Xms64m"'

JAVA_HOME="${JAVA_HOME:-/usr/lib/jvm/java-17-openjdk-amd64}"
JAVACMD="${JAVA_HOME}/bin/java"

CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar

exec "$JAVACMD" $DEFAULT_JVM_OPTS $JAVA_OPTS $GRADLE_OPTS \
    -classpath "$CLASSPATH" \
    org.gradle.wrapper.GradleWrapperMain "$@"
