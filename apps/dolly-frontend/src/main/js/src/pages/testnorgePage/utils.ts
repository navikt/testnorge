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
		statsborgerskap: {
			land: '',
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
			identtype: {
				fnr: false,
				dnr: false,
			},
			falskIdentitet: false,
			utenlandskIdentitet: false,
		},
		bosted: {
			kommunenr: '',
			postnr: '',
			bydel: '',
		},
		diverse: {
			kjoenn: '',
			utflyttet: false,
			innflyttet: false,
		},
	},
}

const getIdenttype = (values: any) => {
	if (values?.personinformasjon?.identifikasjon?.identtype?.fnr) {
		return 'FNR'
	} else if (values?.personinformasjon?.identifikasjon?.identtype?.dnr) {
		return 'DNR'
	}
	return ''
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
			kjoenn: values?.personinformasjon?.diverse?.kjoenn,
			foedsel: {
				fom: values?.personinformasjon?.alder?.foedselsdato?.fom,
				tom: values?.personinformasjon?.alder?.foedselsdato?.tom,
			},
			statsborgerskap: {
				land: values?.personinformasjon?.statsborgerskap?.land,
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
				identtype: getIdenttype(values),
				adressebeskyttelse: values?.personinformasjon?.identifikasjon?.adressebeskyttelse,
			},
			relasjoner: {
				barn: values?.personinformasjon?.barn?.barn,
				doedfoedtBarn: values?.personinformasjon?.barn?.doedfoedtBarn,
			},
			utflyttingFraNorge: {
				utflyttet: values?.personinformasjon?.diverse?.utflyttet,
			},
			innflyttingTilNorge: {
				innflytting: values?.personinformasjon?.diverse?.innflyttet,
			},
			adresser: {
				bostedsadresse: getBostedsadresseSearchValues(values),
			},
			geografiskTilknytning: {
				gtBydel: values?.personinformasjon?.bosted?.bydel,
			},
			tag: 'TESTNORGE',
			excludeTags: ['DOLLY'],
		}
	}
}

const getBostedsadresseSearchValues = (values: any) => {
	return {
		postnummer: values?.personinformasjon?.bosted?.postnr,
		kommunenummer: values?.personinformasjon?.bosted?.kommunenr,
	}
}
