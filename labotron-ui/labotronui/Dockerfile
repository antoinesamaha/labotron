# Use latest Flutter which includes Dart SDK 3.6.0+
FROM ghcr.io/cirruslabs/flutter:3.35.7 AS build

WORKDIR /workspace

# Copy the workspace structure (both neofoc and labotron)
COPY . .

# Navigate to the labotronui directory
WORKDIR /workspace/labotron/labotron-ui/labotronui

# Install dependencies
RUN flutter pub get

# Build the web app
RUN flutter build web --release

# Production stage with nginx
FROM nginx:1.32.2-alpine

# Install gettext for envsubst
RUN apk add --no-cache gettext

# Copy built Flutter app
COPY --from=build /workspace/labotron/labotron-ui/labotronui/build/web /usr/share/nginx/html

# Copy templates for environment variable substitution
COPY --from=build /workspace/labotron/labotron-ui/labotronui/web/env.js.template /usr/share/nginx/html/env.js.template
COPY --from=build /workspace/labotron/labotron-ui/labotronui/web/index.html.template /usr/share/nginx/html/index.html.template

# Create entrypoint script for environment substitution
RUN echo '#!/bin/sh' > /docker-entrypoint.d/10-substitute-env.sh && \
    echo 'echo "Substituting environment variables..."' >> /docker-entrypoint.d/10-substitute-env.sh && \
    echo 'echo "API_URL: $API_URL"' >> /docker-entrypoint.d/10-substitute-env.sh && \
    echo 'echo "API_URL_HTTPS: $API_URL_HTTPS"' >> /docker-entrypoint.d/10-substitute-env.sh && \
    echo 'echo "FLUTTER_BASE_HREF: $FLUTTER_BASE_HREF"' >> /docker-entrypoint.d/10-substitute-env.sh && \
    echo 'export FLUTTER_BASE_HREF=${FLUTTER_BASE_HREF:-"/"}' >> /docker-entrypoint.d/10-substitute-env.sh && \
    echo 'envsubst < /usr/share/nginx/html/env.js.template > /usr/share/nginx/html/env.js' >> /docker-entrypoint.d/10-substitute-env.sh && \
    echo 'envsubst < /usr/share/nginx/html/index.html.template > /usr/share/nginx/html/index.html' >> /docker-entrypoint.d/10-substitute-env.sh && \
    echo 'echo "Environment substitution completed"' >> /docker-entrypoint.d/10-substitute-env.sh && \
    chmod +x /docker-entrypoint.d/10-substitute-env.sh

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]