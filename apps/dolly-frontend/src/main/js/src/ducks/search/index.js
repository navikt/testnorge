import { LOCATION_CHANGE } from 'connected-react-router'
import { createAction } from 'redux-actions'
import { SEARCH_IDENT } from '~/pages/gruppe/PersonVisning/TidligereBestillinger/TidligereBestillinger'

export const setSearchText = createAction('SEARCH/SET_TEXT')
export const resetSearch = createAction('SEARCH/RESET_SEARCH')

const initialState = ''

export default (state = initialState, action) => {
	{
		switch (action.type) {
			case LOCATION_CHANGE:
			case resetSearch.toString():
				sessionStorage.removeItem(SEARCH_IDENT)
				return initialState
			case setSearchText.toString():
				return action.payload
			default:
				return state
		}
	}
}
