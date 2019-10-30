const isDevMode = process.env.NODE_ENV !== 'production'

const config = {
	services: {
		dollyBackend: '/api/v1',
		arenaForvalterUrl: 'https://arena-forvalteren.nais.preprod.local',
		instdataUrl: 'https://testnorge-inst.nais.preprod.local',
		kodeverkUrl: 'https://kodeverk.nais.preprod.local',
		krrStubUrl: 'https://krr-stub.nais.preprod.local',
		sigrunStubUrl: 'https://sigrun-skd-stub.nais.preprod.local',
		tpsfUrl: 'https://tps-forvalteren-u2.nais.preprod.local',
		udiStubUrl: 'https://udi-stub.nais.preprod.local'
	},
	debug: false
}

// !DEVELOPMENT
if (isDevMode) {
	config.debug = true
	config.services.dollyBackend = 'https://dolly-u2.nais.preprod.local/api/v1'
	// config.services.dollyBackend = 'http://localhost:8080'
}

export default config
