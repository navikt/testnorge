import useSWR from 'swr'
import { fetcher } from '@/api'

const getTransaksjonsidUrl = (system, ident, bestillingsid) =>
	bestillingsid
		? `/dolly-backend/api/v1/transaksjonid?system=${system}&ident=${ident}&bestillingId=${bestillingsid}`
		: `/dolly-backend/api/v1/transaksjonid?ident=${ident}&system=${system}`

export const useTransaksjonsid = (system, ident, bestillingsid = null) => {
	const shouldFetch = !!(system && ident)

	const { data, isLoading, error } = useSWR<any, Error>(
		shouldFetch ? getTransaksjonsidUrl(system, ident, bestillingsid) : null,
		fetcher,
	)

	return {
		transaksjonsid: data,
		loading: isLoading,
		error: error,
	}
}
