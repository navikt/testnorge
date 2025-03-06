import useSWR from 'swr'
import { fetcher } from '@/api'

const getPersonerUrl = (identer: string | null) =>
	`/testnav-pdl-forvalter/api/v1/personer?identer=${identer}`

const getEksistensUrl = (identer: string[] | null) =>
	`/testnav-pdl-forvalter/api/v1/eksistens?identer=${identer}`

export const usePdlfPersoner = (identer: string) => {
	const { data, isLoading, error } = useSWR<any, Error>(getPersonerUrl(identer), fetcher)

	return {
		pdlfPersoner: data,
		loading: isLoading,
		error: error,
	}
}

export const usePdlfEksistens = (identer: string[] | null) => {
	const { data, isLoading, error } = useSWR<any, Error>(
		identer?.length > 0 && getEksistensUrl(identer),
		fetcher,
	)

	return {
		pdlfEksistens: data,
		loading: isLoading,
		error: error,
	}
}
