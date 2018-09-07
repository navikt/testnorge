import { LOCATION_CHANGE } from 'connected-react-router'
import { createAction } from 'redux-actions'

export const setSearchText = createAction('SEARCH/SET_TEXT')

const initialState = ''

export default (state = initialState, action) => {
	switch (action.type) {
		case LOCATION_CHANGE:
			return initialState
		case setSearchText:
			return action.payload.text
		default:
			return state
	}
}
