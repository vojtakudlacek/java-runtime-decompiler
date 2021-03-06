#!/bin/bash
# Locate JDK from $PATH
path=`which java`
# go through symlinks until we are at /usr/lib/jvm/...
while [[ ${path} != /usr/lib/jvm/* ]]
do
path=`readlink $path`
done
while [[ $(dirname "$path") != /usr/lib/jvm ]]
do
path="$(dirname "$path")"
done
export JAVA_HOME=$path

# launch application
java -cp ${JAVA_HOME}/lib/tools.jar:\
$HOME/.m2/repository/com/fifesoft/rsyntaxtextarea/2.6.1/rsyntaxtextarea-2.6.1.jar:\
$HOME/.m2/repository/io/github/soc/directories/10/directories-10.jar:\
$HOME/.m2/repository/com/google/code/gson/gson/2.8.5/gson-2.8.5.jar:\
$HOME/.m2/repository/java/runtime-decompiler/1.0.0-SNAPSHOT/runtime-decompiler-1.0.0-SNAPSHOT.jar\
 org.jrd.backend.data.Main
