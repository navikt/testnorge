import useSWR from 'swr'
import { fetcher } from '@/api'

const levenedeArbeidsforholdUrl = '/testnav-levende-arbeidsforhold-ansettelse/api/v1/logg'

export const getIdentUrl = (ident: string) => {
	return `${levenedeArbeidsforholdUrl}/ident/${ident}`
}

export const getOrgnummerUrl = (orgnummer: string) => {
	return `${levenedeArbeidsforholdUrl}/organisasjon/${orgnummer}`
}

const getLoggUrl = (page: number, size: number, sort: string) => {
	return `${levenedeArbeidsforholdUrl}?page=${page}&size=${size}&sort=${sort}`
}

export const useLevendeArbeidsforholdLogg = (page: number, size: number, sort: string) => {
	const { data, isLoading, error } = useSWR<any, Error>(getLoggUrl(page, size, sort), fetcher)

	return {
		loggData: data,
		loading: isLoading,
		error: error,
	}
}
