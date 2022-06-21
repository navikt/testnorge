const path = require('path');
const CssMinimizerPlugin = require('css-minimizer-webpack-plugin');

module.exports = {
  stories: ['../src/**/*.stories.mdx', '../src/**/*.stories.@(js|jsx|ts|tsx)'],
  addons: ['@storybook/addon-links', '@storybook/addon-essentials'],
  optimization: new CssMinimizerPlugin(),
  webpackFinal: async (config) => {
    config.module.rules.push({
      test: /\.css$/i,
      use: ['css-loader'],
    });
    config.module.rules.push({
      test: /\.less$/,
      use: ['style-loader', 'css-loader', 'less-loader'],
    });
    config.resolve.extensions.push('.ts', '.tsx');
    return config;
  },
};
