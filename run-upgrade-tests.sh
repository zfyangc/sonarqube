#!/bin/bash
# Run upgrade tests. SonarQube must be already built in order to
# make the ZIP file available for tests.

# Arguments:
# 1. the path to Orchestrator properties file. If empty, then default values are used. Example: "file:///Users/me/orchestrator.properties"
#
# Example:
# ./run-upgrade-tests.sh "file:///Users/me/orchestrator-mysql56.properties"

set -euo pipefail

ORCHESTRATOR_CONFIG_URL=$1
shift 1

export JAVA_HOME=/opt/sonarsource/jvm/java-1.9.0-sun-x64
export PATH=$JAVA_HOME/bin:$PATH

cd tests
mvn verify \
  -Pwith-db-drivers \
  -Dorchestrator.configUrl=$ORCHESTRATOR_CONFIG_URL \
  -Dcategory=Upgrade -B -e -V $*
