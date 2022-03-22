import { PdlforvalterApi } from '~/service/Api'
import { createActions } from 'redux-actions'
import { handleActions } from '~/ducks/utils/immerHandleActions'
import { onSuccess } from '~/ducks/utils/requestActions'
import { LOCATION_CHANGE } from 'connected-react-router'

export const actions = createActions({
	hentPerson: [
		PdlforvalterApi.getPersoner,
		(identer) => ({
			identer,
		}),
	],
})

const initialState = {
	data: {},
}

export default handleActions(
	{
		[LOCATION_CHANGE](state, action) {
			return initialState
		},
		[onSuccess(actions.hentPerson)](state, action) {
			action.payload?.data?.forEach((ident) => {
				state.data[ident.person.ident] = ident
			})
		},
	},
	initialState
)
