import useSWR from 'swr'
import { fetcher } from '@/api'
import useSWRImmutable from 'swr/immutable'

const getSigrunstubBaseUrl = () => `/testnav-sigrunstub-proxy/api`

export const usePensjonsgivendeInntektKodeverk = () => {
	const { data, isLoading, error } = useSWRImmutable<any, Error>(
		`${getSigrunstubBaseUrl()}/pensjonsgivendeinntektforfolketrygden/kodeverk`,
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

export const useLignetInntekt = (ident, harSigrunstub) => {
	const endpoint = `${getSigrunstubBaseUrl()}/v1/lignetinntekt`
	const { data, error, isLoading } = useSWR(
		ident && harSigrunstub ? [endpoint, { personidentifikator: ident }] : null,
		([url, params]) => fetcher(url, params),
	)

	return {
		data: data?.responseList,
		loading: isLoading,
		error,
	}
}

export const usePensjonsgivendeInntekt = (ident, harPensjonsgivendeInntekt) => {
	const endpoint = `${getSigrunstubBaseUrl()}/v1/pensjonsgivendeinntektforfolketrygden`
	const { data, error, isLoading } = useSWR(
		ident && harPensjonsgivendeInntekt ? [endpoint, { norskident: ident }] : null,
		([url, params]) => fetcher(url, params),
	)

	return {
		data,
		loading: isLoading,
		error,
	}
}

export const useSummertSkattegrunnlag = (ident, harSummertSkattegrunnlag) => {
	const endpoint = `${getSigrunstubBaseUrl()}/v2/summertskattegrunnlag`
	const { data, error, isLoading } = useSWR(
		ident && harSummertSkattegrunnlag ? [endpoint, { norskident: ident }] : null,
		([url, params]) => fetcher(url, params),
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
