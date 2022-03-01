export const initialValues = {
	personinformasjon: {
		alder: {
			fra: '18',
			til: '70',
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
		identitet: {
			falskIdentitet: false,
			utenlandskIdentitet: false,
		},
		diverse: {
			kjoenn: '',
			utflyttet: false,
			innflyttet: false,
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
			identitet: {
				falskIdentitet: values?.personinformasjon?.identitet?.falskIdentitet,
				utenlandskIdentitet: values?.personinformasjon?.identitet?.utenlandskIdentitet,
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
			tag: 'TESTNORGE',
			excludeTags: ['DOLLY'],
		}
	}
}
