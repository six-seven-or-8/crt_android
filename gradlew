#!/bin/sh

#
# Copyright © 2015-2021 the original authors.
# Licensed under the Apache License, Version 2.0
#

APP_NAME="Gradle"
APP_BASE_NAME=${0##*/}

# Resolve APP_HOME
app_path=$0
while [ -h "$app_path" ]; do
    ls=$(ls -ld "$app_path")
    link=${ls#*' -> '}
    case $link in
      /*)   app_path=$link ;;
      *)    app_path=$(dirname "$app_path")/$link ;;
    esac
done
APP_HOME=$(cd "$(dirname "$app_path")" && pwd -P) || exit

CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar

# Determine JAVACMD
if [ -n "$JAVA_HOME" ]; then
    JAVACMD=$JAVA_HOME/bin/java
    if [ ! -x "$JAVACMD" ]; then
        echo "ERROR: JAVA_HOME is invalid: $JAVA_HOME" >&2
        exit 1
    fi
else
    JAVACMD=java
fi

# JVM options — sin eval, sin comillas problemáticas
exec "$JAVACMD" \
    -Xmx64m \
    -Xms64m \
    "-Dorg.gradle.appname=$APP_BASE_NAME" \
    -classpath "$CLASSPATH" \
    org.gradle.wrapper.GradleWrapperMain \
    "$@"
