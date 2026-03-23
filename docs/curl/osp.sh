#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080}"
OSP_CLIENT_ID="${OSP_CLIENT_ID:-osp-client}"
OSP_CLIENT_SECRET="${OSP_CLIENT_SECRET:-osp-secret}"
OSP_BOT_ID="${OSP_BOT_ID:-orange-bot}"

BASIC_AUTH=$(printf '%s:%s' "$OSP_CLIENT_ID" "$OSP_CLIENT_SECRET" | base64)

TOKEN=$(curl -sS -X POST "$BASE_URL/v3/auth/" \
  -H "Authorization: Basic $BASIC_AUTH" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=client_credentials&scope=osp.send" | jq -r '.access_token')

echo "Token: $TOKEN"

curl -sS -X POST "$BASE_URL/v3/bot/v1/$OSP_BOT_ID/messages" \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"message":{"text":"Hola desde OSP curl"}}' | jq

curl -sS -X POST "$BASE_URL/webhook/orange/$OSP_BOT_ID/uuid-123" \
  -H "Content-Type: application/json" \
  -d '{"messageStatus":{"status":"Displayed"}}' | jq
