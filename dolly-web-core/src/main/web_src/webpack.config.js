const path = require('path')
const webpack = require('webpack')
const HtmlWebpackPlugin = require('html-webpack-plugin')
const MiniCssExtractPlugin = require('mini-css-extract-plugin')
const OptimizeCssAssetsPlugin = require('optimize-css-assets-webpack-plugin')
const CleanWebpackPlugin = require('clean-webpack-plugin')
const Dotenv = require('dotenv-webpack')
const pkg = require('./package.json')
const GitRevisionPlugin = require('git-revision-webpack-plugin')

const gitRevisionPlugin = new GitRevisionPlugin({
	commithashCommand: 'rev-parse --short HEAD'
})

// Buildtype
const TARGET = process.env.npm_lifecycle_event

// Set env variable
process.env.NODE_ENV = process.env.NODE_ENV || 'development'
const devMode = process.env.NODE_ENV !== 'production'

const outputDir = {
	development: 'dist/dev',
	production: '../../../target/classes/public'
}

const corsHeaders = {
	"Access-Control-Allow-Origin": "*",
	"Access-Control-Allow-Methods": "GET, POST, PUT, DELETE, PATCH, OPTIONS",
	"Access-Control-Allow-Headers": "content-type, Authorization"
  };

const webpackConfig = {
	mode: process.env.NODE_ENV,
	devtool: 'source-map',
	entry: ['babel-polyfill', './src/index.js'],
	output: {
		filename: 'bundle.js',
		publicPath: '/',
		path: path.join(__dirname, outputDir.development)
	},
	stats: 'minimal',
	devServer: {
		stats: 'minimal',
		contentBase: path.join(__dirname, 'public'),
		historyApiFallback: true,
		proxy: {
			"/api": {
				secure: false,
				target: 'https://dolly-web-u2.nais.preprod.local', 
				headers: corsHeaders,
				changeOrigin: true,
			}
		}
	},
	plugins: [
		new webpack.DefinePlugin({
			'process.env': {
				NODE_ENV: JSON.stringify(process.env.NODE_ENV) || '"development"'
			},
			BUILD: {
				VERSION: JSON.stringify(pkg.version) || '""',
				COMMITHASH: JSON.stringify(gitRevisionPlugin.commithash()),
				BRANCH: JSON.stringify(gitRevisionPlugin.branch())
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
			appMountId: 'root',
			lang: 'nb'
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
				use: [devMode ? 'style-loader' : MiniCssExtractPlugin.loader, 'css-loader']
			},
			{
				// images
				test: /\.(ico|jpe?g|png|gif|woff|woff2|eot|otf|ttf|svg)$/,
				use: ['file-loader']
			}
		]
	}
}

// If webpack dev server
if (TARGET === 'start') {
	webpackConfig.plugins = [
		new Dotenv({
			path: path.resolve(__dirname, '.env.utv'),
			systemvars: true
		})
	].concat(webpackConfig.plugins)
}

// If dev build
if (TARGET === 'build-dev') {
	webpackConfig.output = {
		path: path.join(__dirname, outputDir.development),
		filename: 'bundle.js',
		publicPath: '/'
	}
	webpackConfig.plugins = [
		new CleanWebpackPlugin([outputDir.development]),
		new Dotenv({
			path: path.resolve(__dirname, '.env.utv'),
			systemvars: true
		})
	].concat(webpackConfig.plugins)
}

// If production build
if (TARGET === 'build') {
	webpackConfig.devtool = 'none'
	webpackConfig.stats = 'normal'
	webpackConfig.output = {
		path: path.join(__dirname, outputDir.production),
		filename: 'bundle.[contenthash:8].js',
		publicPath: '/'
	}
	webpackConfig.plugins = [
		new CleanWebpackPlugin([outputDir.production]),
		new Dotenv({
			path: path.resolve(__dirname, '.env.prod'),
			systemvars: true
		})
	]
		.concat(webpackConfig.plugins)
		.concat([
			new OptimizeCssAssetsPlugin({
				assetNameRegExp: /\.css$/g,
				cssProcessor: require('cssnano'),
				cssProcessorPluginOptions: {
					preset: ['default', { discardComments: { removeAll: true } }]
				},
				canPrint: true
			})
		])
}

module.exports = webpackConfig
