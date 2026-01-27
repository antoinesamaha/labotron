#!/bin/bash

# Development environment variable substitution script
# This generates the actual files from templates for local development

# Load environment variables from .env file if it exists
if [ -f .env ]; then
    echo "Loading environment from .env file..."
    export $(cat .env | grep -v '^#' | xargs)
fi

# Set default development values if not provided
export API_URL=${API_URL:-"http://localhost:8080"}
export API_URL_HTTPS=${API_URL_HTTPS:-"https://localhost:8443"}
export FLUTTER_BASE_HREF=${FLUTTER_BASE_HREF:-"/"}

echo "Generating files from templates for development..."
echo "API_URL: $API_URL"
echo "API_URL_HTTPS: $API_URL_HTTPS"
echo "FLUTTER_BASE_HREF: $FLUTTER_BASE_HREF"

# Generate env.js from template
envsubst < web/env.js.template > web/env.js

# Generate index.html from template  
envsubst < web/index.html.template > web/index.html

echo "Template substitution completed!"
echo "Generated files:"
echo "  - web/env.js"
echo "  - web/index.html"
