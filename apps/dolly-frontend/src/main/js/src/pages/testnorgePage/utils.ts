import { PdlData } from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { getKjoenn } from '~/ducks/fagsystem'

export const initialValues = {
	personinformasjon: {
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
			utflyttet: false,
			innflyttet: false,
		},
		relasjoner: {
			sivilstand: '',
			harBarn: '',
			harDoedfoedtBarn: '',
			forelderBarnRelasjoner: [],
		},
		ident: {
			ident: '',
		},
		identer: [],
		identifikasjon: {
			adressebeskyttelse: '',
			identtype: '',
			falskIdentitet: false,
			utenlandskIdentitet: false,
		},
		adresser: {
			bostedsadresse: {
				kommunenummer: '',
				postnummer: '',
			},
			kontaktadresse: {
				norskAdresse: false,
				utenlanskAdresse: false,
			},
			oppholdsadresse: {
				norskAdresse: false,
				utenlanskAdresse: false,
				oppholdAnnetSted: '',
			},
		},
		personstatus: '',
	},
}

export const getSearchValues = (randomSeed: string, values: any) => {
	let identer = values?.personinformasjon?.identer ? [...values.personinformasjon.identer] : []
	if (values?.personinformasjon?.ident?.ident) {
		identer.push(values?.personinformasjon?.ident?.ident)
		identer = identer.filter((item: string) => item)
	}

	const personstatus = values?.personinformasjon?.personstatus
	const kunLevende = personstatus === null || personstatus.isEmpty || personstatus !== 'DOED'

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
			kjoenn: values?.personinformasjon?.kjoenn,
			foedsel: {
				fom: values?.personinformasjon?.alder?.foedselsdato?.fom,
				tom: values?.personinformasjon?.alder?.foedselsdato?.tom,
			},
			statsborgerskap: {
				land: values?.personinformasjon?.nasjonalitet?.statsborgerskap,
			},
			sivilstand: {
				type: values?.personinformasjon?.relasjoner?.sivilstand,
			},
			alder: {
				fra: values?.personinformasjon?.alder?.fra,
				til: values?.personinformasjon?.alder?.til,
			},
			identer: identer,
			identifikasjon: values?.personinformasjon?.identifikasjon,
			relasjoner: values?.personinformasjon?.relasjoner,
			utflyttingFraNorge: {
				utflyttet: values?.personinformasjon?.nasjonalitet?.utflyttet,
			},
			innflyttingTilNorge: {
				innflytting: values?.personinformasjon?.nasjonalitet?.innflyttet,
			},
			adresser: values?.personinformasjon?.adresser,
			personstatus: {
				status: personstatus,
			},
			tag: 'TESTNORGE',
			excludeTags: ['DOLLY'],
		}
	}
}

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
