import useSWR from 'swr'
import { fetcher } from '@/api'

const getMalerUrl = '/dolly-backend/api/v1/malbestilling'
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
	const { data, isLoading, error, mutate } = useSWR<MalResponse, Error>(getMalerUrl, fetcher, {
		fallbackData: { malbestillinger: ['TEMP', []] },
	})

	return {
		maler: data?.malbestillinger,
		loading: isLoading,
		error: error,
		mutate: mutate,
	}
}

export const useDollyMalerBrukerOgMalnavn = (brukerId: string, malNavn?: string) => {
	const { data, isLoading, error, mutate } = useSWR<MalResponse, Error>(
		brukerId && `${getMalerUrl}?brukerId=${brukerId}${malNavn ? `&malNavn=${malNavn}` : ''}`,
		fetcher,
	)

	const maler =
		data?.malbestillinger && Object.values(data.malbestillinger)?.length > 0
			? Object.values(data.malbestillinger)?.[0]
			: []

	return {
		maler: maler,
		loading: isLoading,
		error: error,
		mutate: mutate,
	}
}

export const useDollyOrganisasjonMaler = () => {
	const { data, isLoading, error, mutate } = useSWR<MalResponse, Error>(
		getOrganisasjonMalerUrl,
		fetcher,
		{
			fallbackData: { malbestillinger: ['TEMP', []] },
		},
	)

	return {
		maler: data?.malbestillinger,
		loading: isLoading,
		error: error,
		mutate: mutate,
	}
}

export const useDollyOrganisasjonMalerBrukerOgMalnavn = (brukerId: string, malNavn?: string) => {
	const { data, isLoading, error, mutate } = useSWR<MalResponse, Error>(
		brukerId &&
			`${getOrganisasjonMalerUrl}?brukerId=${brukerId}${malNavn ? `&malNavn=${malNavn}` : ''}`,
		fetcher,
	)

	const maler =
		data?.malbestillinger && Object.values(data.malbestillinger)?.length > 0
			? Object.values(data.malbestillinger)?.[0]
			: []

	return {
		maler: maler,
		loading: isLoading,
		error: error,
		mutate: mutate,
	}
}
