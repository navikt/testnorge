import useSWR from 'swr'
import { fetcher } from '@/api'

const baseUrl = '/testnav-arbeidssoekerregisteret-proxy'
const getTyperUrl = (type) => `${baseUrl}/api/v1/typer/${type}`
const getRegistreringUrl = (ident) => `${baseUrl}/api/v1/arbeidssoekerregistrering/${ident}`

export const useArbeidssoekerTyper = (type) => {
	const { data, isLoading, error } = useSWR<any, Error>(getTyperUrl(type), fetcher)

	const options = data?.map((option) => ({
		value: option.key,
		label: option.value,
	}))

	return {
		data: options,
		loading: isLoading,
		error: error,
	}
}

export const useArbeidssoekerregistrering = (ident, harBestilling) => {
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
