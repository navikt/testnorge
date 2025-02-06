import useSWR from 'swr'
import { fetcher } from '@/api'

const baseUrl = '/testnav-arbeidssoekerregisteret-proxy'
const getUrl = (type) => `${baseUrl}/api/v1/typer/${type}`

export const useArbeidssoekerTyper = (type) => {
	const { data, isLoading, error } = useSWR<any, Error>(getUrl(type), fetcher)

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
