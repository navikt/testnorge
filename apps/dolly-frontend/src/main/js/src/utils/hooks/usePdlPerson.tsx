import useSWR from 'swr'
import { PdlDataWrapper } from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { fetcher } from '@/api'

const getPersonoppslagUrl = (ident: string, miljoe: string | null) =>
	`/person-service/api/v2/personer/ident/${ident}${miljoe ? '?pdlMiljoe=' + miljoe : ''}`

export const usePdlMiljoeinfo = (ident: string, hentQ1 = false) => {
	const { data, error, isLoading } = useSWR<PdlDataWrapper, Error>(
		getPersonoppslagUrl(ident, hentQ1 ? 'Q1' : null),
		fetcher
	)

	const errorMessage = data?.errors?.[0]?.message

	return {
		pdlData: data?.data,
		loading: isLoading,
		error: error || errorMessage,
	}
}
