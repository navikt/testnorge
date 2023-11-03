import useSWR from 'swr'
import { fetcher } from '@/api'

type KodeverkListe = {
	koder: Array<KodeverkType>
}

type KodeverkType = {
	label: string
	value: string
}

const getKodeverkUrl = (kodeverkNavn) =>
	kodeverkNavn.includes('Valuta') || kodeverkNavn.includes('Landkode')
		? getLandValutaKodeverk(kodeverkNavn)
		: `/dolly-backend/api/v1/kodeverk/${kodeverkNavn}`

const getLandValutaKodeverk = (kodeverk) =>
	`https://sokos-kontoregister-person.intern.dev.nav.no/api/system/v1/hent-${
		kodeverk.includes('Valuta') ? 'valutakoder' : 'landkoder'
	}`

export const useKodeverk = (kodeverkNavn) => {
	const { data, isLoading, error } = useSWR<KodeverkListe, Error>(
		[getKodeverkUrl(kodeverkNavn), { accept: 'application/json', 'allow-cross-origin': '*' }],
		([url, headers]) => fetcher(url, headers),
	)

	return {
		kodeverk: data,
		loading: isLoading,
		error: error,
	}
}
