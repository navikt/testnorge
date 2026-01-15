import useSWRImmutable from 'swr/immutable'
import { fetcher } from '@/api'

export const useArbeidsforholdstyperInntektstub = () => {
	const { data, isLoading, error } = useSWRImmutable<Record<string, string>, Error>(
		`/testnav-kodeverk-service/api/v1/kodeverk/Arbeidsforholdstyper`,
		fetcher,
	)

	return { arbeidsforholdstyper: data, loading: isLoading, error }
}
