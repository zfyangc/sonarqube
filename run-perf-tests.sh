#!/bin/bash
set -euo pipefail

export JAVA_HOME=/opt/sonarsource/jvm/java-1.9.0-sun-x64
export PATH=$JAVA_HOME/bin:$PATH

echo 'Run performance tests'
cd tests
mvn verify -B -e -V -Dcategory=Performance $*
