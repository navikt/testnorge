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
	setSorting,
	setUpdateNow,
} = createActions({
	navigerTilPerson: DollyApi.navigerTilPerson,
	navigerTilBestilling: DollyApi.navigerTilBestilling,
	setSidetall: (sidetall) => sidetall,
	setSideStoerrelse: (sideStoerrelse) => sideStoerrelse,
	setSorting: (sorting) => sorting,
	setVisning: (visning) => visning,
	resetNavigering() {},
	resetPaginering() {},
	resetFeilmelding() {},
	setUpdateNow() {},
})

const initialState = {
	visning: 'personer',
	visPerson: null,
	hovedperson: null,
	visBestilling: null,
	navigerTilGruppe: null,
	feilmelding: undefined,
	sidetall: 0,
	sideStoerrelse: 10,
	sorting: null,
	update: null,
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
			state.hovedperson = action.payload.data.identHovedperson
			state.visPerson = action.payload.data.identNavigerTil
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
			state.feilmelding = undefined
		},
		[setVisning](state, action) {
			state.visning = action.payload
		},
		[setSorting](state, action) {
			state.sorting = action.payload
		},
		[setUpdateNow](state) {
			state.update = new Date()
		},
	},
	initialState
)
