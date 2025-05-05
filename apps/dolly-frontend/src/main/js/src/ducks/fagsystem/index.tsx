import { createActions } from 'redux-actions'
import dayjs from 'dayjs'
import {
	BankkontoApi,
	BrregstubApi,
	DollyApi,
	InntektstubApi,
	KrrApi,
	PdlforvalterApi,
	SkjermingApi,
	TpsMessagingApi,
} from '@/service/Api'
import { onSuccess } from '@/ducks/utils/requestActions'
import { successMiljoSelector } from '@/ducks/bestillingStatus'
import { handleActions } from '@/ducks/utils/immerHandleActions'
import { formatAlder, formatKjonn } from '@/utils/DataFormatter'
import * as _ from 'lodash-es'
import { getTypePerson } from '@/components/bestilling/sammendrag/kriterier/BestillingKriterieMapper'

export const actions = createActions(
	{
		getTpsMessaging: [
			TpsMessagingApi.getTpsPersonInfoAllEnvs,
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
			SkjermingApi.getSkjerming,
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
	},
	{
		prefix: 'fagsystem', // String used to prefix each type
	},
)

const initialState = {
	tpsf: {},
	tpsMessaging: {},
	inntektstub: {},
	krrstub: {},
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
		[onSuccess(actions.getTpsMessaging)](state, action) {
			state.tpsMessaging[action.meta.ident] = action?.payload.data?.[0]?.person
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
				{},
			)
			const geografiskTilknytningBolk =
				action.payload.data?.data?.hentGeografiskTilknytningBolk?.reduce(
					(map, person) => ({
						...map,
						[person.ident]: person.geografiskTilknytning,
					}),
					{},
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
	},
	initialState,
)

const deleteIdentState = (state, ident) => {
	delete state.tpsf[ident]
	delete state.inntektstub[ident]
	delete state.krrstub[ident]
	delete state.pdl[ident]
	delete state.pdlforvalter[ident]
	delete state.udistub[ident]
	delete state.brregstub[ident]
	delete state.kontoregister[ident]
	delete state.tpsMessaging[ident]
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
			case 'INNTK':
				return dispatch(actions.getInntektstub(personId))
			case 'TPS_MESSAGING':
				return dispatch(actions.getTpsMessaging(personId))
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
		Object.values(item).some((v) => (v || '').toString().toLowerCase().includes(query)),
	)
}

