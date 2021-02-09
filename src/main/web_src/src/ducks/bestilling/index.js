import { createActions } from 'redux-actions'
import { LOCATION_CHANGE, push } from 'connected-react-router'
import { DollyApi } from '~/service/Api'
import _set from 'lodash/fp/set'
import _get from 'lodash/get'
import { handleActions } from '~/ducks/utils/immerHandleActions'
import { rootPaths } from '@/components/bestillingsveileder/utils'
import Logger from '@/logger'
import { v4 as uuid } from 'uuid'

export const actions = createActions(
	{
		postBestillingLeggTilPaaPerson: DollyApi.createBestillingLeggTilPaaPerson,
		postBestillingFraEksisterendeIdenter: DollyApi.createBestillingFraEksisterendeIdenter,
		postBestilling: DollyApi.createBestilling,
		postOrganisasjonBestilling: DollyApi.createOrganisasjonBestilling,
		bestillingFeilet: error => ({ error })
	},
	{ prefix: 'bestveil' }
)

const initialState = {
	error: null
}

export default handleActions(
	{
		[LOCATION_CHANGE](state, action) {
			return initialState
		},
		[actions.bestillingFeilet](state, action) {
			state.error = action.payload.error
		}
	},
	initialState
)

const trackBestilling = values => {
	const _uuid = uuid()
	Object.keys(values)
		.filter(key => rootPaths.find(value => value === key))
		.forEach(key => {
			Logger.trace({
				event: 'Bestilling av omraade: ' + key,
				uuid: _uuid
			})
		})
}

/**
 * Sender de ulike bestillingstypene fra Bestillingsveileder
 */
export const sendBestilling = (values, opts, gruppeId) => async (dispatch, getState) => {
	let bestillingAction = null

	if (opts.is.leggTil) {
		bestillingAction = actions.postBestillingLeggTilPaaPerson(
			opts.personFoerLeggTil.tpsf.ident,
			values
		)
	} else if (opts.is.opprettFraIdenter) {
		values = _set('opprettFraIdenter', opts.opprettFraIdenter, values)
		bestillingAction = actions.postBestillingFraEksisterendeIdenter(gruppeId, values)
	} else if (values.organisasjon) {
		trackBestilling(values)
		bestillingAction = actions.postOrganisasjonBestilling(values)
	} else {
		// Sett identType (denne blir ikke satt tidligere grunnet at den sitter inne i tpsf-noden)
		values = _set('tpsf.identtype', opts.identtype, values)

		trackBestilling(values)
		bestillingAction = actions.postBestilling(gruppeId, values)
	}

	const response = await dispatch(bestillingAction)

	//IF ALL IS GOOD - REDIRECT
	const res = _get(response, 'action.payload.data', null)
	const type = _get(response, 'action.type', null)
	if (res.error) {
		dispatch(actions.bestillingFeilet(res))
	} else if (type.includes('OrganisasjonBestilling')) {
		dispatch(push(`/organisasjoner`))
	} else {
		dispatch(push(`/gruppe/${gruppeId}`))
	}
}
