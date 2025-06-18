import useSWR from 'swr'
import { fetcher } from '@/api'

const getGrupperUrl = (pageNo, pageSize, brukerId) =>
	`/dolly-backend/api/v1/gruppe?pageNo=${pageNo}&pageSize=${pageSize}${
		brukerId ? '&brukerId=' + brukerId : ''
	}`

const getEgneGrupperUrl = (brukerId) => `/dolly-backend/api/v1/gruppe?brukerId=${brukerId}`

const getPaginertGruppeUrl = (
	gruppeId: string,
	pageNo: number,
	pageSize: number,
	sortKolonne?: string,
	sortRetning?: string,
) => {
	const sorting =
		sortKolonne && sortRetning ? `&sortRetning=${sortRetning}&sortKolonne=${sortKolonne}` : ''
	return `/dolly-backend/api/v1/gruppe/${gruppeId}/page/${pageNo}?pageSize=${pageSize}${sorting}`
}

const getHelGruppeUrl = (gruppeId: string) => `/dolly-backend/api/v1/gruppe/${gruppeId}`

export type PaginertGruppe = {
	antallElementer: number
	antallPages: number
	contents: Gruppe[]
	favoritter: Gruppe[]
	pageNo: number
	pageSize: number
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
	sortRetning = null,
) => {
	if (!gruppeId) {
		return {
			loading: false,
			error: 'GruppeId mangler!',
		}
	}
	const { data, isLoading, error } = useSWR<Gruppe, Error>(
		getPaginertGruppeUrl(gruppeId, pageNo, pageSize, sortKolonne, sortRetning),
		fetcher,
		{
			refreshInterval: autoRefresh ? 2000 : null,
			dedupingInterval: autoRefresh ? 2000 : null,
		},
	)

	return {
		identer: data?.identer?.reduce((acc, curr) => {
			acc[curr.ident] = { ...curr, gruppeId: data.id }
			return acc
		}, {}),
		gruppeId: data?.id,
		gruppe: data,
		loading: isLoading,
		error: error,
	}
}

export const useGruppeIdenter = (gruppeId) => {
	if (!gruppeId) {
		return {
			loading: false,
			error: 'GruppeId mangler!',
		}
	}
	const { data, isLoading, error } = useSWR<Gruppe, Error>(
		gruppeId ? getHelGruppeUrl(gruppeId) : null,
		fetcher,
	)

	return {
		identer: data?.identer?.map((person) => {
			return {
				ident: person.ident,
				master: person.master,
			}
		}),
		loading: isLoading,
		error: error,
	}
}

export const useGrupper = (pageNo, pageSize, brukerId?: string) => {
	const { data, isLoading, error } = useSWR<PaginertGruppe, Error>(
		getGrupperUrl(pageNo, pageSize, brukerId),
		fetcher,
	)

	return {
		grupper: data,
		loading: isLoading,
		error: error,
	}
}

export const useEgneGrupper = (brukerId?: string) => {
	const { data, isLoading, error } = useSWR<PaginertGruppe, Error>(
		brukerId ? getEgneGrupperUrl(brukerId) : null,
		fetcher,
	)

	return {
		grupper: data,
		loading: isLoading,
		error: error,
	}
}
