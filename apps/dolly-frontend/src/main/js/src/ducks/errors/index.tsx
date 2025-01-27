import { createAction } from 'redux-actions'
import { createSelector } from 'reselect'
import * as _ from 'lodash-es'

export const clearAllErrors = createAction('ERRORS/CLEAR_ALL_ERRORS')
export const addGlobalError = createAction('ERRORS/ADD_GLOBAL_ERROR')

// SELECTORS
export const createErrorMessageSelector = (actions) => (state) => {
	const errors = actions.map((action) => state.errors[action])
	if (errors && errors[0]) {
		return errors[0]
	}
	return ''
}

// Pick any error
export const applicationErrorSelector = createSelector(
	(state) => state.errors,
	(errors) => {
		const filtered = _.filter(errors, (val) => val !== '')
		return filtered[Object.keys(filtered)[0]]
	},
)

const initialState = {}

export default function errorReducer(state = initialState, action) {
	const { type, payload } = action
	const matches = /(.*)_(REQUEST|FAILURE)/.exec(type)

	if (type === clearAllErrors.toString()) return initialState

	if (type === addGlobalError.toString()) {
		return {
			...state,
			errors: action.payload,
		}
	}

	// not a *_REQUEST / *_FAILURE actions, so we ignore them
	if (!matches) return state

	const [requestNameFull, requestName, requestState] = matches
	return {
		...state,
		// Store errorMessage
		// e.g. stores errorMessage when receiving GET_TODOS_FAILURE
		//      else clear errorMessage when receiving GET_TODOS_REQUEST
		[requestName]: requestState === 'FAILURE' ? payload.customMessage || payload.message : '',
	}
}
