import useSWR, { mutate as globalMutate } from 'swr'
import { fetcher } from '@/api'

const pdlForvalterPersonerBasePath = '/testnav-pdl-forvalter/api/v1/personer'

const getPersonerUrl = (identer: string | null) =>
	`${pdlForvalterPersonerBasePath}?identer=${identer}`

const getEksistensUrl = (identer: string[] | null) =>
	`/testnav-pdl-forvalter/api/v1/eksistens?identer=${identer}`

export const usePdlForvalterPerson = (ident: string) => {
	const { data, isLoading, error, mutate } = useSWR<any, Error>(
		ident ? getPersonerUrl(ident) : null,
		fetcher,
	)

	return {
		pdlforvalterPerson: data?.[0],
		loading: isLoading,
		error: error,
		refresh: () =>
			mutate().then(() =>
				globalMutate(
					(key) =>
						typeof key === 'string' &&
						key.startsWith(pdlForvalterPersonerBasePath) &&
						key !== getPersonerUrl(ident),
				),
			),
	}
}

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
