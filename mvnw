#!/usr/bin/env sh
set -eu

BASEDIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
WRAPPER_DIR="$BASEDIR/.mvn/wrapper"
MAVEN_VERSION="3.9.9"
MAVEN_HOME="$WRAPPER_DIR/apache-maven-$MAVEN_VERSION"
MAVEN_ZIP="$WRAPPER_DIR/apache-maven-$MAVEN_VERSION-bin.zip"
MAVEN_CMD="$MAVEN_HOME/bin/mvn"
MAVEN_URL="https://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.zip"

if [ ! -x "$MAVEN_CMD" ]; then
  mkdir -p "$WRAPPER_DIR"
  if [ ! -f "$MAVEN_ZIP" ]; then
    echo "Downloading Apache Maven $MAVEN_VERSION..."
    if command -v curl >/dev/null 2>&1; then
      curl -fsSL "$MAVEN_URL" -o "$MAVEN_ZIP"
    elif command -v wget >/dev/null 2>&1; then
      wget -q "$MAVEN_URL" -O "$MAVEN_ZIP"
    else
      echo "curl or wget is required to download Maven." >&2
      exit 1
    fi
  fi
  echo "Extracting Apache Maven $MAVEN_VERSION..."
  unzip -q -o "$MAVEN_ZIP" -d "$WRAPPER_DIR"
fi

exec "$MAVEN_CMD" "$@"
