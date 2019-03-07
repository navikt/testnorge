import { DollyApi } from '~/service/Api'
import { createAction, handleActions } from 'redux-actions'
import success from '~/utils/SuccessAction'
import { SortKodeverkArray } from '~/service/services/dolly/Utils'

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

	const kodeArray = SortKodeverkArray(kodeverk)

	const result = kodeArray.find(kode => kode.value === value)

	if (result) {
		return result
	}

	// Når "ikke spesifisert" verdi er valgt fra step 2
	if (!value) {
		return { label: '' }
	}
	// Viser bruker feilmelding istedenfor loadingloop
	return { label: value + ' - Finnes ikke i kodeverk' }
}
