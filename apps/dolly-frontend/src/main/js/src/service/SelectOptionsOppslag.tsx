import { useAsync } from 'react-use'
import { BrregstubApi, DollyApi, KrrApi, Norg2Api, PdlforvalterApi } from '~/service/Api'
import Api from '~/api'
import _isNil from 'lodash/isNil'
import { Person, PersonData } from '~/components/fagsystem/pdlf/PdlTypes'

const uri = `/dolly-backend/api/v1`

export type Option = {
	value: any
	label: string
	tema?: string
}

type Data = {
	label: string
	id: string
	navn: string
	value: {
		data: any
	}
}

export const SelectOptionsOppslag = {
	hentGruppeIdentOptions: async (gruppeId: string) => {
		const gruppe = await DollyApi.getGruppeById(gruppeId).then((response: any) => {
			return response.data?.identer?.map((person: PersonData) => {
				if (person.master === 'PDL' || person.master === 'PDLF') return person.ident
			})
		})
		const options = await PdlforvalterApi.getPersoner(gruppe).then((response: any) => {
			const personListe: Array<{ value: string; label: string }> = []
			response.data.forEach((id: Person) => {
				personListe.push({
					value: id.person.ident,
					label: `${id.person.ident} - ${id.person.navn[0].fornavn} ${id.person.navn[0].etternavn}`,
				})
			})
			return personListe
		})
		return options ? options : Promise.resolve()
	},

	hentHelsepersonell: () => Api.fetchJson(`${uri}/helsepersonell`, { method: 'GET' }),

	hentKrrLeverandoerer: () => {
		return useAsync(async () => KrrApi.getSdpLeverandoerListe(), [KrrApi.getSdpLeverandoerListe])
	},

	hentNavEnheter: () => {
		return useAsync(async () => Norg2Api.getNavEnheter(), [Norg2Api.getNavEnheter])
	},

	hentPersonnavn: () => {
		return useAsync(async () => DollyApi.getPersonnavn(), [DollyApi.getPersonnavn])
	},

	hentGruppe: () => {
		return useAsync(
			async () => DollyApi.getFasteDatasettGruppe('DOLLY'),
			[DollyApi.getFasteDatasettGruppe]
		)
	},

	hentInntektsmeldingOptions: (enumtype: string) =>
		Api.fetchJson(`${uri}/inntektsmelding/${enumtype}`, { method: 'GET' }),

	hentArbeidsforholdstyperInntektstub: () => {
		return useAsync(
			async () => DollyApi.getKodeverkByNavn('Arbeidsforholdstyper'),
			[DollyApi.getKodeverkByNavn]
		)
	},

	hentFullmaktOmraader: () => {
		return useAsync(async () => DollyApi.getKodeverkByNavn('Tema'), [DollyApi.getKodeverkByNavn])
	},

	hentRollerFraBrregstub: () => {
		return useAsync(async () => BrregstubApi.getRoller(), [BrregstubApi.getRoller])
	},

	hentUnderstatusFraBrregstub: () => {
		return useAsync(async () => BrregstubApi.getUnderstatus(), [BrregstubApi.getUnderstatus])
	},

	hentVirksomheterFraOrgforvalter: () => {
		return Api.fetchJson(`/testnav-organisasjon-forvalter/api/v2/organisasjoner/virksomheter`, {
			method: 'GET',
		})
	},

	formatOptions: (type: string, data: Data) => {
		if (type === 'personnavn') {
			const persondata: any[] = data.value && data.value.data ? data.value.data : []
			const options: Option[] = []
			persondata.length > 0 &&
				persondata.forEach((personInfo) => {
					if (!_isNil(personInfo.fornavn)) {
						const mellomnavn = !_isNil(personInfo.mellomnavn) ? ' ' + personInfo.mellomnavn : ''
						const navn = personInfo.fornavn + mellomnavn + ' ' + personInfo.etternavn
						options.push({ value: personInfo.fornavn.toUpperCase(), label: navn.toUpperCase() })
					}
				})
			return options
		} else if (type === 'navnOgFnr') {
			const persondata = data.value && data.value.data ? data.value.data.liste : []
			const options: Option[] = []
			persondata.length > 0 &&
				persondata.forEach(
					(personInfo: { fornavn: string; mellomnavn: string; etternavn: string; fnr: string }) => {
						if (!_isNil(personInfo.fornavn)) {
							const mellomnavn = !_isNil(personInfo.mellomnavn) ? ' ' + personInfo.mellomnavn : ''
							const navnOgFnr =
								(personInfo.fornavn + mellomnavn + ' ' + personInfo.etternavn).toUpperCase() +
								': ' +
								personInfo.fnr
							options.push({ value: personInfo.fnr, label: navnOgFnr })
						}
					}
				)
			return options
		} else if (type === 'arbeidsforholdstyper') {
			const options = data.value ? data.value.data.koder : []
			options.length > 0 &&
				options.forEach((option: Option) => {
					if (option.value === 'frilanserOppdragstakerHonorarPersonerMm') {
						option.label = 'Frilansere/oppdragstakere, honorar, m.m.'
					}
					if (option.value === 'pensjonOgAndreTyperYtelserUtenAnsettelsesforhold') {
						option.label = 'Pensjoner og andre typer ytelser uten ansettelsesforhold'
					}
				})
			return options
		} else if (type === 'understatuser') {
			const statuser = data.value ? Object.entries(data.value.data) : []
			const options: Option[] = []
			statuser.forEach((status) => {
				options.push({ value: parseInt(status[0]), label: `${status[0]}: ${status[1]}` })
			})
			return options
		} else if (type === 'roller') {
			const roller = data.value ? Object.entries(data.value.data) : []
			const options: Option[] = []
			roller.forEach((rolle: [string, string]) => {
				options.push({ value: rolle[0], label: rolle[1] })
			})
			return options
		} else if (type === 'navEnheter') {
			const enheter = data.value ? Object.entries(data.value.data) : []
			const options: Option[] = []
			enheter.forEach((enhet: [string, any]) => {
				options.push({
					value: enhet?.[1]?.enhetNr,
					label: `${enhet?.[1]?.navn} (${enhet?.[1]?.enhetNr})`,
				})
			})
			return options
		} else if (type === 'sdpLeverandoer') {
			const leverandoerer = data.value ? Object.entries(data.value.data) : []
			const options: Option[] = []
			leverandoerer.forEach((leverandoer: [string, any]) => {
				data = leverandoer[1]
				options.push({ value: parseInt(data.id), label: data.navn })
			})
			return options
		} else if (type === 'fullmaktOmraader') {
			const omraader = data.value ? Object.entries(data.value.data.koder) : []
			const ugyldigeKoder = ['BII', 'KLA', 'KNA', 'KOM', 'LGA', 'MOT', 'OVR']
			const options: Option[] = []
			options.push({ value: '*', label: '* (Alle)' })
			omraader
				.filter((omr: [string, Option]) => {
					return !ugyldigeKoder.includes(omr[1].value)
				})
				.forEach((omraade: [string, Data]) => {
					data = omraade[1]
					options.push({ value: data.value, label: data.label })
				})
			return options
		}
	},
}
