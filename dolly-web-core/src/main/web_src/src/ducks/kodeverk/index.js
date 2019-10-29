import { createAction, handleActions } from 'redux-actions'
import _get from 'lodash/get'
import _isNil from 'lodash/isNil'
import { DollyApi } from '~/service/Api'
import success from '~/utils/SuccessAction'
import { SortKodeverkArray } from '~/service/services/dolly/Utils'

// Helper
const keyIndex = data => {
	return data.reduce((obj, curr) => {
		return {
			...obj,
			[curr.value]: curr
		}
	}, {})
}

export const getKodeverk = createAction(
	'GET_KODEVERK',
	kodeverkNavn => DollyApi.getKodeverkByNavn(kodeverkNavn),
	kodeverkNavn => ({
		kodeverkNavn
	})
)

const initialState = {}

export default handleActions(
	{
		[success(getKodeverk)](state, action) {
			const kodeverk = SortKodeverkArray(action.payload.data)
			const mappedToKey = keyIndex(kodeverk)
			return {
				...state,
				[action.meta.kodeverkNavn]: mappedToKey
			}
		}
	},
	initialState
)

/*** THUNKS */
export const fetchKodeverk = kodeverkNavn => (dispatch, getState) => {
	const { oppslag } = getState()
	if (oppslag[kodeverkNavn]) return false
	return dispatch(getKodeverk(kodeverkNavn))
}

///* SELECTORS
export const getKodeverkSelector = (state, kodeverk) => {
	const path = state.kodeverk[kodeverk]
	return path && Object.values(path)
}

export const getKodeverkByIdSelector = (state, kodeverk, value) => {
	const defaultResponse = {
		label: `${value} - Finnes ikke i kodeverk`
	}
	return _get(state.kodeverk, `${kodeverk}.${value}`, defaultResponse)
}
