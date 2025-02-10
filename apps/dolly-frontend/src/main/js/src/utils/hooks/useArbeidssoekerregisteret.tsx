import useSWR from 'swr'
import { fetcher } from '@/api'

const baseUrl = '/testnav-arbeidssoekerregisteret-proxy'
const getTyperUrl = (type: string) => `${baseUrl}/api/v1/typer/${type}`
const getRegistreringUrl = (ident: string) => `${baseUrl}/api/v1/arbeidssoekerregistrering/${ident}`

export const useArbeidssoekerTyper = (type: string) => {
	const { data, isLoading, error } = useSWR<any, Error>(getTyperUrl(type), fetcher)

	const options = data?.map((option: any) => ({
		value: option.key,
		label: option.value,
	}))

	return {
		data: options,
		loading: isLoading,
		error: error,
	}
}

export const useArbeidssoekerregistrering = (ident: string, harBestilling: boolean) => {
	const { data, isLoading, error } = useSWR<any, Error>(
		harBestilling ? getRegistreringUrl(ident) : null,
		fetcher,
	)

	return {
		data: data,
		loading: isLoading,
		error: error,
	}
}
