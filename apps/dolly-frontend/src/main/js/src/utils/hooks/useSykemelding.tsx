import useSWR from 'swr'
import { sykemeldingFetcher } from '@/api'

const getValideringUrl = (shouldValidate: boolean) =>
	shouldValidate ? '/testnav-sykemelding-api/api/v1/sykemeldinger/validate' : null

export const useSykemeldingValidering = (shouldValidate: boolean, values: any) => {
	const { data, isLoading, error } = useSWR<any, Error>(
		getValideringUrl(shouldValidate),
		(url: string) => sykemeldingFetcher(url, values),
	)

	return {
		validation: data,
		loading: isLoading,
		error: error,
	}
}
