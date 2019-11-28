import { LOCATION_CHANGE } from 'connected-react-router'
import { createAction, handleActions, combineActions } from 'redux-actions'
import _get from 'lodash/get'
import _isNil from 'lodash/isNil'
import _find from 'lodash/find'
import { DollyApi } from '~/service/Api'
import { onSuccess } from '~/ducks/utils/requestActions'

// GET
export const getGruppe = createAction('GET_GRUPPE', DollyApi.getGruppeById)
export const getGrupper = createAction('GET_GRUPPER', DollyApi.getGrupper)
export const getGrupperByUserId = createAction('GET_GRUPPER_BY_USER_ID', DollyApi.getGruppeByUserId)

// CRUD
export const createGruppe = createAction('CREATE_GRUPPE', DollyApi.createGruppe)
export const updateGruppe = createAction('UPDATE_GRUPPE', DollyApi.updateGruppe)
export const deleteGruppe = createAction('DELETE_GRUPPE', DollyApi.deleteGruppe, gruppeId => ({
	gruppeId
}))
export const updateIdentAttributter = createAction(
	'UPDATE_IDENTATTRIBUTTER',
	DollyApi.updateIdentAttributter
)

// UI
export const settVisning = createAction('SETT_VISNING')

const initialState = {
	data: null,
	visning: 'mine'
}

const getSuccess = combineActions(
	onSuccess(getGruppe),
	onSuccess(getGrupper),
	onSuccess(getGrupperByUserId)
)

export default handleActions(
	{
		[LOCATION_CHANGE](state, action) {
			return initialState
		},
		[getSuccess](state, action) {
			const { data } = action.payload
			return { ...state, data: Array.isArray(data) ? data : [data] }
		},
		[onSuccess(updateGruppe)](state, action) {
			return {
				...state,
				data: state.data.map((item, idx) => ({
					...item,
					...(item.id === action.payload.data.id && action.payload.data)
				}))
			}
		},
		[success(updateIdentAttributter)](state, action) {
			return {
				...state,
				data: state.data.map(item => ({
					...item,
					identer: item.identer.map((identer, idx) => ({
						...identer,
						ibruk:
							action.payload.data.ident === identer.ident
								? action.payload.data.ibruk
								: identer.ibruk
					}))
				}))
			}
		},

		[success(deleteGruppe)](state, action) {
			return {
				...state,
				data: state.data.filter(item => item.id !== action.meta.gruppeId)
			}
		},
		[settVisning](state, action) {
			return { ...state, visning: action.payload }
		}
	},
	initialState
)

// Thunk
export const fetchGrupperTilBruker = () => async (dispatch, getState) => {
	const { brukerId } = getState().bruker.brukerData
	return dispatch(getGrupperByUserId(brukerId))
}

// Selector
export const sokSelectorOversikt = (items, searchStr) => {
	if (!items) return null
	if (!searchStr) return items

	const query = searchStr.toLowerCase()
	return items.filter(item => {
		const searchValues = [
			_get(item, 'id'),
			_get(item, 'navn'),
			_get(item, 'hensikt'),
			_get(item, 'identer', []).length
		]
			.filter(v => !_isNil(v))
			.map(v => v.toString().toLowerCase())

		return searchValues.some(v => v.includes(query))
	})
}

export const getIdentByIdSelector = (state, personId) => {
	return _get(state, 'gruppe.data[0].identer', []).find(v => v.ident === personId)
}

export const antallBestillingerSelector = gruppeArray => {
	if (!gruppeArray) return 0
	return _get(gruppeArray, '[0].identer', [])
		.map(b => b.bestillingId)
		.flat().length
}
