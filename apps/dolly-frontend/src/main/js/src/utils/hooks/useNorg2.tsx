import { fetcher } from '@/api'
import { Option } from '@/service/SelectOptionsOppslag'
import useSWRImmutable from 'swr/immutable'

const norg2Url = `/testnav-dolly-proxy/norg2/norg2/api/v1/enhet?enhetStatusListe=AKTIV&oppgavebehandlerFilter=KUN_OPPGAVEBEHANDLERE`

type EnhetType = {
	enhetNr: string
	navn: string
}

export const useNavEnheter = () => {
	const { data, isLoading, error } = useSWRImmutable<string[], Error>(norg2Url, fetcher)
	const navEnheterOptions: Option[] = []
	data?.forEach((enhet: EnhetType | any) => {
		navEnheterOptions.push({
			value: enhet?.enhetNr,
			label: `${enhet?.navn} (${enhet?.enhetNr})`,
		})
	})

	return {
		navEnheter: navEnheterOptions,
		loading: isLoading,
		error: error,
	}
}
