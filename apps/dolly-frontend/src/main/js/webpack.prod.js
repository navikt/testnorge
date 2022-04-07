const path = require('path')
const { merge } = require('webpack-merge')
const common = require('./webpack.common.js')
const MiniCssExtractPlugin = require('mini-css-extract-plugin')
const OptimizeCssAssetsPlugin = require('css-minimizer-webpack-plugin')
const TerserPlugin = require('terser-webpack-plugin')

module.exports = merge(common, {
	mode: 'production',
	devtool: 'source-map',
	plugins: [
		new OptimizeCssAssetsPlugin(),
		new MiniCssExtractPlugin({
			filename: '[name].[contenthash:8].css',
		}),
	],
	output: {
		path: path.join(__dirname, 'build'),
		filename: 'bundle.[contenthash:8].js',
		publicPath: '/',
	},
	optimization: {
		minimize: true,
		minimizer: [
			new TerserPlugin({
				parallel: true,
				terserOptions: {
					keep_classnames: true,
					keep_fnames: true,
				},
			}),
		],
	},
	module: {
		rules: [
			{
				test: /\.less$/,
				use: [
					MiniCssExtractPlugin.loader,
					'css-loader',
					'less-loader?{"lessOptions":{"globalVars":{"nodeModulesPath":"\'~\'", "coreModulePath":"\'~\'"}}}',
				],
			},
			{
				test: /\.css$/,
				use: [MiniCssExtractPlugin.loader, 'css-loader'],
			},
		],
	},
})
