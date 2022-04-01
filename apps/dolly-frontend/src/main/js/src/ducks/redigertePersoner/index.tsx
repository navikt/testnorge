import { DollyApi, PdlforvalterApi } from '~/service/Api'
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
	hentPdlPersoner: [
		DollyApi.getPersonFraPdl,
		(identer) => ({
			identer,
		}),
	],
})

const initialState = {
	pdlforvalter: {},
	pdl: {},
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
		[onSuccess(actions.hentPdlPersoner)](state, action) {
			state.pdl[action.meta?.identer] = action.payload?.data?.data?.hentPerson
		},
	},
	initialState
)
