import useSWR from 'swr'
import { fetcher } from '@/api'

type KodeverkListe = {
	koder: Array<KodeverkType>
}

type KodeverkType = {
	label: string
	value: string
}

const getKodeverkUrl = (kodeverkNavn) => `/dolly-backend/api/v1/kodeverk/${kodeverkNavn}`

export const useKodeverk = (kodeverkNavn) => {
	const { data, isLoading, error } = useSWR<KodeverkListe, Error>(
		getKodeverkUrl(kodeverkNavn),
		fetcher
	)

	return {
		kodeverk: data,
		loading: isLoading,
		error: error,
	}
}
