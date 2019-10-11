import _groupBy from 'lodash/groupBy'
import _set from 'lodash/set'
import _get from 'lodash/get'
import _isEmpty from 'lodash/isEmpty'
import DataFormatter from '~/utils/DataFormatter'
import DataSourceMapper from '~/utils/DataSourceMapper'
import { AttributtManager } from '~/service/Kodeverk'

// TODO: Kan getValues og transformAttributt merges?
export const getValues = (attributeList, values) => {
	return attributeList.reduce((accumulator, attribute) => {
		let value = _transformAttributt(attribute, attributeList, values[attribute.id])
		const pathPrefix = DataSourceMapper(attribute.dataSource)

		if (pathPrefix == DataSourceMapper('SIGRUN')) {
			const groupByTjeneste = _groupBy(value, 'tjeneste')
			let tjenester = Object.keys(groupByTjeneste)
			let dataArr = []
			tjenester.forEach(tjeneste => {
				const groupedByInntektsaar = _groupBy(groupByTjeneste[tjeneste], 'inntektsaar')
				const keys = Object.keys(groupedByInntektsaar)
				keys.forEach(key => {
					const current = groupedByInntektsaar[key]
					dataArr.push({
						grunnlag: current.map(temp => ({
							tekniskNavn: temp.typeinntekt,
							verdi: temp.beloep
						})),
						inntektsaar: key,
						tjeneste: tjeneste
					})
				})
			})

			return _set(accumulator, pathPrefix, dataArr)
		}

		if (pathPrefix === DataSourceMapper('AAREG')) {
			let dataArr = []
			value.forEach(element => {
				let aaregObj = {}
				attribute.items.forEach(item => {
					const path = item.path
					const id = item.id
					const keyValue = findAaregKeyValue(item, element)
					if (item.id === 'utenlandsopphold' || item.id === 'permisjon') {
						if (!keyValue || _isEmpty(keyValue[0])) return
					}
					item.subItems
						? Object.assign(aaregObj, { [path]: keyValue })
						: Object.assign(aaregObj, { [path]: { ...aaregObj[path], [id]: keyValue } })
				})

				// aktorID = PERS
				if (aaregObj.arbeidsgiver.aktoertype == 'PERS') {
					const aktoertype = aaregObj.arbeidsgiver.aktoertype
					const arbeidsgiverIdent = aaregObj.arbeidsgiver.ident
					Object.assign(aaregObj, {
						arbeidsgiver: { aktoertype, ident: arbeidsgiverIdent }
					})
				}

				// Spesifikk ekstra felter for AAREG, ikke-sett av UI
				Object.assign(aaregObj, {
					arbeidsforholdstype: 'ordinaertArbeidsforhold',
					arbeidsavtale: {
						...aaregObj.arbeidsavtale,
						arbeidstidsordning: 'ikkeSkift',
						avtaltArbeidstimerPerUke: 37.5
					}
				})
				if (aaregObj.permisjon) {
					aaregObj.permisjon.map((perm, idx) => {
						perm.permisjonOgPermittering &&
							Object.assign(aaregObj.permisjon[idx], {
								...aaregObj.permisjon[idx],
								['permisjonsId']: idx.toString()
							})
					})
				}

				dataArr.push(aaregObj)
			})
			return _set(accumulator, pathPrefix, dataArr)
		}

		if (pathPrefix === DataSourceMapper('KRR')) {
			return _set(accumulator, pathPrefix, value[0])
		}

		if (pathPrefix === DataSourceMapper('PDLF')) {
			if (attribute.id === 'kontaktinformasjonForDoedsbo') {
				const doedsboValues = values.kontaktinformasjonForDoedsbo[0]
				const navnObj = deletePropertiesWithoutValues({
					fornavn: doedsboValues.fornavn,
					mellomnavn: doedsboValues.mellomnavn,
					etternavn: doedsboValues.etternavn
				})
				const adressatObj = {
					adressatType: doedsboValues.adressatType
				}

				if (adressatObj.adressatType === 'PERSON_MEDID')
					Object.assign(adressatObj, { idnummer: doedsboValues.idnummer })
				else if (adressatObj.adressatType === 'PERSON_UTENID')
					Object.assign(adressatObj, {
						foedselsdato: DataFormatter.parseDate(doedsboValues.foedselsdato),
						navn: navnObj
					})
				else if (adressatObj.adressatType === 'ADVOKAT')
					Object.assign(adressatObj, {
						organisasjonsnavn: doedsboValues.advokat_orgnavn,
						organisajonsnummer: doedsboValues.advokat_orgnr,
						kontaktperson: navnObj
					})
				else if (adressatObj.adressatType === 'ORGANISASJON')
					Object.assign(adressatObj, {
						organisasjonsnavn: doedsboValues.org_orgnavn,
						organisajonsnummer: doedsboValues.org_orgnummer,
						kontaktperson: navnObj
					})

				const doedsboObj = { adressat: deletePropertiesWithoutValues(adressatObj) }
				const otherAttributes = attribute.items.filter(
					item => !item.path || (item.path && !item.path.includes('adressat'))
				)
				otherAttributes.map(item => {
					let addedItem = { [item.id]: doedsboValues[item.id] }
					if (item.id.includes('_')) addedItem = { [item.id.split('_')[1]]: doedsboValues[item.id] }
					else if (item.inputType === 'date')
						addedItem = { [item.id]: DataFormatter.parseDate(doedsboValues[item.id]) }

					doedsboValues[item.id] && Object.assign(doedsboObj, addedItem)
				})

				//fiks adresse
				return _set(accumulator, pathPrefix, { kontaktinformasjonForDoedsbo: doedsboObj })
			}

			if (attribute.id === 'utenlandskIdentifikasjonsnummer') {
				// return _set(accumulator, `${pathPrefix}.${attribute.path || attribute.id}`, value[0])
				return _set(accumulator, `${pathPrefix}.${attribute.path || attribute.id}`, value)
			}
			if (attribute.id === 'falskIdentitet') {
				const falskIdData = value[0]

				const personnavn = {}
				const falskIdObj = {}

				Object.keys(falskIdData).map(attr => {
					if (falskIdData[attr]) {
						if (attr.includes('navn')) {
							Object.assign(personnavn, { [attr]: falskIdData[attr] })
							Object.assign(falskIdObj, { personnavn: personnavn })
						} else if (falskIdData.identitetType === 'UKJENT') {
							Object.assign(falskIdObj, {
								rettIdentitetErUkjent: true,
								[attr]: falskIdData[attr]
							})
						} else {
							Object.assign(falskIdObj, { [attr]: falskIdData[attr] })
						}
					}
				})
				return _set(accumulator, `${pathPrefix}.${attribute.path || attribute.id}`, {
					rettIdentitet: falskIdObj
				})
			}
		}

		if (pathPrefix === DataSourceMapper('INST')) {
			return _set(accumulator, pathPrefix, value)
		}

		if (pathPrefix === DataSourceMapper('ARENA')) {
			return _set(accumulator, pathPrefix, value[0])
		}
		if (attribute.id === 'barn') {
			const valueCopy = JSON.parse(JSON.stringify(value))
			value.map((barn, idx) => {
				//Loop gjennom liste med barn
				//Samme action for begge alternativ. TODO: Skrive felles
				if (barn.innvandret) {
					Object.entries(valueCopy[idx].barn_innvandret[0]).map(attr => {
						_set(
							valueCopy[idx],
							attr[0],
							isDate(attr[1]) ? DataFormatter.parseDate(attr[1]) : attr[1]
						)
					})
					delete valueCopy[idx].barn_innvandret
				}
				if (barn.utvandret) {
					Object.entries(valueCopy[idx].barn_utvandret[0]).map(attr => {
						_set(
							valueCopy[idx],
							attr[0],
							isDate(attr[1]) ? DataFormatter.parseDate(attr[1]) : attr[1]
						)
					})
					delete valueCopy[idx].barn_utvandret
				}
				if (barn.forsvunnet) {
					Object.entries(valueCopy[idx].forsvunnet[0]).map(attr => {
						_set(
							valueCopy[idx],
							attr[0],
							isDate(attr[1]) ? DataFormatter.parseDate(attr[1]) : attr[1]
						)
					})
					delete valueCopy[idx].barn_forsvunnet
				}
			})
			return _set(accumulator, `${pathPrefix}.${attribute.path || attribute.id}`, valueCopy)
		}

		if (
			attribute.id.includes('innvandret') ||
			attribute.id.includes('utvandret') ||
			attribute.id.includes('forsvunnet')
		) {
			// Viktig at denne ligger etter bolken med barn
			Object.entries(value[0]).map(attr => {
				_set(
					accumulator,
					attribute.id.includes('partner_')
						? `${pathPrefix}.relasjoner.partner.${attr[0]}`
						: `${pathPrefix}.${attr[0]}`,
					attr[1]
				)
			})
			return accumulator
		}

		if (pathPrefix === DataSourceMapper('UDI')) {
			const udiData = value[0]
			let udiObj = {}

			if (attribute.id === 'arbeidsadgang') {
				Object.keys(udiData).map(attr => {
					if (udiData[attr]) {
						if (attr === 'arbeidsadgangFraDato') {
							Object.assign(udiObj, { periode: { fra: udiData[attr] } })
						} else if (attr === 'arbeidsadgangTilDato') {
							if (udiObj.periode) {
								Object.assign(udiObj.periode, { til: udiData[attr] })
							} else {
								Object.assign(udiObj, { periode: { til: udiData[attr] } })
							}
						} else {
							Object.assign(udiObj, { [attr]: udiData[attr] })
						}
					}
				})
			}

			if (attribute.id === 'oppholdStatus') {
				const oppholdsstatus = udiData.oppholdsstatus
				if (oppholdsstatus === 'eosEllerEFTAOpphold') {
					const typeOpphold = udiData.eosEllerEFTAtypeOpphold
					const periode = `${typeOpphold}Periode`
					const effektuering = `${typeOpphold}Effektuering`
					Object.assign(udiObj, {
						[typeOpphold]: udiData[typeOpphold]
					})
					if (udiData.oppholdFraDato) {
						Object.assign(udiObj, { [periode]: { fra: udiData.oppholdFraDato } })
					}
					if (udiData.oppholdTilDato) {
						if (periode in udiObj) {
							Object.assign(udiObj[periode], { til: udiData.oppholdTilDato })
						} else {
							Object.assign(udiObj, { [periode]: { til: udiData.oppholdTilDato } })
						}
					}
					if (udiData.effektueringsDato) {
						Object.assign(udiObj, { [effektuering]: udiData.effektueringsDato })
					}
				} else if (udiData.oppholdsstatus === 'oppholdSammeVilkaar') {
					if (udiData.tredjelandsBorgereValg === 'oppholdSammeVilkaar') {
						Object.assign(udiObj, { [udiData.oppholdsstatus]: {} })
						if (udiData.oppholdSammeVilkaarFraDato) {
							Object.assign(udiObj[udiData.oppholdsstatus], {
								oppholdSammeVilkaarPeriode: { fra: udiData.oppholdSammeVilkaarFraDato }
							})
						}
						if (udiData.oppholdSammeVilkaarTilDato) {
							if ('oppholdSammeVilkaarPeriode' in udiObj[udiData.oppholdsstatus]) {
								Object.assign(udiObj[udiData.oppholdsstatus].oppholdSammeVilkaarPeriode, {
									til: udiData.oppholdSammeVilkaarTilDato
								})
							} else {
								Object.assign(udiObj[udiData.oppholdsstatus], {
									oppholdSammeVilkaarPeriode: { til: udiData.oppholdSammeVilkaarTilDato }
								})
							}
						}
						if (udiData.oppholdSammeVilkaarEffektuering) {
							Object.assign(udiObj[udiData.oppholdsstatus], {
								oppholdSammeVilkaarEffektuering: udiData.oppholdSammeVilkaarEffektuering
							})
						}
						if (udiData.oppholdstillatelseType) {
							Object.assign(udiObj[udiData.oppholdsstatus], {
								oppholdstillatelseType: udiData.oppholdstillatelseType
							})
						}
						if (udiData.oppholdstillatelseVedtaksDato) {
							Object.assign(udiObj[udiData.oppholdsstatus], {
								oppholdstillatelseVedtaksDato: udiData.oppholdstillatelseVedtaksDato
							})
						}
					} else if (udiData.tredjelandsBorgereValg === 'UAVKLART') {
						Object.assign(udiObj, { uavklart: true })
					} else if (udiData.tredjelandsBorgereValg === 'ikkeOppholdSammeVilkaar') {
						udiObj = false
						attribute.path = 'harOppholdsTillatelse'
					}
				}
			}

			if (attribute.id === 'aliaser') {
				udiObj = []
				value.forEach((alias, idx) => {
					alias.nyIdent === 'navn'
						? (udiObj[idx] = { nyIdent: false })
						: (udiObj[idx] = { identtype: alias.identtype, nyIdent: true })
				})
			}

			if (attribute.id === 'flyktning' || attribute.id === 'soeknadOmBeskyttelseUnderBehandling') {
				udiObj = value
			}
			return _set(accumulator, `${pathPrefix}.${attribute.path || attribute.id}`, udiObj)
		}

		return _set(accumulator, `${pathPrefix}.${attribute.path || attribute.id}`, value)
	}, {})
}

