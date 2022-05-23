import { PdlforvalterApi } from '~/service/Api'
// @ts-ignore
import { createActions } from 'redux-actions'
import { handleActions } from '~/ducks/utils/immerHandleActions'
import { onSuccess } from '~/ducks/utils/requestActions'
import { Person } from '~/components/fagsystem/pdlf/PdlTypes'
import { RootStateOrAny } from 'react-redux'
import { LOCATION_CHANGE } from 'redux-first-history'

export const { hentPdlforvalterPersoner, increaseAntallFjernet, decreaseAntallFjernet } =
	createActions({
		hentPdlforvalterPersoner: [
			PdlforvalterApi.getPersoner,
			(identer: Array<string>) => ({
				identer,
			}),
		],
		increaseAntallFjernet() {},
		decreaseAntallFjernet() {},
	})

const initialState = {
	pdlforvalter: {},
	antallPersonerFjernet: 0,
}

export default handleActions(
	{
		[LOCATION_CHANGE]() {
			return initialState
		},
		[onSuccess(hentPdlforvalterPersoner)](state: RootStateOrAny, action: any) {
			action.payload?.data?.forEach((ident: Person) => {
				state.pdlforvalter[ident.person.ident] = ident
			})
		},
		// @ts-ignore
		[increaseAntallFjernet](state: RootStateOrAny) {
			state.antallPersonerFjernet++
		},
		// @ts-ignore
		[decreaseAntallFjernet](state: RootStateOrAny) {
			state.antallPersonerFjernet--
		},
	},
	initialState
)
