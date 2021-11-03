const path = require('path');
const { merge } = require('webpack-merge');
const common = require('./webpack.common.js');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');

module.exports = merge(common, {
  mode: 'development',
  devtool: 'inline-source-map',
  devServer: {
    port: 3000,
    static: path.join(__dirname, 'public'),
    historyApiFallback: true,
    proxy: {
      '/testnav-person-service': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      },
      '/testnav-organisasjon-faste-data-service': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      },
      '/testnav-person-faste-data-service': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      },
      '/testnorge-profil-api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      },
      '/testnav-organisasjon-service': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      },
      '/oauth2/authorization/aad': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      },
      '/login/oauth2/code/aad': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
      },
    },
  },
  plugins: [
    new MiniCssExtractPlugin({
      filename: '[name].css',
    }),
  ],
  output: {
    path: path.join(__dirname, 'dist'),
    filename: 'bundle.js',
  },
});
