import { DollyApi, PdlforvalterApi } from '~/service/Api'
// @ts-ignore
import { createActions } from 'redux-actions'
import { handleActions } from '~/ducks/utils/immerHandleActions'
import { onSuccess } from '~/ducks/utils/requestActions'
import { Person } from '~/components/fagsystem/pdlf/PdlTypes'
import { RootStateOrAny } from 'react-redux'
import { LOCATION_CHANGE } from 'redux-first-history'

export const actions = createActions({
	hentPdlforvalterPersoner: [
		PdlforvalterApi.getPersoner,
		(identer: Array<string>) => ({
			identer,
		}),
	],
	getSkjermingsregister: [
		DollyApi.getSkjerming,
		(ident) => ({
			ident,
		}),
	],
})

const initialState = {
	pdlforvalter: {},
	skjermingsregister: {},
}

export default handleActions(
	{
		[LOCATION_CHANGE]() {
			return initialState
		},
		[onSuccess(actions.hentPdlforvalterPersoner)](state: RootStateOrAny, action: any) {
			action.payload?.data?.forEach((ident: Person) => {
				state.pdlforvalter[ident.person.ident] = ident
			})
		},
		[onSuccess(actions.getSkjermingsregister)](state: RootStateOrAny, action: any) {
			state.skjermingsregister[action.meta.ident] = action.payload?.data
		},
		// @ts-ignore
	},
	initialState
)
