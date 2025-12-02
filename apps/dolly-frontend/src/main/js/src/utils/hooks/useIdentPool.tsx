import useSWR from 'swr'
import Request from '@/service/services/Request'

const identPoolUrl = '/testnav-ident-pool/api/v2/ident/valider'

export const useValiderIdent = (ident: string) => {
	const { data, isLoading, error } = useSWR(
		ident ? [identPoolUrl, { ident: ident }] : null,
		([url, headers]) => Request.post(url, headers),
	)

	return {
		validering: data?.data,
		loading: isLoading,
		error,
	}
}
