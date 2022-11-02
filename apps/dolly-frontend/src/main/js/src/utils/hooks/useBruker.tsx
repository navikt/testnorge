import useSWR from 'swr'
import useSWRImmutable from 'swr/immutable'
import { fetcher, imageFetcher } from '~/api'
import logoutBruker from '~/components/utlogging/logoutBruker'
import { runningTestcafe } from '~/service/services/Request'

const getBrukereUrl = `/dolly-backend/api/v1/bruker`
const getCurrentBrukerUrl = `/dolly-backend/api/v1/bruker/current`
const getProfilUrl = '/testnorge-profil-api/api/v1/profil'
const getProfilBildeUrl = `${getProfilUrl}/bilde`

type BrukerProfil = {
	visningsNavn: string
	epost: string
	avdeling: string
	organisasjon: string
	type: string
}

type BrukerType = {
	brukerId: string
	brukernavn: string
	brukertype: string
	epost: string
	favoritter: []
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

	if (error && !runningTestcafe()) {
		console.error('Klarte ikke å hente aktiv bruker, logger ut..')
		logoutBruker()
	}

	return {
		currentBruker: data || ({} as BrukerType),
		loading: !error && !data,
		error: error,
	}
}

export const useBrukerProfil = () => {
	const { data, error } = useSWR<BrukerProfil, Error>(getProfilUrl, fetcher)

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
