import useSWR from 'swr'
import { fetcher } from '~/api'

const getMalerUrl = '/dolly-backend/api/v1/bestilling/malbestilling'

export type Mal = {
	malNavn: string
	id: number
}

type MalResponse = {
	malbestillinger: [string, Mal[]]
}

export const useDollyMaler = () => {
	const { data, error } = useSWR<MalResponse, Error>(getMalerUrl, fetcher, {
		fallbackData: { malbestillinger: ['TEMP', []] },
	})

	return {
		maler: data?.malbestillinger,
		loading: !error && !data,
		error: error,
	}
}

export const useDollyMalerBrukerOgMalnavn = (brukerId: string, malNavn: string) => {
	const { data, error } = useSWR<Mal[], Error>(
		`${getMalerUrl}/bruker?brukerId=${brukerId}${malNavn ? `&malNavn=${malNavn}` : ''}`,
		fetcher,
		{ fallbackData: [] }
	)

	return {
		maler: data,
		loading: !error && !data,
		error: error,
	}
}
