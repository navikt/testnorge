const isDevMode = process.env.NODE_ENV !== 'production'

const config = {
	services: {
		dollyBackend: '/api/v1',
		testnorgeStatiskForvalter: 'https://testnorge-statisk-data-forvalter.nais.preprod.local/api/v1'
	}
}

// !DEVELOPMENT
if (isDevMode) {
	config.services.dollyBackend = 'https://dolly-t2.nais.preprod.local/api/v1',
		config.services.testnorgeStatiskForvalter= 'https://testnorge-statisk-data-forvalter.nais.preprod.local/api/v1'
}

export default config
