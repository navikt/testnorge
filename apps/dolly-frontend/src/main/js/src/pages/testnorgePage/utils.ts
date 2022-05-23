import { PdlData } from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { getKjoenn } from '~/ducks/fagsystem'

export const initialValues = {
	alder: {
		fra: '',
		til: '',
		foedselsdato: {
			fom: '',
			tom: '',
		},
	},
	kjoenn: '',
	nasjonalitet: {
		statsborgerskap: '',
		utflyttingFraNorge: false,
		innflyttingTilNorge: false,
	},
	relasjoner: {
		sivilstand: '',
		barn: '',
		harBarn: '',
		harDoedfoedtBarn: '',
		forelderBarnRelasjoner: [] as string[],
		foreldreansvar: '',
	},
	ident: {
		ident: '',
	},
	identer: [] as string[],
	identifikasjon: {
		adressebeskyttelse: '',
		identtype: '',
		falskIdentitet: false,
		utenlandskIdentitet: false,
	},
	adresser: {
		bostedsadresse: {
			borINorge: '',
			kommunenummer: '',
			historiskKommunenummer: '',
			postnummer: '',
		},
		harUtenlandskAdresse: '',
		harKontaktadresse: '',
		harOppholdsadresse: '',
	},
	personstatus: '',
}

export const getSearchValues = (randomSeed: string, values: any) => {
	let identer = values?.identer ? [...values.identer] : []
	if (values?.ident?.ident) {
		identer.push(values?.ident?.ident)
		identer = identer.filter((item: string) => item)
	}

	const personstatus = values?.personstatus
	let kunLevende = true
	if (personstatus === 'DOED' || values?.adresser?.kontaktadresse?.kontaktadresseForDoedsbo) {
		kunLevende = false
	}

	if (identer.length > 0) {
		return {
			page: 1,
			pageSize: 100,
			randomSeed: randomSeed,
			terminateAfter: 100,
			identer: identer,
			tag: 'TESTNORGE',
			excludeTags: ['DOLLY'],
		}
	} else {
		return {
			page: 1,
			pageSize: 100,
			randomSeed: randomSeed,
			terminateAfter: 100,
			kunLevende: kunLevende,
			kjoenn: values?.kjoenn,
			foedsel: {
				fom: values?.alder?.foedselsdato?.fom,
				tom: values?.alder?.foedselsdato?.tom,
			},
			nasjonalitet: values?.nasjonalitet,
			sivilstand: {
				type: values?.relasjoner?.sivilstand,
			},
			alder: {
				fra: values?.alder?.fra,
				til: values?.alder?.til,
			},
			identer: identer,
			identifikasjon: values?.identifikasjon,
			relasjoner: {
				sivilstand: values?.relasjoner?.sivilstand,
				harDoedfoedtBarn: values?.relasjoner?.harDoedfoedtBarn,
				forelderBarnRelasjoner: values?.relasjoner?.forelderBarnRelasjoner,
				barn: values?.relasjoner?.barn,
				harBarn: values?.relasjoner?.barn === 'M' ? 'Y' : values?.relasjoner?.barn,
				foreldreansvar: values?.relasjoner?.foreldreansvar,
			},
			adresser: values?.adresser,
			personstatus: {
				status: personstatus,
			},
			tag: 'TESTNORGE',
			excludeTags: ['DOLLY'],
		}
	}
}

export const yesNoOptions = [
	{ value: 'Y', label: 'Ja' },
	{ value: 'N', label: 'Nei' },
]

export const getIdent = (person: PdlData) => {
	const identer = person.hentIdenter?.identer?.filter(
		(ident) => ident.gruppe === 'FOLKEREGISTERIDENT'
	)
	return identer.length > 0 ? identer[0].ident : ''
}

export const getFornavn = (person: PdlData) => {
	const navn = person.hentPerson?.navn.filter((personNavn) => !personNavn.metadata.historisk)
	return navn.length > 0 ? navn[0].fornavn : ''
}

export const getEtternavn = (person: PdlData) => {
	const navn = person.hentPerson?.navn.filter((personNavn) => !personNavn.metadata.historisk)
	return navn.length > 0 ? navn[0].etternavn : ''
}

export const getPdlKjoenn = (person: PdlData) => {
	return person.hentPerson?.kjoenn[0] ? getKjoenn(person.hentPerson?.kjoenn[0].kjoenn) : 'U'
}
