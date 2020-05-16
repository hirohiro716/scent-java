#!/bin/bash
jar_name="test.jar"
rm -R bin/ 2>/dev/null
cp -R src/ bin/
rm -R bin/*.java
find src/ -name *.java | xargs javac -d bin/
jar cvfm $jar_name manifest.mf -C bin .
