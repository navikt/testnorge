import useSWR from 'swr'
import { fetcher } from '@/api'

const getTransaksjonsidUrl = (system, ident, bestillingsid) =>
	bestillingsid
		? `/dolly-backend/api/v1/transaksjonid?system=${system}&ident=${ident}&bestillingId=${bestillingsid}`
		: `/dolly-backend/api/v1/transaksjonid?ident=${ident}&system=${system}`

export const useTransaksjonsid = (system, ident, bestillingsid = null) => {
	if (!system) {
		return {
			loading: false,
			error: 'System mangler!',
		}
	}

	if (!ident) {
		return {
			loading: false,
			error: 'Ident mangler!',
		}
	}

	const { data, error } = useSWR<any, Error>(
		getTransaksjonsidUrl(system, ident, bestillingsid),
		fetcher
	)

	return {
		transaksjonsid: data,
		loading: !error && !data,
		error: error,
	}
}
