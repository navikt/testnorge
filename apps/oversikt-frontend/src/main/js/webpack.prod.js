const path = require("path-browserify");
const { merge } = require("webpack-merge");
const common = require("./webpack.common.js");

module.exports = merge(common, {
  mode: "production",
  devtool: "source-map",
  stats: "minimal",
  output: {
    filename: "[name].[contenthash:8].js",
    path: path.resolve(__dirname, "build"),
    publicPath: '/'
  },
  optimization: {
    minimize: true,
  },
});
