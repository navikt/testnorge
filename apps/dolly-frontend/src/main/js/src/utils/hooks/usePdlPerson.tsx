import useSWR from 'swr'
import {
	PdlDataBolk,
	PdlDataWrapper,
} from '@/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import { fetcher } from '@/api'

const personServiceUrl = '/person-service/api/v2/personer/'
const getPersonoppslagUrl = (ident: string, miljoe: string | null) =>
	`${personServiceUrl}ident/${ident}${miljoe ? '?pdlMiljoe=' + miljoe : ''}`
const getPersonoppslagBolkUrl = (identer: Array<string>) =>
	`${personServiceUrl}identer?identer=${identer}`

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

export const usePdlPersonbolk = (identer: Array<string>) => {
	const { data, error, isLoading } = useSWR<PdlDataBolk, Error>(
		getPersonoppslagBolkUrl(identer),
		fetcher,
	)

	const errorMessage = data?.errors?.[0]?.message
	return {
		pdlPersoner: data?.data,
		loading: isLoading,
		error: error || errorMessage,
	}
}

// TODO: Vi kan ikke få aktoerId fra allerede fetched data?
export const usePdlAktoerId = (ident: string, shouldFetch: boolean) => {
	const { data, error, isLoading } = useSWR<PdlDataWrapper, Error>(
		ident && shouldFetch ? getPersonoppslagUrl(ident, null) : null,
		fetcher,
	)

	const aktoerId = data?.data?.hentIdenter?.identer?.find(
		(identInfo) => identInfo.gruppe === 'AKTORID' && !identInfo.historisk,
	)?.ident

	return {
		aktoerId,
		loading: isLoading,
		error,
	}
}
