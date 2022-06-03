import { createActions } from 'redux-actions'
import _get from 'lodash/get'
import _isNil from 'lodash/isNil'
import { DollyApi } from '~/service/Api'
import { createLoadingSelector } from '~/ducks/loading'
import { onSuccess } from '~/ducks/utils/requestActions'
import { handleActions } from '~/ducks/utils/immerHandleActions'
import { LOCATION_CHANGE } from 'redux-first-history'

export const actions = createActions(
	{
		getByUserId: DollyApi.getGruppeByUserId,
		create: DollyApi.createGruppe,
		update: DollyApi.updateGruppe,
		laas: DollyApi.updateGruppeLaas,
		sendGruppeTags: DollyApi.updateGruppeSendTags,
		getGruppeExcelFil: DollyApi.getExcelFil,
		remove: [
			DollyApi.deleteGruppe,
			(gruppeId) => ({
				gruppeId,
			}),
		],
		updateIdentIbruk: DollyApi.updateIdentIbruk,
		updateBeskrivelse: DollyApi.updateIdentBeskrivelse,
		importZIdent: DollyApi.importZIdent,
		setVisning: (visning) => visning,
	},
	{
		prefix: 'gruppe', // String used to prefix each type
	}
)

const initialState = {
	ident: {},
	byId: {},
	gruppeInfo: {},
	mineIds: [],
	importerteZIdenter: null,
	visning: 'personer',
}

export default handleActions(
	{
		[LOCATION_CHANGE](state, action) {
			return initialState
		},
		[onSuccess(actions.update)](state, action) {
			state.byId[action.payload.data.id] = action.payload.data
		},
		[onSuccess(actions.laas)](state, action) {
			state.byId[action.payload.data.id] = action.payload.data
		},
		[onSuccess(actions.remove)](state, action) {
			delete state.byId[action.meta.gruppeId]
			state.mineIds = state.mineIds.filter((v) => v !== action.meta.gruppeId)
		},
		[onSuccess(actions.importZIdent)](state, action) {
			state.importerteZIdenter = action.payload.data
		},
	},
	initialState
)

// Thunk
export const fetchMineGrupper = (brukerId) => async (dispatch) => {
	return dispatch(actions.getByUserId(brukerId))
}

// Selector
export const loadingGrupper = createLoadingSelector([actions.getByUserId])

export const sokSelectorGruppeOversikt = (state) => {
	const { search, gruppe } = state
	const items = Object.values(gruppe.byId)

	if (!items) return null
	if (!search) return items

	const query = search.toLowerCase()
	return items.filter((item) => {
		const searchValues = [
			_get(item, 'id'),
			_get(item, 'navn'),
			_get(item, 'hensikt'),
			_get(item, 'identer', []).length,
		]
			.filter((v) => !_isNil(v))
			.map((v) => v.toString().toLowerCase())

		return searchValues.some((v) => v.includes(query))
	})
}
