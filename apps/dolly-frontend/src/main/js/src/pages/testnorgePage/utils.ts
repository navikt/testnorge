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
		sivilstand: {
			type: '',
		},
		barn: {
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
	},
}

export const getSearchValues = (randomSeed: string, values: any) => {
	let identer = values?.personinformasjon?.identer ? [...values.personinformasjon.identer] : []
	if (values?.personinformasjon?.ident?.ident) {
		identer.push(values?.personinformasjon?.ident?.ident)
		identer = identer.filter((item: string) => item)
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
			kjoenn: values?.personinformasjon?.identifikasjon?.kjoenn,
			foedsel: {
				fom: values?.personinformasjon?.alder?.foedselsdato?.fom,
				tom: values?.personinformasjon?.alder?.foedselsdato?.tom,
			},
			statsborgerskap: {
				land: values?.personinformasjon?.nasjonalitet?.statsborgerskap,
			},
			sivilstand: {
				type: values?.personinformasjon?.sivilstand?.type,
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
				barn: values?.personinformasjon?.barn?.barn,
				doedfoedtBarn: values?.personinformasjon?.barn?.doedfoedtBarn,
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
			tag: 'TESTNORGE',
			excludeTags: ['DOLLY'],
		}
	}
}
