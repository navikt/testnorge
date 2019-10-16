const isProdBuild = process.env.NODE_ENV === 'production'

const config = {
	services: {
		dollyBackend: '/api/v1',
		tpsf: process.env.tpsf || '/external/tpsf/api/v1'
	},
	debug: true
}

// Force values in production build
if (isProdBuild) {
	config.debug = false
}

export default config
