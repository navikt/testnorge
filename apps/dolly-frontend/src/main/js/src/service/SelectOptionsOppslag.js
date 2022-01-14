import { useAsync } from 'react-use'
import { BrregstubApi, DollyApi, KrrApi, Norg2Api } from '~/service/Api'
import Api from '~/api'
import _isNil from 'lodash/isNil'

const uri = `/dolly-backend/api/v1`

export const SelectOptionsOppslag = {
	hentOrgnr: () => {
		return useAsync(
			async () => Api.fetchJson(`${uri}/orgnummer`, { method: 'GET' }),
			[Api.fetchJson]
		)
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

	hentInntektsmeldingOptions: (enumtype) =>
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

	hentValutaer: () => {
		return useAsync(
			async () => DollyApi.getKodeverkByNavn('Valutaer'),
			[DollyApi.getKodeverkByNavn]
		)
	},

	hentRollerFraBrregstub: () => {
		return useAsync(async () => BrregstubApi.getRoller(), [BrregstubApi.getRoller])
	},

	hentUnderstatusFraBrregstub: () => {
		return useAsync(async () => BrregstubApi.getUnderstatus(), [BrregstubApi.getUnderstatus])
	},

	hentTagsFraDolly: () => {
		return useAsync(async () => DollyApi.getTags(), [DollyApi.getTags])
	},

	hentVirksomheterFraOrgforvalter: () => {
		return Api.fetchJson(`/testnav-organisasjon-forvalter/api/v2/organisasjoner/virksomheter`, {
			method: 'GET',
		})
	},

	formatOptions: (type, data) => {
		if (type === 'personnavn') {
			const persondata = data.value && data.value.data ? data.value.data : []
			const options = []
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
			const options = []
			persondata.length > 0 &&
				persondata.forEach((personInfo) => {
					if (!_isNil(personInfo.fornavn)) {
						const mellomnavn = !_isNil(personInfo.mellomnavn) ? ' ' + personInfo.mellomnavn : ''
						const navnOgFnr =
							(personInfo.fornavn + mellomnavn + ' ' + personInfo.etternavn).toUpperCase() +
							': ' +
							personInfo.fnr
						options.push({ value: personInfo.fnr, label: navnOgFnr })
					}
				})
			return options
		} else if (type === 'arbeidsforholdstyper') {
			const options = data.value ? data.value.data.koder : []
			options.length > 0 &&
				options.forEach((option) => {
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
			const options = []
			statuser.forEach((status) => {
				options.push({ value: parseInt(status[0]), label: `${status[0]}: ${status[1]}` })
			})
			return options
		} else if (type === 'roller') {
			const roller = data.value ? Object.entries(data.value.data) : []
			const options = []
			roller.forEach((rolle) => {
				options.push({ value: rolle[0], label: rolle[1] })
			})
			return options
		} else if (type === 'navEnheter') {
			const enheter = data.value ? Object.entries(data.value.data) : []
			const options = []
			enheter.forEach((enhet) => {
				options.push({
					value: enhet?.[1]?.enhetNr,
					label: `${enhet?.[1]?.navn} (${enhet?.[1]?.enhetNr})`,
				})
			})
			return options
		} else if (type === 'sdpLeverandoer') {
			const leverandoerer = data.value ? Object.entries(data.value.data) : []
			const options = []
			leverandoerer.forEach((leverandoer) => {
				data = leverandoer[1]
				options.push({ value: parseInt(data.id), label: data.navn })
			})
			return options
		} else if (type === 'fullmaktOmraader') {
			const omraader = data.value ? Object.entries(data.value.data.koder) : []
			const ugyldigeKoder = ['BII', 'KLA', 'KNA', 'KOM', 'LGA', 'MOT', 'OVR']
			const options = []
			options.push({ value: '*', label: '* (Alle)' })
			omraader
				.filter((omr) => {
					return !ugyldigeKoder.includes(omr[1].value)
				})
				.forEach((omraade) => {
					data = omraade[1]
					options.push({ value: data.value, label: data.label })
				})
			return options
		}
	},
}
