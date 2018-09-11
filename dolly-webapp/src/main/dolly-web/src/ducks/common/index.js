import { LOCATION_CHANGE } from 'connected-react-router'
import success from '~/utils/SuccessAction'
import { createGruppe } from '~/ducks/gruppe'
import { actions as teamActions } from '~/ducks/teams'

const redirectReducer = (state = null, action) => {
	switch (action.type) {
		case success(createGruppe):
			return `/gruppe/${action.payload.data.id}`
		case success(teamActions.api.create):
			return `/team/${action.payload.data.id}`
		case LOCATION_CHANGE:
		default:
			return null
	}
}

export default function commonReducer(state, action) {
	return {
		redirectTo: redirectReducer(state, action)
	}
}
