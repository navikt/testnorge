import Formatters from '~/utils/DataFormatter'
import _set from 'lodash/set'

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

	if (reduxStateValue.utvandretTilLand || reduxStateValue.innvandretFraLand) {
		const utvandretValues = _mapInnOgUtvandretValues(reduxStateValue)
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
		'innvandretFraLandFlyttedato',
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

const _mapInnOgUtvandretValues = values => {
	// Trenger en forenkling. Lage en generisk løsning for alle attributter som samles i en undergruppe i formik (1 check i step 1 og 2 inputbokser i step2).
	let valuesArray = JSON.parse(JSON.stringify(values))
	valuesArray.barn &&
		valuesArray.barn.map(enkeltBarn => {
			enkeltBarn.barn_innvandret = [
				{
					innvandretFraLand: enkeltBarn.barn_innvandretFraLand && enkeltBarn.barn_innvandretFraLand,
					innvandretFraLandFlyttedato:
						enkeltBarn.barn_innvandretFraLandFlyttedato &&
						enkeltBarn.barn_innvandretFraLandFlyttedato
				}
			]
			enkeltBarn.barn_utvandret = [
				{
					utvandretTilLand: enkeltBarn.barn_utvandretTilLand && enkeltBarn.barn_utvandretTilLand,
					utvandretTilLandFlyttedato:
						enkeltBarn.barn_utvandretTilLandFlyttedato && enkeltBarn.barn_utvandretTilLandFlyttedato
				}
			]
		})

	valuesArray.partner_innvandret = [
		{
			innvandretFraLand:
				valuesArray.partner_innvandretFraLand && valuesArray.partner_innvandretFraLand,
			innvandretFraLandFlyttedato:
				valuesArray.partner_innvandretFraLandFlyttedato &&
				valuesArray.partner_innvandretFraLandFlyttedato
		}
	]
	valuesArray.partner_utvandret = [
		{
			utvandretTilLand:
				valuesArray.partner_utvandretTilLand && valuesArray.partner_utvandretTilLand,
			utvandretTilLandFlyttedato:
				valuesArray.partner_utvandretTilLandFlyttedato &&
				valuesArray.partner_utvandretTilLandFlyttedato
		}
	]

	valuesArray.innvandret = [
		{
			innvandretFraLand: valuesArray.innvandretFraLand && valuesArray.innvandretFraLand,
			innvandretFraLandFlyttedato:
				valuesArray.innvandretFraLandFlyttedato && valuesArray.innvandretFraLandFlyttedato
		}
	]

	valuesArray.utvandret = [
		{
			utvandretTilLand: valuesArray.utvandretTilLand && valuesArray.utvandretTilLand,
			utvandretTilLandFlyttedato:
				valuesArray.utvandretTilLandFlyttedato && valuesArray.utvandretTilLandFlyttedato
		}
	]
	return valuesArray
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
