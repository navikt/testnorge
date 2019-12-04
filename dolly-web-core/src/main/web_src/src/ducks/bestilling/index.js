import { createActions } from 'redux-actions'
import { DollyApi } from '~/service/Api'

export const actions = createActions({
	postBestillingFraEksisterendeIdenter: DollyApi.createBestillingFraEksisterendeIdenter,
	postBestilling: DollyApi.createBestilling,
	getBestillingMaler: DollyApi.getBestillingMaler
})

export const sendBestilling = (values, gruppeId) => async (dispatch, getState) => {
	console.log('Send bestilling', values)
	if (values.opprettFraIdenter) {
		return dispatch(actions.postBestillingFraEksisterendeIdenter(gruppeId, values))
	} else {
		return dispatch(actions.postBestilling(gruppeId, values))
	}
}
