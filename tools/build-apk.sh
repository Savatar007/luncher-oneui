#!/usr/bin/env bash
# Build the debug APK with the toolchain installed under ~/workspace/tools
# (Temurin JDK 17, Gradle 8.14.3, manual Android SDK: platform 35,
# build-tools 35.0.0). Used by Muse to build on this machine.
set -euo pipefail

export JAVA_HOME="$HOME/workspace/tools/jdk"
export ANDROID_HOME="$HOME/workspace/tools/sdk"
export ANDROID_SDK_ROOT="$HOME/workspace/tools/sdk"
export GRADLE_USER_HOME="$HOME/.gradle"
export PATH="$JAVA_HOME/bin:$HOME/workspace/tools/gradle/bin:$PATH"

cd "$(dirname "$0")/.."
echo "sdk.dir=$ANDROID_HOME" > local.properties

gradle --no-daemon assembleDebug

APK="app/build/outputs/apk/debug/app-debug.apk"
if [ -f "$APK" ]; then
  echo "APK built: $APK"
  "$ANDROID_HOME/build-tools/35.0.0/aapt2" dump badging "$APK" | head -4
else
  echo "APK not found at $APK" >&2
  exit 1
fi
