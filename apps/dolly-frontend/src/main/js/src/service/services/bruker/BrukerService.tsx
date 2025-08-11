import api from '@/api'
import { Bruker } from '@/pages/brukerPage/types'

const brukerServiceUrl = '/testnav-bruker-service/api/v2/brukere'

export default {
	getBruker(orgnummer: string) {
		return api
			.fetchJson(`testnav-bruker-service/api/v1/brukere?organisasjonsnummer=${orgnummer}`, {
				method: 'GET',
			})
			.then((brukere: Bruker[]) => brukere[0])
	},

	opprettBruker(brukernavn: string, epost: string, organisasjonsnummer: string) {
		const bruker: Bruker = {
			brukernavn: brukernavn,
			epost: epost,
			organisasjonsnummer: organisasjonsnummer,
		}
		return api
			.fetchJson(`${brukerServiceUrl}`, { method: 'POST' }, bruker)
			.then((response: any) => {
				return response
			})
			.catch(() => {
				return null
			})
	},

	updateBruker(brukernavn: string, epost: string, organisasjonsnummer: string) {
		const bruker: Bruker = {
			brukernavn: brukernavn,
			epost: epost,
			organisasjonsnummer: organisasjonsnummer,
		}
		return api
			.fetchJson(`${brukerServiceUrl}`, { method: 'PUT' }, bruker)
			.then((response: any) => {
				return response
			})
			.catch(() => {
				return null
			})
	},
}
