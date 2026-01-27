#!/bin/bash

# Automatic development setup script
# Run this once to set up your local development environment

echo "🚀 Setting up labotronui for development..."

# Check if .env exists, if not create from example
if [ ! -f .env ]; then
    echo "📝 Creating .env from .env.example..."
    cp .env.example .env
    echo "✅ Created .env file. You can edit it to customize your API URLs."
else
    echo "📋 Using existing .env file."
fi

# Generate development files
echo "🔧 Generating development files from templates..."
./generate-dev-files.sh

# Install dependencies if needed
if [ ! -d ".dart_tool" ]; then
    echo "📦 Installing Flutter dependencies..."
    flutter pub get
fi

echo ""
echo "✅ Development setup complete!"
echo ""
echo "🎯 Your environment:"
echo "   - API_URL: $(grep API_URL .env | cut -d'=' -f2)"
echo "   - API_URL_HTTPS: $(grep API_URL_HTTPS .env | cut -d'=' -f2)"
echo ""
echo "🏃 To run the development server:"
echo "   flutter run -d web-server --web-port=3000"
echo ""
echo "📝 To modify API URLs, edit the .env file and run:"
echo "   ./generate-dev-files.sh"
