const { defineConfig } = require("@vue/cli-service");
const path = require("path");

module.exports = defineConfig({
  transpileDependencies: true,
  outputDir: path.resolve(__dirname, "dist"),
  publicPath: "./",
  devServer: {
    // Forward /api and /api/auth to the Express proxy (same as production) or set to gateway URL in .env.development.local
    proxy: {
      "/api": { target: "http://localhost:3000", changeOrigin: true },
    },
  },
});
