import Formatters from '~/utils/DataFormatter'
import _set from 'lodash/set'
import { ReactReduxContext } from 'react-redux/lib/components/Context'

export const getAttributesFromMal = mal => {
	const tpsfKriterier = JSON.parse(mal.tpsfKriterier)
	const bestKriterier = JSON.parse(mal.bestKriterier)
	let attrArray = []
	attrArray = Object.keys(tpsfKriterier).filter(k => {
		if (
			k !== 'identtype' &&
			k !== 'relasjoner' &&
			k !== 'regdato' &&
			!k.includes('innvandretFraLand') &&
			!k.includes('utvandretTilLand')
		) {
			return k
		}
	})
	if (tpsfKriterier.boadresse) {
		tpsfKriterier.boadresse.flyttedato && attrArray.push('boadresse_flyttedato')
		if (tpsfKriterier.boadresse.adressetype === 'MATR') {
			var index = attrArray.indexOf('boadresse')
			if (index !== -1) {
				attrArray[index] = 'matrikkeladresse'
			}
		}
	}

	if (tpsfKriterier.relasjoner) {
		tpsfKriterier.relasjoner.barn && attrArray.push('barn')
		tpsfKriterier.relasjoner.partner && attrArray.push('partner')
	}

	tpsfKriterier.innvandretFraLand && attrArray.push('innvandret')
	tpsfKriterier.utvandretTilLand && attrArray.push('utvandret')

	if (tpsfKriterier.utvandretTilLand) {
		attrArray.push('utvandret')
		delete attrArray[attrArray.indexOf('utvandretTilLand')]
		tpsfKriterier.utvandretTilLandFlyttedato &&
			delete attrArray[attrArray.indexOf('utvandretTilLandFlyttedato')]
	}
	if (tpsfKriterier.erForsvunnet) {
		attrArray.push('forsvunnet')
		delete attrArray[attrArray.indexOf('erForsvunnet')]
		tpsfKriterier.forsvunnetDato && delete attrArray[attrArray.indexOf('forsvunnetDato')]
	}

	if (bestKriterier.pdlforvalter) {
		Object.keys(bestKriterier.pdlforvalter).map(pdlattr => {
			attrArray.push(pdlattr)
		})
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
		const pdlforvalter = reg[0] === 'pdlforvalter'
		const navn = pdlforvalter ? Object.keys(reg[1])[0] : reg[0]
		const values = pdlforvalter ? reg[1][navn] : reg[1]
		let valueArray = _mapRegistreValue(navn, values)

		if (Array.isArray(valueArray)) {
			_mapArrayValuesToObject(reduxStateValue, valueArray, navn)
		}
	})

	if (
		reduxStateValue.utvandretTilLand ||
		reduxStateValue.innvandretFraLand ||
		reduxStateValue.forsvunnet
	) {
		const utvandretValues = _mapInnOgUtvandret(reduxStateValue)
		reduxStateValue = utvandretValues
	}
	if (reduxStateValue.adressetype && reduxStateValue.adressetype === 'MATR') {
		const matrikkeladresseValues = _mapAdresseValues(reduxStateValue)
		reduxStateValue = matrikkeladresseValues
	}
	return reduxStateValue
}

