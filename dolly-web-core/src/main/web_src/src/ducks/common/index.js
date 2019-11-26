import { LOCATION_CHANGE } from 'connected-react-router'
import { onSuccess } from '~/ducks/utils/requestActions'
import { createGruppe, deleteGruppe } from '~/ducks/gruppe'
import { actions as bestillingActions } from '~/ducks/bestilling'

export const redirectReducer = (state = null, action) => {
	switch (action.type) {
		case onSuccess(createGruppe):
			return `/gruppe/${action.payload.data.id}`
		case bestillingActions.abortBestilling.toString():
			return `/gruppe/${action.payload}`
		case onSuccess(bestillingActions.postBestilling):
			return `/gruppe/${action.payload.data.gruppeId}`
		case onSuccess(bestillingActions.postBestillingFraEksisterendeIdenter):
			return `/gruppe/${action.payload.data.gruppeId}`
		case onSuccess(deleteGruppe):
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
