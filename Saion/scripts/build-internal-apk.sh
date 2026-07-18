#!/usr/bin/env bash

set -euo pipefail
IFS=$'\n\t'

log() {
  printf '[%s] %s\n' "$(date '+%H:%M:%S')" "$*"
}

fail() {
  log "ERROR: $*"
  exit 1
}

require_command() {
  command -v "$1" >/dev/null 2>&1 || fail "Required command not found: $1"
}

read_property() {
  local key="$1"
  awk -v property_key="$key" 'index($0, property_key "=") == 1 { print substr($0, length(property_key) + 2) }' "$LOCAL_PROPERTIES" | tail -n 1
}

resolve_existing_path() {
  local candidate="$1"

  if [[ -f "$candidate" ]]; then
    printf '%s\n' "$candidate"
    return 0
  fi

  if [[ -f "$ROOT_DIR/$candidate" ]]; then
    printf '%s\n' "$ROOT_DIR/$candidate"
    return 0
  fi

  return 1
}

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
LOCAL_PROPERTIES="$ROOT_DIR/local.properties"
GRADLEW="$ROOT_DIR/gradlew"
DEFAULT_LOCAL_BUNDLETOOL="$ROOT_DIR/tools/bundletool/bundletool"

if command -v bundletool >/dev/null 2>&1; then
  BUNDLETOOL_BIN="$(command -v bundletool)"
elif [[ -x "$DEFAULT_LOCAL_BUNDLETOOL" ]]; then
  BUNDLETOOL_BIN="$DEFAULT_LOCAL_BUNDLETOOL"
else
  fail "bundletool is not installed. Add it to PATH or place it at $DEFAULT_LOCAL_BUNDLETOOL"
fi

TIMESTAMP="$(date '+%Y%m%d-%H%M%S')"
OUTPUT_DIR="$ROOT_DIR/app/build/outputs/bundletool/internal/$TIMESTAMP"
AAB_PATH="$ROOT_DIR/app/build/outputs/bundle/internal/app-internal.aab"
APKS_PATH="$OUTPUT_DIR/app-internal.apks"
EXTRACT_DIR="$OUTPUT_DIR/extracted"
UNIVERSAL_APK_PATH="$EXTRACT_DIR/universal.apk"

log "Starting internal APK build pipeline"
log "Project root: $ROOT_DIR"
log "Using bundletool: $BUNDLETOOL_BIN"

cd "$ROOT_DIR"

require_command unzip

[[ -f "$LOCAL_PROPERTIES" ]] || fail "local.properties not found: $LOCAL_PROPERTIES"
[[ -x "$GRADLEW" ]] || fail "gradlew is not executable: $GRADLEW"

log "Reading release signing configuration from local.properties"
STORE_FILE_RAW="$(read_property "release.signing.store.file")"
STORE_PASSWORD="$(read_property "release.signing.store.password")"
KEY_ALIAS="$(read_property "release.signing.key.alias")"
KEY_PASSWORD="$(read_property "release.signing.key.password")"

[[ -n "$STORE_FILE_RAW" ]] || fail "Missing release.signing.store.file in local.properties"
[[ -n "$STORE_PASSWORD" ]] || fail "Missing release.signing.store.password in local.properties"
[[ -n "$KEY_ALIAS" ]] || fail "Missing release.signing.key.alias in local.properties"
[[ -n "$KEY_PASSWORD" ]] || fail "Missing release.signing.key.password in local.properties"

STORE_FILE="$(resolve_existing_path "$STORE_FILE_RAW")" || fail "Keystore file not found: $STORE_FILE_RAW"

log "Signing keystore: $STORE_FILE"
log "Creating output directory: $OUTPUT_DIR"
mkdir -p "$OUTPUT_DIR" "$EXTRACT_DIR"

log "Step 1/4: Building internal app bundle"
"$GRADLEW" :app:bundleInternal

[[ -f "$AAB_PATH" ]] || fail "Expected bundle not found: $AAB_PATH"
log "App bundle created: $AAB_PATH"

log "Step 2/4: Generating universal APK set with bundletool"
"$BUNDLETOOL_BIN" build-apks \
  --bundle="$AAB_PATH" \
  --output="$APKS_PATH" \
  --mode=universal \
  --ks="$STORE_FILE" \
  --ks-pass="pass:$STORE_PASSWORD" \
  --ks-key-alias="$KEY_ALIAS" \
  --key-pass="pass:$KEY_PASSWORD"

[[ -f "$APKS_PATH" ]] || fail "Expected APK set not found: $APKS_PATH"
log "APK set created: $APKS_PATH"

log "Step 3/4: Extracting universal APK"
unzip -oq "$APKS_PATH" universal.apk -d "$EXTRACT_DIR"

[[ -f "$UNIVERSAL_APK_PATH" ]] || fail "Expected universal APK not found: $UNIVERSAL_APK_PATH"
log "Universal APK extracted: $UNIVERSAL_APK_PATH"

log "Step 4/4: Final output paths"
printf 'AAB_PATH=%s\n' "$AAB_PATH"
printf 'APKS_PATH=%s\n' "$APKS_PATH"
printf 'APK_PATH=%s\n' "$UNIVERSAL_APK_PATH"
