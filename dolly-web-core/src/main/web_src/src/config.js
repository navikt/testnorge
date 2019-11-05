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
	systems: {
		AAREG: 'Arbeidsregister (AAREG)',
		TPSF: 'Tjenestebasert personsystem (TPS)',
		INST2: 'Institusjonsopphold (INST2)',
		KRRSTUB: 'Digital kontaktinformasjon (DKIF)',
		SIGRUNSTUB: 'Skatteinntekt grunnlag (SIGRUN)',
		ARENA: 'Arena fagsystem',
		PDL_FORVALTER: 'Persondataløsningen (PDL)',
		PDL_FALSKID: 'Falsk identitet (PDL)',
		PDL_DODSBO: 'Kontaktinformasjon dødsbo (PDL)',
		PDL_UTENLANDSID: 'Utenlandsk id (PDL)',
		UDISTUB: 'Utlendingsdirektoratet (UDI)'
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
