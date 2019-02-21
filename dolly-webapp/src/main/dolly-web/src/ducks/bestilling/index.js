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
import { handleActions, createActions, combineActions } from 'redux-actions'
import success from '~/utils/SuccessAction'
import { AttributtManager } from '~/service/Kodeverk'

const AttributtManagerInstance = new AttributtManager()

export const actions = createActions(
	{
		POST_BESTILLING: [
			async (gruppeId, values) => {
				const res = await DollyApi.createBestilling(gruppeId, values) // Payload
				// TODO: Med nye reducer setup trenger vi ikke 2dette lenge
				return { ...res, data: { ...res.data, ny: true } }
			},
			gruppeId => ({ gruppeId }) // Meta
		]
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
	'ABORT_BESTILLING'
)

const initialState = {
	antall: 1,
	page: 0,
	attributeIds: [],
	environments: [],
	identtype: '',
	values: {}
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
			return {
				...state,
				attributeIds: _xor(state.attributeIds, [action.payload])
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
			return {
				...state,
				values: action.payload.values,
				page: (state.page += action.payload.goBack ? -1 : 1)
			}
		},
		[actions.deleteValues](state, action) {
			return {
				...state,
				values: Object.keys(state.values)
					.filter(key => !action.payload.values.includes(key))
					.reduce((obj, key) => ({ ...obj, [key]: state.values[key] }), {}),
				attributeIds: state.attributeIds.filter(key => !action.payload.values.includes(key))
			}
		},
		[actions.deleteValuesArray](state, action) {
			let copy = JSON.parse(JSON.stringify(state.values))
			let attributeIds = state.attributeIds.slice()
			attributeIds.filter(key => action.payload.values.includes(key)).forEach(key => {
				copy[key].splice(action.payload.index, 1)
				if (copy[key].length == 0) {
					attributeIds.splice(attributeIds.indexOf(key), 1)
					delete copy[key]
				}
			})
			return {
				...state,
				values: copy,
				attributeIds: attributeIds
			}
		},

		[combineActions(actions.abortBestilling, LOCATION_CHANGE, success(actions.postBestilling))](
			state,
			action
		) {
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
	const { attributeIds, antall, environments, identtype, values } = bestillingState
	const AttributtListe = AttributtManagerInstance.listAllSelected(attributeIds)

	let final_values = {
		antall: antall,
		environments: environments,
		...getValues(AttributtListe, values)
	}
	// mandatory
	final_values = _set(final_values, 'tpsf.regdato', new Date())
	final_values.tpsf.identtype = identtype

	// TODO: SPECIAL HANDLING - Hva gjør vi her?
	if (_get(final_values, 'tpsf.boadresse.gateadresse')) {
		final_values.tpsf.boadresse.adressetype = 'GATE'
	}
	console.log('POSTING BESTILLING', final_values)

	// TODO: Hvis det dukker opp flere slike tilfelle, vurder å expande AttributeSystem
	// KUN FOR egen ansatt - spesielt tilfelle
	if (final_values.tpsf.egenAnsattDatoFom != null) {
		if (final_values.tpsf.egenAnsattDatoFom) {
			final_values.tpsf.egenAnsattDatoFom = new Date()
		} else final_values.tpsf.egenAnsattDatoFom = null
	}

	return final_values
}

const getValues = (attributeList, values) => {
	return attributeList.reduce((accumulator, attribute) => {
		let value = values[attribute.id]
		const pathPrefix = DataSourceMapper(attribute.dataSource)

		const isDate = inputType => inputType === 'date'

		// Convert to Date objects
		if (isDate(attribute.inputType)) value = DataFormatter.parseDate(value)
		// Do the same for Array values
		if (attribute.items) {
			const dateFields = attribute.items.filter(item => isDate(item.inputType))
			value = value.map((item, idx) => {
				return dateFields.reduce((acc, dateAttribute) => {
					const pathId = dateAttribute.id
					return Object.assign({}, acc, {
						[pathId]: DataFormatter.parseDate(item[pathId])
					})
				}, item)
			})
		}

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
		return _set(accumulator, `${pathPrefix}.${attribute.path || attribute.id}`, value)
	}, {})
}

export const sendBestilling = gruppeId => async (dispatch, getState) => {
	const { currentBestilling } = getState()
	const values = bestillingFormatter(currentBestilling)
	return dispatch(actions.postBestilling(gruppeId, values))
}
