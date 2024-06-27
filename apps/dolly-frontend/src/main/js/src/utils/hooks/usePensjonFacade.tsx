import useSWR from 'swr'
import api from '@/api'
import { v4 as _uuid } from 'uuid'

const pensjonFacadeGenererUrl =
	'/testnav-pensjon-testdata-facade-proxy/api/v1/generate-inntekt-med-gjennomsnitt-g'

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

export const usePensjonFacadeGjennomsnitt = (body) => {
	const { data, isLoading, error, mutate } = useSWR<PensjonResponse, Error>(
		validateBody(body) ? [pensjonFacadeGenererUrl] : null,
		([url]) => {
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
		{},
	)

	return {
		pensjon: data?.data,
		loading: isLoading,
		error: error,
		mutate: mutate,
	}
}
