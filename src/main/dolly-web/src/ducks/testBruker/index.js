import { TpsfApi, SigrunApi } from '~/service/Api'
import { LOCATION_CHANGE } from 'connected-react-router'
import { createAction } from 'redux-actions'
import _get from 'lodash/get'

export const UPDATE_TESTBRUKER = createAction('UPDATE_TESTBRUKER', TpsfApi.updateTestbruker)

const initialState = {
	items: null
}

export const GET_TPSF_TESTBRUKERE = createAction('GET_TPSF_TESTBRUKERE', identArray => {
	return TpsfApi.getTestbrukere(identArray)
})

export const GET_SIGRUN_TESTBRUKERE = createAction('GET_SIGRUN_TESTBRUKERE', identArray => {
	return SigrunApi.getTestbrukere(identArray)
})

export default function testbrukerReducer(state = initialState, action) {
	switch (action.type) {
		case LOCATION_CHANGE:
			return initialState
		case `${GET_TPSF_TESTBRUKERE}_SUCCESS`:
			return { ...state, items: { ...state.items, tpsf: action.payload.data } }
		case `${GET_SIGRUN_TESTBRUKERE}_SUCCESS`:
			return { ...state, items: { ...state.items, sigrun: action.payload } }
		case `${UPDATE_TESTBRUKER}_SUCCESS`:
			return state
		default:
			return state
	}
}

// Selector
export const sokSelector = (items, searchStr) => {
	if (!items) return null
	if (!searchStr) return items

	const query = searchStr.toLowerCase()
	return items.filter(item => {
		return item.some(v => v.toLowerCase().includes(query))
	})
}
