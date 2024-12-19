import { sykemeldingFetcher } from '@/api'
import { AxiosError } from 'axios'
import useSWRMutation from 'swr/mutation'

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
