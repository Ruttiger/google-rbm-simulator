#!/usr/bin/env bash
set -euo pipefail

mkdir -p docs/discovery

API_KEY=$(echo "${google_api_key_json_envvar:-}" | jq -r '.apiKey // empty')
if [[ -z "$API_KEY" ]]; then
  echo "apiKey no encontrado en google_api_key_json_envvar" >&2
  exit 1
fi

tmpfile=$(mktemp)
http_status=$(curl -sS -w '%{http_code}' "https://rcsbusinessmessaging.googleapis.com/\$discovery/rest?version=v1&key=${API_KEY}" -o "$tmpfile")
if [[ "$http_status" -ne 200 ]]; then
  echo "Error HTTP $http_status al obtener discovery" >&2
  cat "$tmpfile" >&2
  rm "$tmpfile"
  exit 1
fi

if jq -e '.error' "$tmpfile" >/dev/null 2>&1; then
  echo "Respuesta de discovery contiene error" >&2
  cat "$tmpfile" >&2
  rm "$tmpfile"
  exit 1
fi

mv "$tmpfile" docs/discovery/rbm-v1.json
