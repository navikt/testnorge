import { createActions } from 'redux-actions'
import { LOCATION_CHANGE } from 'connected-react-router'
import _get from 'lodash/get'
import _last from 'lodash/last'
import _isEmpty from 'lodash/isEmpty'
import {
	AaregApi,
	ArenaApi,
	BrregstubApi,
	DollyApi,
	InntektstubApi,
	InstApi,
	KrrApi,
	PensjonApi,
	SigrunApi,
	TpsfApi,
	UdiApi
} from '~/service/Api'
import { onSuccess } from '~/ducks/utils/requestActions'
import { selectIdentById } from '~/ducks/gruppe'
import { getBestillingById, successMiljoSelector } from '~/ducks/bestillingStatus'
import { handleActions } from '~/ducks/utils/immerHandleActions'
import Formatters from '~/utils/DataFormatter'

export const actions = createActions(
	{
		getTpsf: TpsfApi.getPersoner,
		getSigrun: [
			SigrunApi.getPerson,
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
		getPensjon: [
			PensjonApi.getPoppInntekt,
			ident => ({
				ident
			})
		],
		getInntektstub: [
			InntektstubApi.getInntektsinformasjon,
			ident => ({
				ident
			})
		],
		getKrr: [
			KrrApi.getPerson,
			ident => ({
				ident
			})
		],
		getArena: [
			ArenaApi.getPerson,
			ident => ({
				ident
			})
		],
		getAareg: [
			AaregApi.getArbeidsforhold,
			ident => ({
				ident
			})
		],
		getInst: [
			InstApi.getPerson,
			ident => ({
				ident
			})
		],
		getUdi: [
			UdiApi.getPerson,
			ident => ({
				ident
			})
		],
		getBrreg: [
			BrregstubApi.getPerson,
			ident => ({
				ident
			})
		],
		getPDL: [
			DollyApi.getPersonFraPdl,
			ident => ({
				ident
			})
		],
		slettPerson: [
			DollyApi.slettPerson,
			ident => ({
				ident
			})
		]
	},
	{
		prefix: 'fagsystem' // String used to prefix each type
	}
)

// TODO: DENNE MÅ FIKSES
// export const GET_KRR_PERSON = createAction(
// 	'GET_KRR_PERSON',
// 	async ident => {
// 		try {
// 			const res = await KrrApi.getPerson(ident)
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
	inntektstub: {},
	krrstub: {},
	arenaforvalteren: {},
	aareg: {},
	pdlforvalter: {},
	instdata: {},
	udistub: {},
	pensjonforvalter: {},
	brregstub: {}
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
			if (inntektData) {
				state.sigrunstub[action.meta.ident] = inntektData.map(i => {
					const sekvens = action.payload.data.find(s => s.gjelderPeriode === i.inntektsaar)
					const sekvensnummer = sekvens && sekvens.sekvensnummer.toString()
					return { ...i, sekvensnummer }
				})
			}
		},
		[onSuccess(actions.getInntektstub)](state, action) {
			state.inntektstub[action.meta.ident] = action.payload.data
		},
		[onSuccess(actions.getKrr)](state, action) {
			state.krrstub[action.meta.ident] = action.payload.data
		},
		[onSuccess(actions.getArena)](state, action) {
			state.arenaforvalteren[action.meta.ident] = action.payload.data
		},
		[onSuccess(actions.getAareg)](state, action) {
			state.aareg[action.meta.ident] = action.payload.data
		},
		[onSuccess(actions.getPensjon)](state, action) {
			state.pensjonforvalter[action.meta.ident] = action.payload.data
		},
		[onSuccess(actions.getUdi)](state, action) {
			state.udistub[action.meta.ident] = action.payload?.data?.person
		},
		[onSuccess(actions.getBrreg)](state, action) {
			state.brregstub[action.meta.ident] = action.payload.data
		},
		[onSuccess(actions.getPDL)](state, action) {
			state.pdlforvalter[action.meta.ident] = action.payload.data
		},
		[onSuccess(actions.getInst)](state, action) {
			state.instdata[action.meta.ident] = action.payload.data
		},
		[onSuccess(actions.slettPerson)](state, action) {
			delete state.tpsf[action.meta.ident]
			delete state.sigrunstub[action.meta.ident]
			delete state.inntektstub[action.meta.ident]
			delete state.krrstub[action.meta.ident]
			delete state.arenaforvalteren[action.meta.ident]
			delete state.aareg[action.meta.ident]
			delete state.pdlforvalter[action.meta.ident]
			delete state.instdata[action.meta.ident]
			delete state.udistub[action.meta.ident]
			delete state.pensjonforvalter[action.meta.ident]
			delete state.brregstub[action.meta.ident]
		}
	},
	initialState
)

