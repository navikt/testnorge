const path = require('path');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const OptimizeCssAssetsPlugin = require('optimize-css-assets-webpack-plugin');
const { CleanWebpackPlugin } = require('clean-webpack-plugin');

module.exports = {
  entry: {
    main: './src/index.ts',
  },
  devtool: 'inline-source-map',
  module: {
    rules: [
      {
        test: /\.css$/i,
        use: [MiniCssExtractPlugin.loader, 'css-loader'],
      },
      {
        test: /\.less$/,
        use: [MiniCssExtractPlugin.loader, 'css-loader', 'less-loader'],
      },
      {
        test: /\.png/,
        type: 'asset/inline',
      },
      {
        test: /\.svg/,
        type: 'asset/inline',
      },
      {
        test: /\.js|.ts(x?)$/,
        exclude: /node_modules/,
        loader: 'ts-loader',
      },
    ],
  },
  externals: {
    react: 'react',
  },
  resolve: {
    extensions: ['.tsx', '.ts', '.js'],
  },
  plugins: [
    new OptimizeCssAssetsPlugin({
      assetNameRegExp: /\.css$/g,
      cssProcessor: require('cssnano'),
      cssProcessorPluginOptions: {
        preset: ['default', { discardComments: { removeAll: true } }],
      },
      canPrint: true,
    }),
    new MiniCssExtractPlugin({
      filename: '[name].[contenthash:8].css',
    }),
    new CleanWebpackPlugin(),
  ],
  output: {
    filename: '[name].bundle.js',
    path: path.resolve(__dirname, 'lib'),
    library: '@navikt/dolly-komponenter',
    libraryTarget: 'umd',
  },
};
