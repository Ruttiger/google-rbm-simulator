#!/usr/bin/env bash
set -euo pipefail

mkdir -p docs/discovery
API_KEY=$(echo "${google_api_key_json_envvar:-}" | jq -r '.apiKey // empty')
if [[ -z "$API_KEY" ]]; then
  echo "apiKey no encontrado en google_api_key_json_envvar" >&2
  exit 1
fi
curl -sS "https://rcsbusinessmessaging.googleapis.com/\$discovery/rest?version=v1&key=${API_KEY}" -o docs/discovery/rbm-v1.json
