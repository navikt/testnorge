import { createAction } from 'redux-actions'
import { LOCATION_CHANGE } from 'connected-react-router'
import _get from 'lodash/get'
import _set from 'lodash/set'
import _merge from 'lodash/merge'
import { DollyApi, TpsfApi, SigrunApi, KrrApi, ArenaApi, InstApi, UdiApi } from '~/service/Api'
import success from '~/utils/SuccessAction'
import { DataSource } from '~/service/kodeverk/AttributtManager/Types'
import {
	mapIdentAndEnvironementForTps,
	mapValuesFromDataSource,
	mapSigrunSekvensnummer
} from './utils'

const initialState = {
	items: {
		tpsf: null,
		sigrunstub: null,
		krrstub: null,
		arenaforvalteren: null,
		aareg: null,
		pdlforvalter: null,
		instdata: null,
		udistub: null
	}
}

const actionTypes = {
	UPDATE_TESTBRUKER_REQUEST: 'UPDATE_TESTBRUKER_REQUEST',
	UPDATE_TESTBRUKER_SUCCESS: 'UPDATE_TESTBRUKER_SUCCESS',
	UPDATE_TESTBRUKER_ERROR: 'UPDATE_TESTBRUKER_ERROR'
}

const updateTestbrukerRequest = () => ({ type: actionTypes.UPDATE_TESTBRUKER_REQUEST })
const updateTestbrukerSuccess = () => ({ type: actionTypes.UPDATE_TESTBRUKER_SUCCESS })
const updateTestbrukerError = () => ({ type: actionTypes.UPDATE_TESTBRUKER_ERROR })

export const GET_TPSF_TESTBRUKERE = createAction('GET_TPSF_TESTBRUKERE', TpsfApi.getTestbrukere)

export const GET_SIGRUN_TESTBRUKER = createAction(
	'GET_SIGRUN_TESTBRUKER',
	SigrunApi.getTestbruker,
	ident => ({
		ident
	})
)

export const GET_SIGRUN_SEKVENSNR = createAction(
	'GET_SIGRUN_SEKVENSNR',
	SigrunApi.getSekvensnummer,
	ident => ({
		ident
	})
)

export const GET_KRR_TESTBRUKER = createAction(
	'GET_KRR_TESTBRUKER',
	async ident => {
		try {
			const res = await KrrApi.getTestbruker(ident)
			return res
		} catch (err) {
			if (err.response && err.response.status === 404) {
				console.error(err.response.data.melding)
				//ERROR 404 betyr at det ikke finnes data for identen, fake opp datastruktur slik at reducer blir consistent
				return { data: [null] }
			}
			return err
		}
	},
	ident => ({
		ident
	})
)

export const GET_ARENA_TESTBRUKER = createAction(
	'GET_ARENA_TESTBRUKER',
	ArenaApi.getTestbruker,
	ident => ({
		ident
	})
)

export const GET_AAREG_TESTBRUKER = createAction(
	'GET_AAREG_TESTBRUKER',
	DollyApi.getArbeidsforhold,
	ident => ({
		ident
	})
)

export const GET_INST_TESTBRUKER = createAction(
	'GET_INST_TESTBRUKER',
	InstApi.getTestbruker,
	ident => ({
		ident
	})
)

export const GET_UDI_TESTBRUKER = createAction(
	'GET_UDI_TESTBRUKER',
	UdiApi.getTestbruker,
	ident => ({
		ident
	})
)

export const GET_TESTBRUKER_PERSONOPPSLAG = createAction(
	'GET_TESTBRUKER_PERSONOPPSLAG',
	DollyApi.getPersonFraPersonoppslag,
	ident => ({
		ident
	})
)

export const FRIGJOER_TESTBRUKER = createAction(
	'FRIGJOER_TESTBRUKER',
	DollyApi.deleteTestIdent,
	(gruppeId, ident) => ({
		ident
	})
)