// Transform attributes before order is sent
// Date, boolean...
const _transformAttributt = (attribute, attributes, value) => {
	if (!attribute) return null
	if (attribute.dataSource === 'SIGRUN') {
		return value
	} else if (attribute.dataSource === 'AAREG') {
		let valueDeepCopy = JSON.parse(JSON.stringify(value))
		attribute.items.forEach(item => {
			if (item.inputType === 'date') {
				valueDeepCopy.forEach((element, i) => {
					const transformedElement = {
						...element,
						[item.id]: DataFormatter.parseDate(element[item.id])
					}

					valueDeepCopy[i] = transformedElement
				})
			}

			if (item.subItems) {
				// finn inputType 'date' i subItems
				const subKategoriId = item.id
				valueDeepCopy.map((element, idx) => {
					let transformed = Object.assign(element)
					if (
						element[subKategoriId] &&
						element[subKategoriId] !== '' &&
						element[subKategoriId][0] !== ''
					) {
						let subsubArray = []
						element[subKategoriId].map(rad => {
							rad && subsubArray.push(parseSubItemDate(item, rad, Object.assign(rad)))
						})

						transformed = {
							...transformed,
							[item.id]: subsubArray
						}
					}
					valueDeepCopy[idx] = transformed
				})
			}
		})

		return valueDeepCopy
	}
	if (attribute.items) {
		let attributeList = attribute.items.reduce((res, acc) => ({ ...res, [acc.id]: acc }), {})
		if (attribute.isMultiple) {
			return value.map(val =>
				Object.assign(
					{},
					...Object.entries(val).map(([key, value]) => {
						if (!attributeList[key]) return
						let pathId = attributeList[key].path.split('.')
						return {
							//  Hente kun siste key, f.eks barn.kjønn => kjønn
							[pathId[pathId.length - 1]]: _transformAttributt(
								attributeList[key],
								attributes,
								value
							)
						}
					})
				)
			)
		} else {
			return value.map(val =>
				Object.assign(
					{},
					...Object.entries(val).map(([key, value]) => {
						return {
							[key]: _transformAttributt(attributeList[key], attributes, value)
						}
					})
				)
			)
		}
	} else if (attribute.transform) {
		// Only affects attributes that has property "transform: (value: any, attributter: Attributt[]) => any"
		value = attribute.transform(value, attributes)
	}
	if (attribute.inputType === 'date') {
		value = DataFormatter.parseDate(value)
	}
	return value
}

