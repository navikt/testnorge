import useSWR from 'swr'
import { PdlDataWrapper } from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { fetcher } from '@/api'

const personServiceUrl = '/person-service/api/v2/personer/'
const getPersonoppslagUrl = (ident: string, miljoe: string | null) =>
	`${personServiceUrl}ident/${ident}${miljoe ? '?pdlMiljoe=' + miljoe : ''}`
const getPersonoppslagBolkUrl = (ident) => `${personServiceUrl}identer?identer=${ident}`

export const usePdlMiljoeinfo = (ident: string, hentQ1 = false) => {
	const { data, error, isLoading } = useSWR<PdlDataWrapper, Error>(
		getPersonoppslagUrl(ident, hentQ1 ? 'Q1' : null),
		fetcher,
	)

	const errorMessage = data?.errors?.[0]?.message

	return {
		pdlData: data?.data,
		loading: isLoading,
		error: error || errorMessage,
	}
}

export const usePdlPersonbolk = (ident: string) => {
	const { data, error, isLoading } = useSWR<PdlDataWrapper, Error>(
		getPersonoppslagBolkUrl(ident),
		fetcher,
	)

	const errorMessage = data?.errors?.[0]?.message

	return {
		pdlData: data?.data,
		loading: isLoading,
		error: error || errorMessage,
	}
}
