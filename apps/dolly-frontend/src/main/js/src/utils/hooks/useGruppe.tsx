import useSWR from 'swr'
import { fetcher } from '~/api'

const getGruppeUrl = `/dolly-backend/api/v1/gruppe`
const getPaginertGruppeUrl = (gruppeId: number, pageNo: number, pageSize: number) =>
	`${getGruppeUrl}/${gruppeId}/page/${pageNo}?pageSize=${pageSize}`

export type Gruppe = {
	erLaast: boolean
	id: number
	navn: string
	hensikt: string
	antallIdenter: number
	opprettetAv: { brukernavn: string; navIdent: string }
	datoEndret: Date
	erEierAvGruppe: boolean
	identer: any[]
	tags: string[]
}

export const useGruppeById = (gruppeId: number, pageNo = 0, pageSize = 10) => {
	const { data, error } = useSWR<Gruppe, Error>(
		getPaginertGruppeUrl(gruppeId, pageNo, pageSize),
		fetcher
	)

	return {
		identer: data?.identer.reduce((acc, curr) => {
			acc[curr.ident] = { ...curr, gruppeId: data.id }
			return acc
		}, {}),
		gruppeId: data?.id,
		gruppe: data,
		loading: !error && !data,
		error: error,
	}
}
export const useGruppeAlle = (brukerId?: string) => {
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
