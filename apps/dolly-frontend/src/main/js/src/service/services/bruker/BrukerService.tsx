import Request from '~/service/services/Request'

const brukerServiceUrl = '/testnav-bruker-service/api/v1/brukere'
const userOrgJwtHeader = 'User-Organaisasjon-Jwt'

type Bruker = {
	id?: string
	brukernavn: string
	organisasjonsnummer: string
	opprettet?: string
	sistInnlogget?: string
}

export default {
	getBrukere() {
		return Request.get(`${brukerServiceUrl}`).then((response) => {
			if (response != null) return response
		})
	},

	opprettBruker(bruker: Bruker) {
		return Request.post(`${brukerServiceUrl}`, bruker).then((response) => {
			if (response != null) return response
		})
	},

	opprettToken(id: string) {
		return Request.post(`${brukerServiceUrl}/${id}/token`).then((response) => {
			if (response != null) return response
		})
	},

	getBruker(id: string, orgJwt: string) {
		return Request.get(`${brukerServiceUrl}/${id}`, {
			[userOrgJwtHeader]: orgJwt,
		}).then((response) => {
			if (response != null) return response
		})
	},

	deleteBruker(id: string, orgJwt: string) {
		return Request.delete(`${brukerServiceUrl}/${id}`, {
			[userOrgJwtHeader]: orgJwt,
		}).then((response) => {
			if (response != null) return response
		})
	},
}
