import useSWR from 'swr'
import { fetcher } from '@/api'
import ArbeidssoekerregisteretService from '@/service/services/arbeidssoekerregisteret/ArbeidssoekerregisteretService'

const baseUrl = '/testnav-arbeidssoekerregisteret-proxy'
const getTyperUrl = (type: string) => `${baseUrl}/api/v1/typer/${type}`

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
	const { data, isLoading, error, mutate } = useSWR<any, Error>(
		harBestilling ? ArbeidssoekerregisteretService.getRegistreringUrl(ident) : null,
		fetcher,
	)

	return {
		data: data,
		loading: isLoading,
		error: error,
		mutate,
	}
}
