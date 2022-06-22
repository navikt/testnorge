import { DollyApi } from '~/service/Api'
import { createActions } from 'redux-actions'
import { onFailure, onSuccess } from '~/ducks/utils/requestActions'
import { handleActions } from '~/ducks/utils/immerHandleActions'
import { LOCATION_CHANGE } from 'redux-first-history'
import { VisningType } from '~/pages/gruppe/Gruppe'

export const {
	navigerTilPerson,
	navigerTilBestilling,
	setSideStoerrelse,
	setSidetall,
	setVisning,
	resetNavigering,
	resetPaginering,
	resetFeilmelding,
} = createActions({
	navigerTilPerson: DollyApi.navigerTilPerson,
	navigerTilBestilling: DollyApi.navigerTilBestilling,
	setSidetall: (sidetall) => sidetall,
	setSideStoerrelse: (sideStoerrelse) => sideStoerrelse,
	setVisning: (visning) => visning,
	resetNavigering,
	resetPaginering,
	resetFeilmelding,
})

const initialState = {
	visning: 'personer',
	visPerson: null,
	visBestilling: null,
	navigerTilGruppe: null,
	feilmelding: null,
	sidetall: 0,
	sideStoerrelse: 10,
}

export default handleActions(
	{
		[LOCATION_CHANGE](_state, action) {
			if (action.payload.action !== 'REPLACE') return initialState
		},
		[onFailure(navigerTilPerson)](state, action) {
			state.feilmelding = action.payload.data?.message
		},
		[onFailure(navigerTilBestilling)](state, action) {
			state.feilmelding = action.payload.data?.message
		},
		[onSuccess(navigerTilPerson)](state, action) {
			state.feilmelding = action.payload?.data?.message
			state.visPerson = action.payload.data.identHovedperson
			state.sidetall = action.payload.data.sidetall
			state.navigerTilGruppe = action.payload.data.gruppe?.id
			state.visning = VisningType.VISNING_PERSONER
		},
		[onSuccess(navigerTilBestilling)](state, action) {
			state.feilmelding = action.payload?.data?.message
			state.visBestilling = action.payload.data.bestillingNavigerTil
			state.sidetall = action.payload.data.sidetall
			state.navigerTilGruppe = action.payload.data.gruppe?.id
			state.visning = VisningType.VISNING_BESTILLING
		},
		[setSidetall](state, action) {
			state.sidetall = action.payload
		},
		[resetPaginering](state) {
			state.sidetall = initialState.sidetall
			state.sideStoerrelse = initialState.sideStoerrelse
		},
		[setSideStoerrelse](state, action) {
			state.sideStoerrelse = action.payload
		},
		[resetNavigering](state) {
			return {
				...initialState,
				visning: state.visning,
				sidetall: state.sidetall,
				sideStoerrelse: state.sideStoerrelse,
			}
		},
		[resetFeilmelding](state) {
			state.feilmelding = null
		},
		[setVisning](state, action) {
			state.visning = action.payload
		},
	},
	initialState
)
