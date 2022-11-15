const path = require('path-browserify')
const HtmlWebpackPlugin = require('html-webpack-plugin')
const MiniCssExtractPlugin = require('mini-css-extract-plugin')
const { CleanWebpackPlugin } = require('clean-webpack-plugin')

module.exports = {
	module: {
		rules: [
			{
				test: /\.less$/i,
				use: [
					{
						loader: MiniCssExtractPlugin.loader,
					},
					{
						loader: 'css-loader',
					},
					{
						loader: 'less-loader',
					},
				],
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
			{
				test: /\.(ico|jpe?g|png|svg|gif|woff|woff2|eot|otf|ttf)$/,
				exclude: /node_modules/,
				type: 'asset/inline',
			},
			{
				test: /\.js|.ts(x?)$/,
				exclude: /node_modules/,
				use: {
					loader: 'babel-loader',
				},
			},
		],
	},
	resolve: {
		alias: {
			stream: 'stream-browserify',
			'~': path.resolve(__dirname, 'src'),
			'@': path.resolve(__dirname, 'src'),
			lessVars: path.resolve(__dirname, 'src/styles/variables.less'),
			lessUtils: path.resolve(__dirname, 'src/styles/utils.less'),
		},
		extensions: ['.tsx', '.ts', '.js'],
	},
	externals: {
		'@navikt/ds-css': {
			commonjs: '@navikt/ds-css',
			commonjs2: '@navikt/ds-css',
			amd: '@navikt/ds-css',
			root: '@navikt/ds-css',
		},
	},
	plugins: [
		new CleanWebpackPlugin(),
		new MiniCssExtractPlugin({
			filename: '[name].[contenthash:8].css',
		}),
		new HtmlWebpackPlugin({
			template: path.resolve(__dirname, './public/index.html'),
			filename: 'index.html',
		}),
	],
}
