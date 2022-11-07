import useSWR from 'swr'
import { fetcher } from '~/api'

const getGruppeUrl = `/dolly-backend/api/v1/gruppe`
const getPaginertGruppeUrl = (
	gruppeId: string,
	pageNo: number,
	pageSize: number,
	sortKolonne?: string,
	sortRetning?: string
) => {
	const sorting =
		sortKolonne && sortRetning ? `&sortRetning=${sortRetning}&sortKolonne=${sortKolonne}` : ''
	return `${getGruppeUrl}/${gruppeId}/page/${pageNo}?pageSize=${pageSize}${sorting}`
}

export type Gruppe = {
	erLaast: boolean
	id: number
	navn: string
	hensikt: string
	antallIdenter: number
	antallBestillinger: number
	opprettetAv: { brukernavn: string; navIdent: string }
	datoEndret: Date
	erEierAvGruppe: boolean
	identer: any[]
	tags: string[]
}

export const useGruppeById = (
	gruppeId: string,
	pageNo = 0,
	pageSize = 10,
	autoRefresh = false,
	sortKolonne = null,
	sortRetning = null
) => {
	if (!gruppeId) {
		return {
			loading: false,
			error: 'GruppeId mangler!',
		}
	}
	const { data, error } = useSWR<Gruppe, Error>(
		getPaginertGruppeUrl(gruppeId, pageNo, pageSize, sortKolonne, sortRetning),
		fetcher,
		{
			refreshInterval: autoRefresh ? 2000 : null,
			dedupingInterval: autoRefresh ? 2000 : null,
		}
	)

	return {
		identer: data?.identer?.reduce((acc, curr) => {
			acc[curr.ident] = { ...curr, gruppeId: data.id }
			return acc
		}, {}),
		gruppeId: data?.id,
		gruppe: data,
		loading: !error && !data,
		error: error,
	}
}
export const useGrupper = (brukerId?: string) => {
	const { data, error } = useSWR<Gruppe[], Error>(
		`${getGruppeUrl}${brukerId ? `?brukerId=${brukerId}` : ''}`,
		fetcher
	)

	const grupperSorted = data
		?.sort((gruppe, gruppe2) => (gruppe.id < gruppe2.id ? 1 : -1))
		.reduce((acc: { [key: string]: Gruppe }, curr) => ((acc[curr.id] = curr), acc), {})

	return {
		grupper: data,
		grupperById: grupperSorted,
		loading: !error && !data,
		error: error,
	}
}
