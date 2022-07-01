const path = require('path');
const { merge } = require('webpack-merge');
const common = require('./webpack.common.js');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const OptimizeCssAssetsPlugin = require('css-minimizer-webpack-plugin');

module.exports = merge(common, {
  mode: 'production',
  devtool: 'source-map',
  stats: 'minimal',
  plugins: [
    new OptimizeCssAssetsPlugin(),
    new MiniCssExtractPlugin({
      filename: '[name].[contenthash:8].css',
    }),
  ],
  output: {
    filename: 'bundle.[contenthash:8].js',
    path: path.resolve(__dirname, 'build'),
    publicPath: '/',
    clean: true,
  },
  optimization: {
    minimize: true,
  },
});
