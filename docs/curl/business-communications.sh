#!/bin/bash
# Examples for Business Communications endpoints.
# Requires jq and the simulator running on localhost:8080

TOKEN=$(curl -s -X POST http://localhost:8080/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=client_credentials&client_id=test-client&client_secret=secret" | jq -r .access_token)

echo "Token: $TOKEN"

echo "Create brand"
curl -s -X POST http://localhost:8080/v1/brands \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"displayName":"Acme Corp"}' | jq

echo "Create agent"
curl -s -X POST http://localhost:8080/v1/brands/1/agents \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"displayName":"Acme Bot"}' | jq

echo "Request verification"
curl -s -X POST http://localhost:8080/v1/brands/1/agents/1:requestVerification \
  -H "Authorization: Bearer $TOKEN" | jq

echo "Approve verification"
curl -s -X PATCH http://localhost:8080/v1/brands/1/agents/1/verification \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"state":"VERIFIED"}' | jq

echo "Request launch"
curl -s -X POST http://localhost:8080/v1/brands/1/agents/1:requestLaunch \
  -H "Authorization: Bearer $TOKEN" | jq

echo "Approve launch"
curl -s -X PATCH http://localhost:8080/v1/brands/1/agents/1/launch \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"state":"APPROVED"}' | jq

echo "Create integration"
curl -s -X POST http://localhost:8080/v1/brands/1/agents/1/integrations \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"agentWebhookIntegration":{"webhookUri":"http://localhost:8081/callback"}}' | jq

echo "List regions"
curl -s -H "Authorization: Bearer $TOKEN" http://localhost:8080/v1/regions | jq

echo "Register webhook"
curl -s -X POST http://localhost:8080/v1/brands/1/agents/1/webhooks \
  -H 'Content-Type: application/json' \
  -d '{"webhookUrl":"http://localhost:8081/callback","clientToken":"s3cr3t"}' | jq

echo "Sink endpoint"
curl -s -X POST http://localhost:8080/webhook/google/1 \
  -H 'Content-Type: application/json' \
  -d '{"secret":"abc"}' | jq
