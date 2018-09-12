import { LOCATION_CHANGE } from 'connected-react-router'
import success from '~/utils/SuccessAction'
import { createGruppe } from '~/ducks/gruppe'
import { actions as teamActions } from '~/ducks/teams'
import { actions as bestillingActions } from '~/ducks/bestilling'

const redirectReducer = (state = null, action) => {
	switch (action.type) {
		case success(createGruppe):
			return `/gruppe/${action.payload.data.id}`
		case success(teamActions.api.create):
			return `/team/${action.payload.data.id}`
		case bestillingActions.abortBestilling.toString():
			return `/gruppe/${action.payload}`
		case success(bestillingActions.postBestilling):
			return `/gruppe/${action.meta.gruppeId}`
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
