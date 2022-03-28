const path = require('path')
const merge = require('webpack-merge')
const common = require('./webpack.common.js')
const MiniCssExtractPlugin = require('mini-css-extract-plugin')

module.exports = (env) =>
	merge(common, {
		mode: 'development',
		devtool: 'inline-source-map',
		resolve: {
			alias: {
				'react-dom$': 'react-dom/profiling',
			},
		},
		devServer: {
			static: path.join(__dirname, 'dist/dev'),
			compress: true,
			port: 3000,
			open: {
				target: [`http://localhost:3000/login`],
				app: {
					name: 'Google Chrome',
				},
			},
			historyApiFallback: true,
			client: {
				overlay: {
					errors: true,
					warnings: true,
				},
			},
			headers: {
				'Access-Control-Allow-Origin': '*',
			},
			proxy: {
				'/api': {
					target: env.backend,
					secure: false,
				},
				'/oauth2/authorization/aad': {
					target: env.backend,
					secure: false,
				},
				'/oauth2/authorization/idporten': {
					target: env.backend,
					secure: false,
				},
				'/login/oauth2/code/aad': {
					target: env.backend,
					secure: false,
				},
				'/login/oauth2/code/idporten': {
					target: env.backend,
					secure: false,
				},
				'/logout': {
					target: env.backend,
					secure: false,
				},
				'/oauth2/logout': {
					target: env.backend,
					secure: false,
				},
				'/session/ping': {
					target: env.backend,
					secure: false,
				},
				'/session/user': {
					target: env.backend,
					secure: false,
				},
				'/testnav-organisasjon-faste-data-service/api': {
					target: env.backend,
					changeOrigin: true,
					secure: false,
				},
				'/testnav-tps-messaging-service/api': {
					target: env.backend,
					changeOrigin: true,
					secure: false,
				},
				'/testnav-adresse-service/api': {
					target: env.backend,
					changeOrigin: true,
					secure: false,
				},
				'/testnav-joark-dokument-service/api': {
					target: env.backend,
					changeOrigin: true,
					secure: false,
				},
				'/testnav-inntektstub-proxy/api': {
					target: env.backend,
					changeOrigin: true,
					secure: false,
				},
				'/testnav-norg2-proxy/norg2': {
					target: env.backend,
					changeOrigin: true,
					secure: false,
				},
				'/tps-forvalteren-proxy/api': {
					target: env.backend,
					changeOrigin: true,
					secure: false,
				},
				'/testnav-brregstub-proxy/api': {
					target: env.backend,
					changeOrigin: true,
					secure: false,
				},
				'/testnav-arena-forvalteren-proxy/api': {
					target: env.backend,
					changeOrigin: true,
					secure: false,
				},
				'/testnav-testnorge-aareg-proxy/api': {
					target: env.backend,
					changeOrigin: true,
					secure: false,
				},
				'/testnav-krrstub-proxy/api': {
					target: env.backend,
					changeOrigin: true,
					secure: false,
				},
				'/testnav-testnorge-inst-proxy/api': {
					target: env.backend,
					changeOrigin: true,
					secure: false,
				},
				'/testnav-sigrunstub-proxy/api': {
					target: env.backend,
					changeOrigin: true,
					secure: false,
				},
				'/testnav-pensjon-testdata-facade-proxy/api': {
					target: env.backend,
					changeOrigin: true,
					secure: false,
				},
				'/dolly-backend/api': {
					target: env.backend,
					changeOrigin: true,
					secure: false,
				},
				'/testnorge-profil-api/api': {
					target: env.backend,
					changeOrigin: true,
					secure: false,
				},
				'/testnav-varslinger-service/api': {
					target: env.backend,
					changeOrigin: true,
					secure: false,
				},
				'/testnav-organisasjon-forvalter/api': {
					target: env.backend,
					changeOrigin: true,
					secure: false,
				},
				'/testnav-organisasjon-service/api': {
					target: env.backend,
					changeOrigin: true,
					secure: false,
				},
				'/testnav-miljoer-service/api': {
					target: env.backend,
					changeOrigin: true,
					secure: false,
				},
				'/udi-stub/api': {
					target: env.backend,
					changeOrigin: true,
					secure: false,
				},
				'/person-search-service/api': {
					target: env.backend,
					changeOrigin: true,
					secure: false,
				},
				'/testnav-person-organisasjon-tilgang-service/api': {
					target: env.backend,
					changeOrigin: true,
					secure: false,
				},
				'/testnav-pdl-forvalter/api': {
					target: env.backend,
					changeOrigin: true,
					secure: false,
				},
				'/testnav-bruker-service/api': {
					target: env.backend,
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
		module: {
			rules: [
				{
					test: /\.less$/,
					use: [
						'style-loader',
						'css-loader',
						'less-loader?{"lessOptions":{"globalVars":{"nodeModulesPath":"\'~\'", "coreModulePath":"\'~\'"}}}',
					],
				},
				{
					test: /\.css$/,
					use: ['style-loader', 'css-loader'],
				},
			],
		},
	})
