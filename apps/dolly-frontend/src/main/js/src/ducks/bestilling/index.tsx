import { createActions } from 'redux-actions'
import { DollyApi } from '@/service/Api'
import * as _ from 'lodash-es'
import _set from 'lodash/fp/set'
import { handleActions } from '@/ducks/utils/immerHandleActions'
import { getLeggTilIdent, rootPaths } from '@/components/bestillingsveileder/utils'
import { v4 as uuid } from 'uuid'
import { Logger } from '@/logger/Logger'

export const actions = createActions(
	{
		postBestillingLeggTilPaaPerson: DollyApi.createBestillingLeggTilPaaPerson,
		postBestillingFraEksisterendeIdenter: DollyApi.createBestillingFraEksisterendeIdenter,
		postBestilling: DollyApi.createBestilling,
		postOrganisasjonBestilling: DollyApi.createOrganisasjonBestilling,
		postTestnorgeBestilling: DollyApi.importerPersonerFraPdl,
		bestillingFeilet: (error) => ({ error }),
	},
	{ prefix: 'bestveil' }
)

const initialState = {
	error: null,
}

export default handleActions(
	{
		[actions.bestillingFeilet](state, action) {
			state.error = action.payload.error
		},
	},
	initialState
)

const trackBestilling = (values) => {
	const _uuid = uuid()
	Object.keys(values)
		.filter((key) => rootPaths.find((value) => value === key))
		.forEach((key) => {
			Logger.trace({
				event: 'Bestilling av omraade: ' + key,
				uuid: _uuid,
			})
		})
}

/**
 * Sender de ulike bestillingstypene fra Bestillingsveileder
 */
export const sendBestilling = (values, opts, gruppeId, navigate) => async (dispatch) => {
	let bestillingAction

	if (opts.is.leggTil) {
		const ident = getLeggTilIdent(opts.personFoerLeggTil, opts.identMaster)
		bestillingAction = actions.postBestillingLeggTilPaaPerson(ident, values)
	} else if (opts.is.opprettFraIdenter) {
		values = _set('opprettFraIdenter', opts.opprettFraIdenter, values)
		bestillingAction = actions.postBestillingFraEksisterendeIdenter(gruppeId, values)
	} else if (opts.is.importTestnorge) {
		values = _set(
			'identer',
			opts.importPersoner.map((person) => person.ident),
			values
		)
		if (!values.environments) {
			values = _set('environments', [], values)
		}
		bestillingAction = actions.postTestnorgeBestilling(values.gruppeId, values)
	} else if (values.organisasjon) {
		trackBestilling(values)
		bestillingAction = actions.postOrganisasjonBestilling(values)
	} else {
		trackBestilling(values)
		bestillingAction = actions.postBestilling(gruppeId, values)
	}

	const response = await dispatch(bestillingAction)

	//IF ALL IS GOOD - REDIRECT
	const res = _.get(response, 'action.payload.data', null)
	const type = _.get(response, 'action.type', null)
	if (res.error) {
		dispatch(actions.bestillingFeilet(res))
	} else if (type.includes('OrganisasjonBestilling')) {
		navigate(`/organisasjoner`)
	} else if (opts.is.importTestnorge) {
		navigate(`/gruppe/${values.gruppeId}`)
	} else {
		navigate(`/gruppe/${gruppeId}`)
	}
}
