# Wakeb Maps

A full-stack mapping application for place search, saved locations and account management. The frontend uses Vue 3 and Leaflet; the backend is split into Spring Boot services backed by MySQL.

[Live application](https://maps.cloudbase.website/) · [Arabic public page](https://maps.cloudbase.website/ar/)

## Features

- Search for places and select a result on an interactive map.
- Save places and review search history after signing in.
- Submit custom locations and manage account preferences.
- Administrative tools for authorized users.
- English and Arabic public introductions with separate search metadata.

Location access is optional for browsing and search. Features that use your starting position require browser permission. Account screens are not all translated.

## Repository layout

- `client/`: Vue application, Express production server and localized build scripts.
- `Backend/api-gateway/`: gateway to backend services.
- `Backend/Auth-Service/` and `Backend/User-Service/`: authentication and user management.
- `Backend/Place-Service/`, `Backend/SavedPlace-Service/` and `Backend/Search-Service/`: place operations.
- `.github/workflows/`: build and image checks.
- `Jenkinsfile`: Jenkins pipeline configuration.

## Frontend development

Use the Node version supported by the current Docker build (Node 22) and npm.

```sh
git clone https://github.com/eid-12/Wakeb-Project.git
cd Wakeb-Project/client
npm ci
npm run serve
```

Use the development URL printed by Vue CLI. This project uses Vue CLI, not Vite. Configure the existing API/provider settings for your own environment; never publish private keys or database credentials.

## Production frontend

```sh
cd client
npm ci
npm run build
npm start
```

The Express server serves the built application. Its production container uses port 3000. Backend services are separate applications; port 3000 is not the Spring Boot backend port.

## Backend setup

Each directory under `Backend/` is a separate service. Review its Maven configuration, database connection and service routes before running it. There is no single root backend command that starts the whole system.

```sh
cd Backend/Auth-Service
./mvnw spring-boot:run
```

On Windows, use `mvnw.cmd` where the service provides the wrapper. Supply a local MySQL instance and environment-specific configuration. Do not reuse production credentials for local development.

## Deployment and checks

The frontend image is `minipcer/mapping-frontend`. Releases are tagged by commit and include a revision label. The build workflow checks the actual frontend image before publication. Cloudflare Tunnel and Nginx Proxy Manager provide the existing public routing; publishing an image alone does not update every running container.

Keep production configuration and data outside the repository. Confirm both language URLs after a release. A successful build is not a claim that all authenticated account operations have passed end-to-end testing.

## License and attribution

See [LICENSE](LICENSE) for Apache-2.0 terms. Preserve the contributions and notices of the original project authors.
