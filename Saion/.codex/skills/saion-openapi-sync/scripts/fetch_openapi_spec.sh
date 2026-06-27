#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
OUT_PATH="${1:-$SCRIPT_DIR/../references/saion-openapi.json}"
URL="${SAION_OPENAPI_URL:-https://dev.saion.app/api/api-specs}"

mkdir -p "$(dirname "$OUT_PATH")"
curl -L --fail --silent --show-error "$URL" -o "$OUT_PATH"
echo "$OUT_PATH"
