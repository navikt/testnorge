export const getSoekOptions = (values: any) => {
	let newSoekOptions = ''
	for (let key in values) {
		if (key === 'antallResultat') continue
		if (Object.prototype.toString.call(values[key]) === '[object Object]') {
			for (let innerKey in values[key]) {
				const value = (values[key][innerKey] + '').toUpperCase()
				if (value !== '') {
					if (newSoekOptions === '') newSoekOptions = key + '.' + innerKey + '=' + value
					else newSoekOptions = newSoekOptions + '&' + key + '.' + innerKey + '=' + value
				}
			}
		} else {
			const value = (values[key] + '').toUpperCase()
			if (value !== '') {
				if (newSoekOptions === '') newSoekOptions = key + '=' + value
				else newSoekOptions = newSoekOptions + '&' + key + '=' + value
				newSoekOptions = newSoekOptions + '&' + key + '=' + value
			}
		}
	}
	return newSoekOptions
}

export const initialValues = {
	personIdent: {
		id: '',
		type: '',
		status: ''
	},
	personInfo: {
		kjoenn: '',
		datoFoedt: ''
	},
	navn: {
		fornavn: '',
		mellomnavn: '',
		slektsnavn: ''
	},
	sivilstand: {
		type: ''
	},
	statsborger: {
		land: ''
	},
	boadresse: {
		adresse: '',
		land: '',
		kommune: '',
		postnr: ''
	},
	relasjoner: {
		rolle: ''
	},
	antallResultat: 20
}

export const infoTekst =
	'Syntetiske testdata er tilgjengelig i Dolly gjennom tekniske APIer og Mini-Norge. Mini-Norge er NAVs syntetiske basispopulasjon med egenskaper tilsvarende normalen i Norge. Antall innbyggere er pr mars 2020 ca 200 000. Befolkningen er dynamisk og det gjøres løpende endringer – nye personer fødes, folk skifter jobb osv. Mini-Norge kan brukes av alle og er godt egnet til basistester, volumtester m.m. der det ikke er behov for spesialtilfeller.\n\n' +
	'Mini-Norge er kun tilgjengelig i Q2 som er et helsyntetisk testmiljø.\n\n' +
	'Beta-versjon for søk i Mini-Norge jobbes med fortløpende.'

export const antallResultatOptions = [
	{ value: 10, label: '10' },
	{ value: 20, label: '20' },
	{ value: 30, label: '30' },
	{ value: 40, label: '40' },
	{ value: 50, label: '50' },
	{ value: 60, label: '60' },
	{ value: 70, label: '70' },
	{ value: 80, label: '80' },
	{ value: 90, label: '90' },
	{ value: 100, label: '100' }
]
