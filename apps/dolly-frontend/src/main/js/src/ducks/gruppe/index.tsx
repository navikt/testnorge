import { createActions } from 'redux-actions'
import { DollyApi } from '@/service/Api'
import { onSuccess } from '@/ducks/utils/requestActions'
import { handleActions } from '@/ducks/utils/immerHandleActions'
import { LOCATION_CHANGE } from 'redux-first-history'

export const actions = createActions(
	{
		create: DollyApi.createGruppe,
		update: DollyApi.updateGruppe,
		laas: DollyApi.updateGruppeLaas,
		sendGruppeTags: DollyApi.updateGruppeSendTags,
		getGruppeExcelFil: DollyApi.getExcelFil,
		getOrgExcelFil: DollyApi.getOrgExcelFil,
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
