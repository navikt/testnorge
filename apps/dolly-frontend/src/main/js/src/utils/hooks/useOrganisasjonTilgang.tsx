import useSWR from 'swr'
import { fetcher } from '@/api'
import { useBrukerProfil } from '@/utils/hooks/useBruker'

const getOrganisasjonMiljoeUrl = (orgnummer: string) =>
	`/testnav-organisasjon-tilgang-service/api/v1/miljoer/organisasjon/orgnummer?orgnummer=${orgnummer}`

type OrganisasjonMiljoe = {
	id: number
	organisasjonNummer: string
	miljoe: string
}

export const useOrganisasjonMiljoe = () => {
	const { brukerProfil } = useBrukerProfil()
	const orgnummer = brukerProfil?.orgnummer

	if (!orgnummer) {
		return {
			loading: false,
		}
	}

	const { data, isLoading, error } = useSWR<OrganisasjonMiljoe, Error>(
		getOrganisasjonMiljoeUrl(orgnummer),
		fetcher,
	)

	return {
		organisasjonMiljoe: data,
		loading: isLoading,
		error: error,
	}
}
