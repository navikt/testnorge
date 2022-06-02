import { PdlData } from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'

export const initialValues = {
	alder: {
		fra: '',
		til: '',
	},
	foedsel: {
		fom: '',
		tom: '',
	},
	kjoenn: '',
	nasjonalitet: {
		statsborgerskap: '',
		utflyttingFraNorge: false,
		innflyttingTilNorge: false,
		innflytting: {
			fraflyttingsland: '',
			historiskFraflyttingsland: '',
		},
		utflytting: {
			tilflyttingsland: '',
			historiskTilflyttingsland: '',
		},
	},
	sivilstand: {
		type: '',
	},
	relasjoner: {
		harBarn: '',
		harDoedfoedtBarn: '',
		forelderBarnRelasjoner: [] as string[],
		foreldreansvar: '',
	},
	identer: [''],
	identifikasjon: {
		adressebeskyttelse: '',
		identtype: '',
		falskIdentitet: false,
		utenlandskIdentitet: false,
	},
	adresser: {
		bostedsadresse: {
			borINorge: '',
			postnummer: '',
			bydelsnummer: '',
			kommunenummer: '',
			historiskPostnummer: '',
			historiskBydelsnummer: '',
			historiskKommunenummer: '',
		},
		harUtenlandskAdresse: '',
		harKontaktadresse: '',
		harOppholdsadresse: '',
	},
	personstatus: {
		status: '',
	},
}

const fellesSearchValues = {
	page: 1,
	pageSize: 100,
	terminateAfter: 100,
	tag: 'TESTNORGE',
	excludeTags: ['DOLLY'],
}

export const getSearchValues = (randomSeed: string, values: any) => {
	const identer = values?.identer ? [...values.identer].filter((n) => n) : []
	const kunLevende = values?.personstatus?.status !== 'DOED'

	if (identer.length > 0) {
		return Object.assign({}, fellesSearchValues, { identer: identer, randomSeed: randomSeed })
	} else {
		const searchValues = Object.assign(
			{},
			JSON.parse(JSON.stringify(fellesSearchValues)),
			JSON.parse(JSON.stringify(values))
		)

		searchValues['randomSeed'] = randomSeed
		searchValues['kunLevende'] = kunLevende
		if (searchValues.relasjoner.harBarn === 'M') {
			searchValues.relasjoner.harBarn = 'Y'
		}

		delete searchValues.identer
		return searchValues
	}
}

export const yesNoOptions = [
	{ value: 'Y', label: 'Ja' },
	{ value: 'N', label: 'Nei' },
]

export const getPdlIdent = (person: PdlData) => {
	const identer = person.hentIdenter?.identer?.filter(
		(ident) => ident.gruppe === 'FOLKEREGISTERIDENT'
	)
	return identer.length > 0 ? identer[0].ident : ''
}
