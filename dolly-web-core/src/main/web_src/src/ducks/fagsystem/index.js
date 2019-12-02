import { createActions } from 'redux-actions'
import { LOCATION_CHANGE } from 'connected-react-router'
import _get from 'lodash/get'
import _set from 'lodash/set'
import _merge from 'lodash/merge'
import { DollyApi, TpsfApi, SigrunApi, KrrApi, ArenaApi, InstApi, UdiApi } from '~/service/Api'
import { onSuccess } from '~/ducks/utils/requestActions'
import { selectIdentById } from '~/ducks/gruppe'
import { getBestillingById, successMiljoSelector } from '~/ducks/bestillingStatus'
import { handleActions } from '~/ducks/utils/immerHandleActions'
import Formatters from '~/utils/DataFormatter'

export const actions = createActions(
	{
		getTpsf: TpsfApi.getTestbrukere,
		getSigrun: [
			SigrunApi.getTestbruker,
			ident => ({
				ident
			})
		],
		getSigrunSekvensnr: [
			SigrunApi.getSekvensnummer,
			ident => ({
				ident
			})
		],
		getKrr: [
			KrrApi.getTestbruker,
			ident => ({
				ident
			})
		],
		getArena: [
			ArenaApi.getTestbruker,
			ident => ({
				ident
			})
		],
		getAareg: [
			DollyApi.getArbeidsforhold,
			ident => ({
				ident
			})
		],
		getInst: [
			InstApi.getTestbruker,
			ident => ({
				ident
			})
		],
		getUdi: [
			UdiApi.getTestbruker,
			ident => ({
				ident
			})
		],
		getPDL: [
			DollyApi.getPersonFraPersonoppslag,
			ident => ({
				ident
			})
		],
		frigjoerTestbruker: [
			DollyApi.deleteTestIdent,
			(gruppeId, ident) => ({
				ident
			})
		]
	},
	{
		prefix: 'fagsystem' // String used to prefix each type
	}
)

// TODO: DENNE MÅ FIKSES
// export const GET_KRR_TESTBRUKER = createAction(
// 	'GET_KRR_TESTBRUKER',
// 	async ident => {
// 		try {
// 			const res = await KrrApi.getTestbruker(ident)
// 			return res
// 		} catch (err) {
// 			if (err.response && err.response.status === 404) {
// 				console.error(err.response.data.melding)
// 				//ERROR 404 betyr at det ikke finnes data for identen, fake opp datastruktur slik at reducer blir consistent
// 				return { data: [null] }
// 			}
// 			return err
// 		}
// 	},
// 	ident => ({
// 		ident
// 	})
// )

const initialState = {
	tpsf: {},
	sigrunstub: {},
	krrstub: {},
	arenaforvalteren: {},
	aareg: {},
	pdlforvalter: {},
	instdata: {},
	udistub: {}
}

export default handleActions(
	{
		[LOCATION_CHANGE](state, action) {
			return initialState
		},
		[onSuccess(actions.getTpsf)](state, action) {
			action.payload.data.forEach(ident => {
				state.tpsf[ident.ident] = ident
			})
		},
		[onSuccess(actions.getSigrun)](state, action) {
			state.sigrunstub[action.meta.ident] = action.payload.data.responseList
		},
		[onSuccess(actions.getSigrunSekvensnr)](state, action) {
			const inntektData = state.sigrunstub[action.meta.ident]
			state.sigrunstub[action.meta.ident] = inntektData.map(i => {
				const sekvens = action.payload.data.find(s => s.gjelderPeriode === i.inntektsaar)
				const sekvensnummer = sekvens && sekvens.sekvensnummer.toString()
				return { ...i, sekvensnummer }
			})
		},
		[onSuccess(actions.getKrr)](state, action) {
			state.krrstub[action.meta.ident] = action.payload.data[0]
		},
		[onSuccess(actions.getArena)](state, action) {
			state.arenaforvalteren[action.meta.ident] = action.payload.data
		},
		[onSuccess(actions.getAareg)](state, action) {
			state.aareg[action.meta.ident] = action.payload.data
		},
		[onSuccess(actions.getUdi)](state, action) {
			state.udistub[action.meta.ident] = action.payload.data.person
		},
		[onSuccess(actions.getPDL)](state, action) {
			state.pdlforvalter[action.meta.ident] = action.payload.data
		},
		[onSuccess(actions.getInst)](state, action) {
			state.instdata[action.meta.ident] = action.payload.data
		},
		[onSuccess(actions.frigjoerTestbruker)](state, action) {
			delete state.tpsf[action.mate.ident]
			delete state.sigrunstub[action.mate.ident]
			delete state.krrstub[action.mate.ident]
			delete state.arenaforvalteren[action.mate.ident]
			delete state.aareg[action.mate.ident]
			delete state.pdlforvalter[action.mate.ident]
			delete state.instdata[action.mate.ident]
			delete state.udistub[action.mate.ident]
		}
	},
	initialState
)

