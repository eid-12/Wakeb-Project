

FROM node:18-bullseye AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN node -v && npm -v && pwd && ls -la && ls -la public || true
RUN test -f public/index.html || (echo "❌ public/index.html missing" && exit 1)
RUN npm run build --verbose


FROM node:18-bullseye
WORKDIR /app
COPY package*.json ./
RUN npm ci --omit=dev
COPY server.js ./server.js
COPY --from=build /app/dist ./dist

# التعديل هنا:
ENV PORT=3000
ENV NODE_ENV=production
EXPOSE 3000

CMD ["node","server.js"]
