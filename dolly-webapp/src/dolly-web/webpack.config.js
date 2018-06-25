const path = require('path')
const webpack = require('webpack')
const MiniCssExtractPlugin = require('mini-css-extract-plugin')
const pkg = require('./package.json')

// Buildtype
const TARGET = process.env.npm_lifecycle_event

// Set env variable
process.env.NODE_ENV = process.env.NODE_ENV || 'development'
const devMode = process.env.NODE_ENV !== 'production'

const outputDir = {
	development: 'dist/devOutput',
	production: 'dist/output'
}

const statsOutputSettings = {
	colors: true,
	chunks: false,
	modules: false
}

const webpackConfig = {
	mode: process.env.NODE_ENV,
	devtool: 'source-map',
	entry: './src/index.js',
	output: {
		filename: 'bundle.js',
		publicPath: '/',
		path: path.join(__dirname, 'dist')
	},
	stats: statsOutputSettings,
	devServer: {
		stats: statsOutputSettings,
		contentBase: path.join(__dirname, 'public'),
		historyApiFallback: true,
		proxy: {
			'/api/v1': {
				target: 'http://localhost:8080'
			}
		}
	},
	plugins: [
		new webpack.DefinePlugin({
			'process.env': {
				NODE_ENV: JSON.stringify(process.env.NODE_ENV) || '"development"',
				CLIENT_VERSION: JSON.stringify(pkg.version) || '""'
			}
		}),
		new MiniCssExtractPlugin({
			// Options similar to the same options in webpackOptions.output
			// both options are optional
			filename: devMode ? '[name].css' : '[name].[hash].css'
		})
	],
	resolve: {
		alias: {
			'~': path.resolve(__dirname, 'src')
		}
	},
	module: {
		rules: [
			{
				test: /\.js$/,
				include: [path.resolve(__dirname, 'src')],
				exclude: [path.resolve(__dirname, 'node_modules/')],
				loader: 'babel-loader'
			},
			{
				test: /\.less$/,
				use: [
					devMode ? 'style-loader' : MiniCssExtractPlugin.loader,
					'css-loader',
					'less-loader?{"globalVars":{"nodeModulesPath":"\'~\'", "coreModulePath":"\'~\'"}}'
				]
			},
			{
				test: /\.css$/,
				use: ['style-loader', 'css-loader']
			},
			{
				// images
				test: /\.(ico|jpe?g|png|gif|woff|woff2|eot|ttf|svg)$/,
				use: ['file-loader']
			}
		]
	}
}

// If dev build
if (TARGET === 'build-dev') {
	webpackConfig.output = {
		path: path.join(__dirname, outputDir.development),
		filename: 'bundle.js',
		publicPath: '/' + outputDir.development + '/'
	}
}

// If production build
if (TARGET === 'build') {
	webpackConfig.devtool = 'none'
	webpackConfig.output = {
		path: path.join(__dirname, outputDir.production),
		filename: 'bundle.js',
		publicPath: '/' + outputDir.production + '/'
	}
}

module.exports = webpackConfig
