import { PdlforvalterApi } from '~/service/Api'
import { createActions } from 'redux-actions'
import { handleActions } from '~/ducks/utils/immerHandleActions'
import { onSuccess } from '~/ducks/utils/requestActions'
import { LOCATION_CHANGE } from 'connected-react-router'

export const actions = createActions({
	hentPdlforvalterPersoner: [
		PdlforvalterApi.getPersoner,
		(identer) => ({
			identer,
		}),
	],
})

const initialState = {
	pdlforvalter: {},
}

export default handleActions(
	{
		[LOCATION_CHANGE](state, action) {
			return initialState
		},
		[onSuccess(actions.hentPdlforvalterPersoner)](state, action) {
			action.payload?.data?.forEach((ident) => {
				state.pdlforvalter[ident.person.ident] = ident
			})
		},
	},
	initialState
)