// Filter attributes included in filter argument
// Removes all children if attribute is a parent and dependencies
export const _filterAttributes = (values, filter, attribute, dependencies) => {
	return attribute
		.filter(
			attr =>
				!filter.includes(attr.id) &&
				!filter.includes(attr.parent) &&
				!dependencies[attr.id] &&
				(attr.items == null || values[attr.id] != null) // Values are not set before step 2
		)
		.map(attr => {
			return [
				attr.id,
				attr.items
					? values[attr.id].map(val => _filterAttributes(val, filter, attr.items, dependencies))
					: values[attr.id]
			]
		})
		.reduce((res, [key, val]) => ({ ...res, [key]: val }), {})
}

// Remove all values with ids in filter at index
// If last element in array is removed, then remove children and dependencies
export const _filterArrayAttributes = (values, selectedIds, filter, index) => {
	let copy = JSON.parse(JSON.stringify(values))
	let attributeIds = selectedIds.slice()
	const AttributtManagerInstance = new AttributtManager()
	let deletedIds = []
	attributeIds.filter(key => filter.includes(key)).forEach(key => {
		copy[key].splice(index, 1)
		if (copy[key].length == 0) {
			let ind = attributeIds.indexOf(key)
			deletedIds.push(attributeIds[ind])
			attributeIds.splice(ind, 1)
			delete copy[key]
		}
	})
	let dependencies = AttributtManagerInstance.listDependencies(deletedIds)
	return {
		values:
			Object.keys(dependencies).length > 0
				? _filterAttributes(
						copy,
						deletedIds,
						AttributtManagerInstance.listAllSelected(attributeIds),
						dependencies
				  )
				: copy,
		attributeIds: attributeIds.filter(attr => !dependencies[attr])
	}
}

