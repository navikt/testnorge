import useSWR from 'swr'
import useSWRImmutable from 'swr/immutable'
import { fetcher, imageFetcher } from '@/api'
import logoutBruker from '@/components/utlogging/logoutBruker'
import { runningCypressE2E } from '@/service/services/Request'

const getBrukereUrl = `/dolly-backend/api/v1/bruker`
const getCurrentBrukerUrl = `/dolly-backend/api/v1/bruker/current`
const getProfilUrl = '/testnorge-profil-api/api/v1/profil'
const getProfilBildeUrl = `${getProfilUrl}/bilde`
// const getOrganisasjonTilgangUrl = `/testnav-organisasjon-tilgang-service/api/v1/organisasjoner`

const getOrganisasjonTilgangUrl = (orgnummer: string) =>
	`/testnav-organisasjon-tilgang-service/api/v1/miljoer/organisasjon/orgnummer?orgnummer=${orgnummer}`

// const getPersonOrganisasjonTilgangUrl = `/testnav-person-organisasjon-tilgang-service/api/v1/person/organisasjoner`

type BrukerProfil = {
	visningsNavn: string
	epost: string
	avdeling: string
	organisasjon: string
	orgnummer: string | undefined
	type: string
}

type BrukerType = {
	brukerId: string
	brukernavn: string
	brukertype: string
	epost: string
	favoritter: []
}

type OrganisasjonMiljoe = {
	id: number
	organisasjonNummer: string
	miljoe: string
}

export const useAlleBrukere = () => {
	const { data, error } = useSWR<BrukerType, Error>(getBrukereUrl, fetcher)

	return {
		brukere: data,
		loading: !error && !data,
		error: error,
	}
}

export const useCurrentBruker = () => {
	const { data, error } = useSWR<BrukerType, Error>(getCurrentBrukerUrl, fetcher)

	if (error && !runningCypressE2E()) {
		console.error('Klarte ikke å hente aktiv bruker, logger ut..')
		logoutBruker()
	}
	// console.log('data currentBruker: ', data) //TODO - SLETT MEG

	return {
		currentBruker: data,
		loading: !error && !data,
		error: error,
	}
}

export const useBrukerProfil = () => {
	const { data, error } = useSWR<BrukerProfil, Error>(getProfilUrl, fetcher)
	// console.log('data brukerProfil: ', data) //TODO - SLETT MEG

	return {
		brukerProfil: data,
		loading: !error && !data,
		error: error,
	}
}

export const useBrukerProfilBilde = () => {
	const { data, error } = useSWRImmutable<Blob, Error>(getProfilBildeUrl, imageFetcher)

	return {
		brukerBilde: data,
		loading: !error && !data,
		error: error,
	}
}

export const useOrganisasjonTilgang = (orgnummer: string) => {
	if (!orgnummer) return null

	const { data, error } = useSWR<OrganisasjonMiljoe, Error>(
		getOrganisasjonTilgangUrl(orgnummer),
		// getOrganisasjonTilgangUrl,
		fetcher
	)
	// console.log('data orgmiljoe: ', data) //TODO - SLETT MEG

	return {
		organisasjonTilgang: data,
		loading: !error && !data,
		error: error,
	}
}

// export const usePersonOrganisasjonTilgang = (orgnummer: string) => {}
