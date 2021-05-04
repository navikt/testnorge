const path = require("path-browserify");
const { merge } = require("webpack-merge");
const common = require("./webpack.common.js");

module.exports = merge(common, {
  mode: "production",
  devtool: "inline-source-map",
  devServer: {
    port: 3000,
    contentBase: path.join(__dirname, "public"),
    historyApiFallback: true,
    proxy: {
      "/api": {
        target: "http://localhost:8080",
        changeOrigin: true,
      },
      "/testnorge-profil-api/api": {
        target: "http://localhost:8080",
        changeOrigin: true,
      },
    },
  },
  output: {
    path: path.join(__dirname, "dist"),
    filename: "bundle.js",
    clean: true,
  },
});
