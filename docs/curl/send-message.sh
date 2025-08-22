#!/usr/bin/env bash
# Example request to send a message via RBM
curl -s -X POST "https://rcsbusinessmessaging.googleapis.com/v1/phones/${PHONE}/agentMessages?agentId=${AGENT_ID}&messageId=msg-1" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "contentMessage": { "text": "Hola desde curl" }
  }'
