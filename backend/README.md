# SamacharDaily Backend

Backend repository for the SamacharDaily Android app.

> This README is intended as the handoff/shareable backend document after separating backend code from the Android workspace.

## Overview

SamacharDaily is a multilingual news aggregation platform supporting:
- Personalized feeds
- Article details and bookmarks
- Search
- Recommendations
- Notifications via FCM
- Scraper-driven content ingestion

The Android client talks to the backend through the API Gateway.

## Suggested Repository Structure

```text
backend/
├── api-gateway/            # Public entrypoint for mobile/web clients
├── auth-service/           # Google OAuth + JWT + refresh tokens
├── content-service/        # Articles, categories, bookmarks, interactions
├── feed-service/           # Personalized + trending feed
├── search-service/         # Search and suggestions
├── notification-service/   # FCM token registration and notification history
├── recommendation-service/ # Similar articles / user recommendations
├── scraper-service/        # RSS / News API / custom ingestion jobs
├── web-app/                # Optional web reader app
├── admin-dashboard/        # Optional admin portal
├── db/                     # Migrations, seed data, SQL utilities
├── scripts/                # Health checks, topic creation, ops helpers
├── docker-compose.yml
├── docker-compose.prod.yml
├── .env.example
└── README.md
```

## Service Ports

Current environment values indicate the following local service ports:

| Service | Port |
|---|---:|
| API Gateway | `3000` |
| Auth Service | `3001` |
| Content Service | `3002` |
| Feed Service | `3003` |
| Search Service | `3004` |
| Notification Service | `3005` |

Supporting infrastructure:

| Dependency | Default |
|---|---|
| PostgreSQL | `5432` |
| Redis | `6379` |
| Kafka | `9092` inside Docker network |
| Elasticsearch | `9200` |

## Android Integration Notes

The Android app currently expects the gateway base URL to look like:

```text
http://<host>:3000/
```

Example endpoints currently used by the app:

### Auth
- `POST /api/v1/auth/google`
- `POST /api/v1/auth/refresh`
- `POST /api/v1/auth/logout`
- `GET /api/v1/auth/me`

### Bookmarks
- `GET /api/v1/bookmarks`
- `POST /api/v1/bookmarks`
- `DELETE /api/v1/bookmarks/{articleId}`

### Articles
- `GET /api/v1/articles`
- `GET /api/v1/articles/{id}`
- `POST /api/v1/articles/{id}/like`
- `GET /api/v1/articles/{id}/interactions`
- `GET /api/v1/categories`

### Feed
- `GET /api/v1/feed`
- `GET /api/v1/feed/trending`
- `GET /api/v1/feed/category/{categoryId}`

### Search
- `GET /api/v1/search`
- `GET /api/v1/search/suggestions`
- `GET /api/v1/search/trending`

### Recommendations
- `GET /api/v1/recommendations/for-user`
- `GET /api/v1/recommendations/similar/{articleId}`
- `GET /api/v1/recommendations/trending`
- `POST /api/v1/recommendations/feedback`

### Notifications
- `POST /api/v1/notifications/register-token`
- `DELETE /api/v1/notifications/unregister-token`
- `GET /api/v1/notifications/history`
- `POST /api/v1/notifications/mark-read/{notificationId}`
- `GET /api/v1/notifications/preferences`

### Health
- `GET /health`

## API Rules Expected by Android

- Protected endpoints must accept `Authorization: Bearer <accessToken>`
- Token refresh must use `POST /api/v1/auth/refresh`
- API responses should follow a consistent envelope, for example:

```json
{
  "success": true,
  "data": {}
}
```

For failures:

```json
{
  "success": false,
  "error": "Human readable message",
  "code": "ERROR_CODE"
}
```

## Local Development Setup

1. Create a safe env file from `.env.example`
2. Start infrastructure and services
3. Run migrations / seed data
4. Verify gateway and health endpoints
5. Point Android app to the gateway host

Example local startup:

```bash
cd backend
docker compose up -d
docker compose ps
```

If services are split between Docker and local node processes, document the exact startup order here.

## Environment Variables Policy

Keep only `.env.example` in Git.

Never commit:
- real DB passwords
- JWT secrets
- OAuth client secrets
- Firebase private keys
- AWS credentials
- third-party API keys

Recommended files:
- `.env.example` -> placeholders only
- `.env.local` -> developer machine only
- GitHub Actions secrets / cloud secret manager -> staging and production

## Database and Migrations

Document these items in the backend repo:
- migration tool being used
- migration execution order
- seed scripts
- rollback approach
- whether fresh volume is required or existing DB upgrade is supported

Core domain tables expected by the app include:
- `users`
- `categories`
- `user_category_prefs`
- `articles`
- `user_article_interactions`
- `bookmarks`
- `tags`
- `article_tags`
- `notifications`
- `ad_placements`
- `refresh_tokens`
- `fcm_tokens`
- `read_history`

## Infra Dependencies

Current backend environment indicates usage of:
- PostgreSQL
- Redis
- Kafka
- Elasticsearch
- Firebase Admin / FCM
- News API / RSS feeds
- AWS S3 / CloudFront

Kafka topics expected in the platform:
- `clickstream`
- `article_ingestion`
- `breaking_news`
- `notification_events`
- `feed_invalidation`

## CI/CD Recommendation

Use GitHub Actions unless you have a strong on-premise/Jenkins requirement.

Suggested environments:
- `dev`
- `staging`
- `prod`

Suggested flow:
1. Pull request -> lint, unit tests, build
2. Merge to `develop` -> deploy staging
3. Tag or merge to `main` -> deploy production with approval

## Deployment Notes

Recommended MVP deployment options:
- Backend containers: Render / Railway / Fly.io / ECS
- PostgreSQL: managed Postgres
- Redis: managed Redis
- Elasticsearch: Elastic Cloud / managed search
- Object storage: S3-compatible storage

For production, document:
- ingress URL / domain
- TLS termination
- environment secret source
- monitoring/logging
- rollback steps
- backup and restore procedure

## Security Checklist

Before sharing or publishing this backend repo:

- [ ] Remove any committed real `.env` file
- [ ] Rotate exposed secrets if they were ever committed or shared
- [ ] Replace real values with placeholders in `.env.example`
- [ ] Restrict CORS for production
- [ ] Use HTTPS in production
- [ ] Disable verbose request/response logging in production
- [ ] Add environment-based config for app base URLs
- [ ] Verify refresh-token storage and logout invalidation

## Immediate Follow-up Recommended

Because a real-looking `.env` file exists in the current workspace, do this before publishing:

1. Create `backend/.env.example` with placeholder values only
2. Add `.env` to `.gitignore`
3. Rotate these secrets if they were exposed:
   - `GOOGLE_CLIENT_SECRET`
   - `NEWS_API_KEY`
   - `JWT_SECRET`
   - `JWT_REFRESH_SECRET`
   - `POSTGRES_PASSWORD`
   - `REDIS_PASSWORD`
   - any Firebase/AWS credentials

## Ownership / Handoff Section

Fill this before sharing externally:

- Backend repo URL:
- Default branch:
- Staging URL:
- Production URL:
- Android app repo URL:
- Primary maintainer:
- API owner:
- DevOps owner:

---

If backend endpoints or contracts change, update this file and inform the Android app team so Retrofit models and token flow stay aligned.

