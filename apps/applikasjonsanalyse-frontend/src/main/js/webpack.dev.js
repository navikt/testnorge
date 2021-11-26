const path = require('path-browserify');
const { merge } = require('webpack-merge');
const common = require('./webpack.common.js');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');

module.exports = merge(common, {
  mode: 'development',
  devtool: 'inline-source-map',
  devServer: {
    port: 3000,
    static: path.join(__dirname, 'public'),
    hot: true,
    proxy: {
      '/applikasjonsanalyse-service/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      },
      '/testnorge-profil-api/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      },
      '/oauth2/authorization/aad': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      },
    },
  },
  plugins: [new MiniCssExtractPlugin()],
  output: {
    path: path.join(__dirname, 'build'),
    filename: 'bundle.js',
  },
});
