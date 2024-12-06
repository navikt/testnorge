import { sykemeldingFetcher } from '@/api'
import { AxiosError } from 'axios'
import useSWR from 'swr'

export const useSykemeldingValidering = (values: any) => {
	const { data, error, mutate } = useSWR<any, AxiosError<any>>(
		'/testnav-sykemelding-api/api/v1/sykemeldinger/validate',
		(url: string) => sykemeldingFetcher(url, values),
		{ shouldRetryOnError: false },
	)
	let missingFields = []
	console.log('data: ', data) //TODO - SLETT MEG
	console.log('error: ', error) //TODO - SLETT MEG

	if (error?.status === 400) {
		missingFields = error?.response?.data?.message?.split(',').map((field: string) => field.trim())
	}

	return {
		validation: data,
		missingFields: missingFields,
		mutate: mutate,
		error: error,
	}
}
