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
		nasjonalitet: {
			statsborgerskap: '',
			utflyttet: false,
			innflyttet: false,
		},
		relasjoner: {
			sivilstand: '',
			barn: false,
			doedfoedtBarn: false,
		},
		ident: {
			ident: '',
		},
		identer: [],
		identifikasjon: {
			adressebeskyttelse: '',
			identtype: '',
			kjoenn: '',
			falskIdentitet: false,
			utenlandskIdentitet: false,
		},
		bosted: {
			kommunenr: '',
			postnr: '',
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
			kjoenn: values?.personinformasjon?.identifikasjon?.kjoenn,
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
			identifikasjon: {
				falskIdentitet: values?.personinformasjon?.identifikasjon?.falskIdentitet,
				utenlandskIdentitet: values?.personinformasjon?.identifikasjon?.utenlandskIdentitet,
				identtype: values?.personinformasjon?.identifikasjon?.identtype,
				adressebeskyttelse: values?.personinformasjon?.identifikasjon?.adressebeskyttelse,
			},
			relasjoner: {
				barn: values?.personinformasjon?.relasjoner?.barn,
				doedfoedtBarn: values?.personinformasjon?.relasjoenr?.doedfoedtBarn,
			},
			utflyttingFraNorge: {
				utflyttet: values?.personinformasjon?.nasjonalitet?.utflyttet,
			},
			innflyttingTilNorge: {
				innflytting: values?.personinformasjon?.nasjonalitet?.innflyttet,
			},
			adresser: {
				bostedsadresse: {
					postnummer: values?.personinformasjon?.bosted?.postnr,
					kommunenummer: values?.personinformasjon?.bosted?.kommunenr,
				},
			},
			personstatus: {
				status: personstatus,
			},
			tag: 'TESTNORGE',
			excludeTags: ['DOLLY'],
		}
	}
}
