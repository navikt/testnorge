import { LOCATION_CHANGE } from 'connected-react-router'
import { DollyApi } from '~/service/Api'
import _xor from 'lodash/fp/xor'
import _get from 'lodash/get'
import _set from 'lodash/set'
import DataFormatter from '~/utils/DataFormatter'
import { handleActions, createActions, combineActions } from 'redux-actions'
import success from '~/utils/SuccessAction'
import { AttributtManager } from '~/service/Kodeverk'
import DataSourceMapper from './DateSourceMapper'

const AttributtManagerInstance = new AttributtManager()

export const actions = createActions(
	{
		POST_BESTILLING: [
			(gruppeId, values) => DollyApi.createBestilling(gruppeId, values), // Payload
			gruppeId => ({ gruppeId }) // Meta
		]
	},
	'NEXT_PAGE',
	'PREV_PAGE',
	'TOGGLE_ATTRIBUTE',
	'UNCHECK_ALL_ATTRIBUTES',
	'SET_ENVIRONMENTS',
	'SET_VALUES',
	'START_BESTILLING',
	'ABORT_BESTILLING'
)

const initialState = {
	page: 0,
	attributeIds: [],
	environments: [],
	antall: 0,
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
			return { ...state, attributeIds: _xor(state.attributeIds, [action.payload]) }
		},
		[actions.uncheckAllAttributes](state, action) {
			return { ...state, attributeIds: [] }
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
		// tpsf: {
		// 	regdato: new Date(),
		// 	identtype: identtype,
		// 	...formatTpsfValues(AttributtListe, values)
		// },
		// sigrunRequest: {
		// 	// ...formatSigrunValues(AttributtListe, values)
		// }

		...getValues(AttributtListe, values)
	}

	// mandatory
	final_values = _.set(final_values, 'tpsf.regdato', new Date())

	final_values.tpsf.identtype = identtype

	// TODO: SPECIAL HANDLING - Hva gjør vi her?
	if (_get(final_values, 'tpsf.boadresse.gateadresse')) {
		final_values.tpsf.boadresse.adressetype = 'GATE'
	}

	//if (_get(final_values, 'tpsf.relasjoner.barn')) {
	//	final_values.tpsf.relasjoner.barn = [final_values.tpsf.relasjoner.barn]
	//}

	console.log('POSTING BESTILLING', final_values)

	return final_values
}

const getValues = (attributeList, values) => {
	var result
	result = attributeList.reduce((accumulator, attribute) => {
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

		// TODO: SIGRUN har en annen format som ikke er lett å generaliseres. Suggestion: separate formatter for hver register?
		if (pathPrefix == DataSourceMapper('SIGRUN')) {
			console.log('SIGRUNN')

			// value.forEach(item => {
			// 	console.log('acc', accumulator)
			// 	return (
			// 		_set(accumulator, `${pathPrefix}.grunnlag`, [
			// 			{ tekniskNavn: item.typeinntekt, verdi: item.beloep }
			// 		]) &&
			// _set(accumulator, `${pathPrefix}.inntektsaar`, '2017') &&
			// 	_set(accumulator, `${pathPrefix}.tjeneste`, 'Beregnet skatt')
			// 	)
			// })

			return (
				_set(accumulator, `${pathPrefix}.grunnlag`, [
					{ tekniskNavn: value[0].typeinntekt, verdi: value[0].beloep }
				]) &&
				_set(accumulator, `${pathPrefix}.inntektsaar`, value[0].inntektsaar) &&
				_set(accumulator, `${pathPrefix}.tjeneste`, attribute.id)
			)
		} else {
			// Tpsf
			return _set(accumulator, `${pathPrefix}.${attribute.path || attribute.id}`, value)
		}
	}, {})

	console.log(result)
	return result
}

export const sendBestilling = gruppeId => async (dispatch, getState) => {
	const { bestilling } = getState()
	const values = bestillingFormatter(bestilling)
	return dispatch(actions.postBestilling(gruppeId, values))
}
