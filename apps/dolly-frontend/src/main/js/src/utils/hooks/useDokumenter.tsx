import useSWR from 'swr'
import { fetcher } from '@/api'

const getDokumentUrl = (dokumentId: string) =>
	`/dolly-backend/api/v1/dokument/dokument/${dokumentId}`

export const useDokument = (dokumentId: string) => {
	const { data, isLoading, error } = useSWR<any, Error>(
		dokumentId ? getDokumentUrl(dokumentId) : null,
		fetcher,
	)

	return {
		dokument: data,
		loading: isLoading,
		error: error,
	}
}

export const useDokumenterFraMal = (malId: string) => {
	const { data, isLoading, error } = useSWR<any, Error>(
		malId ? `/dolly-backend/api/v1/dokument/mal/${malId}` : null,
		fetcher,
	)

	return {
		dokumenter: data,
		loading: isLoading,
		error: error,
	}
}
