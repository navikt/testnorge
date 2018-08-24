const path = require('path')
const webpack = require('webpack')
const HtmlWebpackPlugin = require('html-webpack-plugin')
const MiniCssExtractPlugin = require('mini-css-extract-plugin')
const CleanWebpackPlugin = require('clean-webpack-plugin')
const pkg = require('./package.json')

// Buildtype
const TARGET = process.env.npm_lifecycle_event

// Set env variable
process.env.NODE_ENV = process.env.NODE_ENV || 'development'
const devMode = process.env.NODE_ENV !== 'production'

const outputDir = {
	development: 'dist/dev',
    production: './../resources/public'
	// production: 'dist/production'
}

const statsOutputSettings = {
	colors: true,
	chunks: false,
	modules: false
}

const webpackConfig = {
	mode: process.env.NODE_ENV,
	devtool: 'source-map',
	entry: ['babel-polyfill', './src/index.js'],
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
			filename: devMode ? '[name].css' : '[name].[contenthash:8].css'
		}),
		new HtmlWebpackPlugin({
			title: 'Dolly',
			favicon: 'src/assets/favicon.ico',
			inject: false,
			template: require('html-webpack-template'),
			appMountId: 'root'
		})
	],
	resolve: {
		alias: {
			'~': path.resolve(__dirname, 'src'),
			lessVars: path.resolve(__dirname, 'src/styles/variables.less'),
			lessUtils: path.resolve(__dirname, 'src/styles/utils.less')
		},
		extensions: ['.js', '.json', '.ts', '.tsx']
	},
	module: {
		rules: [
			{
				test: /\.ts(x?)$/,
				exclude: /node_modules/,
				use: ['babel-loader', 'ts-loader']
			},
			{
				test: /\.js$/,
				exclude: /node_modules/,
				use: ['babel-loader']
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
				test: /\.(ico|jpe?g|png|gif|woff|woff2|eot|otf|ttf|svg)$/,
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
		publicPath: '/'
	}
}

// If production build
if (TARGET === 'build') {
	webpackConfig.devtool = 'none'
	webpackConfig.output = {
		path: path.join(__dirname, outputDir.production),
		filename: 'bundle.[contenthash:8].js',
		publicPath: '/'
	}
	webpackConfig.plugins = [new CleanWebpackPlugin(['dist'])].concat(webpackConfig.plugins)
}

module.exports = webpackConfig
