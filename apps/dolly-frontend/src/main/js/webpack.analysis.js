const prod = require('./webpack.prod.js')
const { merge } = require('webpack-merge')
const { BundleAnalyzerPlugin } = require('webpack-bundle-analyzer')

module.exports = merge(prod, {
	stats: 'normal',
	plugins: [
		new BundleAnalyzerPlugin({
			analyzerPort: 'auto',
		}),
	],
})
