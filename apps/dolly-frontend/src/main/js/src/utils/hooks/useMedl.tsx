import useSWR from 'swr'
import { fetcher } from '@/api'

const getMedlUrl = (ident) => `/testnav-medl-proxy/rest/v1/person/${ident}`

type MedlResponse = {
	response: any
}

export const useMedlperson = (ident: string, harMedlBestilling: boolean) => {
	const { data, error, mutate } = useSWR<MedlResponse, Error>(getMedlUrl(ident), fetcher, {})

	if (!ident) {
		return {
			loading: false,
			error: 'Ident mangler!',
		}
	}

	if (!harMedlBestilling) {
		return {
			loading: false,
		}
	}

	return {
		maler: data?.malbestillinger,
		loading: !error && !data,
		error: error,
		mutate: mutate,
	}
}