// Thunk
export const fetchTpsfTestbrukere = () => (dispatch, getState) => {
	const state = getState()
	const identer = Object.keys(state.gruppe.ident)
	if (identer && identer.length >= 1) dispatch(actions.getTpsf(identer))
}

/**
 * Sjekke hvilke fagsystemer som har bestillingsstatus satt til 'OK'.
 * De systemene som har OK fetches
 */
export const fetchDataFraFagsystemer = personId => (dispatch, getState) => {
	const state = getState()

	// Person fra gruppe
	const person = selectIdentById(state, personId)

	// Bestillingen(e) fra bestillingStatuser
	const bestillinger = person.bestillingId.map(id => getBestillingById(state, id))

	// Samlet liste over alle statuser
	const statusArray = bestillinger.reduce((acc, curr) => acc.concat(curr.status), [])

	// Liste over systemer som har data
	const success = successMiljoSelector(statusArray)

	// Samle alt fra PDL under en ID
	if (Object.keys(success).some(a => a.substring(0, 3) === 'PDL')) {
		success.PDL = 'PDL'
	}

	Object.keys(success).forEach(system => {
		switch (system) {
			case 'KRRSTUB':
				return dispatch(actions.getKrr(personId))
			case 'SIGRUNSTUB':
				dispatch(actions.getSigrun(personId))
				return dispatch(actions.getSigrunSekvensnr(personId))
			case 'ARENA':
				return dispatch(actions.getArena(personId))
			case 'PDL':
				return dispatch(actions.getPDL(personId))
			case 'UDISTUB':
				return dispatch(actions.getUdi(personId))
			case 'AAREG':
				return dispatch(actions.getAareg(personId, success[system][0]))
			case 'INST2':
				return dispatch(actions.getInst(personId, success[system][0]))
		}
	})
}

// Selectors
export const sokSelector = (items, searchStr) => {
	if (!items) return []
	if (!searchStr) return items

	const query = searchStr.toLowerCase()
	return items.filter(item => {
		return Object.values(item).some(v =>
			v
				.toString()
				.toLowerCase()
				.includes(query)
		)
	})
}

export const selectTestbrukerListe = state => {
	const { gruppe, fagsystem } = state
	console.log('fagsystem :', fagsystem)
	if (!fagsystem.tpsf) return null

	return Object.values(fagsystem.tpsf).map(ident => ({
		ident: ident.ident,
		gruppeId: gruppe.ident[ident.ident].gruppeId,
		identtype: ident.identtype,
		navn: `${ident.fornavn} ${ident.mellomnavn || ''} ${ident.etternavn}`,
		kjonn: Formatters.kjonnToString(ident.kjonn),
		alder: Formatters.formatAlder(ident.alder, ident.doedsdato),
		bestillingId: gruppe.ident[ident.ident].bestillingId.toString()
	}))
}

export const selectDataForIdent = (state, ident) => {
	return {
		tpsf: state.fagsystem.tpsf[ident],
		sigrunstub: state.fagsystem.sigrunstub[ident],
		krrstub: state.fagsystem.krrstub[ident],
		arenaforvalteren: state.fagsystem.arenaforvalteren[ident],
		aareg: state.fagsystem.aareg[ident],
		pdlforvalter: state.fagsystem.pdlforvalter[ident],
		instdata: state.fagsystem.instdata[ident],
		udistub: state.fagsystem.udistub[ident]
	}
}
