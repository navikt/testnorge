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
			historyApiFallback: true,
			headers: {
				'Access-Control-Allow-Origin': '*'
			},
			proxy: {
				'/api': {
					target: env.backend,
					secure: false
				},
				'/oauth2': {
					target: env.backend,
					secure: false
				},
				'/login': {
					target: env.backend,
					secure: false
				},
				'/logout': {
					target: env.backend,
					secure: false
				},
				'/session/ping': {
					target: env.backend,
					secure: false
				},
				'/testnav-organisasjon-faste-data-service/api': {
					target: env.backend,
					changeOrigin: true,
					secure: false
				},
				'/testnav-adresse-service/api': {
					target: env.backend,
					changeOrigin: true,
					secure: false
				},
				'/testnav-joark-dokument-service/api': {
					target: env.backend,
					changeOrigin: true,
					secure: false
				},
				'/testnav-inntektstub-proxy/api': {
					target: env.backend,
					changeOrigin: true,
					secure: false
				},
				'/tps-forvalteren-proxy/api': {
					target: env.backend,
					changeOrigin: true,
					secure: false
				},
				'/testnav-hodejegeren-proxy/api': {
					target: env.backend,
					changeOrigin: true,
					secure: false
				},
				'/testnav-brregstub-proxy/api': {
					target: env.backend,
					changeOrigin: true,
					secure: false
				}
			}
		},
		plugins: [
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
