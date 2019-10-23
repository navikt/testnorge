import { LOCATION_CHANGE } from 'connected-react-router'
import success from '~/utils/SuccessAction'
import { createGruppe, deleteGruppe } from '~/ducks/gruppe'
import { actions as bestillingActions } from '~/ducks/bestilling'

export const redirectReducer = (state = null, action) => {
	switch (action.type) {
		case success(createGruppe):
			return `/gruppe/${action.payload.data.id}`
		case bestillingActions.abortBestilling.toString():
			return `/gruppe/${action.payload}`
		case success(bestillingActions.postBestilling):
			return `/gruppe/${action.payload.data.gruppeId}`
		case success(bestillingActions.postBestillingFraEksisterendeIdenter):
			return `/gruppe/${action.payload.data.gruppeId}`
		case success(deleteGruppe):
			return '/'
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
