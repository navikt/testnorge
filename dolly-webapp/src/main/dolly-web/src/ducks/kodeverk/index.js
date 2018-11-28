import { DollyApi } from '~/service/Api'
import { createAction, handleActions } from 'redux-actions'
import success from '~/utils/SuccessAction'

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
			return { ...state, [action.meta.kodeverkNavn]: action.payload.data }
		}
	},
	initialState
)

export const fetchKodeverk = kodeverkNavn => (dispatch, getState) => {
	const { kodeverk } = getState()

	if (kodeverk[kodeverkNavn]) {
		return
	}

	return dispatch(getKodeverk(kodeverkNavn))
}

export const kodeverkLabelSelector = (state, kodeverkNavn, value) => {
	const kodeverk = state.kodeverk[kodeverkNavn]
	if (!kodeverk) return null

	return kodeverk.koder.find(kode => kode.value === value)
}
