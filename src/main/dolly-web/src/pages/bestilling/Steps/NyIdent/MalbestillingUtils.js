import Formatters from '~/utils/DataFormatter'

export const getAttributesFromMal = mal => {
	const tpsfKriterier = JSON.parse(mal.tpsfKriterier)
	const bestKriterier = JSON.parse(mal.bestKriterier)

	let attrArray = []
	attrArray = Object.keys(tpsfKriterier).filter(k => {
		if (k !== 'identtype' && k !== 'relasjoner' && k !== 'regdato') {
			return k
		}
	})

	if (tpsfKriterier.relasjoner) {
		tpsfKriterier.relasjoner.barn && attrArray.push('barn')
		tpsfKriterier.relasjoner.partner && attrArray.push('partner')
	}

	Object.keys(bestKriterier).forEach(reg => {
		attrArray.push(_mapRegistreKey(reg))
	})

	return attrArray
}

export const getValuesFromMal = mal => {
	let reduxStateValue = {}
	const tpsfKriterierArray = Object.entries(JSON.parse(mal.tpsfKriterier))
	const bestKriterierArray = Object.entries(JSON.parse(mal.bestKriterier))

	_mapValuesToObject(reduxStateValue, tpsfKriterierArray)

	bestKriterierArray.forEach(reg => {
		let valueArray = _mapRegistreValue(reg[0], reg[1])
		if (Array.isArray(valueArray)) {
			_mapArrayValuesToObject(reduxStateValue, valueArray, reg[0])
		}
	})

	console.log('reduStateValue :', reduxStateValue)
	return reduxStateValue
}

const _mapValuesToObject = (objectToAssign, valueArray, keyPrefix = '') => {
	valueArray.forEach(v => {
		let key = v[0]

		if (key === 'regdato') return

		let value = v[1]

		if (value) {
			let customKeyPrefix = keyPrefix
			if (keyPrefix === 'barn_' && key === 'identtype') {
				customKeyPrefix = ''
			}
			value = _formatValueForObject(key, value)

			if (key === 'boadresse') {
				//TODO: boAdresse fungerer ikke ennaa
				_mapValuesToObject(objectToAssign, Object.entries(value), 'boadresse_')
			} else if (key === 'postadresse') {
				_mapValuesToObject(objectToAssign, Object.entries(value[0]))
			} else {
				// Formater values før vi lager objekt
				if (key === 'relasjoner') {
					value.partner &&
						_mapValuesToObject(objectToAssign, Object.entries(value.partner), 'partner_')

					if (value.barn && Array.isArray(value.barn)) {
						_mapArrayValuesToObject(objectToAssign, value.barn, 'barn', 'barn_')
					}
				} else {
					Object.assign(objectToAssign, {
						[customKeyPrefix + key]: value
					})
				}
			}
		}
	})
}

const _mapArrayValuesToObject = (objectToAssign, valueArray, key, keyPrefix = '') => {
	const mappedKey = _mapRegistreKey(key)
	let valueArrayObj = []

	valueArray.forEach(v => {
		let childObj = {}
		_mapValuesToObject(childObj, Object.entries(v), keyPrefix)
		valueArrayObj.push(childObj)
	})
	Object.assign(objectToAssign, {
		[mappedKey]: valueArrayObj
	})
}

const _formatValueForObject = (key, value) => {
	const dateAttributes = ['foedtFoer', 'foedtEtter', 'doedsdato', 'fom', 'tom', 'gyldigFra']

	if (dateAttributes.includes(key)) {
		value = Formatters.formatDate(value)
	} else if (key === 'egenAnsattDatoFom') {
		value = true
	}

	return value
}

const _mapRegistreKey = key => {
	// TODO: Nå som disse id-ene er brukt flere steder på prosjektet gjennom mappingen, vurder å lage en constant klasse
	switch (key) {
		case 'aareg':
			return 'arbeidsforhold'
		case 'sigrunStub':
			return 'inntekt'
		case 'krrStub':
			return 'krr'
		default:
			return key
	}
}

const _mapRegistreValue = (key, value) => {
	let mappedValue = []
	switch (key) {
		case 'aareg':
			console.log('value :', value)
			value.forEach(arb => {
				let arbObj = {
					yrke: arb.arbeidsavtale.yrke,
					fom: Formatters.formatDate(arb.ansettelsesPeriode.fom),
					tom: Formatters.formatDate(arb.ansettelsesPeriode.tom),
					stillingsprosent: arb.arbeidsavtale.stillingsprosent,
					aktoertype: arb.arbeidsgiver.aktoertype,
					permisjon:
						arb.permisjon &&
						arb.permisjon.map(per => ({
							permisjonOgPermittering: per.permisjonOgPermittering,
							fom: Formatters.formatDate(per.permisjonsPeriode.fom),
							tom: Formatters.formatDate(per.permisjonsPeriode.tom),
							permisjonsprosent: per.permisjonsprosent
						})),
					utenlandsopphold:
						arb.utenlandsopphold &&
						arb.utenlandsopphold.map(utl => ({
							land: utl.land,
							fom: Formatters.formatDate(utl.periode.fom),
							tom: Formatters.formatDate(utl.periode.tom)
						}))
				}

				if (arb.arbeidsgiver.aktoertype === 'ORG') {
					arbObj = { ...arbObj, orgnummer: arb.arbeidsgiver.orgnummer }
				} else if (arb.arbeidsgiver.aktoertype === 'PERS')
					arbObj = { ...arbObj, ident: arb.arbeidsgiver.ident }

				mappedValue.push(arbObj)
			})
			// arbeidsforhold[
			// 	{
			// 	  "yrke": "3310101",
			// 	  "fom": "20.05.1999",
			// 	  "tom": "",
			// 	  "stillingsprosent": 100,
			// 	  "aktoertype": "PERS",
			// 	  "permisjon": [
			// 		{
			// 		  "permisjonOgPermittering": "permisjonVedMilitaertjeneste",
			// 		  "fom": "01.05.2017",
			// 		  "tom": "01.05.2017",
			// 		  "permisjonsprosent": 100
			// 		}
			// 	  ],
			// 	  "utenlandsopphold": [
			// 		{
			// 		  "land": "DZ",
			// 		  "fom": "20.05.2019",
			// 		  "tom": "10.05.2019"
			// 		}
			// 	  ],
			// 	  "ident": "25016214941"
			// 	}
			//   ]

			return mappedValue
		case 'sigrunStub':
			value.forEach(inntekt => {
				inntekt.grunnlag.forEach(g => {
					mappedValue.push({
						typeinntekt: g.tekniskNavn,
						inntektsaar: inntekt.inntektsaar,
						tjeneste: inntekt.tjeneste,
						beloep: g.verdi
					})
				})
			})
			return mappedValue
		case 'krrStub':
			return [value]
		default:
			return value
	}
}
