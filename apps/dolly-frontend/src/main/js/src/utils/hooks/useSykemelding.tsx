import { fetcher, sykemeldingFetcher } from '@/api'
import { AxiosError } from 'axios'
import useSWRMutation from 'swr/mutation'
import useSWR from 'swr'

type RuleHit = {
	messageForSender: string
	messageForUser: string
	ruleName: string
	ruleStatus: string
}

type SykemeldingResponse = {
	message?: string
	status?: string
	ruleHits?: RuleHit[]
}

type TsmSykemeldingResponse = {
	sykmeldinger: [
		{
			sykmeldingId: string
			aktivitet: [
				{
					fom: any
					tom: any
				},
			]
			ident: string
		},
	]
}

export const useTsmSykemelding = (ident: string) => {
	const { data, isLoading, error } = useSWR<TsmSykemeldingResponse, AxiosError<any>>(
		['/testnav-sykemelding-proxy/tsm/api/sykmelding/ident', ident],
		([url, _ident]) =>
			fetcher(url, {
				'X-ident': ident,
			}),
	)

	return {
		sykemeldinger: data?.sykmeldinger || [],
		loading: isLoading,
		error: error,
	}
}

export const useSykemeldingValidering = (values: any) => {
	const { data, error, trigger } = useSWRMutation<SykemeldingResponse, AxiosError<any>>(
		'/testnav-sykemelding-api/api/v1/sykemeldinger/validate',
		(url: string) => sykemeldingFetcher(url, values),
	)

	const errorMessage =
		error?.status === 400
			? `Mangler felter: ${error.response?.data?.message
					?.split(',')
					.map((field: string) => field.trim())
					.join(', ')}`
			: data?.status === 'INVALID'
				? `Validering av sykemelding feilet: ${data.ruleHits?.[0]?.messageForUser}`
				: ''

	return {
		data: data,
		mutate: trigger,
		errorMessage: errorMessage,
	}
}
