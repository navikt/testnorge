const path = require('path')
const HtmlWebpackPlugin = require('html-webpack-plugin')
const webpack = require('webpack')
const { GitRevisionPlugin } = require('git-revision-webpack-plugin')
const { CleanWebpackPlugin } = require('clean-webpack-plugin')
const pkg = require('./package.json')
const ESLintPlugin = require('eslint-webpack-plugin')

require('core-js/stable')
require('regenerator-runtime/runtime')

const gitRevisionPlugin = new GitRevisionPlugin({
	commithashCommand: 'rev-parse --short HEAD',
})

module.exports = {
	entry: [
		'whatwg-fetch',
		'events-polyfill',
		'core-js/stable',
		'regenerator-runtime/runtime',
		'./src/index.js',
	],
	output: {
		path: path.join(__dirname, 'dist/dev'),
		filename: 'bundle.js',
		publicPath: '/',
	},
	stats: 'minimal',
	plugins: [
		new HtmlWebpackPlugin({
			title: 'Dolly',
			favicon: 'src/assets/favicon.ico',
			inject: true,
			template: 'src/assets/index.html',
			lang: 'nb',
		}),
		new ESLintPlugin(),
		new webpack.DefinePlugin({
			BUILD: {
				VERSION: JSON.stringify(pkg.version) || '""',
				COMMITHASH: JSON.stringify(gitRevisionPlugin.commithash()),
				BRANCH: JSON.stringify(gitRevisionPlugin.branch()),
			},
		}),
		new CleanWebpackPlugin(),
	],
	resolve: {
		alias: {
			'~': path.resolve(__dirname, 'src'),
			'@': path.resolve(__dirname, 'src'),
			lessVars: path.resolve(__dirname, 'src/styles/variables.less'),
			lessUtils: path.resolve(__dirname, 'src/styles/utils.less'),
		},
		extensions: ['.js', '.json', '.ts', '.tsx'],
	},
	externals: {
		'@navikt/ds-css': {
			commonjs: '@navikt/ds-css',
			commonjs2: '@navikt/ds-css',
			amd: '@navikt/ds-css',
			root: '@navikt/ds-css',
		},
	},
	module: {
		rules: [
			{
				test: /\.js|.ts(x?)$/,
				include: path.resolve(__dirname, 'src'),
				exclude: /node_modules/,
				use: ['babel-loader?sourceMap&cacheDirectory'],
			},
			{
				test: /\.svg$/,
				loader: 'svg-inline-loader',
			},
			{
				// images
				test: /\.(ico|jpe?g|png|gif|woff|woff2|eot|otf|ttf)$/,
				use: ['file-loader'],
			},
			{
				test: /\.s[ac]ss$/i,
				use: [
					// Creates `style` nodes from JS strings
					'style-loader',
					// Translates CSS into CommonJS
					'css-loader',
					// Compiles Sass to CSS
					'sass-loader',
				],
			},
		],
	},
}
