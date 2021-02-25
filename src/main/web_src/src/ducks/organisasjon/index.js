import { LOCATION_CHANGE } from 'connected-react-router'
import { createActions } from 'redux-actions'
import { DollyApi, OrgforvalterApi } from '~/service/Api'
import { handleActions } from '../utils/immerHandleActions'
import { onSuccess } from '~/ducks/utils/requestActions'
import _isEmpty from 'lodash/isEmpty'

export const actions = createActions(
	{
		getOrganisasjonBestilling: DollyApi.getOrganisasjonsnummerByUserId,
		getOrganisasjoner: OrgforvalterApi.getOrganisasjonerInfo
	},
	{
		prefix: 'organisasjon'
	}
)

const initialState = {
	bestillinger: [],
	organisasjoner: []
}

export default handleActions(
	{
		[LOCATION_CHANGE](state, action) {
			return initialState
		},
		[onSuccess(actions.getOrganisasjonBestilling)](state, action) {
			state.bestillinger = action.payload.data
		},
		[onSuccess(actions.getOrganisasjoner)](state, action) {
			state.organisasjoner = action.payload.data
		}
	},
	initialState
)

export const fetchOrganisasjoner = dispatch => async brukerId => {
	const { data } = await actions.getOrganisasjonBestilling(brukerId).payload
	let orgNumre = []
	data.forEach(org => {
		if (org.ferdig && org.organisasjonNummer !== 'NA') return orgNumre.push(org.organisasjonNummer)
	})
	dispatch(actions.getOrganisasjoner(orgNumre))
}

export const sokSelectorOrg = (items, searchStr) => {
	if (!items) return []
	if (!searchStr) return items

	const query = searchStr.toLowerCase()
	return items.filter(item =>
		Object.values(item).some(v =>
			(v || '')
				.toString()
				.toLowerCase()
				.includes(query)
		)
	)
}

const hentOrgStatus = (bestillinger, bestillingId) => {
	if (!bestillinger) return null
	let orgStatus = 'Ferdig'
	const bestilling = bestillinger.find(obj => {
		return obj.id === bestillingId
	})
	if (!bestilling?.status) orgStatus = 'Feilet'
	bestilling?.status?.[0].statuser?.forEach(status => {
		if (status?.melding !== 'OK') orgStatus = 'Avvik'
	})
	return orgStatus
}

function getBestillingIdFromOrgnummer(bestillinger, organisasjonsnummer) {
	return bestillinger
		.filter(org => org.organisasjonNummer === organisasjonsnummer)
		.map(org => org.id)
		.sort(function(a, b) {
			return b - a
		})
}

export const mergeList = (organisasjoner, bestillinger) => {
	if (_isEmpty(organisasjoner)) return null

	return organisasjoner.map(orgInfo => {
		const bestillingId = getBestillingIdFromOrgnummer(bestillinger, orgInfo.organisasjonsnummer)
		return {
			orgInfo,
			id: orgInfo.id,
			organisasjonsnummer: orgInfo.organisasjonsnummer,
			organisasjonsnavn: orgInfo.organisasjonsnavn,
			enhetstype: orgInfo.enhetstype,
			status: hentOrgStatus(bestillinger, bestillingId[0]),
			bestillingId: bestillingId
		}
	})
}
