#!/usr/bin/env bash
set -e
find src -name '*.java' > sources.txt
javac -d out @sources.txt
echo "Build OK → out/"
