import useSWR from 'swr'
import { fetcher } from '@/api'
import useSWRImmutable from 'swr/immutable'

const getSigrunstubBaseUrl = () => `/testnav-dolly-proxy/sigrunstub/api`

export const usePensjonsgivendeInntektKodeverk = () => {
	const { data, isLoading, error } = useSWRImmutable<any, Error>(
		`${getSigrunstubBaseUrl()}/v1/pensjonsgivendeinntektforfolketrygden/kodeverk`,
		fetcher,
	)

	return {
		kodeverk: data,
		loading: isLoading,
		error: error,
	}
}

export const usePensjonsgivendeInntektSkatteordning = () => {
	const { data, isLoading, error } = useSWR<any, Error>(
		`${getSigrunstubBaseUrl()}/v1/pensjonsgivendeinntektforfolketrygden/skatteordning`,
		fetcher,
	)

	return {
		skatteordning: data,
		loading: isLoading,
		error: error,
	}
}

export const usePensjonsgivendeInntekt = (ident, harPensjonsgivendeInntekt, retryCount = 8) => {
	const endpoint = `${getSigrunstubBaseUrl()}/v1/pensjonsgivendeinntektforfolketrygden`
	const { data, error, isLoading } = useSWR(
		ident && harPensjonsgivendeInntekt ? [endpoint, { norskident: ident }] : null,
		([url, params]) => fetcher(url, params),
		{ errorRetryCount: retryCount },
	)

	return {
		data,
		loading: isLoading,
		error,
	}
}

export const useSummertSkattegrunnlag = (ident, harSummertSkattegrunnlag, retryCount = 8) => {
	const endpoint = `${getSigrunstubBaseUrl()}/v2/summertskattegrunnlag`
	const { data, error, isLoading } = useSWR(
		ident && harSummertSkattegrunnlag ? [endpoint, { personIdentifikator: ident }] : null,
		([url, params]) => fetcher(url, params),
		{ errorRetryCount: retryCount },
	)

	return {
		data,
		loading: isLoading,
		error,
	}
}

export const useSekvensnummer = (ident) => {
	const endpoint = `${getSigrunstubBaseUrl()}/sekvensnummer/${ident}`
	const { data, error, isLoading } = useSWR(ident ? endpoint : null, (url) => fetcher(url))

	return {
		data,
		loading: isLoading,
		error,
	}
}
