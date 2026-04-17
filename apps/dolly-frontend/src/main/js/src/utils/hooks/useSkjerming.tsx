import useSWR from 'swr'
import api, { fetcher } from '@/api'
import { subDays } from 'date-fns'

const skjermingUrl = '/testnav-dolly-proxy/skjermingsregister/api/v1/skjerming/dolly'

export const useSkjerming = (ident: string) => {
	const { data, isLoading, error, mutate } = useSWR<any, Error>(
		ident?.length > 0 ? `${skjermingUrl}?personident=${ident}` : null,
		() => fetcher(skjermingUrl, { personident: ident }),
		{ errorRetryCount: 0, revalidateOnFocus: false },
	)

	const isErrorResponse = data && (data.status === 404 || data.error)

	return {
		skjerming: isErrorResponse ? null : data,
		loading: isLoading,
		error: error,
		mutate: mutate,
	}
}

export const deleteSkjerming = (ident: string, fornavn: string, etternavn: string) => {
	return api.fetchJson(
		skjermingUrl,
		{ method: 'PUT' },
		{
			personident: ident,
			fornavn: fornavn,
			etternavn: etternavn,
			skjermetFra: subDays(new Date(), 1),
			skjermetTil: new Date(),
		},
	)
}
