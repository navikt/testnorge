import useSWR from 'swr'
import { fetcher } from '~/api'

const getMalerUrl = '/dolly-backend/api/v1/bestilling/malbestilling'
const getOrganisasjonMalerUrl = '/dolly-backend/api/v1/organisasjon/bestilling/malbestilling'

export type Mal = {
	malNavn: string
	id: number
	bestilling: any
}

type MalResponse = {
	malbestillinger: [string, Mal[]]
}

export const useDollyMaler = () => {
	const { data, error, mutate } = useSWR<MalResponse, Error>(getMalerUrl, fetcher, {
		fallbackData: { malbestillinger: ['TEMP', []] },
	})

	return {
		maler: data?.malbestillinger,
		loading: !error && !data,
		error: error,
		mutate: mutate,
	}
}

export const useDollyMalerBrukerOgMalnavn = (brukerId: string, malNavn?: string) => {
	const { data, error, mutate } = useSWR<Mal[], Error>(
		brukerId && `${getMalerUrl}/bruker?brukerId=${brukerId}${malNavn ? `&malNavn=${malNavn}` : ''}`,
		fetcher,
		{ fallbackData: [] }
	)

	return {
		maler: data,
		loading: !error && !data,
		error: error,
		mutate: mutate,
	}
}

export const useDollyOrganisasjonMaler = () => {
	const { data, error, mutate } = useSWR<MalResponse, Error>(getOrganisasjonMalerUrl, fetcher, {
		fallbackData: { malbestillinger: ['TEMP', []] },
	})

	return {
		maler: data?.malbestillinger,
		loading: !error && !data,
		error: error,
		mutate: mutate,
	}
}

export const useDollyOrganisasjonMalerBrukerOgMalnavn = (brukerId: string, malNavn?: string) => {
	const { data, error, mutate } = useSWR<Mal[], Error>(
		brukerId &&
			`${getOrganisasjonMalerUrl}/bruker?brukerId=${brukerId}${
				malNavn ? `&malNavn=${malNavn}` : ''
			}`,
		fetcher,
		{ fallbackData: [] }
	)

	return {
		maler: data,
		loading: !error && !data,
		error: error,
		mutate: mutate,
	}
}
