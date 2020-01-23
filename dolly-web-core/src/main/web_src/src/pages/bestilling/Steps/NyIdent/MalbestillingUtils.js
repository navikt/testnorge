import Formatters from '~/utils/DataFormatter'
import _set from 'lodash/set'

export const getAttributesFromMal = mal => {
	const tpsfKriterier = mal.tpsfKriterier
	const bestKriterier = mal.bestKriterier
	let attrArray = []
	attrArray = Object.keys(tpsfKriterier).filter(k => {
		if (
			k !== 'identtype' &&
			k !== 'relasjoner' &&
			k !== 'regdato' &&
			!k.includes('innvandretFraLand') &&
			!k.includes('utvandretTilLand') &&
			!k.includes('statsborgerskap') &&
			!k.includes('erForsvunnet')
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
	tpsfKriterier.statsborgerskap && attrArray.push('statsborgerskapInfo')
	tpsfKriterier.erForsvunnet && attrArray.push('forsvunnet')

	Object.keys(bestKriterier).forEach(reg => {
		if (reg === 'udistub' || reg === 'pdlforvalter') {
			// Der det ligger flere keys under samme reg
			attrArray.push(...mapMultipleRegistreKey(reg, bestKriterier))
		} else {
			attrArray.push(_mapRegistreKey(reg, bestKriterier))
		}
	})
	return attrArray
}

export const getValuesFromMal = mal => {
	let reduxStateValue = {}
	const tpsfKriterierArray = Object.entries(mal.tpsfKriterier)
	const bestKriterierArray = Object.entries(mal.bestKriterier)
	_mapValuesToObject(reduxStateValue, tpsfKriterierArray)

	bestKriterierArray.forEach(reg => {
		const navn = reg[0]
		const values = reg[1]
		const multipleKeys = navn === 'pdlforvalter'
		let valueArray = []
		if (multipleKeys) {
			Object.entries(values).forEach(pdlattr => {
				_mapArrayValuesToObject(
					reduxStateValue,
					[{ [pdlattr[0]]: _mapRegistreValue(pdlattr[0], pdlattr[1]) }],
					navn
				)
			})
		} else {
			valueArray = _mapRegistreValue(navn, values)
			if (Array.isArray(valueArray)) {
				_mapArrayValuesToObject(reduxStateValue, valueArray, navn)
			}
		}
	})

	if (
		reduxStateValue.utvandretTilLand ||
		reduxStateValue.innvandretFraLand ||
		reduxStateValue.erForsvunnet
	) {
		const utvandretValues = _mapInnOgUtvandret(reduxStateValue)
		reduxStateValue = utvandretValues
	}
	if (reduxStateValue.adressetype && reduxStateValue.adressetype === 'MATR') {
		const matrikkeladresseValues = _mapAdresseValues(reduxStateValue)
		reduxStateValue = matrikkeladresseValues
	}
	if (reduxStateValue.statsborgerskap) {
		const statsborgerskapValues = _mapStatsborgerskap(reduxStateValue)
		reduxStateValue = statsborgerskapValues
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
		key === 'arenaforvalter' ? _mapRegistreKey(Object.keys(valueArray[0])[0]) : _mapRegistreKey(key)
	let valueArrayObj = []

	valueArray.forEach(v => {
		let childObj = {}
		_mapValuesToObject(childObj, Object.entries(v), keyPrefix)
		valueArrayObj.push(childObj)
	})
	const multipleGroupsPerKey = key === 'pdlforvalter' || key === 'udistub'

	multipleGroupsPerKey
		? Object.assign(objectToAssign, {
				...valueArrayObj[0]
		  })
		: Object.assign(objectToAssign, {
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
		'forsvunnetDato',
		'statsborgerskapRegdato',
		'startdato',
		'faktiskSluttdato',
		'forventetSluttdato',
		'oppholdFraDato',
		'oppholdTilDato',
		'effektueringsDato',
		'oppholdSammeVilkaarFraDato',
		'oppholdSammeVilkaarTilDato',
		'oppholdSammeVilkaarEffektuering',
		'oppholdstillatelseVedtaksDato',
		'arbeidsadgangFraDato',
		'arbeidsadgangTilDato',
		'regdato'
	]
	if (Array.isArray(value)) {
		value.forEach((val, idx) =>
			Object.entries(val).forEach(attr => {
				if (dateAttributes.includes(attr[0])) {
					if (attr[1]) {
						value[idx][attr[0]] = Formatters.formatDate(attr[1])
					} else {
						value[idx][attr[0]] = ''
					}
				}
			})
		)
	}

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
		case 'sigrunstub':
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

const mapMultipleRegistreKey = (reg, bestKriterier) => {
	const valgteAttributt = []

	if (reg === 'pdlforvalter') {
		return Object.keys(bestKriterier.pdlforvalter)
	} else if (reg === 'udistub') {
		const muligeAttr = [
			'aliaser',
			'arbeidsadgang',
			'oppholdStatus',
			'soeknadOmBeskyttelseUnderBehandling',
			'flyktning'
		]
		if (bestKriterier.udistub['harOppholdsTillatelse'] === false) {
			bestKriterier.udistub.oppholdStatus = true
		}
		return Object.keys(bestKriterier.udistub).filter(
			attr => muligeAttr.includes(attr) && bestKriterier.udistub[attr]
		)
	}
	return valgteAttributt
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

		if (value[0].toLowerCase().includes('forsvunnet')) {
			if (value[0].includes('partner')) {
				!valuesArray.partner_forsvunnet && (valuesArray.partner_forsvunnet = [{}])
				return (valuesArray.partner_forsvunnet[0][value[0].split('_')[1]] = value[1].toString())
			} else if (value[0].includes('barn')) {
				!valuesArray.barn_forsvunnet && (valuesArray.barn_forsvunnet = [{}])
				return (valuesArray.barn_forsvunnet[0][value[0].split('_')[1]] = value[1].toString())
			} else {
				!valuesArray.forsvunnet && (valuesArray.forsvunnet = [{}])
				return (valuesArray.forsvunnet[0][value[0]] = value[1].toString())
			}
		}
	})
	return valuesArray
}

const _mapStatsborgerskap = values => {
	let valuesArray = JSON.parse(JSON.stringify(values))
	if (valuesArray.barn) {
		//Loop gjennom barn og kjør denne funksjonen for hvert barn
		valuesArray.barn.map((enkeltBarn, idx) => {
			valuesArray.barn[idx] = _mapStatsborgerskap(enkeltBarn)
		})
	}

	Object.entries(valuesArray).map(value => {
		if (value[0].includes('statsborgerskap')) {
			if (value[0].includes('partner')) {
				!valuesArray.partner_statsborgerskapInfo && (valuesArray.partner_statsborgerskapInfo = [{}])
				return (valuesArray.partner_statsborgerskapInfo[0][
					value[0].split('_')[1]
				] = value[1].toString())
			} else if (value[0].includes('barn')) {
				!valuesArray.barn_statsborgerskapInfo && (valuesArray.barn_statsborgerskapInfo = [{}])
				return (valuesArray.barn_statsborgerskapInfo[0][
					value[0].split('_')[1]
				] = value[1].toString())
			} else {
				!valuesArray.statsborgerskapInfo && (valuesArray.statsborgerskapInfo = [{}])
				return (valuesArray.statsborgerskapInfo[0][value[0]] = value[1])
			}
		}
	})
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
		case 'sigrunstub':
			value.forEach((inntekt, i) => {
				if (inntekt.grunnlag && value[i].tjeneste == 'SUMMERT_SKATTEGRUNNLAG') {
					inntekt.grunnlag.forEach(g => {
						mappedValue.push({
							tjeneste: value[i].tjeneste,
							typeinntekt: g.tekniskNavn,
							inntektsaar: inntekt.inntektsaar,
							beloep: g.verdi,
							inntektssted: value.inntektssted,
							inntektssted: 'Fastlandet'
						})
					})
				}
				if (inntekt.grunnlag && value[i].tjeneste == 'BEREGNET_SKATT') {
					inntekt.grunnlag.forEach(g => {
						mappedValue.push({
							tjeneste: value[i].tjeneste,
							typeinntekt: g.tekniskNavn,
							inntektsaar: inntekt.inntektsaar,
							beloep: g.verdi,
							inntektssted: value.inntektssted,
							inntektssted: 'Fastlandet'
						})
					})
				}
				if (inntekt.svalbardGrunnlag && value[i].tjeneste == 'SUMMERT_SKATTEGRUNNLAG') {
					inntekt.svalbardGrunnlag.forEach(g => {
						mappedValue.push({
							typeinntekt: g.tekniskNavn,
							inntektsaar: inntekt.inntektsaar,
							tjeneste: value[i].tjeneste,
							beloep: g.verdi,
							inntektssted: 'Svalbard'
						})
					})
				}
			})

			return mappedValue
		case 'krrstub':
			return [value]
		case 'kontaktinformasjonForDoedsbo':
			const mapObj = {}
			const datoAttr = ['foedselsdato', 'utstedtDato', 'gyldigFom', 'gyldigTom']
			Object.entries(value).map(attr => {
				if (attr[0] === 'adressat') {
					Object.entries(value[attr[0]]).map(adressatAttr => {
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
						} else {
							const attrValue = datoAttr.includes(adressatAttr[0])
								? Formatters.formatDate(adressatAttr[1])
								: adressatAttr[1]
							mapObj[adressatAttr[0]] = attrValue
						}
					})
				} else {
					if (datoAttr.includes(attr[0])) {
						mapObj[attr[0]] = Formatters.formatDate(attr[1])
					} else {
						mapObj[attr[0]] = attr[1]
					}
				}
			})
			return [mapObj]
		case 'utenlandskIdentifikasjonsnummer':
			return value
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
		case 'udistub':
			const mapUdiObj = {}
			if (value.oppholdStatus) {
				const oppholdObj = {}
				Object.entries(value.oppholdStatus).forEach(attr => {
					if (attr[1]) {
						if (attr[0].includes('eosEllerEFTA')) {
							if (attr[0].includes('Periode')) {
								Object.assign(oppholdObj, {
									...oppholdObj,
									oppholdFraDato: attr[1].fra ? Formatters.formatStringDates(attr[1].fra) : '',
									oppholdTilDato: attr[1].til ? Formatters.formatStringDates(attr[1].til) : ''
								})
							} else if (attr[0].includes('Effektuering')) {
								Object.assign(oppholdObj, {
									...oppholdObj,
									effektueringsDato: Formatters.formatStringDates(attr[1])
								})
							} else {
								Object.assign(oppholdObj, {
									...oppholdObj,
									oppholdsstatus: 'eosEllerEFTAOpphold',
									eosEllerEFTAtypeOpphold: attr[0],
									[attr[0]]: attr[1]
								})
							}
						} else if (attr[0] === 'oppholdSammeVilkaar') {
							Object.assign(oppholdObj, {
								...oppholdObj,
								oppholdsstatus: 'oppholdSammeVilkaar',
								tredjelandsBorgereValg: 'oppholdSammeVilkaar'
							})
							Object.entries(attr[1]).forEach(subAttr => {
								if (/\d{4}-\d{2}-\d{2}/.test(subAttr[1])) {
									Object.assign(oppholdObj, {
										...oppholdObj,
										[subAttr[0]]: Formatters.formatStringDates(subAttr[1])
									})
								} else if (subAttr[0] === 'oppholdSammeVilkaarPeriode') {
									Object.assign(oppholdObj, {
										...oppholdObj,
										oppholdSammeVilkaarFraDato: subAttr[1].fra
											? Formatters.formatStringDates(subAttr[1].fra)
											: '',
										oppholdSammeVilkaarTilDato: subAttr[1].til
											? Formatters.formatStringDates(subAttr[1].til)
											: ''
									})
								} else {
									Object.assign(oppholdObj, {
										...oppholdObj,
										[subAttr[0]]: subAttr[1]
									})
								}
							})
						} else if (attr[0] === 'uavklart' && attr[1] === true) {
							Object.assign(oppholdObj, {
								oppholdsstatus: 'oppholdSammeVilkaar',
								tredjelandsBorgereValg: 'UAVKLART'
							})
						}
					}
				})
				_set(mapUdiObj, 'oppholdStatus', [oppholdObj])
			}
			if (value.harOppholdsTillatelse === false) {
				const ikkeOpphold = {}
				Object.assign(ikkeOpphold, {
					oppholdsstatus: 'oppholdSammeVilkaar',
					tredjelandsBorgereValg: 'ikkeOppholdSammeVilkaar'
				})
				_set(mapUdiObj, 'oppholdStatus', [ikkeOpphold])
			}
			if (value.arbeidsadgang) {
				const arbAdg = {}
				Object.entries(value.arbeidsadgang).forEach(attr => {
					if (attr[1] && typeof attr[1] !== 'string') {
						Object.entries(attr[1]).forEach(subAttr => {
							let arbAdgDato = ''
							if (subAttr[0] === 'fra') {
								arbAdgDato = 'arbeidsadgangFraDato'
							} else if (subAttr[0] === 'til') {
								arbAdgDato = 'arbeidsadgangTilDato'
							}
							Object.assign(arbAdg, {
								...arbAdg,
								[arbAdgDato]: Formatters.formatStringDates(subAttr[1])
							})
						})
					} else {
						Object.assign(arbAdg, {
							...arbAdg,
							[attr[0]]: attr[1]
						})
					}
				})
				_set(mapUdiObj, 'arbeidsadgang', [arbAdg])
			}
			if (value.aliaser) {
				mapUdiObj.aliaser = value.aliaser
				value.aliaser.forEach((alias, idx) => {
					mapUdiObj.aliaser[idx].nyIdent = alias.nyIdent ? 'idnummer' : 'navn'
				})
			}
			if (value.flyktning) {
				mapUdiObj.flyktning = value.flyktning
			}
			if (value.soeknadOmBeskyttelseUnderBehandling) {
				mapUdiObj.soeknadOmBeskyttelseUnderBehandling = value.soeknadOmBeskyttelseUnderBehandling
			}
			return [mapUdiObj]
		default:
			return value
	}
}
