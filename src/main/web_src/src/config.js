export default {
	services: {
		dollyBackend: (!!process.env.BACKEND ? process.env.BACKEND : '') + '/api/v1',
		proxyBackend: (!!process.env.BACKEND ? process.env.BACKEND : '') + '/api/proxy'
	}
}
