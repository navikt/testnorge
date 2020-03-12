import { LOCATION_CHANGE } from 'connected-react-router'
import { createAction } from 'redux-actions'

export const setSearchText = createAction('SEARCH/SET_TEXT')
export const resetSearch = createAction('SEARCH/RESET_SEARCH')

const initialState = ''

export default (state = initialState, action) => {
	{
		switch (action.type) {
			case LOCATION_CHANGE:
			case resetSearch.toString():
				return initialState
			case setSearchText.toString():
				return action.payload
			default:
				return state
		}
	}
}
