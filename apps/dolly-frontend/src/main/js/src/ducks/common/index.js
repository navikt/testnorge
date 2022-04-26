import { LOCATION_CHANGE } from 'redux-first-history'
import { onSuccess } from '~/ducks/utils/requestActions'
import { actions } from '~/ducks/gruppe'

export const redirectReducer = (state = null, action) => {
	switch (action.type) {
		case onSuccess(actions.create):
			return `/gruppe/${action.payload.data.id}`
		case onSuccess(actions.remove):
			return '/'
		case LOCATION_CHANGE:
		default:
			return null
	}
}

export default function commonReducer(state, action) {
	return {
		redirectTo: redirectReducer(state, action),
	}
}
