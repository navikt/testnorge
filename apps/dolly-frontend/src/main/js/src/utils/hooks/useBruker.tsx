import useSWR from 'swr'
import useSWRImmutable from 'swr/immutable'
import { fetcher, imageFetcher } from '@/api'
import { runningE2ETest } from '@/service/services/Request'
import { navigateToLogin } from '@/components/utlogging/navigateToLogin'
import { ERROR_ACTIVE_USER } from '@/ducks/errors/ErrorMessages'

const getBrukereUrl = `/dolly-backend/api/v1/bruker`
const getCurrentBrukerUrl = `/dolly-backend/api/v1/bruker/current`
const getProfilUrl = '/testnorge-profil-api/api/v1/profil'
const getProfilBildeUrl = `${getProfilUrl}/bilde`

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
	grupper: []
	representererTeam?: {
		id: string
		brukerId: string
		navn: string
		beskrivelse: string
	}
}

export const useAlleBrukere = () => {
	const { data, isLoading, error } = useSWR<BrukerType, Error>(getBrukereUrl, fetcher)

	return {
		brukere: data,
		loading: isLoading,
		error: error,
	}
}

export const useCurrentBruker = () => {
	const { data, isLoading, error } = useSWR<BrukerType, Error>(getCurrentBrukerUrl, fetcher, {
		revalidateOnFocus: true,
	})

	if (error && !runningE2ETest()) {
		console.error(ERROR_ACTIVE_USER)
		navigateToLogin()
	}

	return {
		currentBruker: data,
		loading: isLoading,
		error: error,
	}
}

export const useBrukerProfil = () => {
	const { data, isLoading, error } = useSWRImmutable<BrukerProfil, Error>(getProfilUrl, fetcher)

	return {
		brukerProfil: data,
		loading: isLoading,
		error: error,
	}
}

export const useBrukerProfilBilde = () => {
	const { data, isLoading, error } = useSWRImmutable<Blob, Error>(getProfilBildeUrl, imageFetcher)

	return {
		brukerBilde: data,
		loading: isLoading,
		error: error,
	}
}

export const useBrukerTeams = () => {
	const { data, isLoading, error, mutate } = useSWR<any, Error>(`${getBrukereUrl}/teams`, fetcher)

	return {
		brukerTeams: data,
		loading: isLoading,
		error: error,
		mutate: mutate,
	}
}
