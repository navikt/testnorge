import useSWR from 'swr'
import { fetcher } from '@/api'

const getKodeverkUrl = (kodeverkstype: string) => {
	return `dolly-backend/api/v1/inntektsmelding/${kodeverkstype}`
}

export const useInntektsmeldingKodeverk = (kodeverkstype: string) => {
	const { data, isLoading, error } = useSWR<any, Error>(getKodeverkUrl(kodeverkstype), fetcher)
	console.log('data: ', data) //TODO - SLETT MEG
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
