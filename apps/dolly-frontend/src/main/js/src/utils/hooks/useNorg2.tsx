import useSWR from 'swr'
import { fetcher } from '@/api'
import { Option } from '@/service/SelectOptionsOppslag'

const norg2Url = `/testnav-norg2-proxy/norg2/api/v1/enhet?enhetStatusListe=AKTIV&oppgavebehandlerFilter=KUN_OPPGAVEBEHANDLERE`

type EnhetType = {
	enhetNr: string
	navn: string
}

export const useNavEnheter = () => {
	const { data, error } = useSWR<string[], Error>(norg2Url, fetcher)
	const navEnheterOptions: Option[] = []
	data?.forEach((enhet: EnhetType | any) => {
		navEnheterOptions.push({
			value: enhet?.enhetNr,
			label: `${enhet?.navn} (${enhet?.enhetNr})`,
		})
	})

	return {
		navEnheter: navEnheterOptions,
		loading: !error && !data,
		error: error,
	}
}
