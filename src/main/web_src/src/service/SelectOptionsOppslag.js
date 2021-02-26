import { useAsync } from 'react-use'
import { BrregstubApi, DollyApi, KrrApi } from '~/service/Api'
import config from '~/config'
import Api from '~/api'
import _isNil from 'lodash/isNil'

const uri = `${config.services.dollyBackend}`

export const SelectOptionsOppslag = {
	hentOrgnr: () => Api.fetchJson(`${uri}/orgnummer`, { method: 'GET' }),

	hentHelsepersonell: () => Api.fetchJson(`${uri}/helsepersonell`, { method: 'GET' }),

	hentKrrLeverandoerer: () => {
		const sdpLeverandoerer = useAsync(async () => {
			const response = await KrrApi.getSdpLeverandoerListe()
			return response
		}, [KrrApi.getSdpLeverandoerListe])
		return sdpLeverandoerer
	},

	hentPersonnavn: () => {
		const navnInfo = useAsync(async () => {
			const response = await DollyApi.getPersonnavn()
			return response
		}, [DollyApi.getPersonnavn])
		return navnInfo
	},

	hentGruppe: () => {
		const datasettInfo = useAsync(async () => {
			const response = await DollyApi.getFasteDatasettGruppe('DOLLY')
			return response
		}, [DollyApi.getFasteDatasettGruppe])
		return datasettInfo
	},

	hentInntektsmeldingOptions: enumtype =>
		Api.fetchJson(`${uri}/inntektsmelding/${enumtype}`, { method: 'GET' }),

	hentArbeidsforholdstyperInntektstub: () => {
		const arbeidsforholdstyper = useAsync(async () => {
			const response = await DollyApi.getKodeverkByNavn('Arbeidsforholdstyper')
			return response
		}, [DollyApi.getKodeverkByNavn])
		return arbeidsforholdstyper
	},

	hentFullmaktOmraader: () => {
		const omraader = useAsync(async () => {
			const response = await DollyApi.getKodeverkByNavn('Tema')
			return response
		}, [DollyApi.getKodeverkByNavn])
		return omraader
	},

	hentRollerFraBrregstub: () => {
		const rollerInfo = useAsync(async () => {
			const response = await BrregstubApi.getRoller()
			return response
		}, [BrregstubApi.getRoller])
		return rollerInfo
	},

	hentUnderstatusFraBrregstub: () => {
		const understatusInfo = useAsync(async () => {
			const response = await BrregstubApi.getUnderstatus()
			return response
		}, [BrregstubApi.getUnderstatus])
		return understatusInfo
	}
}

SelectOptionsOppslag.formatOptions = (type, data) => {
	if (type === 'personnavn') {
		const persondata = data.value && data.value.data ? data.value.data : []
		const options = []
		persondata.length > 0 &&
			persondata.forEach(personInfo => {
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
			persondata.forEach(personInfo => {
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
			options.forEach(option => {
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
		statuser.forEach(status => {
			options.push({ value: parseInt(status[0]), label: `${status[0]}: ${status[1]}` })
		})
		return options
	} else if (type === 'roller') {
		const roller = data.value ? Object.entries(data.value.data) : []
		const options = []
		roller.forEach(rolle => {
			options.push({ value: rolle[0], label: rolle[1] })
		})
		return options
	} else if (type === 'sdpLeverandoer') {
		const leverandoerer = data.value ? Object.entries(data.value.data) : []
		const options = []
		leverandoerer.forEach(leverandoer => {
			data = leverandoer[1]
			options.push({ value: parseInt(data.id), label: data.navn })
		})
		return options
	} else if (type === 'fullmaktOmraader') {
		const omraader = data.value ? Object.entries(data.value.data.koder) : []
		const options = []
		options.push({ value: '*', label: '* (Alle)' })
		omraader.forEach(omraade => {
			data = omraade[1]
			options.push({ value: data.value, label: data.label })
		})
		return options
	}
}
