const path = require('path')
const merge = require('webpack-merge')
const common = require('./webpack.common.js')
const webpack = require('webpack')
const MiniCssExtractPlugin = require('mini-css-extract-plugin')

module.exports = env =>
	merge(common, {
		mode: 'development',
		devtool: 'inline-source-map',
		devServer: {
			port: 3000,
			contentBase: path.join(__dirname, 'public'),
			historyApiFallback: true
		},
		plugins: [
			new webpack.EnvironmentPlugin({
				BACKEND: env.backend
			}),
			new MiniCssExtractPlugin({
				filename: '[name].css'
			})
		],
		module: {
			rules: [
				{
					test: /\.less$/,
					use: [
						'style-loader',
						'css-loader',
						'less-loader?{"globalVars":{"nodeModulesPath":"\'~\'", "coreModulePath":"\'~\'"}}'
					]
				},
				{
					test: /\.css$/,
					use: ['style-loader', 'css-loader']
				}
			]
		}
	})
