#!/usr/bin/env bash
set -euo pipefail

mkdir -p docs/discovery
curl -sS "https://rcsbusinessmessaging.googleapis.com/\$discovery/rest?version=v1&key=${GOOGLE_API_KEY:-}" -o docs/discovery/rbm-v1.json
