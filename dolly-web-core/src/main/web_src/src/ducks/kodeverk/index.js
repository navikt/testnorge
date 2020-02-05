import { createActions, handleActions } from 'redux-actions'
import _get from 'lodash/get'
import _isNil from 'lodash/isNil'
import { DollyApi } from '~/service/Api'
import { onSuccess, onRequest } from '~/ducks/utils/requestActions'
import { SortKodeverkArray } from '~/service/services/dolly/Utils'

export const { getKodeverk } = createActions(
	{
		getKodeverk: [
			DollyApi.getKodeverkByNavn,
			kodeverkNavn => ({
				kodeverkNavn
			})
		]
	},
	{ prefix: 'kodeverk' }
)

const initialState = {
	loading: {},
	data: {}
}

/**
 * Note: Bruker *ikke* immer (immutable state) her, da noen av kodeverkene kan
 */
export default handleActions(
	{
		[onRequest(getKodeverk)](state, action) {
			return {
				...state,
				loading: {
					...state.loading,
					[action.meta.kodeverkNavn]: true
				}
			}
		},
		[onSuccess(getKodeverk)](state, action) {
			const kodeverk = SortKodeverkArray(action.payload.data)
			return {
				...state,
				loading: {
					...state.loading,
					[action.meta.kodeverkNavn]: false
				},
				data: {
					...state.data,
					[action.meta.kodeverkNavn]: kodeverk
				}
			}
		}
	},
	initialState
)

/*** THUNKS */
export const fetchKodeverk = kodeverkNavn => (dispatch, getState) => {
	const { kodeverk } = getState()
	if (kodeverk.data[kodeverkNavn] || kodeverk.loading[kodeverkNavn]) return false
	return dispatch(getKodeverk(kodeverkNavn))
}

///* SELECTORS
//* Selectors når det er lagret som array
export const getKodeverkSelector = (state, kodeverk) => {
	return state.kodeverk.data[kodeverk]
}

export const getKodeverkByIdSelector = (state, kodeverk, value) => {
	const defaultResponse = {
		label: `${value} - Finnes ikke i kodeverk`
	}
	return state.kodeverk.data[kodeverk].find(y => y.value === value) || defaultResponse
}
