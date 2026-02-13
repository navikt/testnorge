import useSWR from 'swr'
import { fetcher } from '@/api'
import DollyEndpoints from '@/service/services/dolly/DollyEndpoints'

type GruppeFragment = {
	id: number
	navn: string
}

export const useSoekGruppe = (fragment: string | null) => {
	const shouldFetch = fragment && fragment.length >= 2

	const { data, isLoading, error } = useSWR<GruppeFragment[], Error>(
		shouldFetch ? DollyEndpoints.grupperFragment(fragment) : null,
		fetcher,
		{
			dedupingInterval: 500,
			revalidateOnFocus: false,
		},
	)

	return {
		grupper: data,
		loading: isLoading,
		error,
	}
}
