import useSWR from 'swr'
import { fetcher } from '@/api'

const getPersonerUrl = (identer: string) =>
	`/testnav-pdl-forvalter/api/v1/personer?identer=${identer}`

export const usePdlfPersoner = (identer: string) => {
	const { data, isLoading, error } = useSWR<any, Error>(getPersonerUrl(identer), fetcher)

	return {
		personer: data,
		loading: isLoading,
		error: error,
	}
}
