import useSWR from 'swr'
import { fetcher } from '@/api'
import { useBrukerProfil } from '@/utils/hooks/useBruker'

const getOrganisasjonMiljoeUrl = (orgnummer: string) =>
	`/testnav-altinn3-tilgang-service/api/v1/miljoer/organisasjon/${orgnummer}`

const organisasjonTilgangUrl = `/testnav-altinn3-tilgang-service/api/v1/organisasjoner`

type OrganisasjonMiljoe = {
	id: number
	organisasjonNummer: string
	miljoe: string
}

type OrganisasjonTilgang = {
	navn: string
	organisasjonsnummer: string
	organisasjonsform: string
	gyldigTil: string
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

export const useOrganisasjonTilgang = () => {
	const { data, isLoading, error, mutate } = useSWR<OrganisasjonTilgang, Error>(
		organisasjonTilgangUrl,
		fetcher,
	)

	return {
		organisasjonTilgang: data,
		loading: isLoading,
		error: error,
		mutate: mutate,
	}
}
