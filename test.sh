#!/usr/bin/env bash
set -e
CP="lib/junit-platform-console-standalone-1.10.2.jar:out"
DIRS="src"
[ -d test ] && DIRS="$DIRS test"
find $DIRS -name '*.java' > sources.txt
javac -d out -cp "$CP" @sources.txt
java -jar lib/junit-platform-console-standalone-1.10.2.jar \
     --class-path "out" --scan-class-path
