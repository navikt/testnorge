import api from '@/api'
import { Bruker } from '~/pages/brukerPage/types'
import { NotFoundError } from '~/error'

const brukerServiceUrl = '/testnav-bruker-service/api/v1/brukere'

export default {
	getBruker(orgnummer: string) {
		return api
			.fetchJson<Bruker[]>(
				`testnav-bruker-service/api/v1/brukere?organisasjonsnummer=${orgnummer}`,
				{
					method: 'GET',
				}
			)
			.then((brukere) => brukere[0])
	},

	opprettBruker(brukernavn: string, organisasjonsnummer: string) {
		let bruker: Bruker = {
			brukernavn: brukernavn,
			organisasjonsnummer: organisasjonsnummer,
		}
		return api
			.fetchJson<Bruker>(`${brukerServiceUrl}`, { method: 'POST' }, bruker)
			.then((response) => {
				return response
			})
			.catch(() => {
				return null
			})
	},

	deleteBruker(id: string, jwt?: string) {
		return api.fetch(`${brukerServiceUrl}/${id}`, {
			method: 'DELETE',
			headers: jwt ? { 'User-Jwt': jwt } : {},
		})
	},

	getBrukernavnStatus(brukernavn: string) {
		return api
			.fetch(`${brukerServiceUrl}/brukernavn/${brukernavn}`, { method: 'GET' })
			.then((response: any) => {
				return response.status
			})
			.catch((e: NotFoundError) => {
				return 404
			})
			.catch((e: Error) => {
				return 500
			})
	},
}
