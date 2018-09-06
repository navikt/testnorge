import { LOCATION_CHANGE } from 'connected-react-router'

export const types = {
	SET: 'search/set'
}

export default (state = '', action) => {
	switch (action.type) {
		case LOCATION_CHANGE:
			return ''
		case types.SET:
			return action.text
		default:
			return state
	}
}

export const setSearchText = text => ({
	type: types.SET,
	text
})
