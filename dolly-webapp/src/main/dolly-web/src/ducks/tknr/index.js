import { DollyApi } from '~/service/Api'
import { LOCATION_CHANGE } from 'connected-react-router'
import _get from 'lodash/get'
import _isNil from 'lodash/isNil'
import { createAction, handleActions, combineActions } from 'redux-actions'
import success from '~/utils/SuccessAction'

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
		[success(getEnhetByTknr)](state, action) {
			const { data } = action.payload
			return { ...state, [data.enhetNr]: data.navn }
		}
	},
	initialState
)

export const tknrLabelSelector = (state, tknr) => {
	const tknrObject = state.tknr[tknr]

	if (!tknrObject) return null
	return tknr + ' - ' + tknrObject
}
