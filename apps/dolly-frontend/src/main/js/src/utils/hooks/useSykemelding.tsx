import useSWR from 'swr'
import { sykemeldingFetcher } from '@/api'

const baseUrl = '/testnav-sykemelding-proxy'
const getValideringUrl = (shouldValidate: boolean) =>
	shouldValidate ? `${baseUrl}/v1/rules/validate` : null

export const useSykemeldingValidering = (shouldValidate: boolean, values: any) => {
	const { data, isLoading, error } = useSWR<any, Error>(
		[getValideringUrl(shouldValidate)],
		([url, _headers]) => sykemeldingFetcher(url, _headers, values),
	)

	return {
		validation: data,
		loading: isLoading,
		error: error,
	}
}
