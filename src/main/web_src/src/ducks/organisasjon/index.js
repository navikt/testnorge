import { LOCATION_CHANGE } from 'connected-react-router'
import { createActions } from 'redux-actions'
import Api from '~/api'
import { handleActions } from '../utils/immerHandleActions'
import { onSuccess } from '~/ducks/utils/requestActions'

export const actions = createActions(
	{
		getOrganisasjoner: Api.fetch
		// TODO: Byttes når organisasjonsforvalter er klar
	},
	{
		prefix: 'organisasjon'
	}
)

const initialState = {
	organisasjon: []
}

export default handleActions(
	{
		[LOCATION_CHANGE](state, action) {
			return initialState
		},
		[onSuccess(actions.getOrganisasjoner)](state, action) {
			state.organisasjon = action.payload.data
		}
	},
	initialState
)
