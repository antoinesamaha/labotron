#!/bin/bash

# Development environment variable substitution script
# This generates the actual files from templates for local development

# Set default development values if not provided
export API_URL=${API_URL:-"http://localhost:8080"}
export API_URL_HTTPS=${API_URL_HTTPS:-"https://localhost:8443"}

echo "Generating files from templates for development..."
echo "API_URL: $API_URL"
echo "API_URL_HTTPS: $API_URL_HTTPS"

# Generate env.js from template
envsubst < web/env.js.template > web/env.js

# Generate index.html from template  
envsubst < web/index.html.template > web/index.html

echo "Template substitution completed!"
echo "Generated files:"
echo "  - web/env.js"
echo "  - web/index.html"
