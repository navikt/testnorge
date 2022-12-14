import { createActions } from 'redux-actions'
import {
	ArenaApi,
	BankkontoApi,
	BrregstubApi,
	DollyApi,
	InntektstubApi,
	KrrApi,
	PdlforvalterApi,
	SigrunApi,
	TpsfApi,
	TpsMessagingApi,
} from '@/service/Api'
import { onSuccess } from '@/ducks/utils/requestActions'
import { successMiljoSelector } from '@/ducks/bestillingStatus'
import { handleActions } from '@/ducks/utils/immerHandleActions'
import Formatters from '@/utils/DataFormatter'
import * as _ from 'lodash-es'

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
		getKontoregister: [
			BankkontoApi.hentKonto,
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
		slettPersonOgRelatertePersoner: [
			DollyApi.slettPersonOgRelatertePersoner,
			(ident, relatertPersonIdenter) => ({
				ident,
				relatertPersonIdenter,
			}),
		],
	},
	{
		prefix: 'fagsystem', // String used to prefix each type
	}
)

const initialState = {
	tpsf: {},
	tpsMessaging: {},
	sigrunstub: {},
	inntektstub: {},
	krrstub: {},
	arenaforvalteren: {},
	pdl: {},
	pdlforvalter: {},
	instdata: {},
	udistub: {},
	pensjonforvalter: {},
	tpforvalter: {},
	brregstub: {},
	skjermingsregister: {},
	kontoregister: {},
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
		[onSuccess(actions.getUdi)](state, action) {
			state.udistub[action.meta.ident] = action.payload?.data?.person
		},
		[onSuccess(actions.getBrreg)](state, action) {
			state.brregstub[action.meta.ident] = action.payload?.data
		},
		[onSuccess(actions.getKontoregister)](state, action) {
			state.kontoregister[action.meta.ident] = action.payload?.data
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
		[onSuccess(actions.slettPerson)](state, action) {
			deleteIdentState(state, action.meta.ident)
		},
		[onSuccess(actions.slettPersonOgRelatertePersoner)](state, action) {
			deleteIdentState(state, action.meta.ident)
			deleteIdentState(state, action.meta.partnerident)
		},
	},
	initialState
)

const deleteIdentState = (state, ident) => {
	delete state.tpsf[ident]
	delete state.sigrunstub[ident]
	delete state.inntektstub[ident]
	delete state.krrstub[ident]
	delete state.arenaforvalteren[ident]
	delete state.pdl[ident]
	delete state.pdlforvalter[ident]
	delete state.udistub[ident]
	delete state.brregstub[ident]
	delete state.kontoregister[ident]
	delete state.tpsMessaging[ident]
}

// Thunk
export const fetchTpsfPersoner = (identer) => (dispatch) => {
	const tpsIdenter = identer
		.map((person) => {
			if (!person.master || (person.master !== 'PDLF' && person.master !== 'PDL')) {
				return person.ident
			}
		})
		.filter((person) => !_.isNil(person))

	if (tpsIdenter && tpsIdenter.length >= 1) dispatch(actions.getTpsf(tpsIdenter))
}

