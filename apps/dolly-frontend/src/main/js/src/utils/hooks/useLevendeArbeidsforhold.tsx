import useSWR from 'swr'
import { fetcher } from '@/api'

const levenedeArbeidsforholdUrl = '/testnav-levende-arbeidsforhold-ansettelse/api/v1/logg'

const getLoggUrl = (page: number, size: number, sort: string) => {
	return `${levenedeArbeidsforholdUrl}?page=${page}&size=${size}&sort=${sort}`
}

export const getIdentUrl = (ident: string) => {
	return `${levenedeArbeidsforholdUrl}/ident/${ident}`
}

export const useLevendeArbeidsforholdLogg = (page: number, size: number, sort: string) => {
	const { data, isLoading, error } = useSWR<any, Error>(getLoggUrl(page, size, sort), fetcher)

	return {
		loggData: data,
		loading: isLoading,
		error: error,
	}
}

export const useLevendeArbeidsforholdIdentsoek = (ident: string) => {
	console.log('ident: ', ident) //TODO - SLETT MEG
	if (!ident) {
		return {
			loading: false,
			error: 'Ident mangler!',
		}
	}
	const { data, isLoading, error } = useSWR<any, Error>(getIdentUrl(ident), fetcher)

	return {
		identData: data,
		loading: isLoading,
		error: error,
	}
}
