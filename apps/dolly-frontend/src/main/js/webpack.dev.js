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
				},
				'/testnav-arena-forvalteren-proxy/api': {
					target: env.backend,
					changeOrigin: true,
					secure: false
				},
				'/testnav-testnorge-aareg-proxy/api': {
					target: env.backend,
					changeOrigin: true,
					secure: false
				},
				'/testnav-krrstub-proxy/api': {
					target: env.backend,
					changeOrigin: true,
					secure: false
				},
				'/testnav-testnorge-inst-proxy/api': {
					target: env.backend,
					changeOrigin: true,
					secure: false
				},
				'/testnav-sigrunstub-proxy/api': {
					target: env.backend,
					changeOrigin: true,
					secure: false
				},
				'/testnav-pensjon-testdata-facade-proxy/api': {
					target: env.backend,
					changeOrigin: true,
					secure: false
				},
				'/dolly-backend/api': {
					target: env.backend,
					changeOrigin: true,
					secure: false
				},
				'/testnorge-profil-api/api': {
					target: env.backend,
					changeOrigin: true,
					secure: false
				},
				'/testnorge-varslinger-api/api': {
					target: env.backend,
					changeOrigin: true,
					secure: false
				},
				'/testnav-organisasjon-forvalter/api': {
					target: env.backend,
					changeOrigin: true,
					secure: false
				},
				'/testnav-organisasjon-service/api': {
					target: env.backend,
					changeOrigin: true,
					secure: false
				},
				'/testnav-miljoer-service/api': {
					target: env.backend,
					changeOrigin: true,
					secure: false
				},
				'/udi-stub/api': {
					target: env.backend,
					changeOrigin: true,
					secure: false
				},
				'/person-search-service/api': {
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
