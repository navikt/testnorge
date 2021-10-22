import Api from './api'

export type Bruker = {
	id: string
	brukernavn: string
	organisasjonsnummer: string
	opprettet: string
	sistInnlogget: string
}

const getBruker = (orgnummer: string) =>
	Api.fetchJson<Bruker[]>(
		`testnav-bruker-service/api/v1/brukere?organisasjonsnummer=${orgnummer}`,
		{
			method: 'GET',
		}
	).then((brukere) => brukere[0])

const createBruker = (orgnummer: string, brukernavn: string) =>
	Api.fetchJson<Bruker>(
		'testnav-bruker-service/api/v1/brukere',
		{
			method: 'POST',
		},
		JSON.stringify({
			organisasjonsnummer: orgnummer,
			brukernavn: brukernavn,
		})
	)

const deleteBruker = (id: string, jwt?: string) =>
	Api.fetch(`testnav-bruker-service/api/v1/brukere/${id}`, {
		headers: jwt ? { 'User-Jwt': jwt } : {},
		method: 'DELETE',
	})

const getToken = (id: string) =>
	Api.fetch(`testnav-bruker-service/api/v1/brukere/${id}/token`, {
		method: 'POST',
	}).then((response) => response.text())

const changeBukernavn = (id: string, brukernavn: string, jwt?: string) =>
	Api.fetch(
		`testnav-bruker-service/api/v1/brukere/${id}/brukernavn`,
		{
			headers: jwt ? { 'User-Jwt': jwt } : {},
			// @ts-ignore
			method: 'PATCH',
		},
		brukernavn
	)

export default {
	getBruker,
	getToken,
	changeBukernavn,
	createBruker,
	deleteBruker,
}
