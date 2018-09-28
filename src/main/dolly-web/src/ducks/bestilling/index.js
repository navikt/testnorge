import { LOCATION_CHANGE } from 'connected-react-router'
import { DollyApi } from '~/service/Api'
import _xor from 'lodash/fp/xor'
import _get from 'lodash/get'
import _set from 'lodash/set'
import { handleActions, createActions, combineActions } from 'redux-actions'
import success from '~/utils/SuccessAction'
import { AttributtManager } from '~/service/Kodeverk'

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
	console.log(bestillingState)
	const { attributeIds, antall, environments, identtype, values } = bestillingState
	const AttributtListe = AttributtManagerInstance.listSelectedGroupedByParent(attributeIds)

	const final_values = {
		antall: antall,
		environments: environments,
		tpsf: {
			regdato: new Date(),
			identtype: identtype,
			...getTpsfValues(AttributtListe, values)
		}
	}

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

const getTpsfValues = (attributeList, values) => {
	//TODO: Legg inn filter for datasource type så vi kun får TPSF verdier.

	return attributeList.reduce((accumulator, attribute) => {
		console.log(attribute)
		if (attribute.multiple) {
			console.log(values)
			return _set(accumulator, attribute.path, values[attribute.id])
		}

		return _set(accumulator, attribute.path || attribute.id, values[attribute.id])
	}, {})
}

export const sendBestilling = gruppeId => async (dispatch, getState) => {
	const { bestilling } = getState()
	const values = bestillingFormatter(bestilling)
	return dispatch(actions.postBestilling(gruppeId, values))
}
