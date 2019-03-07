import { DollyApi } from '~/service/Api'
import { createAction, handleActions } from 'redux-actions'
import success from '~/utils/SuccessAction'
import { SortKodeverkArray } from '~/service/services/dolly/Utils'
import _get from 'lodash/get'
import _isNil from 'lodash/isNil'

export const getKodeverk = createAction(
	'GET_KODEVERK',
	kodeverkNavn => DollyApi.getKodeverkByNavn(kodeverkNavn),
	kodeverkNavn => ({
		kodeverkNavn
	})
)

export const getEnhetByTknr = createAction('GET_TKNR', async tknr => {
	try {
		const res = await DollyApi.getEnhetByTknr(tknr)
		return res
	} catch (err) {
		const failureData = { data: { enhetNr: tknr, navn: 'Finnes ikke i kodeverk' } }
		return failureData
	}
})

const initialState = {}

export default handleActions(
	{
		[success(getKodeverk)](state, action) {
			return { ...state, [action.meta.kodeverkNavn]: action.payload.data }
		},
		[success(getEnhetByTknr)](state, action) {
			const { data } = action.payload
			return { ...state, [data.enhetNr]: data.navn }
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

	// Viser bruker feilmelding istedenfor loadingloop
	return { label: value + ' - Finnes ikke i kodeverk' }
}

export const tknrLabelSelector = (state, tknr) => {
	const tknrObject = state.tknr[tknr]

	if (!tknrObject) return null
	return tknr + ' - ' + tknrObject
}
