import useSWR from 'swr'
import { fetcher } from '@/api'

const getLoggUrl = (page: number, size: number, sort: string) => {
	return `/testnav-levende-arbeidsforhold-ansettelse/api/v1/logg?page=${page}&size=${size}&sort=${sort}`
}

export const useLevendeAnsettelseLogg = (page: number, size: number, sort: string) => {
	const { data, isLoading, error } = useSWR<any, Error>(getLoggUrl(page, size, sort), fetcher)

	return {
		loggData: data,
		loading: isLoading,
		error: error,
	}
}
