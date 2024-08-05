import useSWR from 'swr'
import { fetcher } from '@/api'

const getKodeverkUrl = (kodeverkstype: string) => {
	return `/testnav-skattekort-service/api/v1/kodeverk?kodeverkstype=${kodeverkstype}`
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
