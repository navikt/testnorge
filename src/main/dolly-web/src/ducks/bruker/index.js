import { DollyApi } from '~/service/Api'
import { createAction, handleActions } from 'redux-actions'
import Cookie from '~/utils/Cookie'
import success from '~/utils/SuccessAction'
import { create } from 'domain'

export const getCurrentBruker = createAction('GET_CURRENT_BRUKER', DollyApi.getCurrentBruker)
export const setSplashscreenStatus = createAction('SET_SPLASHSCREEN_STATUS')

const initialState = {
	brukerData: null,
	showSplashscreen: false
}

export default handleActions(
	{
		[success(getCurrentBruker)](state, action) {
			return { ...state, brukerData: action.payload.data }
		},
		[setSplashscreenStatus](state, action) {
			return { ...state, showSplashscreen: action.payload }
		}
	},
	initialState
)

export const fetchCurrentBruker = () => async dispatch => {
	const reqAction = await getCurrentBruker()
	const responsePayload = await reqAction.payload
	const brukerIdent = responsePayload.data.navIdent
	if (Cookie.hasItem(brukerIdent)) {
		return dispatch(reqAction)
	}

	dispatch(setSplashscreenStatus(true))
	return dispatch(reqAction)
}

export const setSplashscreenAccepted = () => (dispatch, getState) => {
	const { bruker } = getState()
	const brukerData = bruker.brukerData
	Cookie.setItem(brukerData.navIdent, true, Infinity)
	return dispatch(setSplashscreenStatus(false))
}
