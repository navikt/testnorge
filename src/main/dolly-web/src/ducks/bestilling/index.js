import { push, LOCATION_CHANGE } from 'connected-react-router'
import { DollyApi } from '~/service/Api'
import _xor from 'lodash/fp/xor'
import _get from 'lodash/get'
import success from '~/utils/SuccessAction'

export const types = {
	NEXT_PAGE: 'bestilling/neste-side',
	PREV_PAGE: 'bestilling/forrige-side',
	TOGGLE_ATTRIBUTES: 'bestilling/toggle-attributter',
	SET_VALUES: 'bestilling/sett-verdier',
	SET_ENVIRONMENTS: 'bestilling/sett-miljo',
	START_BESTILLING: 'bestilling/start',
	ABORT_BESTILLING: 'bestilling/abort',
	POST_BESTILLING: 'bestilling/send-bestilling',
	POST_BESTILLING_SUCCESS: 'bestilling/send-success',
	POST_BESTILLING_ERROR: 'bestilling/send-error'
}

const initialState = {
	page: 0,
	fetching: false,
	attributeIds: [],
	environments: [],
	antall: 0,
	identtype: '',
	values: {},
	error: null
}

// const TEMP_INITIAL_STATE = {
// 	page: 3,
// 	fetching: false,
// 	attributeIds: ['kjonn', 'statsborgerskap'],
// 	environments: [],
// 	antall: 2,
// 	identtype: 'FNR',
// 	values: {
// 		kjonn: 'kvinne',
// 		statsborgerskap: 'Norsk'
// 	},
// 	error: null
// }

export default function bestillingReducer(state = initialState, action) {
	switch (action.type) {
		case LOCATION_CHANGE:
			return initialState
		case types.NEXT_PAGE:
			return { ...state, page: state.page + 1 }
		case types.PREV_PAGE:
			return { ...state, page: state.page - 1 }
		case types.TOGGLE_ATTRIBUTES:
			return { ...state, attributeIds: _xor(state.attributeIds, [action.attributeId]) }
		case types.SET_ENVIRONMENTS:
			return { ...state, environments: action.environmentIds }
		case types.SET_VALUES:
			return { ...state, values: action.values }
		case types.START_BESTILLING:
			return { ...state, identtype: action.identtype, antall: action.antall }
		case types.POST_BESTILLING:
			return { ...state, fetching: true }
		case types.POST_BESTILLING_ERROR:
			return { ...state, fetching: false, error: action.error }

		// Reset state on succesfull & location change
		case LOCATION_CHANGE:
		case types.ABORT_BESTILLING:
		case types.POST_BESTILLING_SUCCESS:
			return initialState

		default:
			return state
	}
}

export const nextPage = () => ({ type: types.NEXT_PAGE })
export const prevPage = () => ({ type: types.PREV_PAGE })
export const toggleAttribute = attributeId => ({ type: types.TOGGLE_ATTRIBUTES, attributeId })
export const setEnvironments = environmentIds => ({ type: types.SET_ENVIRONMENTS, environmentIds })
const setValuesAction = values => ({ type: types.SET_VALUES, values })
const startBestillingAction = (identtype, antall) => ({
	type: types.START_BESTILLING,
	identtype,
	antall
})
const abortBestillingAction = () => ({ type: types.ABORT_BESTILLING })

const _bestillingRequest = () => ({ type: types.POST_BESTILLING })
const _bestillingRequestSuccess = () => ({ type: types.POST_BESTILLING_SUCCESS })
const _bestillingRequestError = error => ({ type: types.POST_BESTILLING_ERROR, error })

export const startBestilling = (identtype, antall) => async dispatch => {
	dispatch(startBestillingAction(identtype, antall))
	dispatch(nextPage())
}

export const setEnvironmentsAndGoBack = environmentIds => dispatch => {
	dispatch(setEnvironments(environmentIds))
	dispatch(prevPage())
}

export const setValuesAndGoBack = values => dispatch => {
	dispatch(setValuesAction(values))
	dispatch(prevPage())
}

export const setValues = values => async dispatch => {
	dispatch(setValuesAction(values))
	dispatch(nextPage())
}

export const abortBestilling = gruppeId => async dispatch => {
	dispatch(abortBestillingAction())

	// Navigate to group page
	dispatch(push(`/gruppe/${gruppeId}`))
}

export const postBestilling = gruppeId => async (dispatch, getState) => {
	const { bestilling } = getState()

	try {
		dispatch(_bestillingRequest())

		const final_values = {
			regdato: new Date(),
			identtype: bestilling.identtype,
			antall: bestilling.antall,
			environments: bestilling.environments,
			...bestilling.values
		}

		// TODO: SPECIAL HANDLING - Hva gjør vi her?
		if (_get(final_values, 'boadresse.gateadresse')) {
			final_values.boadresse.adressetype = 'GATE'
		}

		console.log('POSTING BESTILLING', final_values)

		const res = await DollyApi.createBestilling(gruppeId, final_values)
		dispatch(_bestillingRequestSuccess(res))

		// Navigate to group page
		dispatch(push(`/gruppe/${gruppeId}`))
	} catch (error) {
		dispatch(_bestillingRequestError(error))
	}
}
