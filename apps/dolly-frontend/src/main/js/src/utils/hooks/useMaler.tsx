import useSWR from 'swr'
import { fetcher } from '@/api'

const malbestillingUrl = '/dolly-backend/api/v1/malbestilling'
const getOrganisasjonMalerUrl = '/dolly-backend/api/v1/organisasjon/bestilling/malbestilling'

export type Mal = {
	malNavn: string
	id: number
	malBestilling?: any
	bestilling?: any
}

type MalResponse = {
	malbestillinger: [string, Mal[]]
}

type OversiktResponse = {
	brukereMedMaler: Array<{ brukernavn: string; brukerId: string }>
}

export const useMalbestillingOversikt = () => {
	const { data, isLoading, error, mutate } = useSWR<OversiktResponse, Error>(
		`${malbestillingUrl}/oversikt`,
		fetcher,
	)

	return {
		brukere: data?.brukereMedMaler,
		loading: isLoading,
		error: error,
		mutate: mutate,
	}
}

export const useMalbestillingBruker = (brukerId?: string) => {
	const { data, isLoading, error, mutate } = useSWR<Mal[], Error>(
		`${malbestillingUrl}/brukerId/${brukerId}`,
		fetcher,
	)

	return {
		maler: data,
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

export const useDollyOrganisasjonMalerBrukerOgMalnavn = (brukerId?: string, malNavn?: string) => {
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