export const fetchPdlPersoner = (identer) => (dispatch) => {
	const pdlIdenter = identer.map((person) => {
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
export const fetchDataFraFagsystemer = (person, bestillingerById) => (dispatch) => {
	const personId = person.ident

	// Bestillingen(e) fra bestillingStatuser
	const bestillinger = person.bestillingId.map((id) => bestillingerById?.[id]).filter((b) => b)

	// Samlet liste over alle statuser
	const statusArray = bestillinger.reduce((acc, curr) => acc.concat(curr.status), [])

	// Liste over systemer som har data
	const success = successMiljoSelector(statusArray, personId)

	// Samle alt fra PDL under en ID
	if (Object.keys(success)?.some((a) => a.substring(0, 3) === 'PDL')) {
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
			case 'BRREGSTUB':
				return dispatch(actions.getBrreg(personId))
			case 'SKJERMINGSREGISTER':
				return dispatch(actions.getSkjermingsregister(personId))
			case 'KONTOREGISTER':
				return dispatch(actions.getKontoregister(personId))
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

	if (!bestillingStatus) {
		return totalStatus
	}
	bestillingStatus?.status.forEach((fagsystem) => {
		_.get(fagsystem, 'statuser', []).forEach((status) => {
			if (status.detaljert) {
				_.get(status, 'detaljert', []).forEach((miljoe) => {
					_.get(miljoe, 'identer', []).forEach((miljoeIdent) => {
						if (miljoeIdent === ident && status.melding !== 'OK') {
							totalStatus = 'Avvik'
						}
					})
				})
			} else {
				_.get(status, 'identer', []).forEach((miljoeIdent) => {
					if (miljoeIdent === ident && status.melding !== 'OK') {
						totalStatus = 'Avvik'
					}
				})
			}
		})
	})
	return totalStatus
}

export const selectPersonListe = (identer, bestillingStatuser, fagsystem) => {
	if (
		!identer ||
		(_.isEmpty(fagsystem.tpsf) && _.isEmpty(fagsystem.pdlforvalter) && _.isEmpty(fagsystem.pdl))
	) {
		return null
	}

	const identListe = Object.values(identer).filter(
		(gruppeIdent) =>
			Object.keys(fagsystem.tpsf).includes(gruppeIdent.ident) ||
			Object.keys(fagsystem.pdlforvalter).includes(gruppeIdent.ident) ||
			Object.keys(fagsystem.pdl).includes(gruppeIdent.ident)
	)

	return identListe.map((ident) => {
		if (ident.master === 'TPSF') {
			const tpsfIdent = fagsystem.tpsf[ident.ident]
			const pdlfIdent = fagsystem.pdlforvalter?.[ident.ident]?.person
			return getTpsfIdentInfo(ident, bestillingStatuser, tpsfIdent, pdlfIdent)
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

const getTpsfIdentInfo = (ident, bestillingStatuser, tpsfIdent, pdlfIdent) => {
	if (!tpsfIdent) {
		return getDefaultInfo(ident, bestillingStatuser, 'TPS')
	}
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
		alder: Formatters.formatAlder(
			tpsfIdent.alder,
			tpsfIdent.doedsdato ? tpsfIdent.doedsdato : getPdlDoedsdato(pdlfIdent)
		),
		status: hentPersonStatus(ident?.ident, bestillingStatuser?.[ident?.bestillingId?.[0]]),
	}
}

const getPdlfIdentInfo = (ident, bestillingStatuser, pdlIdent) => {
	if (!pdlIdent) {
		return getDefaultInfo(ident, bestillingStatuser, 'PDL')
	}
	const pdlFornavn = pdlIdent?.navn?.[0]?.fornavn || ''
	const pdlMellomnavn = pdlIdent?.navn?.[0]?.mellomnavn
		? `${pdlIdent?.navn?.[0]?.mellomnavn.charAt(0)}.`
		: ''
	const pdlEtternavn = pdlIdent?.navn?.[0]?.etternavn || ''

	const pdlAlder = (foedselsdato) => {
		if (!foedselsdato) {
			return null
		}
		const diff = new Date(Date.now() - new Date(foedselsdato).getTime())
		return Math.abs(diff.getUTCFullYear() - 1970)
	}
	return {
		ident,
		identNr: pdlIdent.ident,
		bestillingId: ident?.bestillingId,
		identtype: 'FNR',
		kilde: 'PDL',
		navn: `${pdlFornavn} ${pdlMellomnavn} ${pdlEtternavn}`,
		kjonn: pdlIdent.kjoenn?.[0]?.kjoenn,
		alder: Formatters.formatAlder(
			pdlAlder(pdlIdent?.foedsel?.[0]?.foedselsdato),
			getPdlDoedsdato(pdlIdent)
		),
		status: hentPersonStatus(ident?.ident, bestillingStatuser?.[ident?.bestillingId?.[0]]),
	}
}

const getPdlDoedsdato = (pdlIdent) => {
	return pdlIdent?.doedsfall?.filter((doed) => !doed?.metadata?.historisk)?.[0]?.doedsdato
}

const getPdlIdentInfo = (ident, bestillingStatuser, pdlData) => {
	if (!pdlData || (!pdlData.person && !pdlData.hentPerson)) {
		return getDefaultInfo(ident, bestillingStatuser, 'TEST-NORGE')
	}
	const person = pdlData.person || pdlData.hentPerson

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
		status: hentPersonStatus(ident?.ident, bestillingStatuser?.[ident?.bestillingId?.[0]]),
	}
}

const getDefaultInfo = (ident, bestillingStatuser, kilde) => {
	return {
		ident,
		identNr: ident?.ident,
		bestillingId: ident?.bestillingId,
		importFra: '',
		identtype: '',
		kilde: kilde,
		navn: '',
		kjonn: '',
		alder: '',
		status:
			ident?.bestillingId && bestillingStatuser
				? hentPersonStatus(ident?.ident, bestillingStatuser?.[ident?.bestillingId?.[0]])
				: '',
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
		pdl: state.fagsystem.pdl[ident],
		pdlforvalter: state.fagsystem.pdlforvalter[ident],
		udistub: state.fagsystem.udistub[ident],
		brregstub: state.fagsystem.brregstub[ident],
		skjermingsregister: state.fagsystem.skjermingsregister[ident],
		kontoregister: state.fagsystem.kontoregister[ident],
	}
}