const _mapValuesToObject = (objectToAssign, valueArray, keyPrefix = '') => {
	valueArray.forEach(v => {
		let key = v[0]
		if (key === 'regdato') return
		let value = v[1]
		if (value || value === false) {
			let customKeyPrefix = keyPrefix
			if (keyPrefix === 'barn_' && key === 'identtype') {
				customKeyPrefix = ''
			}
			value = _formatValueForObject(key, value)

			if (key === 'boadresse' && value.adressetype === 'MATR') {
				_mapValuesToObject(objectToAssign, Object.entries(value))
			} else if (key === 'boadresse') {
				_mapValuesToObject(objectToAssign, Object.entries(value), 'boadresse_')
			} else if (key === 'postadresse') {
				_mapValuesToObject(objectToAssign, Object.entries(value[0]))
			} else if (key === 'aap' && value !== true) {
				_mapValuesToObject(objectToAssign, [['aap', true]])
				_mapValuesToObject(objectToAssign, Object.entries(value[0]), 'aap_')
			} else if (key === 'aap115' && value !== true) {
				_mapValuesToObject(objectToAssign, [['aap115', true]])
				_mapValuesToObject(objectToAssign, Object.entries(value[0]), 'aap115_')
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
	const mappedKey =
		key === 'pdlforvalter' || key === 'arenaforvalter'
			? _mapRegistreKey(Object.keys(valueArray[0])[0])
			: _mapRegistreKey(key)

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
	const dateAttributes = [
		'foedtFoer',
		'foedtEtter',
		'doedsdato',
		'fom',
		'tom',
		'gyldigFra',
		'gyldigFom',
		'gyldigTom',
		'utstedtDato',
		'foedselsdato',
		'flyttedato',
		'fraDato',
		'tilDato',
		'utvandretTilLandFlyttedato',
		'innvandretFraLandFlytteDato',
		'forsvunnetDato',
		'startdato',
		'faktiskSluttdato',
		'forventetSluttdato'
	]

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
		case 'krrstub':
			return 'krr'
		case 'kontaktinformasjonForDoedsbo':
			return 'kontaktinformasjonForDoedsbo'
		case 'utenlandskIdentifikasjonsnummer':
			return 'utenlandskIdentifikasjonsnummer'
		case 'arenaBrukertype':
			return 'arenaforvalter'
		case 'instdata':
			return 'institusjonsopphold'
		case 'falskIdentitet':
			return 'falskIdentitet'
		default:
			return key
	}
}

const _mapInnOgUtvandret = values => {
	let valuesArray = JSON.parse(JSON.stringify(values))
	if (valuesArray.barn) {
		//Loop gjennom barn og kjør denne funksjonen for hvert barn
		valuesArray.barn.map((enkeltBarn, idx) => {
			valuesArray.barn[idx] = _mapInnOgUtvandret(enkeltBarn)
		})
	}

	Object.entries(valuesArray).map(value => {
		if (value[0].includes('innvandret')) {
			if (value[0].includes('partner')) {
				!valuesArray.partner_innvandret && (valuesArray.partner_innvandret = [{}])
				return (valuesArray.partner_innvandret[0][value[0].split('_')[1]] = value[1])
			} else if (value[0].includes('barn')) {
				!valuesArray.barn_innvandret && (valuesArray.barn_innvandret = [{}])
				return (valuesArray.barn_innvandret[0][value[0].split('_')[1]] = value[1])
			} else {
				!valuesArray.innvandret && (valuesArray.innvandret = [{}])
				return (valuesArray.innvandret[0][value[0]] = value[1])
			}
		}

		if (value[0].includes('utvandret')) {
			if (value[0].includes('partner')) {
				!valuesArray.partner_utvandret && (valuesArray.partner_utvandret = [{}])
				return (valuesArray.partner_utvandret[0][value[0].split('_')[1]] = value[1])
			} else if (value[0].includes('barn')) {
				!valuesArray.barn_utvandret && (valuesArray.barn_utvandret = [{}])
				return (valuesArray.barn_utvandret[0][value[0].split('_')[1]] = value[1])
			} else {
				!valuesArray.utvandret && (valuesArray.utvandret = [{}])
				return (valuesArray.utvandret[0][value[0]] = value[1])
			}
		}

		if (value[0].includes('forsvunnet')) {
			if (value[0].includes('partner')) {
				!valuesArray.partner_forsvunnet && (valuesArray.partner_forsvunnet = [{}])
				return (valuesArray.partner_forsvunnet[0][value[0].split('_')[1]] = value[1])
			} else if (value[0].includes('barn')) {
				!valuesArray.barn_forsvunnet && (valuesArray.barn_forsvunnet = [{}])
				return (valuesArray.barn_forsvunnet[0][value[0].split('_')[1]] = value[1])
			} else {
				!valuesArray.forsvunnet && (valuesArray.forsvunnet = [{}])
				return (valuesArray.forsvunnet[0][value[0]] = value[1])
			}
		}
	})
	return valuesArray
}

const _mapForsvunnetValues = values => {
	let forsvunnetValues = [
		{
			erForsvunnet: values.erForsvunnet,
			forsvunnetDato: values.forsvunnetDato
		}
	]
	let returnValues = values
	returnValues['forsvunnet'] = forsvunnetValues
	return forsvunnetValues
}

const _mapAdresseValues = values => {
	let matrikkeladresseValues = { matrikkeladresse: [] }
	if (values.flyttedato) {
		matrikkeladresseValues.boadresse_flyttedato = values.flyttedato
		delete values.flyttedato
	}
	if (values.adressetype) {
		delete values.adressetype
	}
	if (values.identtype) {
		delete values.identtype
	}
	matrikkeladresseValues.matrikkeladresse.push(values)
	return matrikkeladresseValues
}

const _mapRegistreValue = (key, value) => {
	let mappedValue = []
	switch (key) {
		case 'aareg':
			value.forEach(arb => {
				let arbObj = {
					yrke: arb.arbeidsavtale.yrke,
					fom: Formatters.formatDate(arb.ansettelsesPeriode.fom),
					tom: Formatters.formatDate(arb.ansettelsesPeriode.tom),
					stillingsprosent: arb.arbeidsavtale.stillingsprosent,
					aktoertype: arb.arbeidsgiver.aktoertype,
					permisjon:
						arb.permisjon &&
						arb.permisjon.map(per => {
							if (per.permisjonsId !== null) {
								return {
									permisjonOgPermittering: per.permisjonOgPermittering,
									fom: Formatters.formatDate(per.permisjonsPeriode.fom),
									tom: Formatters.formatDate(per.permisjonsPeriode.tom),
									permisjonsprosent: per.permisjonsprosent
								}
							}
						}),
					utenlandsopphold:
						arb.utenlandsopphold &&
						arb.utenlandsopphold.map(utl => {
							if (utl.land) {
								return {
									land: utl.land,
									fom: Formatters.formatDate(utl.periode.fom),
									tom: Formatters.formatDate(utl.periode.tom)
								}
							}
						})
				}

				if (arb.arbeidsgiver.aktoertype === 'ORG') {
					arbObj = { ...arbObj, orgnummer: arb.arbeidsgiver.orgnummer }
				} else if (arb.arbeidsgiver.aktoertype === 'PERS')
					arbObj = { ...arbObj, ident: arb.arbeidsgiver.ident }

				mappedValue.push(arbObj)
			})

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
		case 'krrstub':
			return [value]
		case 'kontaktinformasjonForDoedsbo':
			const mapObj = {}
			Object.entries(value).map(attr => {
				attr[0] === 'adressat'
					? Object.entries(value[attr[0]]).map(adressatAttr => {
							if (adressatAttr[0] === 'kontaktperson' || adressatAttr[0] === 'navn') {
								Object.entries(value[attr[0]][adressatAttr[0]]).map(navn => {
									mapObj[navn[0]] = navn[1]
								})
							} else if (adressatAttr[0] === 'organisasjonsnavn') {
								value[attr[0]].adressatType === 'ADVOKAT'
									? (mapObj['advokat_orgnavn'] = adressatAttr[1])
									: (mapObj['org_orgnavn'] = adressatAttr[1])
							} else if (adressatAttr[0] === 'organisasjonsnummer') {
								value[attr[0]].adressatType === 'ADVOKAT'
									? (mapObj['advokat_orgnr'] = adressatAttr[1])
									: (mapObj['org_orgnr'] = adressatAttr[1])
							} else mapObj[adressatAttr[0]] = adressatAttr[1]
					  })
					: (mapObj[attr[0]] = attr[1])
			})
			return [mapObj]
		case 'utenlandskIdentifikasjonsnummer':
			return [value]
		case 'arenaforvalter':
			return [value]
		case 'falskIdentitet':
			const mapFalskIdObj = {}
			Object.entries(value.rettIdentitet).map(attr => {
				attr[0] === 'personnavn'
					? Object.entries(value.rettIdentitet[attr[0]]).map(navnAttr => {
							mapFalskIdObj[navnAttr[0]] = navnAttr[1]
					  })
					: (mapFalskIdObj[attr[0]] = attr[1])
			})
			return [mapFalskIdObj]
		default:
			return value
	}
}
