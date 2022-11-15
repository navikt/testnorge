const path = require('path-browserify')
const { merge } = require('webpack-merge')
const common = require('./webpack.common.js')
const MiniCssExtractPlugin = require('mini-css-extract-plugin')

module.exports = merge(common, {
	mode: 'development',
	devtool: 'eval-source-map',
	devServer: {
		port: 3000,
		static: path.join(__dirname, 'public'),
		hot: true,
		historyApiFallback: { index: '/', disableDotRule: true },
		proxy: {
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
