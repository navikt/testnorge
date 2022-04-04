import { handleActions } from '~/ducks/utils/immerHandleActions'
import { onSuccess } from '~/ducks/utils/requestActions'
import { createActions } from 'redux-actions'
import OrganisasjonFasteDataService from '~/service/services/organisasjonFasteDataService/OrganisasjonFasteDataService'

export const actions = createActions({
	getFastedataOrganisasjoner: OrganisasjonFasteDataService.fetchOrganisasjoner,
})

const initialState = {
	organisasjoner: null as Object,
}

export default handleActions(
	{
		[onSuccess(actions.getFastedataOrganisasjoner)](
			state: { organisasjoner: any },
			action: { payload: any }
		) {
			state.organisasjoner = action.payload
		},
	},
	initialState
)
