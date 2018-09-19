import { TpsfApi } from '~/service/Api'
import { LOCATION_CHANGE } from 'connected-react-router'
import { createAction } from 'redux-actions'
import tpsfTransformer from './tpsfTransformer'

export const getInitialValuesSelector = (state, ownProps) => {
	if (!state.testbruker.items) return null

	console.log(state)
	console.log(ownProps)
}

export const GET_TESTBRUKERE = createAction('GET_TESTBRUKERE', identArray =>
	TpsfApi.getTestbrukere(identArray)
)
export const UPDATE_TESTBRUKER = createAction('UPDATE_TESTBRUKER', TpsfApi.updateTestbruker)

const initialState = {
	items: null
}

export default function testbrukerReducer(state = initialState, action) {
	switch (action.type) {
		case LOCATION_CHANGE:
			return initialState
		case `${GET_TESTBRUKERE}_SUCCESS`:
			return { ...state, items: action.payload.data }
		case `${UPDATE_TESTBRUKER}_SUCCESS`:
			return state
		default:
			return state
	}
}
