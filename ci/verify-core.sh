#!/usr/bin/env bash
set -euo pipefail

rm -rf build/self-test
mkdir -p build/self-test

javac -encoding UTF-8 -d build/self-test \
  screen-awake-core/src/main/java/com/kroxaboom/skazka/android/awake/*.java \
  telemetry-core/src/main/java/com/kroxaboom/skazka/android/telemetry/*.java \
  tests/AndroidCoreSelfTest.java

java -cp build/self-test AndroidCoreSelfTest
