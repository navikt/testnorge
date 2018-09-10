import { LOCATION_CHANGE } from 'connected-react-router'
import { createAction } from 'redux-actions'

export const setSort = createAction('SORT/SET')

const initialState = {
	id: 'id',
	order: 'desc'
}

export default (state = initialState, action) => {
	switch (action.type) {
		case LOCATION_CHANGE:
			return initialState
		case `${setSort}`:
			return action.payload
		default:
			return state
	}
}
