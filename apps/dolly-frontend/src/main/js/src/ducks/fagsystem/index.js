import { createActions } from 'redux-actions'
import _get from 'lodash/get'
import _last from 'lodash/last'
import _isEmpty from 'lodash/isEmpty'
import {
	ArenaApi,
	BrregstubApi,
	DollyApi,
	InntektstubApi,
	InstApi,
	KrrApi,
	PdlforvalterApi,
	PensjonApi,
	SigrunApi,
	TpsfApi,
	TpsMessagingApi,
} from '~/service/Api'
import { onSuccess } from '~/ducks/utils/requestActions'
import { getBestillingById, successMiljoSelector } from '~/ducks/bestillingStatus'
import { handleActions } from '~/ducks/utils/immerHandleActions'
import Formatters from '~/utils/DataFormatter'
import { isNil } from 'lodash'

export const actions = createActions(
	{
		getTpsf: TpsfApi.getPersoner,
		getTpsMessaging: [
			TpsMessagingApi.getTpsPersonInfoAllEnvs,
			(ident) => ({
				ident,
			}),
		],
		getSigrun: [
			SigrunApi.getPerson,
			(ident) => ({
				ident,
			}),
		],
		getSigrunSekvensnr: [
			SigrunApi.getSekvensnummer,
			(ident) => ({
				ident,
			}),
		],
		getPensjon: [
			PensjonApi.getPoppInntekt,
			(ident) => ({
				ident,
			}),
		],
		getInntektstub: [
			InntektstubApi.getInntektsinformasjon,
			(ident) => ({
				ident,
			}),
		],
		getKrr: [
			KrrApi.getPerson,
			(ident) => ({
				ident,
			}),
		],
		getArena: [
			ArenaApi.getPerson,
			(ident) => ({
				ident,
			}),
		],
		getAareg: [
			DollyApi.getArbeidsforhold,
			(ident) => ({
				ident,
			}),
		],
		getInst: [
			InstApi.getPerson,
			(ident) => ({
				ident,
			}),
		],
		getUdi: [
			DollyApi.getUdiPerson,
			(ident) => ({
				ident,
			}),
		],
		getBrreg: [
			BrregstubApi.getPerson,
			(ident) => ({
				ident,
			}),
		],
		getPDLPersoner: [
			DollyApi.getPersonerFraPdl,
			(identer) => ({
				identer,
			}),
		],
		getPdlForvalter: [
			PdlforvalterApi.getPersoner,
			(identer) => ({
				identer,
			}),
		],
		getSkjermingsregister: [
			DollyApi.getSkjerming,
			(ident) => ({
				ident,
			}),
		],
		slettPerson: [
			DollyApi.slettPerson,
			(ident) => ({
				ident,
			}),
		],
	},
	{
		prefix: 'fagsystem', // String used to prefix each type
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
	tpsMessaging: {},
	sigrunstub: {},
	inntektstub: {},
	krrstub: {},
	arenaforvalteren: {},
	aareg: {},
	pdl: {},
	pdlforvalter: {},
	instdata: {},
	udistub: {},
	pensjonforvalter: {},
	brregstub: {},
	skjermingsregister: {},
}

export default handleActions(
	{
		[onSuccess(actions.getTpsf)](state, action) {
			action.payload.data.forEach((ident) => {
				state.tpsf[ident.ident] = ident
			})
		},
		[onSuccess(actions.getSigrun)](state, action) {
			state.sigrunstub[action.meta.ident] = action.payload.data.responseList
		},
		[onSuccess(actions.getTpsMessaging)](state, action) {
			state.tpsMessaging[action.meta.ident] = action?.payload.data?.[0]?.person
		},
		[onSuccess(actions.getSigrunSekvensnr)](state, action) {
			const inntektData = state.sigrunstub[action.meta.ident]
			if (inntektData) {
				state.sigrunstub[action.meta.ident] = inntektData.map((i) => {
					const sekvens = action.payload.data.find((s) => s.gjelderPeriode === i.inntektsaar)
					const sekvensnummer = sekvens && sekvens.sekvensnummer.toString()
					return { ...i, sekvensnummer }
				})
			}
		},
		[onSuccess(actions.getInntektstub)](state, action) {
			state.inntektstub[action.meta.ident] = action.payload.data
		},
		[onSuccess(actions.getSkjermingsregister)](state, action) {
			state.skjermingsregister[action.meta.ident] = action.payload.data
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
		[onSuccess(actions.getPDLPersoner)](state, action) {
			const identerBolk = action.payload.data?.data?.hentIdenterBolk?.reduce(
				(map, person) => ({
					...map,
					[person.ident]: person.identer,
				}),
				{}
			)
			const geografiskTilknytningBolk =
				action.payload.data?.data?.hentGeografiskTilknytningBolk?.reduce(
					(map, person) => ({
						...map,
						[person.ident]: person.geografiskTilknytning,
					}),
					{}
				)

			action.payload.data?.data?.hentPersonBolk?.forEach((ident) => {
				state.pdl[ident.ident] = {
					hentIdenter: { identer: identerBolk?.[ident.ident] },
					hentGeografiskTilknytning: geografiskTilknytningBolk?.[ident.ident],
					hentPerson: ident.person,
					ident: ident.ident,
				}
			})
		},
		[onSuccess(actions.getPdlForvalter)](state, action) {
			action.payload?.data?.forEach((ident) => {
				state.pdlforvalter[ident.person.ident] = ident
			})
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
			delete state.pdl[action.meta.ident]
			delete state.pdlforvalter[action.meta.ident]
			delete state.instdata[action.meta.ident]
			delete state.udistub[action.meta.ident]
			delete state.pensjonforvalter[action.meta.ident]
			delete state.brregstub[action.meta.ident]
		},
	},
	initialState
)

// Thunk
export const fetchTpsfPersoner = (identer) => (dispatch) => {
	const tpsIdenter = identer
		.map((person) => {
			if (!person.master || (person.master !== 'PDLF' && person.master !== 'PDL')) {
				return person.ident
			}
		})
		.filter((person) => !isNil(person))

	if (tpsIdenter && tpsIdenter.length >= 1) dispatch(actions.getTpsf(tpsIdenter))
}

export const fetchPdlPersoner = (identer, fagsystem) => (dispatch) => {
	const pdlIdenter = identer
		.filter(
			(person) =>
				!fagsystem.pdl[person.ident] &&
				!fagsystem.pdlforvalter[person.ident] &&
				!fagsystem.tpsf[person.ident]
		)
		.map((person) => {
			return person.ident
		})
	if (pdlIdenter && pdlIdenter.length >= 1) {
		dispatch(actions.getPdlForvalter(pdlIdenter))
		dispatch(actions.getPDLPersoner(pdlIdenter))
	}
}

/**
 * Sjekke hvilke fagsystemer som har bestillingsstatus satt til 'OK'.
 * De systemene som har OK fetches
 */
export const fetchDataFraFagsystemer = (person) => (dispatch, getState) => {
	const state = getState()
	const personId = person.ident

	// Bestillingen(e) fra bestillingStatuser
	const bestillinger = person.bestillingId.map((id) => getBestillingById(state, id))

	// Samlet liste over alle statuser
	const statusArray = bestillinger.reduce((acc, curr) => acc.concat(curr.status), [])

	// Liste over systemer som har data
	const success = successMiljoSelector(statusArray)

	// Samle alt fra PDL under en ID
	if (Object.keys(success).some((a) => a.substring(0, 3) === 'PDL')) {
		success.PDL = 'PDL'
		success.PDL_FORVALTER = 'PDL_FORVALTER'
	}

	Object.keys(success).forEach((system) => {
		switch (system) {
			case 'KRRSTUB':
				return dispatch(actions.getKrr(personId))
			case 'SIGRUNSTUB':
				dispatch(actions.getSigrun(personId))
				return dispatch(actions.getSigrunSekvensnr(personId))
			case 'INNTK':
				return dispatch(actions.getInntektstub(personId))
			case 'TPS_MESSAGING':
				return dispatch(actions.getTpsMessaging(personId))
			case 'ARENA':
				return dispatch(actions.getArena(personId))
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
			case 'SKJERMINGSREGISTER':
				return dispatch(actions.getSkjermingsregister(personId))
		}
	})
}

export const fetchDataFraFagsystemerForSoek = (personId) => (dispatch) => {
	// Liste over systemer
	const systemer = [
		'KRRSTUB',
		'SIGRUNSTUB',
		'INNTK',
		'ARENA',
		'PDL_FORVALTER',
		'PDL',
		'INST2,',
		'PEN_INNTEKT',
		'AAREG',
	]

	systemer.forEach((system) => {
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
			case 'PDL_FORVALTER':
				return dispatch(actions.getPdlForvalter(personId))
			case 'INST2':
				return dispatch(actions.getInst(personId, 'q2'))
			case 'PEN_INNTEKT':
				return dispatch(actions.getPensjon(personId, 'q2'))
			case 'AAREG':
				return dispatch(actions.getAareg(personId, 'q2'))
			case 'SKJERMINGSREGISTER':
				return dispatch(actions.getSkjermingsregister(personId))
		}
	})
}

// Selectors
export const sokSelector = (items, searchStr) => {
	if (!items) return []
	if (!searchStr) return items

	const query = searchStr.toLowerCase()
	return items.filter((item) =>
		Object.values(item).some((v) => (v || '').toString().toLowerCase().includes(query))
	)
}

const hentPersonStatus = (ident, bestillingStatus) => {
	let totalStatus = 'Ferdig'

	if (!bestillingStatus) return totalStatus

	bestillingStatus.status.forEach((fagsystem) => {
		_get(fagsystem, 'statuser', []).forEach((status) => {
			_get(status, 'detaljert', []).forEach((miljoe) => {
				_get(miljoe, 'identer', []).forEach((miljoeIdent) => {
					if (miljoeIdent === ident) {
						if (status.melding !== 'OK') totalStatus = 'Avvik'
					}
				})
			})
		})
	})
	return totalStatus
}

export const selectPersonListe = (identer, bestillingStatuser, fagsystem) => {
	if (
		!identer ||
		(_isEmpty(fagsystem.tpsf) && _isEmpty(fagsystem.pdlforvalter) && _isEmpty(fagsystem.pdl))
	)
		return null

	// Sortert etter bestillingsId
	const identListe = Object.values(identer)
		.sort((first, second) =>
			first.bestillingId && second.bestillingId
				? _last(second?.bestillingId) - _last(first?.bestillingId)
				: _last(second?.ident) - _last(first?.ident)
		)
		.filter(
			(gruppeIdent) =>
				Object.keys(fagsystem.tpsf).includes(gruppeIdent.ident) ||
				Object.keys(fagsystem.pdlforvalter).includes(gruppeIdent.ident) ||
				Object.keys(fagsystem.pdl).includes(gruppeIdent.ident)
		)

	return identListe.map((ident) => {
		if (ident.master === 'TPSF') {
			const tpsfIdent = fagsystem.tpsf[ident.ident]
			return getTpsfIdentInfo(ident, bestillingStatuser, tpsfIdent)
		} else if (ident.master === 'PDLF') {
			const pdlfIdent = fagsystem.pdlforvalter[ident.ident]?.person
			return getPdlfIdentInfo(ident, bestillingStatuser, pdlfIdent)
		} else if (ident.master === 'PDL') {
			const pdlData = fagsystem.pdl[ident.ident]
			return getPdlIdentInfo(ident, bestillingStatuser, pdlData)
		} else {
			return null
		}
	})
}

const getTpsfIdentInfo = (ident, bestillingStatuser, tpsfIdent) => {
	if (!tpsfIdent) return null
	const mellomnavn = tpsfIdent?.mellomnavn ? `${tpsfIdent.mellomnavn.charAt(0)}.` : ''
	return {
		ident,
		identNr: tpsfIdent.ident,
		bestillingId: ident.bestillingId,
		importFra: tpsfIdent.importFra,
		identtype: tpsfIdent.identtype,
		kilde: 'TPS',
		navn: `${tpsfIdent.fornavn} ${mellomnavn} ${tpsfIdent.etternavn}`,
		kjonn: Formatters.kjonn(tpsfIdent.kjonn, tpsfIdent.alder),
		alder: Formatters.formatAlder(tpsfIdent.alder, tpsfIdent.doedsdato),
		status: hentPersonStatus(ident.ident, bestillingStatuser.byId[ident.bestillingId[0]]),
	}
}

const getPdlfIdentInfo = (ident, bestillingStatuser, pdlIdent) => {
	if (!pdlIdent) return null

	const pdlMellomnavn = pdlIdent?.navn?.[0]?.mellomnavn
		? `${pdlIdent?.navn?.[0]?.mellomnavn.charAt(0)}.`
		: ''

	const pdlAlder = (foedselsdato) => {
		if (!foedselsdato) return null
		const diff = new Date(Date.now() - new Date(foedselsdato).getTime())
		return Math.abs(diff.getUTCFullYear() - 1970)
	}

	return {
		ident,
		identNr: pdlIdent.ident,
		bestillingId: ident?.bestillingId,
		identtype: 'FNR',
		kilde: 'PDL',
		navn: `${pdlIdent.navn?.[0]?.fornavn} ${pdlMellomnavn} ${pdlIdent.navn?.[0]?.etternavn}`,
		kjonn: pdlIdent.kjoenn?.[0]?.kjoenn,
		alder: Formatters.formatAlder(
			pdlAlder(pdlIdent?.foedsel?.[0]?.foedselsdato),
			pdlIdent?.doedsfall?.[0]?.doedsdato
		),
		status: hentPersonStatus(ident?.ident, bestillingStatuser.byId[ident.bestillingId[0]]),
	}
}

const getPdlIdentInfo = (ident, bestillingStatuser, pdlData) => {
	if (!pdlData) return null
	const person = pdlData.person || pdlData.hentPerson
	if (!person) return null

	const navn = person.navn[0]
	const mellomnavn = navn?.mellomnavn ? `${navn.mellomnavn.charAt(0)}.` : ''
	const kjonn = person.kjoenn[0] ? getKjoenn(person.kjoenn[0].kjoenn) : 'U'
	const alder = getAlder(person.foedsel[0]?.foedselsdato, person.doedsfall[0]?.doedsdato)

	return {
		ident,
		identNr: ident?.ident,
		bestillingId: ident.bestillingId,
		kilde: 'TEST-NORGE',
		importFra: 'Test-Norge',
		identtype: person?.folkeregisteridentifikator[0]?.type,
		navn: `${navn.fornavn} ${mellomnavn} ${navn.etternavn}`,
		kjonn: Formatters.kjonn(kjonn, alder),
		alder: Formatters.formatAlder(alder, person.doedsfall[0]?.doedsdato),
		status: hentPersonStatus(ident.ident, bestillingStatuser.byId[ident.bestillingId[0]]),
	}
}

export const getAlder = (datoFoedt, doedsdato) => {
	const foedselsdato = new Date(datoFoedt)
	let diff_ms = Date.now() - foedselsdato.getTime()

	if (doedsdato && doedsdato !== '') {
		const ddato = new Date(doedsdato)
		diff_ms = ddato.getTime() - foedselsdato.getTime()
	}

	const age_dt = new Date(diff_ms)
	return Math.abs(age_dt.getUTCFullYear() - 1970)
}

export const getKjoenn = (kjoenn) => {
	switch (kjoenn) {
		case 'KVINNE':
			return 'K'
		case 'MANN':
			return 'M'
		default:
			return 'U'
	}
}

export const selectDataForIdent = (state, ident) => {
	return {
		tpsf: state.fagsystem.tpsf[ident],
		tpsMessaging: state.fagsystem.tpsMessaging[ident],
		sigrunstub: state.fagsystem.sigrunstub[ident],
		inntektstub: state.fagsystem.inntektstub[ident],
		krrstub: state.fagsystem.krrstub[ident],
		arenaforvalteren: state.fagsystem.arenaforvalteren[ident],
		aareg: state.fagsystem.aareg[ident],
		pdl: state.fagsystem.pdl[ident],
		pdlforvalter: state.fagsystem.pdlforvalter[ident],
		instdata: state.fagsystem.instdata[ident],
		udistub: state.fagsystem.udistub[ident],
		pensjonforvalter: state.fagsystem.pensjonforvalter[ident],
		brregstub: state.fagsystem.brregstub[ident],
		skjermingsregister: state.fagsystem.skjermingsregister[ident],
	}
}