// Thunk
export const fetchTpsfPersoner = () => (dispatch, getState) => {
	const state = getState()

	let identListe = []
	Object.values(state.gruppe?.ident)?.forEach(person => identListe.push(person.ident))

	if (identListe && identListe.length >= 1) dispatch(actions.getTpsf(identListe))
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
			case 'INNTK':
				return dispatch(actions.getInntektstub(personId))
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
			case 'PEN_INNTEKT':
				return dispatch(actions.getPensjon(personId, success[system][0]))
			case 'BRREGSTUB':
				return dispatch(actions.getBrreg(personId))
		}
	})
}

export const fetchDataFraFagsystemerForSoek = personId => dispatch => {
	// Liste over systemer
	const systemer = [
		'KRRSTUB',
		'SIGRUNSTUB',
		'INNTK',
		'ARENA',
		'PDL',
		'INST2',
		'PEN_INNTEKT',
		'AAREG'
	]

	systemer.forEach(system => {
		switch (system) {
			case 'KRRSTUB':
				return dispatch(actions.getKrr(personId))
			case 'SIGRUNSTUB':
				dispatch(actions.getSigrun(personId))
				return dispatch(actions.getSigrunSekvensnr(personId))
			case 'INNTK':
				return dispatch(actions.getInntektstub(personId))
			case 'ARENA':
				return dispatch(actions.getArena(personId))
			case 'PDL':
				return dispatch(actions.getPDL(personId))
			case 'INST2':
				return dispatch(actions.getInst(personId, 'q2'))
			case 'PEN_INNTEKT':
				return dispatch(actions.getPensjon(personId, 'q2'))
			case 'AAREG':
				return dispatch(actions.getAareg(personId, 'q2'))
		}
	})
}

// Selectors
export const sokSelector = (items, searchStr) => {
	if (!items) return []
	if (!searchStr) return items

	const query = searchStr.toLowerCase()
	return items.filter(item =>
		Object.values(item).some(v =>
			(v || '')
				.toString()
				.toLowerCase()
				.includes(query)
		)
	)
}

const hentPersonStatus = (ident, bestillingStatus) => {
	if (!bestillingStatus) return null
	let totalStatus = 'Ferdig'

	bestillingStatus.status.forEach(fagsystem => {
		_get(fagsystem, 'statuser', []).forEach(status => {
			_get(status, 'detaljert', []).forEach(miljoe => {
				_get(miljoe, 'identer', []).forEach(miljoeIdent => {
					if (miljoeIdent === ident) {
						if (status.melding !== 'OK') totalStatus = 'Avvik'
					}
				})
			})
		})
	})
	return totalStatus
}

export const selectPersonListe = state => {
	const { gruppe, fagsystem } = state

	if (_isEmpty(fagsystem.tpsf)) return null

	// Sortert etter bestillingsId
	const identer = Object.values(gruppe.ident)
		.filter(gruppeIdent => gruppeIdent.bestillingId != null && gruppeIdent.bestillingId.length > 0)
		.sort((a, b) => _last(b.bestillingId) - _last(a.bestillingId))
		.filter(gruppeIdent => Object.keys(fagsystem.tpsf).includes(gruppeIdent.ident))

	return identer
		.filter(
			ident =>
				ident.bestillingId.length > 0 &&
				state.bestillingStatuser.byId[ident.bestillingId[0]] != null
		)
		.map(ident => {
			const tpsfIdent = fagsystem.tpsf[ident.ident]
			const mellomnavn = tpsfIdent.mellomnavn ? `${tpsfIdent.mellomnavn.charAt(0)}.` : ''

			return {
				ident,
				identNr: tpsfIdent.ident,
				bestillingId: ident.bestillingId,
				importFra: tpsfIdent.importFra,
				identtype: tpsfIdent.identtype,
				navn: `${tpsfIdent.fornavn} ${mellomnavn} ${tpsfIdent.etternavn}`,
				kjonn: Formatters.kjonn(tpsfIdent.kjonn, tpsfIdent.alder),
				alder: Formatters.formatAlder(tpsfIdent.alder, tpsfIdent.doedsdato),
				status: hentPersonStatus(ident.ident, state.bestillingStatuser.byId[ident.bestillingId[0]])
			}
		})
}

export const selectDataForIdent = (state, ident) => {
	return {
		tpsf: state.fagsystem.tpsf[ident],
		sigrunstub: state.fagsystem.sigrunstub[ident],
		inntektstub: state.fagsystem.inntektstub[ident],
		krrstub: state.fagsystem.krrstub[ident],
		arenaforvalteren: state.fagsystem.arenaforvalteren[ident],
		aareg: state.fagsystem.aareg[ident],
		pdlforvalter: state.fagsystem.pdlforvalter[ident],
		instdata: state.fagsystem.instdata[ident],
		udistub: state.fagsystem.udistub[ident],
		pensjonforvalter: state.fagsystem.pensjonforvalter[ident],
		brregstub: state.fagsystem.brregstub[ident]
	}
}
