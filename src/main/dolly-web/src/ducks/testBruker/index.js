import { TpsfApi } from '~/service/Api'
import { LOCATION_CHANGE } from 'connected-react-router'
import { createAction } from 'redux-actions'
import tpsfTransformer from './tpsfTransformer'

export const GET_TESTBRUKERE = createAction('GET_TESTBRUKERE', identArray =>
	TpsfApi.getTestbrukere(identArray).then(tpsfTransformer)
)

const initialState = {
	items: null
}

export default function testbrukerReducer(state = initialState, action) {
	switch (action.type) {
		case LOCATION_CHANGE:
			return initialState
		case `${GET_TESTBRUKERE}_SUCCESS`:
			return { ...state, items: action.payload }
		default:
			return state
	}
}
