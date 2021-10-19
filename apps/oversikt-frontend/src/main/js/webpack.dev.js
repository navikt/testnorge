const path = require('path-browserify')
const { merge } = require('webpack-merge')
const common = require('./webpack.common.js')
const MiniCssExtractPlugin = require('mini-css-extract-plugin')

module.exports = merge(common, {
	mode: 'development',
	devtool: 'eval-source-map',
	devServer: {
		port: 3000,
		contentBase: path.join(__dirname, 'public'),
		hot: true,
		writeToDisk: false,
		historyApiFallback: { index: '/', disableDotRule: true },
		proxy: {
			'/api': {
				target: 'http://localhost:8080',
				changeOrigin: true,
			},
			'/testnorge-profil-api/api': {
				target: 'http://localhost:8080',
				changeOrigin: true,
			},
			'/testnav-person-organisasjon-tilgang-service/api': {
				target: 'http://localhost:8080',
				changeOrigin: true,
			},
			'/testnav-bruker-service/api': {
				target: 'http://localhost:8080',
				changeOrigin: true,
			},
			'/oauth2/authorization/aad': {
				target: 'http://localhost:8080',
				changeOrigin: true,
				secure: false,
			},
			'/oauth2/authorization/idporten': {
				target: 'http://localhost:8080',
				changeOrigin: true,
				secure: false,
			},
		},
	},
	plugins: [new MiniCssExtractPlugin()],
	output: {
		path: path.join(__dirname, 'dist'),
		filename: 'bundle.js',
		publicPath: '/',
		clean: true,
	},
})
