import useSWR from 'swr'
import { fetcher } from '@/api'
import DollyEndpoints from '@/service/services/dolly/DollyEndpoints'

export const useHendelseId = (ident: string, relatertIdent?: string) => {
	const { data, isLoading, error } = useSWR(
		ident ? DollyEndpoints.hendelseId(ident, relatertIdent) : null,
		fetcher,
		{ revalidateOnFocus: false },
	)

	return { data, loading: isLoading, error }
}
