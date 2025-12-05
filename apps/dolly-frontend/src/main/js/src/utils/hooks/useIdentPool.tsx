import { identpoolFetcher } from '@/api'
import useSWRMutation from 'swr/mutation'

const identPoolUrl = '/testnav-ident-pool/api/v2/ident/validerflere'

export const useValiderIdenter = () => {
	const { data, isMutating, error, trigger, reset } = useSWRMutation(
		identPoolUrl,
		(url, { arg }: { arg: string }) => identpoolFetcher(url, { identer: arg }),
		{
			populateCache: false,
			revalidate: false,
		},
	)

	return {
		validering: data,
		loading: isMutating,
		error,
		trigger,
		reset,
	}
}
