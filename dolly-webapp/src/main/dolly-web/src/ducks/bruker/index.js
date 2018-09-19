import { DollyApi } from '~/service/Api'
import { createAction, handleActions, combineActions } from 'redux-actions'
import Cookie from '~/utils/Cookie'
import success from '~/utils/SuccessAction'

export const getCurrentBruker = createAction('GET_CURRENT_BRUKER', DollyApi.getCurrentBruker)
export const setSplashscreenStatus = createAction('SET_SPLASHSCREEN_STATUS')
export const addFavorite = createAction('ADD_FAVORITE', DollyApi.addFavorite)
export const removeFavorite = createAction('REMOVE_FAVORITE', DollyApi.removeFavorite)

const initialState = {
	brukerData: null,
	showSplashscreen: false
}

const successActions = combineActions(
	success(getCurrentBruker),
	success(addFavorite),
	success(removeFavorite)
)

export default handleActions(
	{
		[successActions](state, action) {
			return { ...state, brukerData: action.payload.data }
		},
		[setSplashscreenStatus](state, action) {
			return { ...state, showSplashscreen: action.payload }
		}
	},
	initialState
)

export const fetchCurrentBruker = () => async dispatch => {
	const { action } = await dispatch(getCurrentBruker())
	// const brukerIdent = action.payload.data.navIdent

	// if (!Cookie.hasItem(brukerIdent)) {
	// 	dispatch(setSplashscreenStatus(true))
	// }
}

export const setSplashscreenAccepted = () => (dispatch, getState) => {
	const { navIdent } = getState().bruker.brukerData
	Cookie.setItem(navIdent, true, Infinity)
	return dispatch(setSplashscreenStatus(false))
}
