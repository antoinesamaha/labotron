# Tag with GHCR format
docker tag labotron-web-app ghcr.io/antoinesamaha/labotron-web-app:dev

# Push to GHCR (you'll need a PAT token)
echo "$PAT_GITHUB_PACKAGES" | docker login ghcr.io -u antoinesamaha --password-stdin
docker push ghcr.io/antoinesamaha/labotron-web-app:dev
