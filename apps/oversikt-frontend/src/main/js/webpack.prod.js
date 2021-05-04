const path = require("path-browserify");
const { merge } = require("webpack-merge");
const common = require("./webpack.common.js");
const MiniCssExtractPlugin = require("mini-css-extract-plugin");

module.exports = merge(common, {
  mode: "production",
  devtool: "source-map",
  stats: "minimal",
  output: {
    filename: "bundle.[contenthash:8].js",
    path: path.resolve(__dirname, "build"),
  },
  optimization: {
    minimize: true,
  },
  plugins: [
    new MiniCssExtractPlugin({
      filename: "[fullhash]-[name].css",
    }),
  ],
});
