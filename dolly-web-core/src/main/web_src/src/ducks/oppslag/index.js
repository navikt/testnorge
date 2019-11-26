import { createAction, handleActions } from 'redux-actions'
import _get from 'lodash/get'
import _isNil from 'lodash/isNil'
import { DollyApi } from '~/service/Api'
import { onSuccess } from '~/ducks/utils/requestActions'
import { SortKodeverkArray } from '~/service/services/dolly/Utils'

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
		[onSuccess(getKodeverk)](state, action) {
			return { ...state, [action.meta.kodeverkNavn]: action.payload.data }
		},
		[onSuccess(getEnhetByTknr)](state, action) {
			const { data } = action.payload
			let tknrData = { nr: data.enhetNr, sted: data.navn }
			if (!state.tknr) return { ...state, tknr: [tknrData] }
			if (state.tknr.find(tknr => tknr.nr === tknrData.nr)) return { ...state }
			return { ...state, tknr: [...state.tknr, tknrData] }
		}
	},
	initialState
)

export const fetchKodeverk = kodeverkNavn => (dispatch, getState) => {
	const { oppslag } = getState()
	if (oppslag[kodeverkNavn]) {
		return
	}
	return dispatch(getKodeverk(kodeverkNavn))
}

export const oppslagLabelSelector = (state, navn, value) => {
	const oppslag = state.oppslag[navn]
	if (!oppslag) return null

	if (navn === 'tknr') {
		const findTknr = oppslag.find(tknr => tknr.nr === value)
		let place = ''
		if (findTknr) {
			place = findTknr.sted
		}
		return value + ' - ' + place
	} else {
		const kodeArray = SortKodeverkArray(oppslag)
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
}