const hentPersonStatus = (ident, bestillingStatus) => {
	let totalStatus = 'Ferdig'

	if (!bestillingStatus) {
		return totalStatus
	}
	bestillingStatus?.status?.forEach((fagsystem) => {
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
	if (!identer || (_.isEmpty(fagsystem.pdlforvalter) && _.isEmpty(fagsystem.pdl))) {
		return null
	}

	const identListe = Object.values(identer).filter(
		(gruppeIdent) =>
			Object.keys(fagsystem.pdlforvalter).includes(gruppeIdent.ident) ||
			Object.keys(fagsystem.pdl).includes(gruppeIdent.ident),
	)

	return identListe.map((ident) => {
		if (ident.master === 'PDLF') {
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

const getPdlfIdentInfo = (ident, bestillingStatuser, pdlIdent) => {
	if (!pdlIdent) {
		return getDefaultInfo(ident, bestillingStatuser, 'PDL')
	}
	const pdlFornavn = pdlIdent?.navn?.[0]?.fornavn || ''

	const formaterPdlMellomnavn = (mellomnavn: string) => {
		if (!mellomnavn) {
			return ''
		}
		const harFlereMellomnavn = mellomnavn?.includes(' ')
		if (harFlereMellomnavn) {
			return mellomnavn
		}
		return `${mellomnavn.charAt(0)}.`
	}

	const pdlMellomnavn = formaterPdlMellomnavn(pdlIdent?.navn?.[0]?.mellomnavn)
	const pdlEtternavn = pdlIdent?.navn?.[0]?.etternavn || ''

	const alder = getAlder(
		pdlIdent?.foedsel?.[0]?.foedselsdato || pdlIdent?.foedselsdato?.[0].foedselsdato,
		getPdlDoedsdato(pdlIdent),
		pdlIdent.ident,
	)
	return {
		ident,
		identNr: pdlIdent.ident,
		bestillingId: ident?.bestillingId,
		identtype: 'FNR',
		kilde: 'Dolly',
		navn: `${pdlFornavn} ${pdlMellomnavn} ${pdlEtternavn}`,
		kjonn: pdlIdent.kjoenn?.[0]?.kjoenn,
		alder: formatAlder(alder, getPdlDoedsdato(pdlIdent)),
		status: hentPersonStatus(ident?.ident, bestillingStatuser?.[ident?.bestillingId?.[0]]),
	}
}

const getPdlDoedsdato = (pdlIdent) => {
	return pdlIdent?.doedsfall?.filter((doed) => !doed?.metadata?.historisk)?.[0]?.doedsdato
}

const getPdlIdentInfo = (ident, bestillingStatuser, pdlData) => {
	if (!pdlData || (!pdlData.person && !pdlData.hentPerson)) {
		return getDefaultInfo(ident, bestillingStatuser, 'Test-Norge')
	}
	const person = pdlData.person || pdlData.hentPerson

	const getNavn = (navnListe: Array<any>) => {
		if (navnListe?.length === 1) {
			return navnListe[0]
		} else if (navnListe?.length > 1) {
			return [...navnListe]
				?.filter((navn: any) => navn.metadata.historisk === false)
				?.sort?.((a, b) => {
					const sistEndretA = a.metadata.endringer?.reduce((x, y) => {
						const dato = new Date(y.registrert)
						return dato > x ? dato : x
					}, new Date(0))
					const sistEndretB = b.metadata.endringer?.reduce((x, y) => {
						const dato = new Date(y.registrert)
						return dato > x ? dato : x
					}, new Date(0))
					return sistEndretB - sistEndretA
				})?.[0]
		} else {
			return null
		}
	}

	const navn = getNavn(person.navn)
	const mellomnavn = navn?.mellomnavn ? `${navn.mellomnavn.charAt(0)}.` : ''
	const kjonn = person.kjoenn[0] ? getKjoenn(person.kjoenn[0].kjoenn) : 'U'
	const alder = getAlder(
		person.foedselsdato?.[0]?.foedselsdato || person?.foedsel?.[0]?.foedselsdato,
		person.doedsfall?.[0]?.doedsdato,
		ident.ident,
	)

	return {
		ident,
		identNr: ident?.ident,
		bestillingId: ident.bestillingId,
		kilde: 'Test-Norge',
		importFra: 'Test-Norge',
		identtype: person?.folkeregisteridentifikator?.[0]?.type,
		navn: `${navn.fornavn} ${mellomnavn} ${navn.etternavn}`,
		kjonn: formatKjonn(kjonn, alder),
		alder: formatAlder(alder, person.doedsfall?.[0]?.doedsdato),
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

export const getAlder = (datoFoedt, doedsdato = null, identNr = null as unknown as number) => {
	var foedselsdato = datoFoedt && dayjs(datoFoedt)

	if ((!foedselsdato || !foedselsdato.isValid()) && identNr) {
		const identNrString = identNr.toString()
		const typePerson = getTypePerson(identNrString)
		const dato = identNrString.substring(0, 6)
		var monthDigit = null

		switch (typePerson) {
			case 'Test-Norge':
				monthDigit = _.toNumber(dato.charAt(2)) - 8
				break
			case 'NAV-syntetisk':
				monthDigit = _.toNumber(dato.charAt(2)) - 4
				break
			case 'Standard':
				monthDigit = _.toNumber(dato.charAt(2))
				break
		}
		const day = dato.substring(0, 2)
		const month = monthDigit + dato.substring(3, 4)
		const year = dato.substring(4, 6)
		foedselsdato = dayjs(year + '-' + month + '-' + day)
	}
	const currentDayjsDate = dayjs()
	let alder = currentDayjsDate.diff(foedselsdato, 'years')

	if (doedsdato && doedsdato !== '') {
		const ddato = dayjs(doedsdato)
		alder = ddato.diff(foedselsdato, 'years')
	}

	return alder
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
		inntektstub: state.fagsystem.inntektstub[ident],
		krrstub: state.fagsystem.krrstub[ident],
		pdl: state.fagsystem.pdl[ident],
		pdlforvalter: state.fagsystem.pdlforvalter[ident],
		udistub: state.fagsystem.udistub[ident],
		brregstub: state.fagsystem.brregstub[ident],
		skjermingsregister: state.fagsystem.skjermingsregister[ident],
		kontoregister: state.fagsystem.kontoregister[ident],
	}
}
