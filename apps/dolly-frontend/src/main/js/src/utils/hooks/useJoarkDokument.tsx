import useSWR from 'swr'
import { fetcher } from '@/api'

const joarkUrl = '/testnav-joark-dokument-service/api/v2/journalpost/'

export const useJournalpost = (journalpostId: number, miljo: string) => {
	const { data, isLoading, error } = useSWR<any, Error>(
		[joarkUrl + journalpostId, { miljo: miljo }],
		([url, headers]) => fetcher(url, headers),
	)
	return {
		journalpost: data,
		loading: isLoading,
		error: error,
	}
}

export const useDokument = (
	journalpostId: number,
	dokumentInfoId: number,
	miljo: string,
	dokumentType: string,
) => {
	const { data, isLoading, error } = useSWR<any, Error>(
		[
			`${joarkUrl}${journalpostId}/dokumenter/${dokumentInfoId}?dokumentType=${dokumentType}`,
			{ miljo: miljo },
		],
		([url, headers]) => fetcher(url, headers),
	)
	return {
		dokument: data,
		loading: isLoading,
		error: error,
	}
}
