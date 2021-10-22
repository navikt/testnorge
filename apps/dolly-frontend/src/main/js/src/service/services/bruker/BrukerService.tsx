import Request from '~/service/services/Request'
import api from '@/api'
import { Bruker } from '~/pages/brukerPage/types'
import { NotFoundError } from '~/error'

const brukerServiceUrl = '/testnav-bruker-service/api/v1/brukere'
const userJwtHeader = 'User-Jwt'

export default {
	getBrukere(orgnr: string) {
		return Request.get(`${brukerServiceUrl}?organisasjonsnummer=${orgnr}`)
	},

	opprettBruker(brukernavn: string, organisasjonsnummer: string) {
		let bruker: Bruker = {
			brukernavn: brukernavn,
			organisasjonsnummer: organisasjonsnummer,
		}
		return api
			.fetchJson(`${brukerServiceUrl}`, { method: 'POST' }, bruker)
			.then((response: any) => {
				return response
			})
			.catch((e: Error) => {
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
