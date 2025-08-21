#!/usr/bin/env bash
# Example request to send a message via RBM
curl -s -X POST "https://rcsbusinessmessaging.googleapis.com/v1/phones/${PHONE}/messages" \
  -H "Authorization: Bearer ${TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "message": { "text": "Hola desde curl" }
  }'