export default function testbrukerReducer(state = initialState, action) {
	switch (action.type) {
		case LOCATION_CHANGE:
			if (!action.payload.location.pathname.match(/testbruker/gi)) return initialState
			else return state
		case success(GET_TPSF_TESTBRUKERE):
			return { ...state, items: { ...state.items, tpsf: action.payload.data } }
		case success(FRIGJOER_TESTBRUKER):
			return {
				...state,
				items: {
					...state.items,
					tpsf: state.items.tpsf.filter(item => item.ident !== action.meta.ident),
					sigrunstub: { ...state.items.sigrunstub, [action.meta.ident]: null },
					krrstub: { ...state.items.krrstub, [action.meta.ident]: null },
					udistub: { ...state.items.udistub, [action.meta.ident]: null },
					arenaforvalteren: { ...state.items.arenaforvalteren, [action.meta.ident]: null },
					aareg: { ...state.items.aareg, [action.meta.ident]: null },
					pdlforvalter: { ...state.items.aareg, [action.meta.ident]: null },
					instdata: { ...state.items.aareg, [action.meta.ident]: null }
				}
			}

		case success(GET_SIGRUN_TESTBRUKER):
			return {
				...state,
				items: {
					...state.items,
					sigrunstub: {
						...state.items.sigrunstub,
						[action.meta.ident]: action.payload && action.payload.data
					}
				}
			}
		case success(GET_SIGRUN_SEKVENSNR):
			const inntektData = state.items.sigrunstub[action.meta.ident]
			const sekvensData = action.payload && action.payload.data
			return {
				...state,
				items: {
					...state.items,
					sigrunstub: {
						...state.items.sigrunstub,
						[action.meta.ident]: mapSigrunSekvensnummer(inntektData, sekvensData)
					}
				}
			}
		case success(GET_KRR_TESTBRUKER):
			return {
				...state,
				items: {
					...state.items,
					krrstub: {
						...state.items.krrstub,
						[action.meta.ident]: action.payload.data && action.payload.data[0]
					}
				}
			}
		case success(GET_ARENA_TESTBRUKER):
			return {
				...state,
				items: {
					...state.items,
					arenaforvalteren: {
						...state.items.arenaforvalteren,
						[action.meta.ident]: action.payload && action.payload
					}
				}
			}
		case success(GET_AAREG_TESTBRUKER):
			return {
				...state,
				items: {
					...state.items,
					aareg: {
						...state.items.aareg,
						[action.meta.ident]: action.payload && action.payload.data
					}
				}
			}
		case success(GET_UDI_TESTBRUKER):
			return {
				...state,
				items: {
					...state.items,
					udistub: {
						...state.items.udistub,
						[action.meta.ident]: action.payload && action.payload.data.person
					}
				}
			}
		case success(GET_TESTBRUKER_PERSONOPPSLAG):
			return {
				...state,
				items: {
					...state.items,
					pdlforvalter: {
						...state.items.pdlforvalter,
						[action.meta.ident]: action.payload && action.payload.data
					}
				}
			}
		case success(GET_INST_TESTBRUKER):
			return {
				...state,
				items: {
					...state.items,
					instdata: {
						...state.items.instdata,
						[action.meta.ident]: action.payload && action.payload.data
					}
				}
			}
		case actionTypes.UPDATE_TESTBRUKER_SUCCESS:
			return state
		default:
			return state
	}
}

// Thunk
export const fetchTpsfTestbrukere = () => (dispatch, getState) => {
	const state = getState()
	const identer = _get(state, 'gruppe.data[0].testidenter', []).map(ident => ident.ident)
	if (identer && identer.length >= 1) dispatch(GET_TPSF_TESTBRUKERE(identer))
}

export const updateTestbruker = (values, attributtListe, ident) => async (dispatch, getState) => {
	try {
		dispatch(updateTestbrukerRequest())
		const state = getState()
		const { testbruker } = state

		//TPSF
		const tpsfBody = mapValuesFromDataSource(values, attributtListe, DataSource.TPSF)
		const tpsfCurrentValues = testbruker.items.tpsf[0]
		const sendToTpsBody = mapIdentAndEnvironementForTps(state, ident)
		let tpsfJsonToSend = _merge(tpsfCurrentValues, tpsfBody)

		// KUN FOR egen ansatt - spesielt tilfelle
		if (tpsfJsonToSend.egenAnsattDatoFom) {
			tpsfJsonToSend.egenAnsattDatoFom = new Date()
		} else {
			tpsfJsonToSend.egenAnsattDatoFom = null
		}

		const tpsfRequest = async () => {
			const tpsfRes = await TpsfApi.updateTestbruker(tpsfJsonToSend)
			if (tpsfRes.status === 200) {
				const sendToTpsRes = await TpsfApi.sendToTps(sendToTpsBody)
			}
			return tpsfRes
		}

		const promiseList = [tpsfRequest()]

		//KRR-STUB
		const krrstubPreviousValues = testbruker.items.krrstub && testbruker.items.krrstub[ident]
		if (krrstubPreviousValues) {
			const krrstubBody = mapValuesFromDataSource(values, attributtListe, DataSource.KRR)
			const krrStubRequest = KrrApi.updateTestbruker(
				krrstubPreviousValues.id,
				_merge(krrstubPreviousValues, krrstubBody)
			)
			promiseList.push(krrStubRequest)
		}

		//SIGRUN-STUB - multiple values
		const sigrunstubAtributtListe = attributtListe.filter(
			item => item.dataSource === DataSource.SIGRUN
		)
		if (sigrunstubAtributtListe.length > 0) {
			const sigrunstubRequest = sigrunstubAtributtListe.map(attribute => {
				const currentValues = values[attribute.id]
				const items = attribute.items
				const promises = currentValues.map(valueObject => {
					const headerObject = items.reduce(
						(prev, curr) => {
							return _set(prev, curr.editPath || curr.path || curr.id, valueObject[curr.id])
						},
						{ personidentifikator: ident }
					)
					return SigrunApi.updateTestbruker(headerObject)
				})
				return Promise.all(promises)
			})
			promiseList.push(sigrunstubRequest)
		}

		const updateRes = await Promise.all([promiseList])

		dispatch(updateTestbrukerSuccess())
	} catch (error) {
		console.error(error)
		dispatch(updateTestbrukerError())
	}
}

// Selectors
export const sokSelector = (items, searchStr) => {
	if (!items) return []
	if (!searchStr) return items

	const query = searchStr.toLowerCase()
	return items.filter(item => {
		return item.some(v => v.toLowerCase().includes(query))
	})
}
