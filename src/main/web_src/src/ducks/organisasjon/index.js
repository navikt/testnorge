import { LOCATION_CHANGE } from 'connected-react-router'
import { createActions } from 'redux-actions'
import Api from '~/api'
import { DollyApi, OrgforvalterApi } from '~/service/Api'
import { handleActions } from '../utils/immerHandleActions'
import { onSuccess } from '~/ducks/utils/requestActions'

export const actions = createActions(
	{
		getOrganisasjonBestilling: DollyApi.getOrganisasjonsnummerByUserId,
		getOrganisasjoner: OrgforvalterApi.getOrganisasjonerInfo
		// TODO: Flytte denne til ducks/bestillingStatus? Evt ha den begge steder
	},
	{
		prefix: 'organisasjon'
	}
)

const initialState = {
	organisasjoner: [],
	orgnr: []
}

export default handleActions(
	{
		[LOCATION_CHANGE](state, action) {
			return initialState
		},
		[onSuccess(actions.getOrganisasjonsnummerByUserId)](state, action) {
			state.orgnr = action.payload.data
		},
		[onSuccess(actions.getOrganisasjoner)](state, action) {
			state.organisasjoner = action.payload.data
		}
	},
	initialState
)

// TODO: virker ikke, men mulig vi ikke trenger den
export const fetchOrganisasjoner = () => async (dispatch, getState) => {
	const { brukerId } = getState().bruker.brukerData
	if (brukerId) dispatch(actions.getOrganisasjonBestilling(brukerId))
}
