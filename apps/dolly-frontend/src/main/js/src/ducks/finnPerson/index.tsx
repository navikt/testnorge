import { DollyApi } from '@/service/Api'
import { createActions } from 'redux-actions'
import { onFailure, onSuccess } from '@/ducks/utils/requestActions'
import { handleActions } from '@/ducks/utils/immerHandleActions'
import { VisningType } from '@/pages/gruppe/Gruppe'
import * as _ from 'lodash-es'
import { ERROR_NAVIGATE_IDENT } from '../errors/ErrorMessages'
import { sideStoerrelseLocalStorageKey } from '@/pages/gruppeOversikt/GruppeOversikt'

export const {
	navigerTilPerson,
	navigerTilBestilling,
	setSideStoerrelse,
	setSidetall,
	setVisning,
	setGruppeNavigerTil,
	resetNavigering,
	resetPaginering,
	resetFeilmelding,
	setSorting,
	setUpdateNow,
	locationChange,
} = createActions({
	navigerTilPerson: DollyApi.navigerTilPerson,
	navigerTilBestilling: DollyApi.navigerTilBestilling,
	setSidetall: (sidetall) => sidetall,
	setSideStoerrelse: (sideStoerrelse) => sideStoerrelse,
	setSorting: (sorting) => sorting,
	setVisning: (visning) => visning,
	setGruppeNavigerTil: (gruppeId) => gruppeId,
	resetNavigering: () => ({}),
	resetPaginering: () => ({}),
	resetFeilmelding: () => ({}),
	setUpdateNow: () => ({}),
	locationChange: (location) => ({ location }),
})

const initialState = {
	visning: 'personer',
	visPerson: null,
	hovedperson: null,
	visBestilling: null,
	navigerTilGruppe: null,
	feilmelding: null,
	sidetall: 0,
	sideStoerrelse: localStorage.getItem(sideStoerrelseLocalStorageKey) || 10,
	sorting: null,
	update: null,
}

export default handleActions(
	{
		[locationChange]: (state, action) => {
			const gruppePathRegex = /^\/gruppe\/\d+$/
			return gruppePathRegex.test(action.payload.location.pathname) ? state : initialState
		},
		[onFailure(navigerTilPerson)]: (state, action) => {
			state.feilmelding = action.payload.data?.message || 'Ukjent feil'
		},
		[onFailure(navigerTilBestilling)]: (state, action) => {
			state.feilmelding = action.payload.data?.message || 'Ukjent feil'
		},
		[onSuccess(navigerTilPerson)]: (state, action) => {
			state.feilmelding =
				!action.payload?.data || _.isEmpty(action.payload?.data)
					? ERROR_NAVIGATE_IDENT
					: action.payload?.data?.message
			state.hovedperson = action.payload.data.identHovedperson
			state.visPerson = action.payload.data.identNavigerTil
			state.sidetall = action.payload.data.sidetall
			state.navigerTilGruppe = action.payload.data?.gruppe?.id
			state.visning = VisningType.VISNING_PERSONER
		},
		[onSuccess(navigerTilBestilling)]: (state, action) => {
			state.feilmelding = action.payload?.data?.message || null
			state.visBestilling = action.payload.data.bestillingNavigerTil
			state.sidetall = action.payload.data.sidetall
			state.navigerTilGruppe = action.payload.data.gruppe?.id
			state.visning = VisningType.VISNING_BESTILLING
		},
		[setSidetall]: (state, action) => {
			state.sidetall = action.payload
		},
		[resetPaginering]: (state) => {
			state.sidetall = initialState.sidetall
			// state.sideStoerrelse = initialState.sideStoerrelse
		},
		[setSideStoerrelse]: (state, action) => {
			state.sideStoerrelse = action.payload
		},
		[resetNavigering]: (state) => {
			return {
				...initialState,
				visning: state.visning,
				sidetall: state.sidetall,
				// sideStoerrelse: state.sideStoerrelse,
			}
		},
		[resetFeilmelding]: (state) => {
			state.feilmelding = null
		},
		[setVisning]: (state, action) => {
			state.visning = action.payload
		},
		[setGruppeNavigerTil]: (state, action) => {
			state.navigerTilGruppe = action.payload
		},
		[setSorting]: (state, action) => {
			state.sorting = action.payload
		},
		[setUpdateNow]: (state) => {
			state.update = new Date()
		},
	},
	initialState,
)
