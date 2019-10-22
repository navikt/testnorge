const isProdBuild = process.env.NODE_ENV === 'production'

const config = {
	services: {
		dollyBackend: 'https://dolly-u2.nais.preprod.local',
		arenaForvalterUrl: 'https://arena-forvalteren.nais.preprod.local',
		instdataUrl: 'https://testnorge-inst.nais.preprod.local',
		kodeverkUrl: 'https://kodeverk.nais.preprod.local',
		krrStubUrl: 'https://krr-stub.nais.preprod.local',
		sigrunStubUrl: 'https://sigrun-skd-stub.nais.preprod.local',
		tpsfUrl: 'https://tps-forvalteren-u2.nais.preprod.local',
		udiStubUrl: 'https://udi-stub.nais.preprod.local'
	},
	debug: true
}

// Force values in production build
if (isProdBuild) {
	config.debug = false
}

export default config
