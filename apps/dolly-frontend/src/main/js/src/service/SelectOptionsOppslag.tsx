import { useAsync } from 'react-use'
import { DollyApi, KrrApi } from '@/service/Api'
import Api from '@/api'

const uri = `/dolly-backend/api/v1`

export type Option = {
	value: any
	label: string
	tema?: string
	landkode?: string
	alder?: number
	sivilstand?: string
	vergemaal?: boolean
	doedsfall?: boolean
	foreldre?: Array<string>
	foreldreansvar?: Array<string>
	relasjoner?: Array<string>
}

export const SelectOptionsOppslag = {
	hentKrrLeverandoerer: () => {
		return useAsync(async () => KrrApi.getSdpLeverandoerListe(), [KrrApi.getSdpLeverandoerListe])
	},

	hentInntektsmeldingOptions: (enumtype: string) =>
		Api.fetchJson(`${uri}/inntektsmelding/${enumtype}`, { method: 'GET' }),

	hentArbeidsforholdstyperInntektstub: () => {
		return useAsync(
			async () => DollyApi.getKodeverkByNavn('Arbeidsforholdstyper'),
			[DollyApi.getKodeverkByNavn],
		)
	},
}
