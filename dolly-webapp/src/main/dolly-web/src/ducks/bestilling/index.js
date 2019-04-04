import { LOCATION_CHANGE } from 'connected-react-router'
import { DollyApi } from '~/service/Api'
import _xor from 'lodash/fp/xor'
import _get from 'lodash/get'
import _set from 'lodash/set'
import _groupBy from 'lodash/groupBy'
import _union from 'lodash/union'
import _difference from 'lodash/difference'
import DataFormatter from '~/utils/DataFormatter'
import DataSourceMapper from '~/utils/DataSourceMapper'
import BestillingMapper from '~/utils/BestillingMapper'
import { handleActions, createActions, createAction, combineActions } from 'redux-actions'
import success from '~/utils/SuccessAction'
import { AttributtManager } from '~/service/Kodeverk'
import Bestilling from '../../pages/bestilling/Bestilling'

const AttributtManagerInstance = new AttributtManager()

export const actions = createActions(
	{
		POST_BESTILLING_FRA_EKSISTERENDE_IDENTER: (gruppeId, value) =>
			DollyApi.createBestillingFraEksisterendeIdenter(gruppeId, value),
		POST_BESTILLING: (gruppeId, values) => DollyApi.createBestilling(gruppeId, values)
	},
	'NEXT_PAGE',
	'PREV_PAGE',
	'TOGGLE_ATTRIBUTE',
	'UNCHECK_ALL_ATTRIBUTES',
	'CHECK_ATTRIBUTE_ARRAY',
	'UNCHECK_ATTRIBUTE_ARRAY',
	'SET_ENVIRONMENTS',
	'SET_VALUES',
	'DELETE_VALUES',
	'DELETE_VALUES_ARRAY',
	'START_BESTILLING',
	'SET_IDENT_OPPRETTES_FRA',
	'SET_IDENT_LISTER',
	'ABORT_BESTILLING'
)

const initialState = {
	antall: 1,
	page: 0,
	attributeIds: [],
	environments: [],
	identtype: '',
	values: {},
	identOpprettesFra: BestillingMapper(),
	eksisterendeIdentListe: [],
	ugyldigIdentListe: []
}

export default handleActions(
	{
		[actions.nextPage](state, action) {
			return { ...state, page: state.page + 1 }
		},
		[actions.prevPage](state, action) {
			return { ...state, page: state.page - 1 }
		},
		[actions.toggleAttribute](state, action) {
			let attributeIds = _xor(state.attributeIds, [action.payload])
			return {
				...state,
				values:
					attributeIds.length > state.attributeIds.length
						? state.values
						: _filterAttributes(
								state.values,
								[action.payload],
								AttributtManagerInstance.listAllSelected(state.attributeIds),
								AttributtManagerInstance.listDependencies([action.payload])
						  ),
				attributeIds: attributeIds
			}
		},
		[actions.uncheckAllAttributes](state, action) {
			return { ...state, attributeIds: [] }
		},
		[actions.checkAttributeArray](state, action) {
			return { ...state, attributeIds: _union(state.attributeIds, action.payload) }
		},
		[actions.uncheckAttributeArray](state, action) {
			return { ...state, attributeIds: _difference(state.attributeIds, action.payload) }
		},
		[actions.startBestilling](state, action) {
			return {
				...state,
				identtype: action.payload.identtype,
				antall: action.payload.antall,
				page: state.page + 1
			}
		},
		[actions.setEnvironments](state, action) {
			return {
				...state,
				environments: action.payload.values,
				...(action.payload.goBack && { page: state.page - 1 })
			}
		},
		[actions.setValues](state, action) {
			// Remove empty values

			let copy = JSON.parse(JSON.stringify(action.payload.values))
			Object.entries(copy).forEach(([key, value]) => {
				if (value === '') {
					delete copy[key]
				}
			})

			return {
				...state,
				values: action.payload.values,
				page: (state.page += action.payload.goBack ? -1 : 1)
			}
		},
		[actions.deleteValues](state, action) {
			return {
				...state,
				values: _filterAttributes(
					state.values,
					action.payload.values,
					AttributtManagerInstance.listAllSelected(state.attributeIds),
					AttributtManagerInstance.listDependencies(action.payload.values)
				),
				attributeIds: state.attributeIds.filter(key => !action.payload.values.includes(key))
			}
		},
		[actions.deleteValuesArray](state, action) {
			return {
				...state,
				..._filterArrayAttributes(
					state.values,
					state.attributeIds,
					action.payload.values,
					action.payload.index
				)
			}
		},
		[actions.setIdentOpprettesFra](state, action) {
			return { ...state, identOpprettesFra: action.payload }
		},
		[actions.setIdentLister](state, action) {
			return {
				...state,
				eksisterendeIdentListe: action.payload.gyldigIdentListe,
				ugyldigIdentListe: action.payload.ugyldigIdentListe
			}
		},
		[combineActions(actions.abortBestilling, LOCATION_CHANGE, success(actions.postBestilling))](
			state,
			action
		) {
			return initialState
		},
		[combineActions(
			actions.abortBestilling,
			LOCATION_CHANGE,
			success(actions.postBestillingFraEksisterendeIdenter)
		)](state, action) {
			return initialState
		}
	},
	initialState
)

