import { fetcher } from '@/api'
import { Option } from '@/service/SelectOptionsOppslag'
import useSWRImmutable from 'swr/immutable'

const norg2Url = `/testnav-dolly-proxy/norg2/norg2/api/v1/enhet?enhetStatusListe=AKTIV&oppgavebehandlerFilter=KUN_OPPGAVEBEHANDLERE`
const norg2AlleEnheterUrl = `/testnav-dolly-proxy/norg2/norg2/api/v1/enhet?enhetStatusListe=AKTIV`

type EnhetType = {
	enhetId: number
	navn: string
	enhetNr: string
	antallRessurser: number
	status: string
	orgNivaa: string
	type: string
	organisasjonsnummer: string
	underEtableringDato: string
	aktiveringsdato: string
	underAvviklingDato: string
	nedleggelsesdato: string
	oppgavebehandler: boolean
	versjon: number
	sosialeTjenester: string
	kanalstrategi: string
	orgNrTilKommunaltNavKontor: string
}

export const useNavEnheter = () => {
	const { data, isLoading, error } = useSWRImmutable<EnhetType[], Error>(norg2Url, fetcher)
	const navEnheter: Option[] =
		data?.map((enhet) => ({
			value: enhet?.enhetNr,
			label: `${enhet?.navn} (${enhet?.enhetNr})`,
		})) ?? []

	return {
		navEnheter,
		loading: isLoading,
		error,
	}
}

export const useAlleNavEnheter = () => {
	const { data, isLoading, error } = useSWRImmutable<EnhetType[], Error>(
		norg2AlleEnheterUrl,
		fetcher,
	)
	const alleNavEnheter: Option[] =
		data?.map((enhet) => ({
			value: enhet?.enhetNr,
			label: `${enhet?.navn} - ${enhet?.enhetNr}`,
		})) ?? []

	return {
		alleNavEnheter,
		loading: isLoading,
		error,
	}
}
