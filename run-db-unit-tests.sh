#!/bin/bash
set -euo pipefail

ORCHESTRATOR_CONFIG_URL=$1
shift

export JAVA_HOME=/opt/sonarsource/jvm/java-1.9.0-sun-x64
export PATH=$JAVA_HOME/bin:$PATH

mvn verify \
  -pl :sonar-db-core,:sonar-db-migration,:sonar-db-dao \
  -Dorchestrator.configUrl=$ORCHESTRATOR_CONFIG_URL \
  -Dwith-db-drivers \
  -B -e -V $*