// TODO: Denne må ryddes opp i og gjøres bedre
// - kanskje flyttes ut til egen fil (er jo bare en formatter og ikke thunk)
// - kan dette være mer generisk? bruke datasource nodene i AttributtManager?
// - CNN: LAGT TIL TPSF HARDKODET FOR NÅ FOR TESTING. FINN GENERISK LØSNING
const bestillingFormatter = bestillingState => {
	console.log('bestillingState :', bestillingState)
	const {
		attributeIds,
		antall,
		environments,
		identtype,
		values,
		identOpprettesFra,
		eksisterendeIdentListe
	} = bestillingState
	const AttributtListe = AttributtManagerInstance.listAllSelected(attributeIds)
	let final_values = []

	identOpprettesFra === BestillingMapper()
		? (final_values = {
				antall: antall,
				environments: environments,
				...getValues(AttributtListe, values)
		  })
		: (final_values = {
				opprettFraIdenter: eksisterendeIdentListe,
				environments: environments,
				...getValues(AttributtListe, values)
		  })

	// mandatory
	final_values = _set(final_values, 'tpsf.regdato', new Date())
	identOpprettesFra === BestillingMapper() && (final_values.tpsf.identtype = identtype)

	console.log('final_values :', final_values)

	// TODO: SPECIAL HANDLING - Hva gjør vi her?
	if (_get(final_values, 'tpsf.boadresse.gateadresse')) {
		final_values.tpsf.boadresse.adressetype = 'GATE'
		final_values.tpsf.boadresse.gatekode = values.boadresse_gatekode
	}
	// if (_get(final_values, 'tpsf.postadresse.postLand')) {
	// 	final_values.tpsf.postadresse.postLand = 'NORGE'
	// }

	// if (_get(final_values, 'tpsf.postadresse.postLand')) {
	// 	final_values.tpsf.postadresse = [
	// 		{
	// 			postLand: '404',
	// 			postLinje1: 'string',
	// 			postLinje2: 'string',
	// 			postLinje3: 'string'
	// 		}
	// 	]
	// }

	// if (_get(final_values, 'tpsf.postadresse.postLand')) {
	// 	if (final_values.tpsf.postadresse.get())
	// if
	//		var re = /^\d{4}/;
	// }

	console.log('POSTING BESTILLING', final_values)

	return final_values
}

// TODO: Kan getValues og transformAttributt merges?
const getValues = (attributeList, values) => {
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
			// TODO: Alex, construct a json-object before array
			let dataArr = []

			value.forEach(element => {
				let aaregObj = {}
				attribute.items.forEach(item => {
					const path = item.path
					const id = item.id
					let keyValue = element[id]

					Object.assign(aaregObj, { [path]: { ...aaregObj[path], [id]: keyValue } })
				})

				// aktorID = PERS
				// TODO: bytt heller inputfelte som ny attribute
				if (aaregObj.arbeidsgiver.aktoertype == 'PERS') {
					const aktoertype = aaregObj.arbeidsgiver.aktoertype
					const arbeidsgiverIdent = aaregObj.arbeidsgiver.orgnummer
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
				dataArr.push(aaregObj)
			})
			return _set(accumulator, pathPrefix, dataArr)
		}

		return _set(accumulator, `${pathPrefix}.${attribute.path || attribute.id}`, value)
	}, {})
}

// Transform attributes before order is sent
// Date, boolean...
const _transformAttributt = (attribute, attributes, value) => {
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
		})

		return valueDeepCopy
	} else if (attribute.items) {
		let attributeList = attribute.items.reduce((res, acc) => ({ ...res, [acc.id]: acc }), {})
		return value.map(val =>
			Object.assign(
				{},
				...Object.entries(val).map(([key, value]) => {
					let pathId = attributeList[key].path.split('.')
					return {
						//  Hente kun siste key, f.eks barn.kjønn => kjønn
						[pathId[pathId.length - 1]]: _transformAttributt(attributeList[key], attributes, value)
					}
				})
			)
		)
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
const _filterAttributes = (values, filter, attribute, dependencies) => {
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
const _filterArrayAttributes = (values, selectedIds, filter, index) => {
	let copy = JSON.parse(JSON.stringify(values))
	let attributeIds = selectedIds.slice()
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

export const sendBestilling = gruppeId => async (dispatch, getState) => {
	const { currentBestilling } = getState()
	const values = bestillingFormatter(currentBestilling)

	if (currentBestilling.identOpprettesFra === BestillingMapper('EKSIDENT')) {
		return dispatch(actions.postBestillingFraEksisterendeIdenter(gruppeId, values))
	} else {
		return dispatch(actions.postBestilling(gruppeId, values))
	}
}
