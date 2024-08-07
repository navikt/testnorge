import useSWR from 'swr'
import { fetcher } from '@/api'

const baseUrl = '/testnav-skattekort-service/api/v1'

const getSkattekortUrl = (ident: string) => {
	return `${baseUrl}/skattekort?ident=${ident}`
}

const getKodeverkUrl = (kodeverkstype: string) => {
	return `${baseUrl}/kodeverk?kodeverkstype=${kodeverkstype}`
}

export const useSkattekort = (ident: string, harSkattekortBestilling: boolean) => {
	if (!ident) {
		return {
			loading: false,
			error: 'Ident mangler!',
		}
	}

	if (!harSkattekortBestilling) {
		return {
			loading: false,
		}
	}

	const { data, isLoading, error } = useSWR<any, Error>(getSkattekortUrl(ident), fetcher)

	return {
		skattekort: data,
		loading: isLoading,
		error: error,
	}
}

export const useSkattekortKodeverk = (kodeverkstype: string) => {
	const { data, isLoading, error } = useSWR<any, Error>(getKodeverkUrl(kodeverkstype), fetcher)

	const koder =
		data &&
		Object.keys(data)?.map((key) => ({
			label: key,
			value: data[key],
		}))

	return {
		kodeverk: koder,
		loading: isLoading,
		error: error,
	}
}
