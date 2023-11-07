import useSWR from 'swr'
import { fetcher } from '@/api'

const joarkUrl = '/testnav-joark-dokument-service/api/v2/journalpost/'

export const useDokument = (
	journalpostId: number,
	dokumentInfoId: number,
	miljo: string,
	dokumentType: string,
) => {
	const { data, isLoading, error } = useSWR<any, Error>(
		[
			journalpostId && dokumentInfoId && dokumentType
				? `${joarkUrl}${journalpostId}/dokumenter/${dokumentInfoId}?dokumentType=${dokumentType}`
				: null,
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
