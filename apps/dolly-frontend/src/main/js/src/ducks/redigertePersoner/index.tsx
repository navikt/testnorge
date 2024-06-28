import { DollyApi, PdlforvalterApi, SkjermingApi } from '@/service/Api'
// @ts-ignore
import { createActions } from 'redux-actions'
import { handleActions } from '@/ducks/utils/immerHandleActions'
import { onSuccess } from '@/ducks/utils/requestActions'
import { Person } from '@/components/fagsystem/pdlf/PdlTypes'
import { RootStateOrAny } from 'react-redux'
import { LOCATION_CHANGE } from 'redux-first-history'

export const actions = createActions({
	hentPdlforvalterPersoner: [
		PdlforvalterApi.getPersoner,
		(identer: Array<string>) => ({
			identer,
		}),
	],
	hentPdlPersoner: [
		DollyApi.getPersonerFraPdl,
		(identer: Array<string>) => ({
			identer,
		}),
	],
	getSkjermingsregister: [
		SkjermingApi.getSkjerming,
		(ident) => ({
			ident,
		}),
	],
})

const initialState = {
	pdlforvalter: {},
	pdl: {},
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
		[onSuccess(actions.hentPdlPersoner)](state: RootStateOrAny, action: any) {
			action.payload.data?.data?.hentPersonBolk?.forEach((ident) => {
				state.pdl[ident.ident] = ident
			})
		},
		[onSuccess(actions.getSkjermingsregister)](state: RootStateOrAny, action: any) {
			state.skjermingsregister[action.meta.ident] = action.payload?.data
		},
		// @ts-ignore
	},
	initialState,
)
