import useSWR from 'swr'
import { fetcher } from '@/api'
import {
	Bestilling,
	BestillingStatus,
	BestillingStatusDetaljert,
	BestillingStatusGruppe,
} from '@/types/bestilling'

export type { Bestilling, BestillingStatusGruppe, BestillingStatus, BestillingStatusDetaljert }

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

export type GruppeIdent = {
	ident: string
	bestillingId: number[]
	bestillinger: Bestilling[]
	master: string
	ibruk: boolean
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
	identer: GruppeIdent[]
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
	const { data, isLoading, error } = useSWR<Gruppe, Error>(
		gruppeId ? getPaginertGruppeUrl(gruppeId, pageNo, pageSize, sortKolonne, sortRetning) : null,
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
		loading: !gruppeId ? false : isLoading,
		error: !gruppeId ? 'GruppeId mangler!' : error,
	}
}

export const useGruppeIdenter = (gruppeId) => {
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
		loading: !gruppeId ? false : isLoading,
		error: !gruppeId ? 'GruppeId mangler!' : error,
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
