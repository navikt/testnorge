import { LOCATION_CHANGE } from 'connected-react-router'
import { DollyApi } from '~/service/Api'
import _xor from 'lodash/fp/xor'
import _get from 'lodash/get'
import { handleActions, createActions, combineActions } from 'redux-actions'
import success from '~/utils/SuccessAction'

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

console.log(actions)

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
const bestillingFormatter = bestillingState => {
	const final_values = {
		regdato: new Date(),
		identtype: bestillingState.identtype,
		antall: bestillingState.antall,
		environments: bestillingState.environments,
		...bestillingState.values
	}

	// TODO: SPECIAL HANDLING - Hva gjør vi her?
	if (_get(final_values, 'boadresse.gateadresse')) {
		final_values.boadresse.adressetype = 'GATE'
	}

	console.log('POSTING BESTILLING', final_values)

	return final_values
}

export const sendBestilling = gruppeId => async (dispatch, getState) => {
	const { bestilling } = getState()
	const values = bestillingFormatter(bestilling)
	return dispatch(actions.postBestilling(gruppeId, values))
}
