import useSWR from 'swr'
import api, { fetcher, multiFetcherAfpOffentlig } from '@/api'
import { v4 as _uuid } from 'uuid'
import useSWRMutation from 'swr/mutation'

const baseUrl = '/testnav-pensjon-testdata-facade-proxy'
const pensjonVedtakUrl = `${baseUrl}/api/v2/vedtak`
const pensjonFacadeGenererUrl = `${baseUrl}/api/v1/generate-inntekt-med-gjennomsnitt-g`
const tpOrdningUrl = `${baseUrl}/api/v1/tp/ordning`
const muligedirektekallUrl = `${baseUrl}/q1/api/mock-oppsett/muligedirektekall`

const getMockOppsettUrl = (miljoer, ident) => {
	return miljoer.map((miljoe) => ({
		url: `${baseUrl}/${miljoe}/api/mock-oppsett/${ident}`,
		miljo: miljoe,
	}))
}

export const usePensjonVedtak = (ident, miljo) => {
	const { data, isLoading, error } = useSWR<string[], Error>(
		[`${pensjonVedtakUrl}?miljo=${miljo}`, { fnr: ident }],
		([url, headers]) => fetcher(url, headers),
	)

	return {
		vedtakData: data,
		loading: isLoading,
		error: error,
	}
}

type GjennomsnittG = {
	ar: string
	inntekt: number
	generatedG: number
	grunnbelop: number
}

type PensjonResponse = {
	data: {
		arInntektGList: GjennomsnittG[]
		averageG: number
	}
}

const validateBody = (body) => {
	if (!body) return false
	const { fomAar, tomAar, averageG } = body
	return fomAar && tomAar && averageG
}

export const usePensjonFacadeGenerer = (body: any) => {
	const { data, error, trigger } = useSWRMutation<PensjonResponse, Error>(
		validateBody(body) && pensjonFacadeGenererUrl,
		(url) => {
			return api
				.fetchJson(
					url,
					{
						method: 'POST',
						headers: {
							'Content-Type': 'application/json',
							'Nav-Consumer-Id': 'dolly',
							'Nav-Call-Id': 'dolly ' + _uuid(),
						},
					},
					body,
				)
				.then((response: PensjonResponse) => ({ data: response }))
		},
	)

	return {
		pensjonResponse: data?.data,
		error: error,
		trigger: trigger,
	}
}

export const useTpOrdningKodeverk = () => {
	const { data, isLoading, error } = useSWR<any, Error>(tpOrdningUrl, fetcher)

	const options = data?.map((tpOrdning: any) => ({
		value: tpOrdning.tpnr,
		label: `${tpOrdning.tpnr} - ${tpOrdning.navn}`,
	}))

	return {
		tpOrdningData: options,
		loading: isLoading,
		error: error,
	}
}

export const useMuligeDirektekall = () => {
	const { data, isLoading, error } = useSWR<any, Error>(muligedirektekallUrl, fetcher)

	const options = data?.muligeDirekteKall?.map((direktekall: any) => ({
		value: direktekall.tpId,
		label: `${direktekall.tpId} - ${direktekall.navn}`,
	}))

	return {
		direktekallData: options,
		loading: isLoading,
		error: error,
	}
}

export const useMockOppsett = (miljoer: Array<string>, ident: string, harBestilling: boolean) => {
	const mockOppsettUrl = harBestilling ? getMockOppsettUrl(miljoer, ident) : null
	const { data, isLoading, error } = useSWR<any, Error>(mockOppsettUrl, multiFetcherAfpOffentlig)

	return {
		mockOppsett: data?.sort?.((a, b) => a?.miljo?.localeCompare(b?.miljo)),
		loading: isLoading,
		error: error,
	}
}
