# SamacharDaily — GitHub Push Guide

This guide walks you through pushing the Android project to a private GitHub repository.

## Prerequisites

- GitHub account (create one at https://github.com)
- Git installed locally (download from https://git-scm.com)
- GitHub CLI installed (optional but recommended)

## Step 1: Create GitHub Private Repository

### Option A: Via GitHub Web Interface

1. Go to https://github.com/new
2. **Repository name:** `SamacharDaily-Android`
3. **Description:** `Multilingual news aggregation Android app`
4. **Visibility:** `Private` (select this)
5. Click **Create repository**
6. Copy the HTTPS or SSH URL shown on the next page

### Option B: Via GitHub CLI (faster)

```powershell
gh repo create SamacharDaily-Android --private --source=. --remote=origin --push
```

This will create the repo and push in one command.

---

## Step 2: Initialize Git Locally (First Time Only)

Navigate to the project root in PowerShell:

```powershell
cd E:\Android\SamacharDaily
git init
git config user.name "Your Name"
git config user.email "your.email@example.com"
```

---

## Step 3: Add Remote Repository

Replace `YOUR_GITHUB_USERNAME` and paste the URL you copied:

### Using HTTPS (easier, may prompt for token):
```powershell
git remote add origin https://github.com/YOUR_GITHUB_USERNAME/SamacharDaily-Android.git
```

### Using SSH (recommended if SSH key is configured):
```powershell
git remote add origin git@github.com:YOUR_GITHUB_USERNAME/SamacharDaily-Android.git
```

Verify remote was added:
```powershell
git remote -v
```

---

## Step 4: Add All Files & Commit

```powershell
git add .
git commit -m "Initial commit: SamacharDaily Android app with backend infrastructure

- Android app: Jetpack Compose, MVVM, Hilt, Room, Retrofit, FCM
- Backend services: API Gateway, Auth, Content, Feed, Search, Notifications
- Database migrations and seed data
- Ready for local development and staging deployment"
```

---

## Step 5: Push to GitHub

```powershell
git branch -M main
git push -u origin main
```

If prompted for credentials:
- **HTTPS**: Enter your GitHub username and a Personal Access Token (PAT)
  - Generate PAT at: https://github.com/settings/tokens
  - Scopes needed: `repo`, `read:org`
- **SSH**: Make sure your SSH key is added to GitHub

---

## Step 6: Verify on GitHub

1. Go to https://github.com/YOUR_GITHUB_USERNAME/SamacharDaily-Android
2. Confirm you see:
   - `app/` folder (Android code)
   - `backend/` folder (backend services)
   - `.github/workflows/` (CI/CD pipelines)
   - `README.md` files
   - `.gitignore` with `.env` protection

---

## Step 7: Create Backend Repository (Separate)

Once Android is pushed, repeat the process for backend:

```powershell
# Create new private GitHub repo: SamacharDaily-Backend

cd E:\Android\SamacharDaily\backend
git init
git add .
git commit -m "Initial commit: SamacharDaily Backend Services

- API Gateway, Auth, Feed, Content, Search, Notification services
- PostgreSQL migrations and seed data
- Docker Compose setup for local and production
- Multi-language support (en, hi, mr, and 6 more)
- Ready for deployment to cloud (Render, Railway, ECS, etc.)"

git remote add origin https://github.com/YOUR_GITHUB_USERNAME/SamacharDaily-Backend.git
git branch -M main
git push -u origin main
```

---

## Step 8: Configure GitHub Branch Protection (Recommended)

### For Android Repo:

1. Go to **Settings** → **Branches**
2. Add rule for `main`:
   - ✅ Require pull request reviews before merging
   - ✅ Dismiss stale pull request approvals
   - ✅ Require status checks to pass (CI/CD)
   - ✅ Require branches to be up to date
3. Save

### For Backend Repo:
Same as above.

---

## Step 9: Set Up Default Branch & Develop Branch

```powershell
# Create develop branch for staging
git checkout -b develop
git push -u origin develop
```

Then in GitHub **Settings** → **Default branch**, set to `main` (or `develop` for development-first approach).

---

## Step 10: Protect Secrets (GitHub Actions Setup)

1. Go to **Settings** → **Secrets and variables** → **Actions**
2. Add these secrets for CI/CD:

```
GOOGLE_PLAY_SERVICE_ACCOUNT_JSON  = (upload JSON from Play Console)
FIREBASE_PRIVATE_KEY              = (from Firebase)
NEWS_API_KEY                      = (from NewsAPI.org)
DOCKER_USERNAME                   = (Docker Hub username)
DOCKER_PASSWORD                   = (Docker Hub password)
DOCKER_REGISTRY                   = (docker.io or your registry)
```

These will be used by `.github/workflows/*.yml` for automated builds/deployments.

---

## Troubleshooting

### "fatal: not a git repository"
```powershell
git init
```

### "fatal: remote origin already exists"
```powershell
git remote rm origin
git remote add origin <url>
```

### "authentication failed"
- HTTPS: generate PAT at https://github.com/settings/tokens
- SSH: add key to https://github.com/settings/keys

### "LF will be converted to CRLF"
This is normal on Windows. You can suppress it:
```powershell
git config --global core.safecrlf false
```

---

## Recommended Repository Settings

### Android Repo (`SamacharDaily-Android`)
- **Visibility:** Private
- **Default branch:** `main`
- **Branch protection:** On (main)
- **Topics:** android, kotlin, jetpack-compose, news-app, multilingual
- **About:** Jetpack Compose news app with MVVM architecture

### Backend Repo (`SamacharDaily-Backend`)
- **Visibility:** Private
- **Default branch:** `main`
- **Branch protection:** On (main)
- **Topics:** nodejs, microservices, docker, kafka, postgresql
- **About:** Microservices backend for SamacharDaily news platform

---

## Next: CI/CD Setup

Once repos are on GitHub, the workflows in `.github/workflows/` will run automatically:

### Android CI/CD
- Lint, build, test on every PR
- Deploy internal test track on merge to `develop`
- Deploy production track on tag `v*`

### Backend CI/CD
- Lint npm modules on PR
- Build Docker images on merge to `develop`
- Push to Docker registry on tag `v*`

Check the **Actions** tab in your repos to monitor workflow runs.

---

## Post-Push Steps

1. **Invite team members** to both private repos
2. **Set up branch protection rules** (see Step 8)
3. **Configure deployment secrets** (see Step 10)
4. **Test CI/CD** by creating a test branch and PR
5. **Document deployment targets** (staging/prod URLs in repo description)

Enjoy! 🚀

