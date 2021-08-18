import { LOCATION_CHANGE } from 'connected-react-router'
import { createActions } from 'redux-actions'
import { DollyApi, OrgforvalterApi } from '~/service/Api'
import { handleActions } from '../utils/immerHandleActions'
import { onSuccess } from '~/ducks/utils/requestActions'

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
	bestillinger: null,
	organisasjoner: null
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
