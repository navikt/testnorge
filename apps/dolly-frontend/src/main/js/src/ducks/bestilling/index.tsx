import { createActions } from 'redux-actions'
import { DollyApi } from '@/service/Api'
import { handleActions } from '@/ducks/utils/immerHandleActions'
import { getLeggTilIdent, rootPaths } from '@/components/bestillingsveileder/utils'
import { v4 as uuid } from 'uuid'
import * as _ from 'lodash-es'
import { Logger } from '@/logger/Logger'

export const actions = createActions(
	{
		postBestillingLeggTilPaaPerson: DollyApi.createBestillingLeggTilPaaPerson,
		postBestillingFraEksisterendeIdenter: DollyApi.createBestillingFraEksisterendeIdenter,
		postBestillingLeggTilPaaGruppe: DollyApi.createBestillingLeggTilPaaGruppe,
		postBestilling: DollyApi.createBestilling,
		postOrganisasjonBestilling: DollyApi.createOrganisasjonBestilling,
		postTestnorgeBestilling: DollyApi.importerPersonerFraPdl,
		bestillingFeilet: (error) => ({ error }),
	},
	{ prefix: 'bestveil' },
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
	initialState,
)

export interface SendBestillingOptions {
	is: { [key: string]: boolean }
	personFoerLeggTil?: any
	identMaster?: string
	importPersoner?: { ident: string }[]
}

const trackBestilling = (values: Record<string, any>) => {
	const _uuid = uuid()
	Object.keys(values)
		.filter((key) => rootPaths.find((value) => value === key))
		.forEach((key) => {
			Logger.trace({ event: 'Bestilling av omraade', message: key, uuid: _uuid })
		})
}

/**
 * Sender de ulike bestillingstypene fra Bestillingsveileder
 */
export const sendBestilling =
	(values: any, options: SendBestillingOptions, gruppeId: any, navigate: (url: string) => void) =>
	async (dispatch: any) => {
		const opts = options
		const valgtGruppe = values?.gruppeId || gruppeId
		let bestillingAction

		if (opts.is.leggTil) {
			const ident = getLeggTilIdent(opts.personFoerLeggTil, opts.identMaster)
			bestillingAction = actions.postBestillingLeggTilPaaPerson(ident, values)
		} else if (opts.is.leggTilPaaGruppe) {
			bestillingAction = actions.postBestillingLeggTilPaaGruppe(valgtGruppe, values)
		} else if (opts.is.opprettFraIdenter) {
			bestillingAction = actions.postBestillingFraEksisterendeIdenter(valgtGruppe, values)
		} else if (opts.is.importTestnorge) {
			values = Object.assign({}, values, {
				identer: (options.importPersoner || []).map((person: { ident: string }) => person.ident),
				environments: values.environments || [],
			})
			bestillingAction = actions.postTestnorgeBestilling(values.valgtGruppe, values)
		} else if (values.organisasjon) {
			trackBestilling(values)
			bestillingAction = actions.postOrganisasjonBestilling(values)
		} else {
			trackBestilling(values)
			bestillingAction = actions.postBestilling(valgtGruppe, values)
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
			navigate(`/gruppe/${valgtGruppe}`)
		} else {
			navigate(`/gruppe/${valgtGruppe}`)
		}
	}