export const findAaregKeyValue = (item, element) => {
	let keys = []
	if (item.subItems && element[item.id] !== '') {
		let periodekey = 'periode'
		item.subItems[0].subSubKategori.id === 'permisjon' && (periodekey = 'permisjonsPeriode')
		//element[item.id] !== '' &&
		element[item.id] &&
			element[item.id].map((subitem, idx) => {
				let key = {}
				Object.keys(subitem).map(itemkey => {
					itemkey !== 'fom' && itemkey !== 'tom'
						? Object.assign(key, { [itemkey]: element[item.id][idx][itemkey] })
						: Object.assign(key, {
								[periodekey]: {
									['fom']: element[item.id][idx].fom,
									['tom']: element[item.id][idx].tom
								}
						  })
				})
				keys.push(key)
			})
		return keys
	}
	return element[item.id]
}

export const parseSubItemDate = (item, rad, radTransformation) => {
	item.subItems.map(subItem => {
		if (subItem.inputType === 'date') {
			radTransformation = {
				...radTransformation,
				[subItem.id]: DataFormatter.parseDate(rad[subItem.id])
			}
		}
	})

	return radTransformation
}

export const deletePropertiesWithoutValues = obj => {
	Object.keys(obj).map(key => !obj[key] && delete obj[key])
	return obj
}

export const isDate = attr => {
	if (attr.length !== 10) return false

	const splittet = attr.split('.')
	return splittet[0].length === 2 && splittet[1].length === 2 && splittet[2].length === 4
}
