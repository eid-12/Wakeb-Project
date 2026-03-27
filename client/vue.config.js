const { defineConfig } = require("@vue/cli-service");
const path = require("path");

/**
 * Local dev: Vue runs on `devServer.port` (not 8080) so the API gateway can use 8080.
 * Proxy order: `/api/auth` before `/api` so auth hits the auth service.
 *
 * Override without editing this file:
 *   VUE_DEV_AUTH_ORIGIN=http://localhost:8081
 *   VUE_DEV_GATEWAY_ORIGIN=http://localhost:8080
 * Or use Express (npm run dev:proxy) and point proxy at localhost:3000 instead.
 */
const authOrigin = process.env.VUE_DEV_AUTH_ORIGIN || "http://localhost:8081";
const gatewayOrigin = process.env.VUE_DEV_GATEWAY_ORIGIN || "http://localhost:8080";
const useExpressProxy = process.env.VUE_DEV_USE_EXPRESS === "1";

module.exports = defineConfig({
  transpileDependencies: true,
  outputDir: path.resolve(__dirname, "dist"),
  publicPath: "./",
  devServer: {
    port: Number(process.env.VUE_DEV_PORT) || 5174,
    host: "0.0.0.0",
    proxy: useExpressProxy
      ? {
          "/api": { target: "http://localhost:3000", changeOrigin: true },
        }
      : {
          "/api/auth": {
            target: authOrigin,
            changeOrigin: true,
            pathRewrite: { "^/api/auth": "/api/auth" },
          },
          "/api": {
            target: gatewayOrigin,
            changeOrigin: true,
            pathRewrite: { "^/api": "/api" },
          },
        },
  },
});
