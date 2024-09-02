import useSWR from 'swr'
import api, { fetcher } from '@/api'
import { v4 as _uuid } from 'uuid'
import useSWRMutation from 'swr/mutation'

const pensjonVedtakUrl = '/testnav-pensjon-testdata-facade-proxy/api/v2/vedtak'
const pensjonFacadeGenererUrl =
	'/testnav-pensjon-testdata-facade-proxy/api/v1/generate-inntekt-med-gjennomsnitt-g'

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
