const path = require('path')
const HtmlWebpackPlugin = require('html-webpack-plugin')
const webpack = require('webpack')
const GitRevisionPlugin = require('git-revision-webpack-plugin')
const { CleanWebpackPlugin } = require('clean-webpack-plugin')
const pkg = require('./package.json')
const ESLintPlugin = require('eslint-webpack-plugin')

require('core-js/stable')
require('regenerator-runtime/runtime')

const gitRevisionPlugin = new GitRevisionPlugin({
	commithashCommand: 'rev-parse --short HEAD'
})

module.exports = {
	entry: [
		'whatwg-fetch',
		'events-polyfill',
		'core-js/stable',
		'regenerator-runtime/runtime',
		'./src/index.js'
	],

	output: {
		path: path.join(__dirname, 'dist/dev'),
		filename: 'bundle.js',
		publicPath: '/'
	},
	stats: 'minimal',
	plugins: [
		new HtmlWebpackPlugin({
			title: 'Dolly',
			favicon: 'src/assets/favicon.ico',
			inject: false,
			template: require('html-webpack-template'),
			appMountIds: ['root', 'react-select-root'],
			lang: 'nb'
		}),
		new ESLintPlugin(),
		new webpack.DefinePlugin({
			BUILD: {
				VERSION: JSON.stringify(pkg.version) || '""',
				COMMITHASH: JSON.stringify(gitRevisionPlugin.commithash()),
				BRANCH: JSON.stringify(gitRevisionPlugin.branch())
			}
		}),
		new CleanWebpackPlugin()
	],
	resolve: {
		alias: {
			'~': path.resolve(__dirname, 'src'),
			'@': path.resolve(__dirname, 'src'),
			lessVars: path.resolve(__dirname, 'src/styles/variables.less'),
			lessUtils: path.resolve(__dirname, 'src/styles/utils.less')
		},
		extensions: ['.js', '.json', '.ts', '.tsx']
	},
	module: {
		rules: [
			{
				test: /\.js|.ts(x?)$/,
				include: path.resolve(__dirname, 'src'),
				exclude: /node_modules/,
				use: ['babel-loader']
			},
			{
				test: /\.svg$/,
				loader: 'svg-inline-loader'
			},
			{
				// images
				test: /\.(ico|jpe?g|png|gif|woff|woff2|eot|otf|ttf)$/,
				use: ['file-loader']
			}
		]
	}
}
